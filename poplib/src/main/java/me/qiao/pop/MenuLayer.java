/*
 * Copyright (C) 2004 - 2016 UCWeb Inc. All Rights Reserved.
 * Description : 承载不感兴趣弹窗，类似Dialog，另外处理动画
 *
 * Creation    : 2016/03/18
 * Author      : weihan.hwh@alibaba-inc.com
 */

package me.qiao.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MenuLayer extends FrameLayout {

    private AnimationPanel mPanel;
    private View mMenuView;
    private LayerListener mLayerListener;
    private boolean hasMenuLayout;

    private ColorDrawable mBackgroundColorDrawable;

    public MenuLayer(Context context, View itemView, View menuView, TextView textView, LayerListener layerListener) {
        super(context);
        this.mMenuView = menuView;
        mPanel = new AnimationPanel(context, itemView, menuView , textView);
        mLayerListener = layerListener;
        mBackgroundColorDrawable = new ColorDrawable();
        setBackgroundDrawable(mBackgroundColorDrawable);
        initial();
    }

    public void initial() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(mPanel, layoutParams);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!hasMenuLayout) {
            onLayoutPanel();
            hasMenuLayout = true;
        }
    }

    protected View sourceView() {
        return mPanel.getCardView().getSourceView();
    }

    public void getPanelRect(Rect rect){
        mMenuView.getGlobalVisibleRect(rect);
    }

    private void onLayoutPanel() {
        View originalView = mPanel.getCardView().getSourceView();
        if (originalView == null) {
            return;
        }

        int[] location = new int[2];
        originalView.getLocationOnScreen(location);

        int[] archLocation = new int[2];
        getLocationOnScreen(archLocation);

        mPanel.setTransitionY(location[1] - archLocation[1]);
    }

    public void dismiss(final boolean ClickOk) {
        int hideAnimationTime = 300;
        hide(hideAnimationTime);
        mPanel.startHideAnimation(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLayerListener.onDismiss(ClickOk);
            }
        }, hideAnimationTime);
    }

    public void show() {
        displayAnimation(false, 200);
    }

    private void hide(int hideAnimationTime) {
        displayAnimation(true, hideAnimationTime);
    }

    public interface LayerListener {
        void onDismiss(boolean ClickOk);

        void onShow();
    }

    private void displayAnimation(final boolean isReversed, int duration) {
        getBackgroundAnimation(isReversed, duration);
        if (!isReversed) {
            mPanel.startShowAnimation(100);
        }
    }

    private void getBackgroundAnimation(final boolean isReversed, int duration) {
        final int backgroundAlpha = 50;
        ValueAnimator backgroundAnimator = ValueAnimator.ofFloat(0, 1);
        backgroundAnimator.setDuration(duration);
        backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                fraction = isReversed ? 1 - fraction : fraction;

                int alpha = (int) (backgroundAlpha * fraction);
                mBackgroundColorDrawable.setColor(Color.argb(alpha, 0, 0, 0));
                invalidate();
            }
        });
        backgroundAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isReversed) {
                    mBackgroundColorDrawable.setColor(Color.argb(backgroundAlpha, 0, 0, 0));
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                requestLayout();
                mPanel.beforeShowAnimation();
            }
        });
        backgroundAnimator.start();
    }
}
