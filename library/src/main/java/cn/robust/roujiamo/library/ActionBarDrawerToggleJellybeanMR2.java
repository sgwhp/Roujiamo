package cn.robust.roujiamo.library;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

public class ActionBarDrawerToggleJellybeanMR2 {
    private static final String TAG = "ActionBarDrawerToggleImplJellybeanMR2";

    private static final int[] THEME_ATTRS = new int[] {
            android.R.attr.homeAsUpIndicator
    };

    public static Object setActionBarUpIndicator(Object info, Activity activity,
                                                 Drawable drawable, int contentDescRes) {
        final ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(drawable);
            actionBar.setHomeActionContentDescription(contentDescRes);
        }
        return info;
    }

    public static Object setActionBarDescription(Object info, Activity activity,
                                                 int contentDescRes) {
        final ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.setHomeActionContentDescription(contentDescRes);
        }
        return info;
    }

    public static Drawable getThemeUpIndicator(Activity activity) {
        final TypedArray a = activity.obtainStyledAttributes(THEME_ATTRS);
        final Drawable result = a.getDrawable(0);
        a.recycle();
        return result;
    }
}
