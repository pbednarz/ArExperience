package pl.solaris.arexperience.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import pl.solaris.arexperience.R;
import rx.Observable;

/**
 * Created by pbednarz on 2015-02-10.
 */
public class Utils {

    public static File getPublicDirStorage() {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File picDir = new File(root + "/ArExperience");
        picDir.mkdirs();
        return picDir;
    }

    public static File getPublicFileStorage(String filename) {
        return new File(getPublicDirStorage(), filename);
    }

    public static Uri saveToExternalStorage(Context context, Bitmap bitmap, String name, String extension) {
        try {
            final File f = getPublicFileStorage(name + extension);
            if (!f.exists())
                f.createNewFile();
            FileOutputStream fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();
            refreshSd(f, context);
            return Uri.fromFile(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void refreshSd(File file, Context context) {
        MediaScannerConnection.scanFile(
                context,
                new String[]{file.getAbsolutePath()},
                null,
                (path, uri) -> {// file was scanned
                });
    }

    private static Bitmap getBitmapForVisibleRegion(View view) {
        Bitmap returnedBitmap = null;
        view.setDrawingCacheEnabled(true);
        returnedBitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return returnedBitmap;
    }

    public static Observable<Bitmap> getViewBitmapObservable(View view) {
        return view != null ?
                Observable.defer(() -> Observable.just(getBitmapForVisibleRegion(view)))
                : Observable.empty();
    }

    public static void shareBitmap(Activity activity, String bitmapPath) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity.getString(R.string.share_subejct));
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, activity.getString(R.string.share_msg));
        shareIntent.putExtra(Intent.EXTRA_STREAM,
                Uri.parse(bitmapPath));
        activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.action_share)));
    }
}
