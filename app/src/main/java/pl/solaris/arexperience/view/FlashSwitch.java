package pl.solaris.arexperience.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.metaio.sdk.jni.IMetaioSDKAndroid;

/**
 * Created by pbednarz on 2015-02-11.
 */
public class FlashSwitch extends SwitchCompat implements CompoundButton.OnCheckedChangeListener {
    public FlashSwitch(Context context) {
        super(context);
    }

    public FlashSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlashSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        boolean hasFlash = getContext().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            ViewGroup parent = (ViewGroup) getParent();
            if (parent != null) {
                parent.removeView(this);
            } else {
                setVisibility(GONE);
            }
        } else {
            setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            IMetaioSDKAndroid.startTorch((Activity) getContext());
        } else {
            IMetaioSDKAndroid.stopTorch((Activity) getContext());
        }
    }
}
