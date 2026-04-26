package com.example.emotiondiarysystem.ui.emotion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.emotiondiarysystem.R;

public class WeekBarChartView extends View {

    private final Paint positivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint neutralPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint negativePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint yLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint legendTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int[] positiveData  = new int[7];
    private int[] neutralData   = new int[7];
    private int[] negativeData  = new int[7];
    private int maxValue = 1;

    private String[] dayLabels = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    private int positiveColor;
    private int neutralColor;
    private int negativeColor;
    private int axisColor;
    private int labelColor;

    public WeekBarChartView(Context context) {
        super(context);
        init(context);
    }

    public WeekBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        positiveColor  = ContextCompat.getColor(context, R.color.emotion_positive);
        neutralColor   = ContextCompat.getColor(context, R.color.emotion_neutral);
        negativeColor  = ContextCompat.getColor(context, R.color.emotion_negative);
        axisColor      = 0xFFCBD5E0;
        labelColor     = 0xFF718096;

        positivePaint.setStyle(Paint.Style.FILL);
        positivePaint.setColor(positiveColor);
        neutralPaint.setStyle(Paint.Style.FILL);
        neutralPaint.setColor(neutralColor);
        negativePaint.setStyle(Paint.Style.FILL);
        negativePaint.setColor(negativeColor);

        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setColor(axisColor);
        axisPaint.setStrokeWidth(dp(1f));

        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setColor(labelColor);
        labelPaint.setTextSize(sp(11f));
        labelPaint.setTextAlign(Paint.Align.CENTER);

        yLabelPaint.setStyle(Paint.Style.FILL);
        yLabelPaint.setColor(labelColor);
        yLabelPaint.setTextSize(sp(10f));
        yLabelPaint.setTextAlign(Paint.Align.RIGHT);

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(0xFFE2E8F0);
        gridPaint.setStrokeWidth(dp(0.8f));

        legendTextPaint.setStyle(Paint.Style.FILL);
        legendTextPaint.setColor(labelColor);
        legendTextPaint.setTextSize(sp(10f));
        legendTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    public void setData(int[] pos, int[] neu, int[] neg) {
        if (pos != null && pos.length == 7) this.positiveData = pos;
        if (neu != null && neu.length == 7) this.neutralData  = neu;
        if (neg != null && neg.length == 7) this.negativeData = neg;

        maxValue = 1;
        for (int i = 0; i < 7; i++) {
            int total = positiveData[i] + neutralData[i] + negativeData[i];
            if (total > maxValue) maxValue = total;
        }
        invalidate();
    }

    public void setDayLabels(String[] labels) {
        if (labels != null && labels.length == 7) this.dayLabels = labels;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        float paddingLeft = dp(36f);
        float paddingRight = dp(12f);
        float paddingTop = dp(32f);
        float paddingBottom = dp(36f);

        float chartW = w - paddingLeft - paddingRight;
        float chartH = h - paddingTop - paddingBottom;

        // 背景网格 + Y轴刻度
        for (int i = 0; i <= 4; i++) {
            float y = paddingTop + chartH * i / 4f;
            canvas.drawLine(paddingLeft, y, w - paddingRight, y, gridPaint);
            int val = maxValue - (maxValue * i / 4);
            canvas.drawText(String.valueOf(val), paddingLeft - dp(4f), y + dp(3f), yLabelPaint);
        }

        // X轴
        canvas.drawLine(paddingLeft, paddingTop + chartH, w - paddingRight, paddingTop + chartH, axisPaint);

        // 图例
        drawLegend(canvas, w, dp(16f));

        float groupWidth = chartW / 7f;
        float groupPadding = groupWidth * 0.15f;
        float barsAreaW = groupWidth - groupPadding * 2f;
        float singleBarW = barsAreaW / 3f;

        for (int i = 0; i < 7; i++) {
            float groupX = paddingLeft + i * groupWidth + groupPadding;
            float baseY = paddingTop + chartH;

            float posH = chartH * positiveData[i] / maxValue;
            float neuH = chartH * neutralData[i] / maxValue;
            float negH = chartH * negativeData[i] / maxValue;

            canvas.drawRoundRect(groupX, baseY - posH, groupX + singleBarW, baseY, dp(2f), dp(2f), positivePaint);
            canvas.drawRoundRect(groupX + singleBarW, baseY - neuH, groupX + singleBarW * 2f, baseY, dp(2f), dp(2f), neutralPaint);
            canvas.drawRoundRect(groupX + singleBarW * 2f, baseY - negH, groupX + singleBarW * 3f, baseY, dp(2f), dp(2f), negativePaint);

            float centerX = groupX + barsAreaW / 2f;
            canvas.drawText(dayLabels[i], centerX, baseY + dp(16f), labelPaint);
        }
    }

    private void drawLegend(Canvas canvas, float viewWidth, float legendY) {
        float itemWidth = dp(64f);
        float totalW = itemWidth * 3f;
        float startX = viewWidth - totalW - dp(8f);
        drawLegendItem(canvas, startX, legendY, positiveColor, "积极");
        drawLegendItem(canvas, startX + itemWidth, legendY, neutralColor, "中性");
        drawLegendItem(canvas, startX + itemWidth * 2f, legendY, negativeColor, "消极");
    }

    private void drawLegendItem(Canvas canvas, float x, float y, int color, String text) {
        Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(color);
        canvas.drawCircle(x, y, dp(3f), dotPaint);
        canvas.drawText(text, x + dp(6f), y + dp(3f), legendTextPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
