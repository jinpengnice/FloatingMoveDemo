package com.demo.wecharfloatingdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


/**
 * Author : jinpeng
 * Date   : 2019/11/15
 * Describle : 自定义可拖动悬浮按钮
 */
public class FloatingMoveButton extends FrameLayout {
    private int mWidth, mHeight;
    private int parentWidth, parentHeight;
    private GradientDrawable gradientDrawable;
    private float radius = 60;
    private OnClickListener clickListener;
    private int finalX = 123;

    public FloatingMoveButton(Context context) {
        super(context);
        init();
    }

    public FloatingMoveButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingMoveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ImageView bgImg = new ImageView(getContext());
        bgImg.setAlpha(0.85f);
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#F8F8F8"));
        /*
           指定4个角中每个角的半径。 对于每个角，数组
      *包含2个值，<code> [X_radius，Y_radius] </ code>。 角落是
      *排序为左上，右上，右下，左下。 这个性质
      *仅在形状为{@link #RECTANGLE}类型时才被接受。
      * <p>
      * <strong>注意</ strong>：更改此属性将影响所有实例
      *从资源加载的可绘制对象。 建议调用
      * {@link #mutate（）}，然后再更改此属性。
      *
      * @param半径长度> = 8的数组，包含4对X和Y
      *每个角的半径，以像素为单位
      *
         */
        gradientDrawable.setCornerRadii(new float[]{radius, radius, 0, 0, 0, 0, radius, radius});
        bgImg.setBackground(gradientDrawable);
        //按下高亮
        addView(bgImg);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.mipmap.ic_launcher);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(imageView, params);

//        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
//        this.screenWidth = displayMetrics.widthPixels;
//        this.screenHeight = displayMetrics.heightPixels;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        this.radius = mWidth > mHeight ? mWidth : mHeight;
        View parentView = (View) getParent();
        this.parentWidth = parentView.getMeasuredWidth();
        this.parentHeight = parentView.getMeasuredHeight();
    }

    private float lastX, lastY;
    private float startX, startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (parentWidth == 0 || parentHeight == 0) return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                lastX = event.getRawX();
                lastY = event.getRawY();
                //按下背景高亮
                if (gradientDrawable != null)
                    gradientDrawable.setColorFilter(Color.parseColor("#C1C1C1"), PorterDuff.Mode.MULTIPLY);
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - lastX;
                float dy = event.getRawY() - lastY;

                int left = (int) (getLeft() + dx);
                int top = (int) (getTop() + dy);

                if (left < 0)
                    left = 0;
                if (left >= parentWidth - mWidth)
                    left = parentWidth - mWidth;
                if (top < 0)
                    top = 0;
                if (top >= parentHeight - mHeight)
                    top = parentHeight - mHeight;

                if (getLayoutParams() != null) {
                    if (gradientDrawable != null)
                        gradientDrawable.setCornerRadii(new float[]{radius, radius, radius, radius,
                                radius, radius, radius, radius});
                    ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
                    layoutParams.leftMargin = left;
                    layoutParams.topMargin = top;
                    setLayoutParams(layoutParams);

                    lastX = event.getRawX();
                    lastY = event.getRawY();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (gradientDrawable != null)
                    gradientDrawable.clearColorFilter();
                if (startX == event.getRawX() &&
                        startY == event.getRawY() &&
                        clickListener != null) {
                    clickListener.onClick(this);
                    return true;
                }
                if (getLayoutParams() != null) {
                    final ViewGroup.MarginLayoutParams marginLayoutParams = (MarginLayoutParams) getLayoutParams();
                    if (marginLayoutParams.leftMargin + mWidth / 2 >=
                            parentWidth / 2) {
                        finalX = parentWidth - mWidth;
                    } else {
                        finalX = 0;
                    }

                    ValueAnimator animator = ValueAnimator.ofInt(marginLayoutParams.leftMargin, finalX)
                            .setDuration(200);
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (gradientDrawable != null) {
                                if (marginLayoutParams.leftMargin + mWidth / 2 >=
                                        parentWidth / 2) {
                                    gradientDrawable.setCornerRadii(new float[]{radius, radius, 0, 0, 0, 0, radius, radius});
                                } else {
                                    gradientDrawable.setCornerRadii(new float[]{0, 0, radius, radius, radius, radius, 0, 0});
                                }
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            marginLayoutParams.leftMargin = (int) animation.getAnimatedValue();
                            setLayoutParams(marginLayoutParams);
                        }
                    });
                    animator.start();
                }
                return true;
        }
        return false;
    }

    /**
     * 是否停靠在左边
     *
     * @return
     */
    public boolean isAnchorLeft() {
        return finalX == 0;
    }

    public void setSelfClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnClickListener {
        void onClick(View view);
    }
}
