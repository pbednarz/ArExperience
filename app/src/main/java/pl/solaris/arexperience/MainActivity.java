package pl.solaris.arexperience;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import pl.solaris.arexperience.metaio.RecognitionActivity;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        AsyncTaskCompat.executeParallel(new AssetsExtracter(this), 0);
    }

    public void openContent(Boolean result) {
        if (result) {
            Intent intent = new Intent(this, RecognitionActivity.class);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(this, "Error extracting application assets!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    /**
     * This task extracts all the assets to an external or internal location
     * to make them accessible to metaio SDK
     */
    private static class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> {

        private WeakReference<MainActivity> activityWeakReference;

        private AssetsExtracter(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {
                AssetsManager.extractAllAssets(activityWeakReference.get().getApplicationContext(), BuildConfig.DEBUG);
            } catch (Exception e) {
                MetaioDebug.log(Log.ERROR, "Error extracting assets: " + e.getMessage());
                MetaioDebug.printStackTrace(Log.ERROR, e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (activityWeakReference.get() != null) {
                activityWeakReference.get().openContent(result);
            }
            activityWeakReference.clear();
        }

    }
}
