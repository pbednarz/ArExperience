package pl.solaris.arexperience;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import pl.solaris.arexperience.tutorial.TutorialActivity;
import pl.solaris.arexperience.utils.SharedPreferencesUtil;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SharedPreferencesUtil.isTutorialLearned(this)) {
            SharedPreferencesUtil.saveTutorialLearned(this, true);
            Intent i = new Intent(this, TutorialActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, ContentLoaderActivity.class);
            startActivity(i);
        }
        finish();
    }
}
