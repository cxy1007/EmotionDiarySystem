package com.example.emotiondiarysystem.ui.emotion;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmotionKeywordCloudView extends View {

    private static final int COLOR_POSITIVE = 0xFF48BB78;
    private static final int COLOR_NEUTRAL = 0xFF718096;
    private static final int COLOR_NEGATIVE = 0xFFF56565;

    private static class Keyword {
        String word;
        int count;
        float x;
        float y;
        float size;
        int color;
        float rotation;
        Rect bounds;
    }

    private List<Keyword> keywords = new ArrayList<>();
    private Paint textPaint;
    private Random random = new Random();
    private ValueAnimator animator;
    private float animationProgress = 0f;

    public EmotionKeywordCloudView(Context context) {
        super(context);
        init();
    }

    public EmotionKeywordCloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmotionKeywordCloudView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setKeywords(Map<String, Integer> keywordMap) {
        keywords.clear();

        if (keywordMap == null || keywordMap.isEmpty()) {
            invalidate();
            return;
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(keywordMap.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int maxCount = sortedList.get(0).getValue();
        int minCount = sortedList.get(Math.min(20, sortedList.size() - 1)).getValue();

        for (int i = 0; i < Math.min(20, sortedList.size()); i++) {
            Map.Entry<String, Integer> entry = sortedList.get(i);
            Keyword keyword = new Keyword();
            keyword.word = entry.getKey();
            keyword.count = entry.getValue();

            float ratio = (keyword.count - minCount) / (float) Math.max(1, maxCount - minCount);
            keyword.size = dpToPx(14) + ratio * dpToPx(22);

            int colorIndex = i % 3;
            if (colorIndex == 0) {
                keyword.color = COLOR_POSITIVE;
            } else if (colorIndex == 1) {
                keyword.color = COLOR_NEUTRAL;
            } else {
                keyword.color = COLOR_NEGATIVE;
            }

            keyword.rotation = (random.nextFloat() - 0.5f) * 30f;
            keyword.bounds = new Rect();

            keywords.add(keyword);
        }

        startAnimation();
    }

    private void startAnimation() {
        if (animator != null) {
            animator.cancel();
        }

        animationProgress = 0f;
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (keywords.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int padding = dpToPx(16);
        int centerX = width / 2;
        int centerY = height / 2;

        List<Keyword> placed = new ArrayList<>();

        for (int i = 0; i < keywords.size(); i++) {
            Keyword keyword = keywords.get(i);

            textPaint.setTextSize(keyword.size);
            textPaint.getTextBounds(keyword.word, 0, keyword.word.length(), keyword.bounds);

            boolean placedSuccessfully = false;
            int attempts = 0;
            float bestX = 0, bestY = 0;
            float minOverlap = Float.MAX_VALUE;

            while (!placedSuccessfully && attempts < 200) {
                float t = (float) attempts / 200f;
                float radius = t * Math.min(width, height) / 2.5f;
                float angle = (float) (random.nextDouble() * Math.PI * 2);

                float x = centerX + radius * (float) Math.cos(angle);
                float y = centerY + radius * (float) Math.sin(angle);

                x = Math.max(padding + keyword.bounds.width() / 2f, Math.min(width - padding - keyword.bounds.width() / 2f, x));
                y = Math.max(padding + keyword.bounds.height() / 2f, Math.min(height - padding - keyword.bounds.height() / 2f, y));

                float overlap = calculateOverlap(keyword, x, y, placed);
                if (overlap < minOverlap) {
                    minOverlap = overlap;
                    bestX = x;
                    bestY = y;
                }

                if (overlap == 0) {
                    keyword.x = x;
                    keyword.y = y;
                    placed.add(keyword);
                    placedSuccessfully = true;
                }

                attempts++;
            }

            if (!placedSuccessfully) {
                keyword.x = bestX;
                keyword.y = bestY;
                placed.add(keyword);
            }
        }

        for (int i = 0; i < keywords.size(); i++) {
            Keyword keyword = keywords.get(i);
            float delay = i * 0.05f;
            float progress = Math.max(0, Math.min(1, (animationProgress - delay) / 0.5f));

            if (progress > 0) {
                canvas.save();
                canvas.translate(keyword.x, keyword.y);
                canvas.rotate(keyword.rotation);

                float animatedSize = keyword.size * (0.5f + 0.5f * progress);
                textPaint.setTextSize(animatedSize);
                textPaint.setColor(keyword.color);
                textPaint.setAlpha((int) (255 * progress));

                canvas.drawText(keyword.word, 0, keyword.bounds.height() / 2f, textPaint);
                canvas.restore();
            }
        }
    }

    private float calculateOverlap(Keyword keyword, float x, float y, List<Keyword> placed) {
        float overlap = 0;
        Rect rect1 = new Rect(
            (int) (x - keyword.bounds.width() / 2f),
            (int) (y - keyword.bounds.height() / 2f),
            (int) (x + keyword.bounds.width() / 2f),
            (int) (y + keyword.bounds.height() / 2f)
        );

        for (Keyword placedKeyword : placed) {
            Rect rect2 = new Rect(
                (int) (placedKeyword.x - placedKeyword.bounds.width() / 2f),
                (int) (placedKeyword.y - placedKeyword.bounds.height() / 2f),
                (int) (placedKeyword.x + placedKeyword.bounds.width() / 2f),
                (int) (placedKeyword.y + placedKeyword.bounds.height() / 2f)
            );

            if (Rect.intersects(rect1, rect2)) {
                Rect intersection = new Rect();
                intersection.setIntersect(rect1, rect2);
                overlap += intersection.width() * intersection.height();
            }
        }

        return overlap;
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
