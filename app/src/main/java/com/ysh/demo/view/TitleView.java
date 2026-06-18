package com.ysh.demo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.ysh.demo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import me.jessyan.autosize.utils.AutoSizeUtils;

public class TitleView extends ConstraintLayout implements View.OnClickListener {

    private ImageView ivBack;
    private ImageView ivRight;
    private TextView tvTitle;
    private TextView tvRight;

    private ClickCallback callback;

    public void setCallback(ClickCallback callback) {
        this.callback = callback;
    }

    public TitleView(@NonNull Context context) {
        this(context, null);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_title, this, true);
        int dp2px = AutoSizeUtils.dp2px(context, 12);
        setPadding(dp2px, 0, dp2px, 0);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvRight = findViewById(R.id.tv_right);
        ivRight = findViewById(R.id.iv_right);
        ivBack.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        ivRight.setOnClickListener(this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
        boolean fitsSystemWindows = typedArray.getBoolean(R.styleable.TitleView_fitsSystemWindows, true);
        if (fitsSystemWindows) {
            int statusBarHeight = BarUtils.getStatusBarHeight();
            LayoutParams layoutParams = (LayoutParams) tvTitle.getLayoutParams();
            layoutParams.topMargin = statusBarHeight;
            tvTitle.setLayoutParams(layoutParams);
        }
        int titleColor = typedArray.getResourceId(R.styleable.TitleView_titleColor, R.color.black);
        tvTitle.setTextColor(getResources().getColor(titleColor));
        String title = typedArray.getString(R.styleable.TitleView_title);
        if (title != null) {
            tvTitle.setText(title);
        }
        float size = typedArray.getDimension(R.styleable.TitleView_titleSize, 26);
        tvTitle.setTextSize(size);
        size = typedArray.getDimension(R.styleable.TitleView_rightTextSize, 23);
        tvRight.setTextSize(size);

        int resourceId = typedArray.getResourceId(R.styleable.TitleView_rightIcon, -1);
        if (resourceId != -1) {
            ivRight.setImageResource(resourceId);
        }
        resourceId = typedArray.getResourceId(R.styleable.TitleView_backIcon, -1);
        if (resourceId != -1) {
            ivBack.setImageResource(resourceId);
        }
        typedArray.recycle();
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setRight(String right) {
        tvRight.setText(right);
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public ImageView getIvBack() {
        return ivBack;
    }

    public ImageView getIvRight() {
        return ivRight;
    }

    public TextView getTvRight() {
        return tvRight;
    }

    @Override
    public void onClick(View v) {
        if (callback != null) {
            if (v.getId() == R.id.iv_back) {
                callback.onBack();
            } else if (v.getId() == R.id.tv_right) {
                callback.onRight();
            } else if (v.getId() == R.id.iv_right) {
                callback.onRight();
            }
        }
    }

    public interface ClickCallback {
        void onBack();

        void onRight();
    }
}
