// Copyright 2007-2014 Metaio GmbH. All rights reserved.
package com.metaio.sdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.metaio.sdk.jni.ByteBuffer;
import com.metaio.sdk.jni.GestureHandler;
import com.metaio.sdk.jni.IARELInterpreterCallback;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.Vector3d;

import java.io.File;

/**
 * This is the base activity that can be used to load and run AREL scenes
 */
public class ARELActivity extends ARViewActivity {

    /**
     * Intent extra key for the AREL scene file path (File object). Append this to getPackageName(),
     * e.g.
     * <p>
     * <code>intent.putExtra(getPackageName()+ARELActivity.INTENT_EXTRA_AREL_SCENE, filepath);</code>
     */
    public static final String INTENT_EXTRA_AREL_SCENE = ".AREL_SCENE";

    /**
     * Gesture handler
     */
    protected GestureHandlerAndroid mGestureHandler;
    /**
     * AREL WebView where AREL HTML and JavaScript is displayed and executed
     */
    protected WebView mWebView;

    /**
     * This object is the main interface to AREL
     */
    protected ARELInterpreterAndroidJava mARELInterpreter;

    /**
     * Default ARELInterpreter callback
     */
    private IARELInterpreterCallback mARELCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create the AREL interpreter and its callback
        mARELInterpreter = new ARELInterpreterAndroidJava();
        IARELInterpreterCallback callback = getARELInterpreterCallback();
        if (callback != null)
            mARELInterpreter.registerCallback(callback);
        // create AREL WebView
        mWebView = new WebView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addContentView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // attach a WebView to the AREL interpreter and initialize it
        mARELInterpreter.initWebView(mWebView, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bring to front after resuming camera and GL surface
        mWebView.bringToFront();

        if (mGUIView != null) {
            mGUIView.bringToFront();
        }

        if (mARELInterpreter != null && mRendererInitialized) {
            mARELInterpreter.onResume();
        }

        // Resume WebView timers
        mWebView.resumeTimers();
    }

    @Override
    protected void onPause() {
        if (mARELInterpreter != null && mRendererInitialized) {
            mARELInterpreter.onPause();
        }

        // Pause WebView timers
        mWebView.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            mARELInterpreter.release();
            mARELInterpreter.delete();
            mARELInterpreter = null;
            mARELCallback.delete();
            mARELCallback = null;
            mRendererInitialized = false;
            mWebView.setOnTouchListener(null);
            mWebView = null;
            mGestureHandler.delete();
            mGestureHandler = null;
        } catch (Exception e) {
            MetaioDebug.log(Log.ERROR, "Error releasing AREL resources");
            MetaioDebug.printStackTrace(Log.ERROR, e);
        }

        super.onDestroy();
    }

    @Override
    protected int getGUILayout() {
        return 0;
    }

    @Override
    protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
        // Should not be used for AREL
        return null;
    }

    /**
     * Get AREL interpreter callback handler. Note that the default interpreter calls
     * {@link ARELActivity#loadARELScene()} in {@link IMetaioSDKCallback#onSDKReady()} callback.
     *
     * @return instance of class that implements IARELInterpreterCallback
     */
    protected IARELInterpreterCallback getARELInterpreterCallback() {
        mARELCallback = new ARELInterpreterCallback();
        return mARELCallback;
    }

    @Override
    public void onDrawFrame() {
        // instead of metaioSDK.render, call ARELInterpreterAndroidJava.update()
        if (mRendererInitialized) {
            mARELInterpreter.update();
        }
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();

        if (mGestureHandler == null) {
            // create gesture handler and initialize AREL interpreter
            mGestureHandler = new GestureHandlerAndroid(metaioSDK, GestureHandler.GESTURE_ALL, mWebView, mSurfaceView);
            mARELInterpreter.initialize(metaioSDK, mGestureHandler);
        } else {
            // Update reference to the GLSurfaceView
            mGestureHandler.setGLSurfaceView(mSurfaceView);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        if (mRendererInitialized) {
            mARELInterpreter.onSurfaceChanged(width, height);
        }
    }

    @Override
    protected void loadContents() {
        // Should not be used for AREL
    }

    /**
     * Load AREL scene
     */
    protected void loadARELScene() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final File filepath =
                        (File) getIntent().getSerializableExtra(getPackageName() + INTENT_EXTRA_AREL_SCENE);
                if (filepath != null) {
                    MetaioDebug.log("Loading AREL file: " + filepath.getPath());
                    mARELInterpreter.loadARELFile(filepath);
                } else {
                    MetaioDebug.log(Log.ERROR, "No AREL scene file passed to the intent");
                }

                // TODO: set custom radar properties
                mARELInterpreter.setRadarProperties(IGeometry.ANCHOR_TL, new Vector3d(0f), new Vector3d(1f));

                // show AREL webview and start handling touch events
                mWebView.setOnTouchListener(mGestureHandler);

            }
        });

    }

    @Override
    protected void onGeometryTouched(final IGeometry geometry) {
    }

    /**
     * Called when share screen shot is requested
     *
     * @param bitmap                     Screen shot
     * @param saveToGalleryWithoutDialog true if screen should be directly saved to gallery
     * @return true if handled
     */
    protected boolean onScreenshot(Bitmap bitmap, boolean saveToGalleryWithoutDialog) {
        return false;
    }

    protected void onSceneComplete() {
    }

    /**
     * Default implementation of IARELInterpreterCallback
     */
    class ARELInterpreterCallback extends IARELInterpreterCallback {
        @Override
        public void onSDKReady() {
            loadARELScene();
        }

        @Override
        public boolean shareScreenshot(ByteBuffer image, boolean saveToGalleryWithoutDialog) {
            byte[] bytearray = image.getBuffer();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
            final boolean result = onScreenshot(bitmap, saveToGalleryWithoutDialog);
            bitmap.recycle();
            bitmap = null;
            return result;
        }

        @Override
        public void onSceneReady() {
            super.onSceneReady();
            onSceneComplete();
        }
    }
}
