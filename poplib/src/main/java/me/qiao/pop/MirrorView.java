package me.qiao.pop;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

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
public class MirrorView extends View {

    private View mSourceView;

    public MirrorView(Context context) {
        super(context);
    }

    public MirrorView(Context context, View source) {
        super(context);
        setSourceView(source);
    }

    public void setSourceView(View v) {
        if (mSourceView != v) {
            mSourceView = v;
            invalidate();
        }
    }

    public View getSourceView() {
        return mSourceView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSourceView != null && mSourceView.getVisibility() != View.GONE) {
            canvas.translate(0, mOffsetY);
            mSourceView.draw(canvas);
        }
    }

    public int getSourceViewHeight(){
        if(mSourceView!=null){
            return mSourceView.getMeasuredHeight() ;
        }
        return 0 ;
    }

    private int mOffsetY= 0;
    public void setOffsetY(int offsetY) {
        mOffsetY = offsetY;
    }

}
