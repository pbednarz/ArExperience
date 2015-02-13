package pl.solaris.arexperience.metaio;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import pl.solaris.arexperience.BuildConfig;
import pl.solaris.arexperience.R;

/**
 * Created by pbednarz on 2015-02-10.
 */
public class ContentLoaderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
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

        private WeakReference<ContentLoaderActivity> activityWeakReference;

        private AssetsExtracter(ContentLoaderActivity activity) {
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