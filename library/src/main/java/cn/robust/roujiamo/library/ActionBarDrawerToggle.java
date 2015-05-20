package cn.robust.roujiamo.library;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import cn.robust.roujiamo.library.drawable.AbsRoujiamoDrawable;

/**
 * most of these sources are copied from support-v4
 * @see android.support.v4.app.ActionBarDrawerToggle
 * Created by wuhongping on 15-4-10.
 */
public class ActionBarDrawerToggle implements DrawerLayout.DrawerListener {

    /**
     * Allows an implementing Activity to return an {@link ActionBarDrawerToggle.Delegate} to use
     * with ActionBarDrawerToggle.
     */
    public interface DelegateProvider {

        /**
         * @return Delegate to use for ActionBarDrawableToggles, or null if the Activity
         *         does not wish to override the default behavior.
         */
        Delegate getDrawerToggleDelegate();
    }

    public interface Delegate {
        /**
         * @return Up indicator drawable as defined in the Activity's theme, or null if one is not
         *         defined.
         */
        Drawable getThemeUpIndicator();

        /**
         * Set the Action Bar's up indicator drawable and content description.
         *
         * @param upDrawable     - Drawable to set as up indicator
         * @param contentDescRes - Content description to set
         */
        void setActionBarUpIndicator(Drawable upDrawable, int contentDescRes);

        /**
         * Set the Action Bar's up indicator content description.
         *
         * @param contentDescRes - Content description to set
         */
        void setActionBarDescription(int contentDescRes);
    }

    private interface ActionBarDrawerToggleImpl {
        Drawable getThemeUpIndicator(Activity activity);
        Object setActionBarUpIndicator(Object info, Activity activity,
                                       Drawable themeImage, int contentDescRes);
        Object setActionBarDescription(Object info, Activity activity, int contentDescRes);
    }

    private static class ActionBarDrawerToggleImplBase implements ActionBarDrawerToggleImpl {
        @Override
        public Drawable getThemeUpIndicator(Activity activity) {
            return null;
        }

        @Override
        public Object setActionBarUpIndicator(Object info, Activity activity,
                                              Drawable themeImage, int contentDescRes) {
            // No action bar to set.
            return info;
        }

        @Override
        public Object setActionBarDescription(Object info, Activity activity, int contentDescRes) {
            // No action bar to set
            return info;
        }
    }

    private static class ActionBarDrawerToggleImplHC implements ActionBarDrawerToggleImpl {
        @Override
        public Drawable getThemeUpIndicator(Activity activity) {
            return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(activity);
        }

        @Override
        public Object setActionBarUpIndicator(Object info, Activity activity,
                                              Drawable themeImage, int contentDescRes) {
            return ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(info, activity,
                    themeImage, contentDescRes);
        }

        @Override
        public Object setActionBarDescription(Object info, Activity activity, int contentDescRes) {
            return ActionBarDrawerToggleHoneycomb.setActionBarDescription(info, activity,
                    contentDescRes);
        }
    }

    private static class ActionBarDrawerToggleImplJellybeanMR2
            implements ActionBarDrawerToggleImpl {
        @Override
        public Drawable getThemeUpIndicator(Activity activity) {
            return ActionBarDrawerToggleJellybeanMR2.getThemeUpIndicator(activity);
        }

        @Override
        public Object setActionBarUpIndicator(Object info, Activity activity,
                                              Drawable themeImage, int contentDescRes) {
            return ActionBarDrawerToggleJellybeanMR2.setActionBarUpIndicator(info, activity,
                    themeImage, contentDescRes);
        }

        @Override
        public Object setActionBarDescription(Object info, Activity activity, int contentDescRes) {
            return ActionBarDrawerToggleJellybeanMR2.setActionBarDescription(info, activity,
                    contentDescRes);
        }
    }

    private static final ActionBarDrawerToggleImpl IMPL;

