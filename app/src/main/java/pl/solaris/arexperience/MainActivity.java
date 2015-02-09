package pl.solaris.arexperience;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.metaio.sdk.ARELActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import java.io.File;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private AssetsExtracter mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // extract all the assets
        mTask = new AssetsExtracter();
        mTask.execute(0);

    }

    /**
     * This task extracts all the assets to an external or internal location
     * to make them accessible to metaio SDK
     */
    private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {
                // Extract all assets and overwrite existing files if debug build
                AssetsManager.extractAllAssets(getApplicationContext(), BuildConfig.DEBUG);
            } catch (IOException e) {
                MetaioDebug.log(Log.ERROR, "Error extracting assets: " + e.getMessage());
                MetaioDebug.printStackTrace(Log.ERROR, e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // Start AREL Activity on success
                final File arelConfigFilePath = AssetsManager.getAssetPathAsFile(getApplicationContext(), "index.xml");
                MetaioDebug.log("AREL config to be passed to intent: " + arelConfigFilePath.getPath());
                Intent intent = new Intent(getApplicationContext(), ARELViewActivity.class);
                intent.putExtra(getPackageName() + ARELActivity.INTENT_EXTRA_AREL_SCENE, arelConfigFilePath);
                startActivity(intent);
            } else {
                // Show a toast with an error message
                Toast toast = Toast.makeText(getApplicationContext(), "Error extracting application assets!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }

            finish();
        }

    }
}
