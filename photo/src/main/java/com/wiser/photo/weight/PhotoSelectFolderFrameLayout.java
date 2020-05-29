package com.wiser.photo.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.wiser.photo.R;
import com.wiser.photo.select.FolderAdapter;
import com.wiser.photo.model.PhotoFolderModel;
import com.wiser.photo.select.PhotoSelectFolderFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * **************************************
 * 项目名称:ImagePicker
 *
 * @author wangxy
 * 邮箱：wangxianyu@ksjgs.com
 * 创建时间: 2020/5/29 1:33 PM
 * 用途：
 * **************************************
 */
public class PhotoSelectFolderFrameLayout extends FrameLayout {

    private boolean isRunningAnim;

    private boolean isOpen;

    private View coverView;

    private RecyclerView rlvFolder;

    private AnimatorSet animatorSet;

    private FolderAdapter folderAdapter;

    public PhotoSelectFolderFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public PhotoSelectFolderFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoSelectFolderFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.photo_select_folder_layout,this,true);
        coverView = findViewById(R.id.cover_view);

        rlvFolder = findViewById(R.id.rlv_folder);
        rlvFolder.setLayoutManager(new LinearLayoutManager(getContext()));
        rlvFolder.setAdapter(folderAdapter = new FolderAdapter(getContext()));


        animatorSet = new AnimatorSet();
        animatorSet.addListener(new FolderAnimListener(this));

        coverView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnim();
            }
        });

        setVisibility(INVISIBLE);
    }

    public void setData(ArrayList<PhotoFolderModel> folderModels){
        if (folderAdapter != null) folderAdapter.setItems(folderModels);
    }

    public void setOnFolderClickListener(PhotoSelectFolderFragment.OnFolderClickListener onFolderClickListener){
        if (folderAdapter != null) folderAdapter.setOnFolderClickListener(onFolderClickListener);
    }

    //执行动画
    public void handleAnim(){
        if (isRunningAnim) return;
        ObjectAnimator animator1;
        ObjectAnimator animator2;
        if (!isOpen){
            isOpen = true;
            animator1 = ObjectAnimator.ofFloat(rlvFolder, "translationY", -getMeasuredHeight(), 0);
            animator2 = ObjectAnimator.ofFloat(coverView, "alpha", 0,1);
        }else {
            isOpen = false;
            animator1 = ObjectAnimator.ofFloat(rlvFolder, "translationY", 0, -getMeasuredHeight());
            animator2 = ObjectAnimator.ofFloat(coverView, "alpha", 1,0);
        }
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(400);
        animatorSet.playTogether(animator1,animator2);
        animatorSet.start();
    }

    private static class FolderAnimListener extends AnimatorListenerAdapter {
        private WeakReference<PhotoSelectFolderFrameLayout> reference;

        public FolderAnimListener(PhotoSelectFolderFrameLayout folderFrameLayout){
            reference = new WeakReference<>(folderFrameLayout);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if (reference != null && reference.get() != null) {
                reference.get().isRunningAnim = false;
                if (!reference.get().isOpen) {
                    reference.get().setVisibility(INVISIBLE);
                    reference.get().rlvFolder.setVisibility(INVISIBLE);
                    reference.get().coverView.setVisibility(INVISIBLE);
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
            if (reference != null && reference.get() != null) {
                reference.get().isRunningAnim = false;
                if (!reference.get().isOpen) {
                    reference.get().setVisibility(INVISIBLE);
                    reference.get().rlvFolder.setVisibility(INVISIBLE);
                    reference.get().coverView.setVisibility(INVISIBLE);
                }
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            if (reference != null && reference.get() != null) {
                reference.get().isRunningAnim = true;
                reference.get().setVisibility(VISIBLE);
                reference.get().rlvFolder.setVisibility(VISIBLE);
                reference.get().coverView.setVisibility(VISIBLE);
            }
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isRunningAnim() {
        return isRunningAnim;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        coverView = null;
        rlvFolder = null;
        animatorSet = null;
        folderAdapter = null;
    }
}
