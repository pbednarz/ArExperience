package pl.solaris.arexperience;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.CompoundButton;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKAndroid;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.solaris.arexperience.activity.ContentActivity;

/**
 * Created by pbednarz on 2015-02-10.
 */
public class RecognitionActivity extends ARViewActivity {

    File trackingConfigFile;
    @InjectView(R.id.flashSwitch)
    SwitchCompat flashBtn;
    private MetaioSDKCallbackHandler mCallbackHandler;
    private boolean hasFlash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCallbackHandler = new MetaioSDKCallbackHandler(this);
    }


    @Override
    protected void onCreateView(Bundle savedInstanceState, View mGUIView) {
        super.onCreateView(savedInstanceState, mGUIView);
        ButterKnife.inject(this, mGUIView);
        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (hasFlash) {
            flashBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        IMetaioSDKAndroid.startTorch(RecognitionActivity.this);
                    } else {
                        IMetaioSDKAndroid.stopTorch(RecognitionActivity.this);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        mCallbackHandler = null;
        super.onDestroy();
    }

    @Override
    protected int getGUILayout() {
        return R.layout.activity_recognition;
    }

    @Override
    protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
        return mCallbackHandler;
    }

    @Override
    protected void loadContents() {
//        trackingConfigFile = AssetsManager.getAssetPathAsFile(getApplicationContext(), "tracking/Tracking.xml");
//        final boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile);
        metaioSDK.setTrackingConfiguration("QRCODE");
    }

    @Override
    protected void onGeometryTouched(IGeometry geometry) {

    }

    final static class MetaioSDKCallbackHandler extends IMetaioSDKCallback {
        WeakReference<Activity> activity;

        MetaioSDKCallbackHandler(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void onTrackingEvent(TrackingValuesVector trackingValues) {
            for (int i = 0; i < trackingValues.size(); i++) {
                final TrackingValues v = trackingValues.get(i);
                if (v.isTrackingState()) {
                    final String[] tokens = v.getAdditionalValues().split("::");
                    if (tokens.length > 1 && activity.get() != null) {
                        if (URLUtil.isValidUrl(tokens[1]))
                            activity.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(activity.get(), ContentActivity.class);
                                    i.putExtra(ContentActivity.WEBPAGE_EXTRA, tokens[1]);
                                    activity.get().startActivity(i);
                                }
                            });
                    }
                }

            }
        }

    }
}
