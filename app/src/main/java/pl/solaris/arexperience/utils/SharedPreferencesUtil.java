package pl.solaris.arexperience.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pbednarz on 2015-02-10.
 */
public class SharedPreferencesUtil {

    public static final String TUTORIAL_LEARNED = "pl.solaris.arexperience.utils.TUTORIAL_LEARNED";

    public static void saveTutorialLearned(Activity activity, boolean value) {
        writeToSharedPreferences(activity, TUTORIAL_LEARNED, value);
    }

    public static boolean isTutorialLearned(Activity activity) {
        return readFromSharedPreferences(activity, TUTORIAL_LEARNED, false);
    }

    public static void writeToSharedPreferences(Activity activity, String key, boolean value) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean readFromSharedPreferences(Activity activity, String key, boolean defValue) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, defValue);
    }
}
