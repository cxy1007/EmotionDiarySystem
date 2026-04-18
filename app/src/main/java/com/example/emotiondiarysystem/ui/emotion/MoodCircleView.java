package com.example.emotiondiarysystem.ui.emotion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MoodCircleView extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private int progress = 50; // 默认50%
    private int maxProgress = 100;
    private float strokeWidth = 10f;
    private String centerText = "50";

    public MoodCircleView(Context context) {
        super(context);
        init();
    }

    public MoodCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 背景圆环画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFE0E0E0);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setAntiAlias(true);

        // 进度圆环画笔
        progressPaint = new Paint();
        progressPaint.setColor(getProgressColor());
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setAntiAlias(true);

        // 文字画笔
        textPaint = new Paint();
        textPaint.setColor(0xFF333333);
        textPaint.setTextSize(24);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(centerX, centerY) - (int) (strokeWidth / 2);

        // 绘制背景圆环
        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(rectF, 0, 360, false, backgroundPaint);

        // 绘制进度圆环
        if (progress > 0) {
            float sweepAngle = 360f * progress / maxProgress;
            canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);
        }

        // 不绘制中心文字，让日期文本显示在圆环中心
    }

    public void setProgress(int progress) {
        this.progress = progress;
        progressPaint.setColor(getProgressColor());
        invalidate();
    }

    private int getProgressColor() {
        if (progress <= 30) {
            return 0xFFFF6B6B; // 红色
        } else if (progress <= 60) {
            return 0xFFFFA500; // 橙色
        } else if (progress <= 80) {
            return 0xFF98D8C8; // 浅绿
        } else {
            return 0xFF4CAF50; // 深绿
        }
    }
}
