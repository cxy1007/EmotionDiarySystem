package com.example.emotiondiarysystem.ui.emotion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.emotiondiarysystem.utils.ToastUtil;

public class EmotionRingChartView extends View {

    private static final int COLOR_POSITIVE = 0xFF48BB78;
    private static final int COLOR_NEUTRAL = 0xFF718096;
    private static final int COLOR_NEGATIVE = 0xFFF56565;

    private int positiveCount = 0;
    private int neutralCount = 0;
    private int negativeCount = 0;
    private String dominantEmotion = "";

    private Paint ringPaint;
    private Paint textPaint;
    private RectF ringRect;

    private float currentSweepAngle = 0;
    private ValueAnimator animator;

    private float[] segmentAngles = new float[3];
    private String[] segmentLabels = {"积极", "中性", "消极"};
    private int[] segmentColors = {COLOR_POSITIVE, COLOR_NEUTRAL, COLOR_NEGATIVE};
    private int[] segmentCounts = {0, 0, 0};

    public EmotionRingChartView(Context context) {
        super(context);
        init();
    }

    public EmotionRingChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmotionRingChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(dpToPx(8));
        ringPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFF2D3748);
        textPaint.setTextAlign(Paint.Align.CENTER);

        ringRect = new RectF();
    }

    public void setData(int positive, int neutral, int negative, String dominant) {
        this.positiveCount = positive;
        this.neutralCount = neutral;
        this.negativeCount = negative;
        this.dominantEmotion = dominant;
        this.segmentCounts = new int[]{positive, neutral, negative};

        int total = positive + neutral + negative;
        if (total > 0) {
            segmentAngles[0] = positive * 360f / total;
            segmentAngles[1] = neutral * 360f / total;
            segmentAngles[2] = negative * 360f / total;
        } else {
            segmentAngles[0] = 0;
            segmentAngles[1] = 0;
            segmentAngles[2] = 0;
        }

        startAnimation();
    }

    private void startAnimation() {
        if (animator != null) {
            animator.cancel();
        }

        animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            currentSweepAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setAlpha(0f);
                animate().alpha(1f).setDuration(300).start();
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 3;

        ringRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        float startAngle = -90;
        for (int i = 0; i < 3; i++) {
            float sweepAngle = Math.min(segmentAngles[i], currentSweepAngle - startAngle + 90);
            if (sweepAngle > 0) {
                ringPaint.setColor(segmentColors[i]);
                canvas.drawArc(ringRect, startAngle, sweepAngle, false, ringPaint);
                startAngle += segmentAngles[i];
            }
        }

        if (currentSweepAngle >= 360) {
            textPaint.setTextSize(dpToPx(16));
            textPaint.setFakeBoldText(true);
            String centerText = "主导情绪：" + (dominantEmotion.isEmpty() ? "未知" : dominantEmotion);
            canvas.drawText(centerText, centerX, centerY, textPaint);

            textPaint.setTextSize(dpToPx(12));
            textPaint.setFakeBoldText(false);
            textPaint.setColor(0xFF718096);

            String positiveText = String.format("积极 %.0f%%", positiveCount * 100.0 / Math.max(1, positiveCount + neutralCount + negativeCount));
            String neutralText = String.format("中性 %.0f%%", neutralCount * 100.0 / Math.max(1, positiveCount + neutralCount + negativeCount));
            String negativeText = String.format("消极 %.0f%%", negativeCount * 100.0 / Math.max(1, positiveCount + neutralCount + negativeCount));

            float yPos = centerY + radius + dpToPx(30);
            canvas.drawText(positiveText + "   " + neutralText + "   " + negativeText, centerX, yPos, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            float dx = x - centerX;
            float dy = y - centerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance >= getWidth() / 4 && distance <= getWidth() / 2) {
                float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
                if (angle < -90) angle += 360;
                angle += 90;

                float cumulativeAngle = 0;
                for (int i = 0; i < 3; i++) {
                    cumulativeAngle += segmentAngles[i];
                    if (angle <= cumulativeAngle) {
                        ToastUtil.showShort(getContext(), segmentLabels[i] + "：" + segmentCounts[i] + "篇");
                        break;
                    }
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
