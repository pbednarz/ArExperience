package pl.solaris.arexperience.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import rx.Observable;

/**
 * Created by pbednarz on 2015-02-10.
 */
public class Utils {

    public static File getPublicFileStorage(String filename) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File picDir = new File(root + "/ArExperience");
        picDir.mkdirs();
        return new File(picDir, filename);
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
}
