package com.lzm.KnittingHelp.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.lzm.KnittingHelp.MainActivity;
import com.lzm.KnittingHelp.R;

import java.io.File;

/**
 * Created by DELL on 13/10/2014.
 */
public class Utils {
//    public static void openFragment(MainActivity context, Fragment fragment, String title) {
//        openFragment(context, fragment, title, null);
//    }
//
//    public static void openFragment(MainActivity context, Fragment fragment, String title, Bundle args) {
//        context.setTitle(title);
//        FragmentManager fragmentManager = context.getFragmentManager();
//        ListView mainLayout = (ListView) context.findViewById(R.id.main_list_patterns);
//        if (fragment == null) {
//            fragmentManager.beginTransaction()
//                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                    .hide(fragmentManager.findFragmentById(R.id.content_frame))
//                    .addToBackStack("")
//                    .commit();
//            mainLayout.setVisibility(View.VISIBLE);
//        } else {
//            if (args != null) {
//                fragment.setArguments(args);
//            }
////                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//            fragmentManager.beginTransaction()
//                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                    .replace(R.id.content_frame, fragment)
//                    .addToBackStack("")
//                    .commit();
//            mainLayout.setVisibility(LinearLayout.GONE);
//        }
//    }

    public static String getStringResourceByName(Context c, String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "string", packageName);
        if (resId == 0) {
            return aString;
        } else {
            return c.getString(resId);
        }
    }

    public static String getPluralResourceByName(Context c, String aString, int quantity, String param1) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "plurals", packageName);
        if (resId == 0) {
            return aString;
        } else {
            return c.getResources().getQuantityString(resId, quantity, param1);
        }
    }

    public static int getImageResourceByName(Context c, String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "drawable", packageName);
        return resId;
    }

    public static String getFolder(Context context) {
        String pathFolder;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            pathFolder = Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.db_name);
        } else {
            /* save the folder in internal memory of phone */
            pathFolder = "/data/data/" + context.getPackageName() + File.separator + context.getString(R.string.db_name);
        }
        File folder = new File(pathFolder);
        folder.mkdirs();
        return pathFolder;
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void showSoftKeyboard(Activity activity, View view) {
        try {
            InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//            keyboard.showSoftInput(view, 0);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void toast(String s, Context context) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static void vibrate(Context context) {
        vibrate(100, context);
    }

    public static void vibrate(int length, Context context) {
        Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for [length] milliseconds
        v1.vibrate(length);

        // Output yes if can vibrate, no otherwise
//        if (v1.hasVibrator()) {
//            Log.v("Can Vibrate", "YES");
//        } else {
//            Log.v("Can Vibrate", "NO");
//        }
    }

    public static void vibrate(long[] pattern, Context context) {
        // Start without a delay
        // Vibrate for 100 milliseconds
        // Sleep for 1000 milliseconds
        // long[] pattern = {0, 100, 1000};

        // The first value indicates the number of milliseconds to wait before turning the vibrator ON: 0=Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        // long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        Vibrator v1 = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
        //     '0' here means to repeat indefinitely
        //     '0' is actually the index at which the pattern keeps repeating from (the start)
        //     To repeat the pattern from any other point, you could increase the index, e.g. '1'
        v1.vibrate(pattern, -1);
    }

    public static void playSound(Context context, int sound) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float percent = 0.7f;
        int seventyVolume = (int) (maxVolume * percent);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
        final MediaPlayer mp = MediaPlayer.create(context, sound);
        mp.start();
    }
}
