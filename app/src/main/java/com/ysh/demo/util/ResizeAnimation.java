package com.ysh.demo.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.blankj.utilcode.util.ScreenUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ResizeAnimation extends Animation {

    private final View mView;
    private final int mToHeight;
    private final int mFromHeight;

    private final int mToWidth;
    private final int mFromWidth;
    private final int mMargin;
    int appScreenWidth = ScreenUtils.getAppScreenWidth();
    int appScreenHeight = ScreenUtils.getAppScreenHeight();

    public ResizeAnimation(View v, int fromWidth, int fromHeight, int toWidth, int toHeight, int margin) {
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
        p.rightMargin = mMargin;
        p.bottomMargin = mMargin;
        mView.requestLayout();
    }
}
