package com.lzm.KnittingHelp.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
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
            e.printStackTrace();
        }
    }

}