    static {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 18) {
            IMPL = new ActionBarDrawerToggleImplJellybeanMR2();
        } else if (version >= 11) {
            IMPL = new ActionBarDrawerToggleImplHC();
        } else {
            IMPL = new ActionBarDrawerToggleImplBase();
        }
    }

    /** Fraction of its total width by which to offset the toggle drawable. */
    private static final float TOGGLE_DRAWABLE_OFFSET = 1 / 3f;

    // android.R.id.home as defined by public API in v11
    private static final int ID_HOME = 0x0102002c;

    private final Activity mActivity;
    private final Delegate mActivityImpl;
    private final DrawerLayout mDrawerLayout;
    private boolean mDrawerIndicatorEnabled = true;

    private Drawable mThemeImage;
    private AbsRoujiamoDrawable mSlider;
    private final int mOpenDrawerContentDescRes;
    private final int mCloseDrawerContentDescRes;

    private Object mSetIndicatorInfo;

    /**
     * Construct a new ActionBarDrawerToggle.
     *
     * <p>The given {@link Activity} will be linked to the specified {@link DrawerLayout}.
     * The provided drawer indicator drawable will animate slightly off-screen as the drawer
     * is opened, indicating that in the open state the drawer will move off-screen when pressed
     * and in the closed state the drawer will move on-screen when pressed.</p>
     *
     * <p>String resources must be provided to describe the open/close drawer actions for
     * accessibility services.</p>
     *
     * @param activity The Activity hosting the drawer
     * @param drawerLayout The DrawerLayout to link to the given Activity's ActionBar
     * @param openDrawerContentDescRes A String resource to describe the "open drawer" action
     *                                 for accessibility
     * @param closeDrawerContentDescRes A String resource to describe the "close drawer" action
     *                                  for accessibility
     */
    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, AbsRoujiamoDrawable iconDrawable,
                                 int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        mActivity = activity;

        // Allow the Activity to provide an impl
        if (activity instanceof DelegateProvider) {
            mActivityImpl = ((DelegateProvider) activity).getDrawerToggleDelegate();
        } else {
            mActivityImpl = null;
        }

        mDrawerLayout = drawerLayout;
        mOpenDrawerContentDescRes = openDrawerContentDescRes;
        mCloseDrawerContentDescRes = closeDrawerContentDescRes;

        mThemeImage = getThemeUpIndicator();
        mSlider = iconDrawable;
    }

    /**
     * Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
     *
     * <p>This should be called from your <code>Activity</code>'s
     * {@link Activity#onPostCreate(android.os.Bundle) onPostCreate} method to synchronize after
     * the DrawerLayout's instance state has been restored, and any other time when the state
     * may have diverged in such a way that the ActionBarDrawerToggle was not notified.
     * (For example, if you stop forwarding appropriate drawer events for a period of time.)</p>
     */
    public void syncState() {
        mSlider.open(mDrawerLayout.isDrawerOpen(GravityCompat.START), false, true);
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//            mSlider.setPercentage(1, true);
//        } else {
//            mSlider.setPercentage(0, true);
//        }

        if (mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(mSlider, mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
                    mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
        }
    }

    public void setDrawable(AbsRoujiamoDrawable drawable){
        mSlider = drawable;
        syncState();
    }

    /**
     * Enable or disable the drawer indicator. The indicator defaults to enabled.
     *
     * <p>When the indicator is disabled, the <code>ActionBar</code> will revert to displaying
     * the home-as-up indicator provided by the <code>Activity</code>'s theme in the
     * <code>android.R.attr.homeAsUpIndicator</code> attribute instead of the animated
     * drawer glyph.</p>
     *
     * @param enable true to enable, false to disable
     */
    public void setDrawerIndicatorEnabled(boolean enable) {
        if (enable != mDrawerIndicatorEnabled) {
            if (enable) {
                setActionBarUpIndicator(mSlider, mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
                        mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
            } else {
                setActionBarUpIndicator(mThemeImage, 0);
            }
            mDrawerIndicatorEnabled = enable;
        }
    }

    /**
     * @return true if the enhanced drawer indicator is enabled, false otherwise
     * @see #setDrawerIndicatorEnabled(boolean)
     */
    public boolean isDrawerIndicatorEnabled() {
        return mDrawerIndicatorEnabled;
    }

    /**
     * This method should always be called by your <code>Activity</code>'s
     * {@link Activity#onConfigurationChanged(android.content.res.Configuration) onConfigurationChanged}
     * method.
     *
     * @param newConfig The new configuration
     */
    public void onConfigurationChanged(Configuration newConfig) {
        // Reload drawables that can change with configuration
        mThemeImage = getThemeUpIndicator();
        syncState();
    }

    /**
     * This method should be called by your <code>Activity</code>'s
     * {@link Activity#onOptionsItemSelected(android.view.MenuItem) onOptionsItemSelected} method.
     * If it returns true, your <code>onOptionsItemSelected</code> method should return true and
     * skip further processing.
     *
     * @param item the MenuItem instance representing the selected menu item
     * @return true if the event was handled and further processing should not occur
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == ID_HOME && mDrawerIndicatorEnabled) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    /**
     * {@link DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView The child view that was moved
     * @param slideOffset The new offset of this drawer within its range, from 0-1
     */
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        mSlider.setPercentage(slideOffset, true);
    }

    /**
     * {@link DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now open
     */
    @Override
    public void onDrawerOpened(View drawerView) {
        mSlider.open(true, false, false);
        if (mDrawerIndicatorEnabled) {
            setActionBarDescription(mCloseDrawerContentDescRes);
        }
    }

    /**
     * {@link DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now closed
     */
    @Override
    public void onDrawerClosed(View drawerView) {
        mSlider.open(false, false, false);
        if (mDrawerIndicatorEnabled) {
            setActionBarDescription(mOpenDrawerContentDescRes);
        }
    }

    /**
     * {@link DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param newState The new drawer motion state
     */
    @Override
    public void onDrawerStateChanged(int newState) {
    }

    Drawable getThemeUpIndicator() {
        if (mActivityImpl != null) {
            return mActivityImpl.getThemeUpIndicator();
        }
        return IMPL.getThemeUpIndicator(mActivity);
    }

    void setActionBarUpIndicator(Drawable upDrawable, int contentDescRes) {
        if (mActivityImpl != null) {
            mActivityImpl.setActionBarUpIndicator(upDrawable, contentDescRes);
            return;
        }
        mSetIndicatorInfo = IMPL
                .setActionBarUpIndicator(mSetIndicatorInfo, mActivity, upDrawable, contentDescRes);
    }

    void setActionBarDescription(int contentDescRes) {
        if (mActivityImpl != null) {
            mActivityImpl.setActionBarDescription(contentDescRes);
            return;
        }
        mSetIndicatorInfo = IMPL
                .setActionBarDescription(mSetIndicatorInfo, mActivity, contentDescRes);
    }
}
