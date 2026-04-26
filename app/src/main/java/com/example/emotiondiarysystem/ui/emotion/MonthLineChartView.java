package com.example.emotiondiarysystem.ui.emotion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.emotiondiarysystem.R;

public class MonthLineChartView extends View {

    private Paint positivePaint;
    private Paint neutralPaint;
    private Paint negativePaint;
    private Paint axisPaint;
    private Paint labelPaint;
    private Paint yLabelPaint;
    private Paint gridPaint;
    private Paint legendTextPaint;

    // 每日积极/中性/消极的数量（最多31天）
    private int[] positiveData = new int[31];
    private int[] neutralData  = new int[31];
    private int[] negativeData = new int[31];
    private int activeDays     = 0;
    private int maxValue       = 1;

    private int positiveColor;
    private int neutralColor;
    private int negativeColor;
    private int labelColor;

    public MonthLineChartView(Context context) {
        super(context);
        init(context);
    }

    public MonthLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        positiveColor = ContextCompat.getColor(context, R.color.emotion_positive);
        neutralColor  = ContextCompat.getColor(context, R.color.emotion_neutral);
        negativeColor = ContextCompat.getColor(context, R.color.emotion_negative);
        labelColor    = 0xFF718096;

        positivePaint = makeStrokePaint(positiveColor, 4f);
        neutralPaint  = makeStrokePaint(neutralColor, 4f);
        negativePaint = makeStrokePaint(negativeColor, 4f);

        axisPaint = makeStrokePaint(0xFFCBD5E0, 2f);
        labelPaint = makeStrokePaint(labelColor, 1f);
        labelPaint.setTextSize(sp(11f));
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setStyle(Paint.Style.FILL);

        yLabelPaint = makeStrokePaint(labelColor, 1f);
        yLabelPaint.setTextSize(sp(10f));
        yLabelPaint.setTextAlign(Paint.Align.RIGHT);
        yLabelPaint.setStyle(Paint.Style.FILL);

        gridPaint = makeStrokePaint(0xFFE2E8F0, 1f);

        legendTextPaint = makeFillPaint(labelColor);
        legendTextPaint.setTextSize(sp(10f));
        legendTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    private Paint makeStrokePaint(int color, float strokeWidth) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(strokeWidth);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeJoin(Paint.Join.ROUND);
        return p;
    }

    private Paint makeFillPaint(int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);
        return p;
    }

    public void setData(int[] pos, int[] neu, int[] neg, int days) {
        if (pos != null && pos.length > 0) this.positiveData = pos;
        if (neu != null && neu.length > 0) this.neutralData  = neu;
        if (neg != null && neg.length > 0) this.negativeData = neg;
        this.activeDays = days;

        maxValue = 1;
        for (int i = 0; i < days; i++) {
            int total = positiveData[i] + neutralData[i] + negativeData[i];
            if (total > maxValue) maxValue = total;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        int paddingLeft = (int) dp(40f);
        int paddingRight = (int) dp(12f);
        int paddingTop = (int) dp(32f);
        int paddingBottom = (int) dp(36f);

        int chartW = w - paddingLeft - paddingRight;
        int chartH = h - paddingTop  - paddingBottom;

        // 水平网格和Y轴标签
        for (int i = 0; i <= 4; i++) {
            float y = paddingTop + chartH * i / 4f;
            canvas.drawLine(paddingLeft, y, w - paddingRight, y, gridPaint);
            int val = maxValue - (maxValue * i / 4);
            canvas.drawText(String.valueOf(val), paddingLeft - dp(4f), y + dp(3f), yLabelPaint);
        }

        // X轴
        canvas.drawLine(paddingLeft, paddingTop + chartH, w - paddingRight, paddingTop + chartH, axisPaint);

        if (activeDays == 0) {
            Paint noData = makeStrokePaint(0xFFA0AEC0, 1f);
            noData.setTextSize(sp(14f));
            noData.setTextAlign(Paint.Align.CENTER);
            noData.setStyle(Paint.Style.FILL);
            canvas.drawText("暂无数据", w / 2f, paddingTop + chartH / 2f + 10, noData);
            return;
        }

        drawLegend(canvas, w, paddingTop - dp(12f));

        float stepX = (float) chartW / Math.max(activeDays - 1, 1);

        float[] posX = new float[activeDays];
        float[] posY = new float[activeDays];
        float[] neuX = new float[activeDays];
        float[] neuY = new float[activeDays];
        float[] negX = new float[activeDays];
        float[] negY = new float[activeDays];

        for (int i = 0; i < activeDays; i++) {
            float x = paddingLeft + i * stepX;

            float posH = (float) positiveData[i] / maxValue * chartH;
            float neuH = (float) neutralData[i]  / maxValue * chartH;
            float negH = (float) negativeData[i]  / maxValue * chartH;

            posX[i] = x; posY[i] = paddingTop + chartH - posH;
            neuX[i] = x; neuY[i] = paddingTop + chartH - neuH;
            negX[i] = x; negY[i] = paddingTop + chartH - negH;
        }

        // 绘制折线
        drawLine(canvas, posX, posY, positivePaint);
        drawLine(canvas, neuX, neuY, neutralPaint);
        drawLine(canvas, negX, negY, negativePaint);

        // 绘制数据点
        for (int i = 0; i < activeDays; i++) {
            if (positiveData[i] > 0) {
                canvas.drawCircle(posX[i], posY[i], dp(2.5f), makeFillPaint(positiveColor));
            }
            if (neutralData[i] > 0) {
                canvas.drawCircle(neuX[i], neuY[i], dp(2.5f), makeFillPaint(neutralColor));
            }
            if (negativeData[i] > 0) {
                canvas.drawCircle(negX[i], negY[i], dp(2.5f), makeFillPaint(negativeColor));
            }
        }

        // X轴日期标签（动态调整间隔，避免标签重叠）
        int labelInterval = Math.max(1, (activeDays - 1) / 7 + 1);
        for (int i = 0; i < activeDays; i += labelInterval) {
            float x = paddingLeft + i * stepX;
            canvas.drawText((i + 1) + "日", x, paddingTop + chartH + dp(16f), labelPaint);
        }
        // 最后一个（确保显示）
        if (activeDays > 0) {
            float x = paddingLeft + (activeDays - 1) * stepX;
            canvas.drawText(activeDays + "日", x, paddingTop + chartH + dp(16f), labelPaint);
        }
    }

    private void drawLegend(Canvas canvas, float viewWidth, float y) {
        float itemWidth = dp(64f);
        float totalW = itemWidth * 3f;
        float startX = viewWidth - totalW - dp(8f);
        drawLegendItem(canvas, startX, y, positiveColor, "积极");
        drawLegendItem(canvas, startX + itemWidth, y, neutralColor, "中性");
        drawLegendItem(canvas, startX + itemWidth * 2f, y, negativeColor, "消极");
    }

    private void drawLegendItem(Canvas canvas, float x, float y, int color, String text) {
        Paint lp = makeStrokePaint(color, dp(2f));
        canvas.drawLine(x, y, x + dp(12f), y, lp);
        canvas.drawText(text, x + dp(16f), y + dp(3f), legendTextPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }

    private void drawLine(Canvas canvas, float[] xs, float[] ys, Paint paint) {
        Path path = new Path();
        boolean started = false;
        for (int i = 0; i < xs.length; i++) {
            if (!started) {
                path.moveTo(xs[i], ys[i]);
                started = true;
            } else {
                path.lineTo(xs[i], ys[i]);
            }
        }
        canvas.drawPath(path, paint);
    }
}