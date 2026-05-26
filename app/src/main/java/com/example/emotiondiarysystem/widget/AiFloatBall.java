package com.example.emotiondiarysystem.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.emotiondiarysystem.R;

public class AiFloatBall extends FrameLayout {

    public interface OnFloatBallClickListener {
        void onClick();
    }

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private ImageView floatBallView;

    private int screenWidth;
    private int screenHeight;
    private int statusBarHeight;
    private int navigationBarHeight;

    private float downX;
    private float downY;
    private float lastX;
    private float lastY;
    private boolean isDragging;
    private int touchSlop;
    private OnFloatBallClickListener clickListener;

    public AiFloatBall(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        floatBallView = new ImageView(context);
        floatBallView.setImageResource(R.drawable.ic_ai_assistant);
        // 去掉圆形背景，让整个悬浮球就是对话气泡
        floatBallView.setBackgroundResource(0); // 清空背景
        int size = dp2px(64); // 稍微大一点更清晰
        LayoutParams params = new LayoutParams(size, size);
        floatBallView.setPadding(dp2px(0), dp2px(0), dp2px(0), dp2px(0));
        addView(floatBallView, params);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
    }

    public void setOnFloatBallClickListener(OnFloatBallClickListener listener) {
        this.clickListener = listener;
    }

    public void show() {
        if (getParent() == null) {
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            layoutParams.x = screenWidth - dp2px(68);
            layoutParams.y = screenHeight - dp2px(68) - navigationBarHeight;
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            windowManager.addView(this, layoutParams);
        }
    }

    public void hide() {
        if (getParent() != null) {
            windowManager.removeView(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDragging = false;
                downX = x;
                downY = y;
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - lastX;
                float deltaY = y - lastY;
                if (!isDragging && (Math.abs(deltaX) > touchSlop || Math.abs(deltaY) > touchSlop)) {
                    isDragging = true;
                }
                if (isDragging) {
                    updatePosition((int) (layoutParams.x + deltaX), (int) (layoutParams.y + deltaY));
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isDragging) {
                    if (clickListener != null) {
                        clickListener.onClick();
                    }
                } else {
                    stickToEdge();
                }
                break;
        }
        return true;
    }

    private void updatePosition(int x, int y) {
        int minX = 0;
        int maxX = screenWidth - getWidth();
        int minY = statusBarHeight;
        int maxY = screenHeight - getHeight() - navigationBarHeight;

        layoutParams.x = Math.max(minX, Math.min(maxX, x));
        layoutParams.y = Math.max(minY, Math.min(maxY, y));
        windowManager.updateViewLayout(this, layoutParams);
    }

    private void stickToEdge() {
        int centerX = layoutParams.x + getWidth() / 2;
        int targetX;
        if (centerX < screenWidth / 2) {
            targetX = dp2px(10);
        } else {
            targetX = screenWidth - getWidth() - dp2px(10);
        }
        animateToPosition(targetX, layoutParams.y);
    }

    private void animateToPosition(int targetX, int targetY) {
        final int startX = layoutParams.x;
        final int startY = layoutParams.y;
        final int deltaX = targetX - startX;
        final int deltaY = targetY - startY;

        post(new Runnable() {
            private long startTime = -1;
            private final long duration = 300;

            @Override
            public void run() {
                if (startTime == -1) {
                    startTime = System.currentTimeMillis();
                }
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(elapsed / (float) duration, 1);

                int currentX = (int) (startX + deltaX * progress);
                int currentY = (int) (startY + deltaY * progress);
                updatePosition(currentX, currentY);

                if (progress < 1) {
                    post(this);
                }
            }
        });
    }

    private int dp2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
