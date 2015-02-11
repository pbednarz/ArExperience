package pl.solaris.arexperience.metaio;

import android.os.Bundle;
import android.view.View;

import com.metaio.sdk.ARELActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.solaris.arexperience.R;
import pl.solaris.arexperience.view.FlashSwitch;


public class ARELViewActivity extends ARELActivity {

    @InjectView(R.id.flashSwitch)
    FlashSwitch flashBtn;
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
