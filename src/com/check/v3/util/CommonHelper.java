package com.check.v3.util;

import java.util.Collection;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


public final class CommonHelper {

    private static final String TAG = "CommonHelper";

    public static ProgressBar createProgress(final Context context) {
        final ProgressBar p = new ProgressBar(context);
        p.setIndeterminate(true);
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                40, 40);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        p.setLayoutParams(lp);
        return p;
    }


    public static void hideKeyboard(final Context context, final EditText input) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    public static boolean isEmpty(final Collection<?> c) {
        return (c == null) || (c.size() == 0);
    }

    public static boolean isEmpty(final String str) {
        return (str == null) || str.equals("");
    }

    public static void notify(final Context context, final CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void notify(final Context context, final int resId) {
        Toast.makeText(context, context.getText(resId), Toast.LENGTH_SHORT)
                .show();
    }

    public static void setFullScreen(final Activity activity,
            final boolean fullscreen) {
        if (fullscreen) {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void setPortraitOrientation(final Activity activity,
            final boolean portrait) {
        if (portrait) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

}
