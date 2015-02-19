package pl.solaris.arexperience.metaio;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.metaio.sdk.ARELActivity;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.tools.io.AssetsManager;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.solaris.arexperience.BuildConfig;
import pl.solaris.arexperience.R;
import pl.solaris.arexperience.activity.ContentActivity;
import pl.solaris.arexperience.tutorial.ShowcaseFragment;
import pl.solaris.arexperience.view.FlashSwitch;

/**
 * Created by pbednarz on 2015-02-10.
 */
public class RecognitionActivity extends ARViewActivity {

    public static final int QR_CODE = -1;
    private int currentScanner = QR_CODE;
    public static final int IMAGE_RECOGNITION = 1;
    @InjectView(R.id.flashSwitch)
    FlashSwitch flashBtn;
    @InjectView(R.id.action_qrcode)
    FloatingActionButton actionQr;
    @InjectView(R.id.action_star)
    FloatingActionButton actionStar;
    @InjectView(R.id.actions_menu)
    FloatingActionsMenu actionsMenu;
    @InjectView(R.id.indicator_image)
    ImageView typeImageView;
    @InjectView(R.id.root)
    FrameLayout flRoot;
    private MetaioSDKCallbackHandler mCallbackHandler;

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
        flashBtn.init();
        if (savedInstanceState == null) {
            actionsMenu.collapse();
            actionsMenu.setTranslationY(getResources().getDimensionPixelOffset(R.dimen.fab_anim_offset));
            flRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    flRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    actionsMenu.animate()
                            .translationY(0)
                            .setInterpolator(new OvershootInterpolator(1.f))
                            .setStartDelay(3600)
                            .setDuration(320)
                            .start();
                    return true;
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
        loadContentType(currentScanner);
    }

    protected void loadContentType(@Scanner int scanner) {
        currentScanner = scanner;
        if (scanner == IMAGE_RECOGNITION) {
            File trackingConfigFile = AssetsManager.getAssetPathAsFile(getApplicationContext(), "html/resources/TrackingConfig_80a166e45834cf806bdeebbd5e4da0af.zip");
            loadFileTracking(trackingConfigFile);
        } else {
            loadPredefinedTracking("QRCODE");
        }
    }

    private void loadFileTracking(final File trackingConfigFile) {
        metaioSDK.setTrackingConfiguration(trackingConfigFile);
    }

    private void loadPredefinedTracking(String trackingConfig) {
        metaioSDK.setTrackingConfiguration(trackingConfig);
    }

    @OnClick(R.id.action_qrcode)
    public void qrClicked(View doClick) {
        actionsMenu.collapse();
        if (currentScanner == IMAGE_RECOGNITION) {
            actionQr.setIcon(R.drawable.ic_eye);
            typeImageView.setImageResource(R.drawable.ic_qr);
            loadContentType(QR_CODE);
        } else {
            actionQr.setIcon(R.drawable.ic_qr);
            typeImageView.setImageResource(R.drawable.ic_eye);
            loadContentType(IMAGE_RECOGNITION);
        }
    }

    @OnClick(R.id.action_star)
    public void startClicked(View doClick) {
        ShowcaseFragment.startShowcase(this, currentScanner);
    }

    @OnClick(R.id.action_panorama)
    public void panoramaClicked() {
        actionsMenu.collapse();
        openARELActivity();
    }

    public void openContentActivity(String url) {
        Intent i = new Intent(this, ContentActivity.class);
        i.putExtra(ContentActivity.WEBPAGE_EXTRA, url);
        startActivity(i);
    }

    public void openARELActivity() {
        final File arelConfigFilePath = AssetsManager.getAssetPathAsFile(this, "index.xml");
        Intent intent = new Intent(this, ARELViewActivity.class);
        intent.putExtra(BuildConfig.APPLICATION_ID + ARELActivity.INTENT_EXTRA_AREL_SCENE, arelConfigFilePath);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onGeometryTouched(IGeometry geometry) {
    }

    @IntDef({QR_CODE, IMAGE_RECOGNITION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Scanner {
    }

    final static class MetaioSDKCallbackHandler extends IMetaioSDKCallback {
        WeakReference<RecognitionActivity> activity;

        MetaioSDKCallbackHandler(RecognitionActivity activity) {
            this.activity = new WeakReference<RecognitionActivity>(activity);
        }

        @Override
        public void onTrackingEvent(TrackingValuesVector trackingValues) {
            for (int i = 0; i < trackingValues.size(); i++) {
                final TrackingValues v = trackingValues.get(i);
                if (v.isTrackingState() && activity.get() != null) {
                    final RecognitionActivity this_ = activity.get();
                    if (v.getCosName().equals("junaio_AugmentedRealityBrowser_1")) {
                        this_.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                this_.openARELActivity();
                            }
                        });
                    } else {
                        final String values = v.getAdditionalValues();
                        final String[] tokens = values.split("::");
                        if (tokens.length > 1) {
                            if (URLUtil.isValidUrl(tokens[1])) {
                                this_.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        this_.openContentActivity(tokens[1]);
                                    }
                                });
                            } else {
                                this_.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tokens[1]));
                                        try {
                                            this_.startActivity(intent);
                                        } catch (ActivityNotFoundException e) {
                                            e.printStackTrace();
                                            Toast.makeText(this_, tokens[1], Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }

            }
        }
    }
}
