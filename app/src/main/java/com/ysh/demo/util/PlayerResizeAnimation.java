package com.ysh.demo.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PlayerResizeAnimation extends Animation {

    private final View mView;
    private final int mToHeight;
    private final int mFromHeight;

    private final int mToWidth;
    private final int mFromWidth;
    private final int mMargin;

    public PlayerResizeAnimation(View v, int fromWidth, int fromHeight, int toWidth, int toHeight, int margin) {
        mToHeight = toHeight;
        mToWidth = toWidth;
        mFromHeight = fromHeight;
        mFromWidth = fromWidth;
        mView = v;
        mMargin = margin;
        setDuration(300);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
        ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) mView.getLayoutParams();
        p.height = (int) height;
        p.width = (int) width;
        p.leftMargin = mMargin;
        p.bottomMargin = mMargin;
        mView.requestLayout();
    }
}
