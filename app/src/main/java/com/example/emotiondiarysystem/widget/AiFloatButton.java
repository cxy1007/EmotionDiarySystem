package com.example.emotiondiarysystem.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.emotiondiarysystem.R;

public class AiFloatButton extends FrameLayout {

    public interface OnFloatButtonClickListener {
        void onClick();
    }

    private ImageView floatBallView;
    private float downX, downY;
    private float lastX, lastY;
    private boolean isDragging;
    private int touchSlop;
    private OnFloatButtonClickListener clickListener;
    private int parentWidth, parentHeight;
    private int margin = 20; // dp

    public AiFloatButton(Context context) {
        this(context, null);
    }

    public AiFloatButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AiFloatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        floatBallView = new ImageView(context);
        floatBallView.setImageResource(R.drawable.ic_ai_assistant);
        floatBallView.setBackgroundResource(0); // 去掉圆形背景
        int size = dp2px(64);
        LayoutParams params = new LayoutParams(size, size);
        floatBallView.setPadding(dp2px(0), dp2px(0), dp2px(0), dp2px(0));
        addView(floatBallView, params);

        margin = dp2px(20);
    }

    public void setOnFloatButtonClickListener(OnFloatButtonClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getParent() instanceof View) {
            View parent = (View) getParent();
            parentWidth = parent.getWidth();
            parentHeight = parent.getHeight();
            if (getX() == 0 && getY() == 0) {
                setX(parentWidth - getWidth() - margin);
                setY(parentHeight - getHeight() - margin);
            }
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
                bringToFront();
                return true;

            case MotionEvent.ACTION_MOVE:
                float deltaX = x - lastX;
                float deltaY = y - lastY;
                if (!isDragging && (Math.abs(deltaX) > touchSlop || Math.abs(deltaY) > touchSlop)) {
                    isDragging = true;
                }
                if (isDragging) {
                    float newX = getX() + deltaX;
                    float newY = getY() + deltaY;
                    newX = Math.max(0, Math.min(newX, parentWidth - getWidth()));
                    newY = Math.max(0, Math.min(newY, parentHeight - getHeight()));
                    setX(newX);
                    setY(newY);
                }
                lastX = x;
                lastY = y;
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isDragging) {
                    if (clickListener != null) {
                        clickListener.onClick();
                    }
                } else {
                    stickToEdge();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void stickToEdge() {
        float centerX = getX() + getWidth() / 2;
        float targetX;
        if (centerX < parentWidth / 2) {
            targetX = dp2px(10);
        } else {
            targetX = parentWidth - getWidth() - dp2px(10);
        }
        animate()
                .x(targetX)
                .setDuration(300)
                .setListener(null)
                .start();
    }

    private int dp2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
