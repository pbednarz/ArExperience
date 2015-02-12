package pl.solaris.arexperience.metaio;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.metaio.sdk.ARELActivity;
import com.metaio.sdk.MetaioDebug;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.solaris.arexperience.R;
import pl.solaris.arexperience.utils.Utils;
import pl.solaris.arexperience.view.FlashSwitch;


public class ARELViewActivity extends ARELActivity {

    @InjectView(R.id.flashSwitch)
    FlashSwitch flashBtn;

    @InjectView(R.id.root)
    FrameLayout flRoot;

    @InjectView(R.id.action_share)
    FloatingActionButton shareBtn;

    private boolean hasFlash;

    @Override
    protected int getGUILayout() {
        return R.layout.activity_arelview;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState, View mGUIView) {
        super.onCreateView(savedInstanceState, mGUIView);
        ButterKnife.inject(this, mGUIView);
        flashBtn.init();
        shareBtn.setTranslationY(getResources().getDimensionPixelOffset(R.dimen.fab_big_anim_offset));
        if (savedInstanceState == null) {
            shareBtn.setTranslationY(getResources().getDimensionPixelOffset(R.dimen.fab_big_anim_offset));
        }
    }

    @OnClick(R.id.action_share)
    public void shareClicked() {
        mWebView.loadUrl("javascript:arel.Scene.shareScreenshot(true);");
    }

    @Override
    protected boolean onScreenshot(Bitmap bitmap, boolean saveToGalleryWithoutDialog) {
        boolean result = false;
        String imagePath = null;
        try {
            final String filename = "screenshot-" + DateFormat.format("yyyy-MM-dd-hh-mm-ss", new Date());
            imagePath = Utils.saveToExternalStorage(ARELViewActivity.this, bitmap, filename, ".jpg").toString();
            result = true;
        } catch (Exception e) {
            MetaioDebug.printStackTrace(Log.ERROR, e);
        } finally {
            final boolean finalResult = result;
            final String finalImagePath = imagePath;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (finalResult) {
                        Utils.shareBitmap(ARELViewActivity.this, finalImagePath);
                    } else {
                        Toast.makeText(ARELViewActivity.this, getString(R.string.share_error), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        return result;
    }

    @Override
    protected void onSceneComplete() {
        super.onSceneComplete();
        if (shareBtn.getTranslationY() != 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    shareBtn.animate()
                            .translationY(0)
                            .setInterpolator(new OvershootInterpolator(1.f))
                            .setDuration(320)
                            .start();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
