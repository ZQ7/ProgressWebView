package com.yi2580.progresswebview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhangqi on 2017/5/9.
 * 进度条View
 */
class ProgressView extends View {
    private Paint progressPaint = null;
    private Paint progressCirclePaint = null;
    private int currentProgress = 0;
    private int totalProgress = 0;
    private boolean isHide = false;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        progressPaint = new Paint();
        progressPaint.setColor(Color.BLACK);
        progressCirclePaint = new Paint();
        progressCirclePaint.setColor(Color.BLACK);
        progressCirclePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
    }

    int viewWidth = 0;
    int viewHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        viewHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentProgress <= 100 && isHide) {
            isHide = false;
            this.setAlpha(1);
        }

        canvas.drawRect(0, 0, (float) (viewWidth * (currentProgress / 100.0)), viewHeight, progressPaint);
        canvas.drawCircle((float) (viewWidth * (currentProgress / 100.0)) - viewHeight / 2, viewHeight / 2, viewHeight, progressCirclePaint);
        if (currentProgress >= 100) {
            hideSelf();
        }
    }

    /**
     * 隐藏进度条
     */
    private void hideSelf() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewCompat.animate(ProgressView.this).alpha(0);
                isHide = true;
                ProgressView.this.currentProgress = 0;
            }
        }, 100);

    }

    /**
     * 设置进度条颜色
     * @param defaultColor
     */
    public void setProgressColor(int defaultColor) {
        progressPaint.setColor(defaultColor);
        progressCirclePaint.setColor(defaultColor);

    }

    /**
     * 设置进度
     */
    ValueAnimator animator;
    public void setProgress(int progress) {
        totalProgress = progress;
        if (animator != null) {
            if (animator.isRunning()) {
                animator.cancel();
            }
        }
        animator = ValueAnimator.ofInt(currentProgress, totalProgress);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentProgress = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

}
