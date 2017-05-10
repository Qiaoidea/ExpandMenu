/**
 * ****************************************************************************
 * Copyright (C) 2005-2013 UCWEB Corporation. All rights reserved
 * File        : 2013-11-30
 * <p>
 * Description :
 * <p>
 * Creation    : 2013-11-30
 * Author      : linhz@ucweb.com
 * History     : Creation, 2013-11-30, linhz, Create the file
 * ****************************************************************************
 */
package me.qiao.pop;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class PopMenu implements MenuLayer.LayerListener {

    private Context mContext;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private RootView mRootView;
    private MenuLayer mMenuLayer;

    public PopMenu(Context context, View itemView, View menuView, TextView textView) {
        mContext = context;
        menuView.setBackgroundDrawable(null);
        mMenuLayer = new MenuLayer(context, itemView, menuView, textView, this);

        initWindow();
    }

    private void initWindowLayoutParams() {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        mWindowLayoutParams.dimAmount = 0.0f;
        mWindowLayoutParams.width = LayoutParams.MATCH_PARENT;
        mWindowLayoutParams.height = LayoutParams.MATCH_PARENT;
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT;
    }

    private void initWindow() {
        initWindowLayoutParams();
        if (mRootView == null) {
            mRootView = new RootView(mContext);
        }
        mRootView.removeAllViewsInLayout();

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.BOTTOM;
        mRootView.addView(mMenuLayer, lp);

        onThemeChange();
    }

    public void show() {
        if (mRootView.getParent() != null) {
            return;
        }
        Util.pushAndroidWindow(mContext, mRootView, mWindowLayoutParams);

        mMenuLayer.show();
    }

    public void dismiss(boolean isClickOk) {
        mMenuLayer.dismiss(isClickOk);
    }

    public void dismiss() {
        mMenuLayer.dismiss(false);
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onDismiss(boolean clickOk) {
        if (mRootView.getParent() != null) {
            mWindowLayoutParams.windowAnimations = 0;
            Util.updateAndroidWindowLP(mContext, mRootView, mWindowLayoutParams); // update pop animation
            Util.popAndroidWindow(mContext, mRootView);
        }
    }

    public void onThemeChange() {

    }

    private class RootView extends FrameLayout {
        private boolean mTouchDownOutside = false;

        public RootView(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            boolean ret = super.dispatchTouchEvent(ev);
            if (!ret) {
                Rect rect = new Rect();
                mMenuLayer.getPanelRect(rect);
                int touchX = (int) ev.getX();
                int touchY = (int) ev.getY();
                int action = ev.getAction();
                if (!rect.contains(touchX, touchY) && action == MotionEvent.ACTION_DOWN) {
                    mTouchDownOutside = true;
                }

                if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) && mTouchDownOutside) {
                    mTouchDownOutside = false;
                    dismiss();
                    ret = true;
                }
            }
            return ret;
        }

        private boolean mBackKeyDown = false;

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {

            if (KeyEvent.ACTION_DOWN == event.getAction() && KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
                mBackKeyDown = true;
                return false;
            } else {
                if (mBackKeyDown && event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
            }

            mBackKeyDown = false;

            return super.dispatchKeyEvent(event);
        }
    }

    public static class Builder{
        private Context context;
        private View itemView;
        private View menuView;
        private TextView textView;

        private Builder(Context context){
            this.context = context;
        }

        public Builder setItemView(View itemView) {
            this.itemView = itemView;
            return this;
        }

        public Builder setMenuView(View menuView) {
            this.menuView = menuView;
            return this;
        }

        public Builder setTextView(TextView textView) {
            this.textView = textView;
            return this;
        }

        public PopMenu build(){
            return new PopMenu(this.context, this.itemView, this.menuView, this.textView);
        }

        public static Builder create(Context context){
            return new Builder(context);
        }
    }
}
