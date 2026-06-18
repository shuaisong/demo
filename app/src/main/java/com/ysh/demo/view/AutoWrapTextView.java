package com.ysh.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class AutoWrapTextView extends AppCompatTextView {
    private int viewWidth;
    private final StringBuilder currentLine = new StringBuilder();

    public AutoWrapTextView(Context context) {
        super(context);
    }

    public AutoWrapTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoWrapTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String text = getText().toString();
        if (text.isEmpty()) {
            return;
        }

        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.setTextSize(getTextSize());

        int lineHeight = getLineHeight();
        int x = getPaddingLeft();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int y = (int) (getPaddingTop() + getTextSize() / 2 + (Math.abs(fontMetrics.top) + Math.abs(fontMetrics.bottom)) / 2 - fontMetrics.bottom);

        if (currentLine.length() > 0) {
            currentLine.delete(0, currentLine.length());
        }

        for (int i = 0; i < text.length(); i++) {
            String word = String.valueOf(text.charAt(i));
            String testLine = currentLine.append(word).toString();
            float testWidth = paint.measureText(testLine);
            if (testWidth > viewWidth && !currentLine.toString().isEmpty()) {
                canvas.drawText(currentLine.subSequence(0, currentLine.length() - 1).toString(), x, y, paint);
                y += lineHeight;
                currentLine.delete(0, currentLine.length());
                currentLine.append(word);
            }
        }

        if (!currentLine.toString().isEmpty()) {
            canvas.drawText(currentLine.toString().trim(), x, y, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        String text = getText().toString();
        Paint paint = getPaint();
        int lineCount = 1;
        float currentLineWidth = 0;
        viewWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        for (int i = 0; i < text.length(); i++) {
            float wordWidth = paint.measureText(String.valueOf(text.charAt(i)));
            if (currentLineWidth + wordWidth > viewWidth) {
                lineCount++;
                currentLineWidth = wordWidth;
            } else {
                currentLineWidth += wordWidth;
            }
        }

        int height = getPaddingTop() + getPaddingBottom() +
                lineCount * getLineHeight();
        setMeasuredDimension(getMeasuredWidth(), height);
    }
}
