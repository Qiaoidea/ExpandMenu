package me.qiao.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

import me.qiao.pop.interpolator.EaseInOutQuartInterpolator;

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
public class TextAnimator {
    private Rect[] mLineBounds;
    private int[] mTitleViewLocation = new int[2];
    private float mDeleteLineTotalLength;
    private float mDeleteLineProgress;
    private Paint mDeleteLinePaint;
    private int mMaxCharacterCount;
    private TextView mTitleView;

    public TextAnimator(TextView targetTextView) {
        mTitleView = targetTextView;
        mDeleteLinePaint = new Paint();
    }

    public void drawDeleteLine(Canvas canvas) {
        if (mLineBounds == null) {
            return;
        }
        float deleteLineAccLength = 0;
        for (Rect lineRect : mLineBounds) {
            float width = mDeleteLineProgress * mDeleteLineTotalLength;

            float startX = lineRect.left + mTitleViewLocation[0];
            float y = (lineRect.top + lineRect.bottom) / 2f + mTitleViewLocation[1];
            float stopX = mTitleViewLocation[0];

            boolean isLastLine = false;
            if (lineRect.width() + deleteLineAccLength <= width) {
                deleteLineAccLength += lineRect.width();
                stopX += lineRect.right;
            } else if (lineRect.width() + deleteLineAccLength > width) {
                if (width > mDeleteLineTotalLength) {
                    return;
                }
                stopX += width - deleteLineAccLength;
                isLastLine = true;
            }
            canvas.drawLine(startX, y, stopX, y, mDeleteLinePaint);
            if (isLastLine) {
                return;
            }
        }
    }

    public ValueAnimator getTextAnimator(final View view, final boolean isReverse, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new EaseInOutQuartInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDeleteLineProgress = isReverse ?
                        1 - animation.getAnimatedFraction() : animation.getAnimatedFraction();
                view.invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (mLineBounds != null) {
                    return;
                }

                if(mTitleView == null || !mTitleView.isShown()) return;

                mTitleView.getLocationOnScreen(mTitleViewLocation);
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                mTitleViewLocation[0] += mTitleView.getPaddingLeft() - location[0];
                mTitleViewLocation[1] += mTitleView.getPaddingTop() - location[1];
                int lineSpacing = mTitleView.getExtendedPaddingTop(); //ensureLayout not null
                Layout layout = mTitleView.getLayout();
                if (layout == null) return;
                int count = layout.getLineCount();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    lineSpacing = (int) mTitleView.getLineSpacingExtra();
                } else {
                    lineSpacing = (int) view.getResources().getDisplayMetrics().density;
                }
                mLineBounds = new Rect[count];
                mDeleteLinePaint.setStrokeWidth((int) Util.dip2px(view.getContext(), 1.5f));

                mDeleteLineTotalLength = 0;
                for (int i = 0; i < count; i++) {
                    Rect lineBound = new Rect();
                    layout.getLineBounds(i, lineBound);
                    mMaxCharacterCount = Math.max(layout.getLineEnd(i) - layout.getLineStart(i), mMaxCharacterCount);
                    mLineBounds[i] = lineBound;
                    CharSequence text = mTitleView.getText().subSequence(layout.getLineStart(i), layout.getLineEnd(i));
                    lineBound.right = (int) mTitleView.getPaint().measureText(text.toString()) + lineBound.left;

                    if (i < count - 1) {
                        mLineBounds[i].bottom -= lineSpacing;
                    }
                    mDeleteLineTotalLength += lineBound.width();
                }

                if (Util.isSmartisanBrand()) {
                    mLineBounds[count - 1].bottom -= lineSpacing;
                }
            }
        });
        return valueAnimator;
    }
}
