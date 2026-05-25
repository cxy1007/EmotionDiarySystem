package com.example.emotiondiarysystem.ui.emotion;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.emotiondiarysystem.manager.EmotionStatManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmotionKeywordCloudView extends View {

    private static final int COLOR_POSITIVE = 0xFF276749;
    private static final int COLOR_NEUTRAL = 0xFF718096;
    private static final int COLOR_NEGATIVE = 0xFF702459;

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
    private Random random = new Random(12345);
    private ValueAnimator animator;
    private float animationProgress = 0f;
    private boolean positionsCalculated = false;

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
        positionsCalculated = false;

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
            keyword.size = dpToPx(12) + ratio * dpToPx(20);

            int colorIndex = i % 3;
            if (colorIndex == 0) {
                keyword.color = COLOR_POSITIVE;
            } else if (colorIndex == 1) {
                keyword.color = COLOR_NEUTRAL;
            } else {
                keyword.color = COLOR_NEGATIVE;
            }

            keyword.rotation = (random.nextFloat() - 0.5f) * 15f;
            keyword.bounds = new Rect();

            keywords.add(keyword);
        }

        startAnimation();
    }

    public void setWordDataList(List<EmotionStatManager.WordData> wordDataList) {
        keywords.clear();
        positionsCalculated = false;

        if (wordDataList == null || wordDataList.isEmpty()) {
            invalidate();
            return;
        }

        int maxCount = wordDataList.get(0).count;
        int minCount = wordDataList.get(wordDataList.size() - 1).count;

        for (int i = 0; i < wordDataList.size(); i++) {
            EmotionStatManager.WordData wordData = wordDataList.get(i);
            Keyword keyword = new Keyword();
            keyword.word = wordData.word;
            keyword.count = wordData.count;

            float ratio = (keyword.count - minCount) / (float) Math.max(1, maxCount - minCount);
            keyword.size = dpToPx(12) + ratio * dpToPx(20);

            if (wordData.emotionType == 1) {
                keyword.color = COLOR_POSITIVE;
            } else if (wordData.emotionType == 2) {
                keyword.color = COLOR_NEGATIVE;
            } else {
                keyword.color = COLOR_NEUTRAL;
            }

            keyword.rotation = (random.nextFloat() - 0.5f) * 12f;
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
        animator.setDuration(800);
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
        int padding = dpToPx(12);

        if (!positionsCalculated) {
            calculatePositions(width, height, padding);
            positionsCalculated = true;
        }

        for (int i = 0; i < keywords.size(); i++) {
            Keyword keyword = keywords.get(i);
            float delay = i * 0.04f;
            float progress = Math.max(0, Math.min(1, (animationProgress - delay) / 0.4f));

            if (progress > 0) {
                canvas.save();
                canvas.translate(keyword.x, keyword.y);
                canvas.rotate(keyword.rotation);

                float animatedSize = keyword.size * (0.6f + 0.4f * progress);
                textPaint.setTextSize(animatedSize);
                textPaint.setColor(keyword.color);
                textPaint.setAlpha((int) (255 * progress));

                canvas.drawText(keyword.word, 0, keyword.bounds.height() / 2f, textPaint);
                canvas.restore();
            }
        }
    }

    private void calculatePositions(int width, int height, int padding) {
        List<Keyword> placed = new ArrayList<>();

        float centerX = width / 2f;
        float centerY = height / 2f;

        for (int i = 0; i < keywords.size(); i++) {
            Keyword keyword = keywords.get(i);

            textPaint.setTextSize(keyword.size);
            textPaint.getTextBounds(keyword.word, 0, keyword.word.length(), keyword.bounds);

            float basePriority = 1f - (float) i / keywords.size();

            float bestX = 0, bestY = 0;
            float minOverlap = Float.MAX_VALUE;
            float bestDistScore = Float.MAX_VALUE;

            for (int attempt = 0; attempt < 300; attempt++) {
                float angle = (float) (random.nextDouble() * Math.PI * 2);
                float t = (float) attempt / 300f;
                float radius = t * Math.min(width, height) / 3f * (0.6f + 0.4f * basePriority);

                float x = centerX + radius * (float) Math.cos(angle);
                float y = centerY + radius * (float) Math.sin(angle);

                x = Math.max(padding + keyword.bounds.width() / 2f, Math.min(width - padding - keyword.bounds.width() / 2f, x));
                y = Math.max(padding + keyword.bounds.height() / 2f, Math.min(height - padding - keyword.bounds.height() / 2f, y));

                float overlap = calculateOverlap(keyword, x, y, placed);
                float distFromCenter = (float) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));

                float score = overlap * 1000 + distFromCenter * (1f - basePriority);

                if (score < minOverlap * 1000 + bestDistScore * (1f - basePriority)) {
                    minOverlap = overlap;
                    bestDistScore = distFromCenter;
                    bestX = x;
                    bestY = y;
                }

                if (overlap == 0) {
                    break;
                }
            }

            keyword.x = bestX;
            keyword.y = bestY;
            placed.add(keyword);
        }
    }

    private float calculateOverlap(Keyword keyword, float x, float y, List<Keyword> placed) {
        float overlap = 0;
        Rect rect1 = new Rect(
            (int) (x - keyword.bounds.width() / 2f - dpToPx(4)),
            (int) (y - keyword.bounds.height() / 2f - dpToPx(2)),
            (int) (x + keyword.bounds.width() / 2f + dpToPx(4)),
            (int) (y + keyword.bounds.height() / 2f + dpToPx(2))
        );

        for (Keyword placedKeyword : placed) {
            Rect rect2 = new Rect(
                (int) (placedKeyword.x - placedKeyword.bounds.width() / 2f - dpToPx(4)),
                (int) (placedKeyword.y - placedKeyword.bounds.height() / 2f - dpToPx(2)),
                (int) (placedKeyword.x + placedKeyword.bounds.width() / 2f + dpToPx(4)),
                (int) (placedKeyword.y + placedKeyword.bounds.height() / 2f + dpToPx(2))
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
