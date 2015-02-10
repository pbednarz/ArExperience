package pl.solaris.arexperience;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.metaio.sdk.ARELActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by pbednarz on 2015-02-10.
 */
public class ContentLoaderActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        AsyncTaskCompat.executeParallel(new AssetsExtracter(this), 0);
    }

    /**
     * This task extracts all the assets to an external or internal location
     * to make them accessible to metaio SDK
     */
    private static class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> {

        private WeakReference<Activity> activityWeakReference;

        private AssetsExtracter(Activity activity) {
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
                Activity activity = activityWeakReference.get();
                if (result) {
                    // Start AREL Activity on success
                    final File arelConfigFilePath = AssetsManager.getAssetPathAsFile(activity.getApplicationContext(), "index.xml");
                    MetaioDebug.log("AREL config to be passed to intent: " + arelConfigFilePath.getPath());
                    Intent intent = new Intent(activity.getApplicationContext(), RecognitionActivity.class);
                    intent.putExtra(activity.getPackageName() + ARELActivity.INTENT_EXTRA_AREL_SCENE, arelConfigFilePath);
                    activity.startActivity(intent);
                } else {
                    // Show a toast with an error message
                    Toast toast = Toast.makeText(activity.getApplicationContext(), "Error extracting application assets!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                activity.finish();
            }
        }

    }
}