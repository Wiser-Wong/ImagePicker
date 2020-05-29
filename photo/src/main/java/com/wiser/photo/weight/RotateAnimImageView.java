package com.wiser.photo.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

/**
 * @author Wiser
 *
 *          旋转动画图片
 */
public class RotateAnimImageView extends AppCompatImageView {

    private boolean isRunningAnim;

    private boolean isOpen;

    public RotateAnimImageView(Context context) {
        super(context);
    }

    public RotateAnimImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateAnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //执行动画
    public void handleAnim(){
        if (isRunningAnim) return;
        ObjectAnimator animator;
        if (!isOpen){
            isOpen = true;
            animator = ObjectAnimator.ofFloat(this, "rotation", 0, 180);
        }else {
            isOpen = false;
            animator = ObjectAnimator.ofFloat(this, "rotation", 180, 0);
        }
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(400);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isRunningAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isRunningAnim = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isRunningAnim = true;
            }
        });
        animator.start();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isRunningAnim() {
        return isRunningAnim;
    }
}
