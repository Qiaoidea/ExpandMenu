/*
 * Copyright (C) 2004 - 2016 UCWeb Inc. All Rights Reserved.
 * Description : 不感兴趣弹窗的属性动画都放这
 *
 * Creation    : 2016/03/19
 * Author      : weihan.hwh@alibaba-inc.com
 */

package me.qiao.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.qiao.pop.interpolator.EaseInOutQuartInterpolator;
import me.qiao.pop.interpolator.EaseOutQuartInterpolator;

public class AnimationPanel extends LinearLayout {

    private float mBackgroundAnimationProgress = 0;
    private final int mBackgroundColor;

    private Paint mBgPaint = new Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
    private Path mBgPath;
    private RectF mBgRect;

    private TextAnimator mTextAnimator;

    private final static int BACKGROUND_ANIMATION_DURATION = 500;
    private final static int TEXT_ANIMATION_DURATION = 900;
    private final static int ELEMENT_ANIMATION_DURATION = 300;
    private final static int ELEMENT_ANIMATION_DELAY_DURATION = 50;

    private final float DISTANCE_PERCENT = 8f;
    private float mDistance; //动画左右边框差距

    private View mMenuView;
    private MirrorView mItemView;
    private boolean mIsDownward = true;

    public AnimationPanel(Context context, View itemView, View menuView, TextView targetView) {
        super(context);
        mBackgroundColor = Color.WHITE;

        this.mMenuView = menuView;
        this.mItemView = new MirrorView(context, itemView);
        mTextAnimator = new TextAnimator(targetView);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        mBgPaint.setColor(Color.WHITE);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgRect = new RectF();
        mBgPath = new Path();
    }

    private void checkAnimDirection() {
        View view = mItemView.getSourceView();
        if (view != null) {

            int[] location = new int[2];
            view.getLocationInWindow(location);
            View parent = ((View) view.getParent());
            mMenuView.measure(0, 0);
            mIsDownward = location[1] < parent.getHeight() - mMenuView.getMeasuredHeight();

            LayoutParams itemLayoutParams = new LayoutParams(view.getWidth(), view.getHeight());
            itemLayoutParams.leftMargin = location[0];

            ViewGroup.LayoutParams menuLayoutParams = mMenuView.getLayoutParams();
            if(menuLayoutParams == null){
                menuLayoutParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            }

            if (mIsDownward) {
                addView(mItemView, itemLayoutParams);
                addView(mMenuView, menuLayoutParams);
            } else {
                addView(mMenuView, menuLayoutParams);
                addView(mItemView, itemLayoutParams);
            }

        } else {
            addView(mMenuView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
    }

    public void setTransitionY(int y) {
        if (!mIsDownward) {
            y -= mMenuView.getMeasuredHeight();
        }
        setTranslationY(y);
    }

    public MirrorView getCardView() {
        return mItemView;
    }

    public void startShowAnimation(long delay) {
        int elementAfterBgAnimation = (int) (BACKGROUND_ANIMATION_DURATION * 0.5);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(delay);
        animatorSet.playTogether(
                getBackgroundAnimator(),
                mTextAnimator.getTextAnimator(this, false, TEXT_ANIMATION_DURATION),
                getElementAnimator(mMenuView, elementAfterBgAnimation + ELEMENT_ANIMATION_DELAY_DURATION)
        );
        animatorSet.start();
    }

    public void startHideAnimation(AnimatorListenerAdapter animatorListener, int hideAnimationTime) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                getHideAnimator(hideAnimationTime),
                mTextAnimator.getTextAnimator(this, true, hideAnimationTime)
        );
        animatorSet.addListener(animatorListener);
        animatorSet.start();
    }

    public void beforeShowAnimation() {
        if (getChildCount() == 0) {
            checkAnimDirection();
        }
        mMenuView.setAlpha(0);
    }

    private ValueAnimator getHideAnimator(int duration) {
        final float translationY = Util.dip2px(getContext(), 25);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                float currentTranY = -translationY * fraction;

                mMenuView.setAlpha(1 - fraction);
                mMenuView.setTranslationY(currentTranY);

                mBackgroundAnimationProgress = 1 - fraction;
                invalidate();
            }
        });

        return valueAnimator;
    }

    private ValueAnimator getElementAnimator(final View view, int startDelay) {
        final float translationY = 15 * getResources().getDisplayMetrics().density;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(ELEMENT_ANIMATION_DURATION);
        valueAnimator.setStartDelay(startDelay);
        valueAnimator.setInterpolator(new EaseOutQuartInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                view.setAlpha(fraction);
                view.setTranslationY(-translationY * (1 - fraction));
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setAlpha(1);
                view.setTranslationY(0);
            }
        });
        return valueAnimator;
    }

    private ValueAnimator getBackgroundAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(BACKGROUND_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new EaseInOutQuartInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackgroundAnimationProgress = animation.getAnimatedFraction();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBackgroundAnimationProgress = 1;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mBackgroundAnimationProgress = 0;

            }
        });
        return valueAnimator;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawBackground(canvas);
        super.dispatchDraw(canvas);
        mTextAnimator.drawDeleteLine(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!changed) return;

        mBgRect.set(0, mItemView.getTop(), mItemView.getWidth(), mItemView.getBottom());
        mDistance = mBgRect.width() / DISTANCE_PERCENT;
        mBgPath.close();
        if (mIsDownward) {
            mBgPath.moveTo(mBgRect.left, mBgRect.bottom);
            mBgPath.lineTo(mBgRect.right, mBgRect.bottom);
            mBgPath.lineTo(mBgRect.right, getHeight() + mDistance);
            mBgPath.lineTo(mBgRect.left, getHeight());
            mBgPath.close();
        } else {
            mBgPath.moveTo(mBgRect.left, mBgRect.top);
            mBgPath.lineTo(mBgRect.right, mBgRect.top);
            mBgPath.lineTo(mBgRect.right, -mDistance);
            mBgPath.lineTo(mBgRect.left, 0);
            mBgPath.close();
        }
    }

    /**
     * 白色展开背景动画
     */
    private void drawBackground(Canvas canvas) {
        if (mBackgroundAnimationProgress == 1) {
            canvas.drawColor(mBackgroundColor);
        } else if (mBackgroundAnimationProgress == 0) {
            canvas.drawRect(mBgRect, mBgPaint);
        } else {
            drawRatioProgress(canvas);
        }
    }

    private void drawRatioProgress(Canvas canvas) {
        canvas.drawRect(mBgRect, mBgPaint);

        canvas.save();
        float translationY = (getHeight() - mBgRect.height() + mDistance) * (1 - mBackgroundAnimationProgress);
        if (mIsDownward) {
            canvas.translate(0, -translationY);
        } else {
            canvas.translate(0, translationY);
        }
        canvas.drawPath(mBgPath, mBgPaint);
        canvas.restore();
    }
}
