package com.wiser.photo.preview;

import java.text.MessageFormat;
import java.util.ArrayList;

import com.wiser.photo.PhotoConstant;
import com.wiser.photo.R;
import com.wiser.photo.model.PhotoSelectModel;
import com.wiser.photo.weight.SquaredImageView;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Wiser
 * 
 *         图片预览
 */
public class PhotoPreviewActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

	private SquaredImageView ivPhotoPreviewCheck;

	private TextView					tvPhotoPreviewFinish;

	private ViewPager vpPreviewPhoto;

	private PhotoPreviewFragmentAdapter	photoPreviewFragmentAdapter;

	private LinearLayout				llPreviewSmallPhoto;

	private PreviewSmallPhotoAdapter	smallPhotoAdapter;

	private IPhotoPreviewBiz			iPhotoPreviewBiz;

	private ConstraintLayout			clPhotoPreviewTitle;

	private LinearLayout				llPhotoPreviewBottom;

	public static void intent(FragmentActivity activity, ArrayList<PhotoSelectModel> models, ArrayList<PhotoSelectModel> selectData, int surplusCount, int index, int type, boolean isCamera) {
		if (activity == null) return;
		Intent intent = new Intent(activity, PhotoPreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(PhotoConstant.INTENT_SELECT_PHOTO_KEY, models);
		bundle.putParcelableArrayList(PhotoConstant.PREVIEW_PHOTO_SELECT_DATA_KEY, selectData);
		bundle.putInt(PhotoConstant.SURPLUS_COUNT_KEY, surplusCount);
		bundle.putInt(PhotoConstant.PREVIEW_PHOTO_INDEX_KEY, index);
		bundle.putInt(PhotoConstant.PREVIEW_MODE_KEY, type);
		bundle.putBoolean(PhotoConstant.SHOW_MODE_KEY, isCamera);
		intent.putExtras(bundle);
		activity.startActivityForResult(intent, PhotoConstant.PREVIEW_PHOTO);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_preview_act);

		vpPreviewPhoto = findViewById(R.id.vp_preview_photo);
		SquaredImageView ivPhotoPreviewBack = findViewById(R.id.iv_photo_preview_back);
		tvPhotoPreviewFinish = findViewById(R.id.tv_photo_preview_finish);
		ivPhotoPreviewCheck = findViewById(R.id.iv_photo_preview_check);
		llPreviewSmallPhoto = findViewById(R.id.ll_preview_small);
		clPhotoPreviewTitle = findViewById(R.id.cl_photo_preview_title);
		llPhotoPreviewBottom = findViewById(R.id.ll_photo_preview_bottom);
		RecyclerView rlvPreview = findViewById(R.id.rlv_preview_small);

		ivPhotoPreviewBack.setOnClickListener(this);
		tvPhotoPreviewFinish.setOnClickListener(this);
		ivPhotoPreviewCheck.setOnClickListener(this);

		iPhotoPreviewBiz = new PhotoPreviewBiz(this, getIntent() != null ? getIntent().getExtras() : null);

		vpPreviewPhoto.setAdapter(photoPreviewFragmentAdapter = new PhotoPreviewFragmentAdapter(getSupportFragmentManager(), iPhotoPreviewBiz.getList()));
		vpPreviewPhoto.setOffscreenPageLimit(5);
		vpPreviewPhoto.setCurrentItem(iPhotoPreviewBiz.getPosition());
		vpPreviewPhoto.addOnPageChangeListener(this);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		rlvPreview.setLayoutManager(linearLayoutManager);
		rlvPreview.setAdapter(smallPhotoAdapter = new PreviewSmallPhotoAdapter(this));
		smallPhotoAdapter.setIndex(
				iPhotoPreviewBiz.getList() != null && iPhotoPreviewBiz.getList().size() > iPhotoPreviewBiz.getPosition() ? iPhotoPreviewBiz.getList().get(iPhotoPreviewBiz.getPosition()).position
						: -1);

		iPhotoPreviewBiz.initSelectPhoto();
	}

	@Override public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.iv_photo_preview_back) {// 返回
			backControl();
		} else if (id == R.id.tv_photo_preview_finish) {// 完成
			complete();
		} else if (id == R.id.iv_photo_preview_check) {// 选择
			iPhotoPreviewBiz.selectPhotoClick(iPhotoPreviewBiz.getPosition());
		}
	}

	// 更新title显示隐藏
	public void updateTitleAnim() {
		int animatorId;
		if (iPhotoPreviewBiz.isTitleHide()) {
			iPhotoPreviewBiz.setTitleHideState(false);
			animatorId = R.animator.alpha_show;
		} else {
			iPhotoPreviewBiz.setTitleHideState(true);
			animatorId = R.animator.alpha_hide;
		}
		ObjectAnimator anim1 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, animatorId);
		anim1.setTarget(clPhotoPreviewTitle);
		anim1.addListener(new AnimatorListenerAdapter() {

			@Override public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				clPhotoPreviewTitle.setVisibility(View.VISIBLE);
			}

			@Override public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (iPhotoPreviewBiz.isTitleHide()) clPhotoPreviewTitle.setVisibility(View.GONE);
			}
		});
		anim1.start();
		ObjectAnimator anim2 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, animatorId);
		anim2.setTarget(llPhotoPreviewBottom);
		anim2.addListener(new AnimatorListenerAdapter() {

			@Override public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				llPhotoPreviewBottom.setVisibility(View.VISIBLE);
			}

			@Override public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (iPhotoPreviewBiz.isTitleHide()) llPhotoPreviewBottom.setVisibility(View.GONE);
			}
		});
		anim2.start();
	}

	// 初始化已经选择的图片UI
	public void updateUi(boolean isSelect) {
		if (isSelect) {
			ivPhotoPreviewCheck.setBackgroundResource(R.mipmap.photo_selected);
		} else {
			ivPhotoPreviewCheck.setBackgroundResource(R.mipmap.photo_unselected);
		}

		updateBtnStateUi();
	}

	// 更新选择图片底部按钮UI
	public void updateBtnStateUi() {
		// 预览和完成
		if (iPhotoPreviewBiz.getCount() > 0) {
			// 完成
			tvPhotoPreviewFinish.setEnabled(true);
			tvPhotoPreviewFinish.setBackgroundResource(R.drawable.photo_select_finish_shape_st);
			tvPhotoPreviewFinish.setText(MessageFormat.format("完成({0}/{1})", iPhotoPreviewBiz.getCount(), iPhotoPreviewBiz.getSurplusCount()));
			tvPhotoPreviewFinish.setTextColor(Color.WHITE);
		} else {
			// 完成
			tvPhotoPreviewFinish.setEnabled(false);
			tvPhotoPreviewFinish.setBackgroundResource(R.drawable.photo_select_finish_shape_df);
			tvPhotoPreviewFinish.setText("完成");
			tvPhotoPreviewFinish.setTextColor(Color.parseColor("#50ffffff"));
		}
		updateSmallPhotoUi();
	}

	// 根据小图片预览点击跳转相应位置
	public void setPageItem(PhotoSelectModel model) {
		if (model == null) return;
		if (iPhotoPreviewBiz.isCamera()) {
			if (iPhotoPreviewBiz.isNoBtnPreview()) {
				if (photoPreviewFragmentAdapter.getCount() > model.position && model.position > 0) {
					int index = model.position - 1;
					vpPreviewPhoto.setCurrentItem(index, false);
				}
			} else {
				vpPreviewPhoto.setCurrentItem(iPhotoPreviewBiz.matchIndex(model), false);
			}
		} else {
			if (iPhotoPreviewBiz.isNoBtnPreview()) {
				if (photoPreviewFragmentAdapter.getCount() > model.position) {
					vpPreviewPhoto.setCurrentItem(model.position, false);
				}
			} else {
				vpPreviewPhoto.setCurrentItem(iPhotoPreviewBiz.matchIndex(model), false);
			}
		}
	}

	// 更新小图片展示
	private void updateSmallPhotoUi() {
		if (iPhotoPreviewBiz.getCount() > 0) {
			llPreviewSmallPhoto.setVisibility(View.VISIBLE);
		} else {
			llPreviewSmallPhoto.setVisibility(View.GONE);
		}
		smallPhotoAdapter.setItems(iPhotoPreviewBiz.getSelectData());
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		if (iPhotoPreviewBiz != null) iPhotoPreviewBiz.onDetach();
		iPhotoPreviewBiz = null;
	}

	@Override public void onPageScrolled(int i, float v, int i1) {

	}

	@Override public void onPageSelected(int i) {
		iPhotoPreviewBiz.setPosition(i);
		smallPhotoAdapter.setIndex(iPhotoPreviewBiz.getList() != null && iPhotoPreviewBiz.getList().size() > i ? iPhotoPreviewBiz.getList().get(i).position : -1);
		if (iPhotoPreviewBiz.isSelect(i)) ivPhotoPreviewCheck.setBackgroundResource(R.mipmap.photo_selected);
		else ivPhotoPreviewCheck.setBackgroundResource(R.mipmap.photo_unselected);
	}

	@Override public void onPageScrollStateChanged(int i) {

	}

	@Override public void onBackPressed() {
		backControl();
	}

	// 返回
	public void backControl() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(PhotoConstant.INTENT_SELECT_PHOTO_KEY, iPhotoPreviewBiz.getList());
		bundle.putParcelableArrayList(PhotoConstant.PREVIEW_PHOTO_SELECT_DATA_KEY, iPhotoPreviewBiz.getSelectData());
		bundle.putInt(PhotoConstant.SELECT_PHOTO_COUNT_KEY, iPhotoPreviewBiz.getCount());
		bundle.putInt(PhotoConstant.PREVIEW_MODE_KEY, iPhotoPreviewBiz.getType());
		intent.putExtras(bundle);
		setResult(PhotoConstant.PREVIEW_PHOTO, intent);
		finish();
	}

	// 完成
	private void complete() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(PhotoConstant.PREVIEW_PHOTO_SELECT_DATA_KEY, iPhotoPreviewBiz.getSelectData());
		bundle.putInt(PhotoConstant.PREVIEW_PHOTO_COMPLETE_KEY, PhotoConstant.PREVIEW_PHOTO_COMPLETE_VALUE);
		intent.putExtras(bundle);
		setResult(PhotoConstant.PREVIEW_PHOTO, intent);
		finish();
	}

}
