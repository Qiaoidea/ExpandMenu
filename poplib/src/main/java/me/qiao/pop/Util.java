package me.qiao.pop;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * ****************************************************************************
 * Copyright (C) 2004 - 2016year UCWeb Inc. All Rights Reserved.
 * <p>
 * Description :
 * <p>
 * Creation    : 2016/10/20
 * Author   : Qiaowy
 * ****************************************************************************
 */
public class Util {

    /**
     * 判断Brand的值是否是Smartisan
     */
    public static boolean isSmartisanBrand(){
        return equalsIgnoreCase("smartisan", Build.BRAND);
    }

    public static boolean equalsIgnoreCase(String aOriginalStr, String anotherStr){
        return aOriginalStr != null && anotherStr != null && aOriginalStr.length() == anotherStr.length()
                && aOriginalStr.toLowerCase().equalsIgnoreCase(anotherStr);
    }

    public static float dip2px(Context context, float dips) {
        return dips * context.getResources().getDisplayMetrics().density + 0.5f;
    }

    public static void popAndroidWindow(Context context, View view) {
        // 有parent，也就是有attach到WindowManager才remove之，避免异常，提升性能
        if (context instanceof Activity && view != null && view.getParent() != null) {
            final WindowManager windowManager = ((Activity)context).getWindowManager();
            windowManager.removeView(view);
        }
    }

    public static void pushAndroidWindow(Context context, View view, ViewGroup.LayoutParams lp) {
        if (context instanceof Activity && view != null && view.getParent() == null) {
            decorateWindowLayoutParams(lp);
            final WindowManager windowManager = ((Activity)context).getWindowManager();
            try {
                windowManager.addView(view, lp);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void decorateWindowLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof WindowManager.LayoutParams) {
            final WindowManager.LayoutParams winLP = (WindowManager.LayoutParams) lp;
            if (winLP.type >= WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW &&
                    winLP.type <= WindowManager.LayoutParams.LAST_APPLICATION_WINDOW) {
                winLP.token = null;
            }

            if (winLP.type == WindowManager.LayoutParams.TYPE_APPLICATION) {
                winLP.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                winLP.flags |= WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            }
        }
    }

    /**
     * 更新通过Android的WindowManager添加的View的WindowManager.LayoutParams。
     * @param context 这个view附着的Activity。
     */
    public static void updateAndroidWindowLP(Context context, View view, ViewGroup.LayoutParams lp) {
        // 有parent，也就是有attach到WindowManager才update之，避免异常，提升性能
        if (context instanceof Activity && view != null && view.getParent() != null) {
            decorateWindowLayoutParams(lp);
            final WindowManager windowManager = ((Activity)context).getWindowManager();
            windowManager.updateViewLayout(view, lp);
        }
    }
}
