package com.wiser.photo.util;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author Wiser
 *
 *          帮助类
 */
public class PickerHelper {

    /**
     * 全透状态栏
     */
    public static void setStatusBarFullTransparent(FragmentActivity activity) {
        if (activity == null) return;
        if (Build.VERSION.SDK_INT >= 21) {// 21表示5.0
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {// 19表示4.4
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 虚拟键盘也透明
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 替换子Fragment中的Fragment
     * @param srcFragment
     * @param id
     * @param fragment
     */
    public static void commitChildReplace(Fragment srcFragment, @IdRes int id, Fragment fragment) {
        if (fragment == null) return;
        if (srcFragment == null) return;
        srcFragment.getChildFragmentManager().beginTransaction().replace(id, fragment, fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();
    }

    /**
     * 替换子Fragment中的Fragment
     * @param id
     * @param fragment
     * @param tagName
     */
    public static void commitReplace(FragmentActivity activity,@IdRes int id, Fragment fragment,String tagName) {
        if (activity == null) return;
        if (fragment == null) return;
        activity.getSupportFragmentManager().beginTransaction().replace(id, fragment, tagName).commit();
    }

}
