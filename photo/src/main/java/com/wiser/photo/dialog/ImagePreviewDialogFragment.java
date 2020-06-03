package com.wiser.photo.dialog;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wiser.photo.PhotoConstant;
import com.wiser.photo.R;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * @author Wiser
 * 
 *         预览图片
 */
public class ImagePreviewDialogFragment extends DialogFragment implements View.OnClickListener, ViewPager.OnPageChangeListener, DialogInterface.OnKeyListener {

	private RelativeLayout 				rlImagePreviewTitle;

	private TextView					tvPreviewCount;

	private IImagePreviewBiz			iImagePreviewBiz;

	private OnImagePreviewListener		onImagePreviewListener;

	private ImagePreviewFragmentAdapter	previewFragmentAdapter;

	private ViewPager					vpPreviewPhoto;

	public static ImagePreviewDialogFragment newInstance(ArrayList<String> list, int index, OnImagePreviewListener onImagePreviewListener) {
		ImagePreviewDialogFragment previewDialogFragment = new ImagePreviewDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(PhotoConstant.PREVIEW_PHOTO_PATH_KEY, list);
		bundle.putInt(PhotoConstant.PREVIEW_PHOTO_INDEX_KEY, index);
		previewDialogFragment.setArguments(bundle);
		previewDialogFragment.setOnImagePreviewListener(onImagePreviewListener);
		return previewDialogFragment;
	}

	@Override public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.PreviewPhotoDialogTheme);
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (getDialog() != null) {
			// 去除标题栏
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		View view = inflater.inflate(R.layout.image_preview_act, container, false);

		if (getDialog() != null) {
			// 设置点击空白处是否关闭Dialog
			getDialog().setCanceledOnTouchOutside(true);

			getDialog().setOnKeyListener(this);

			// 设置背景透明 显示弹窗弧度
			if (getDialog().getWindow() != null) {
				getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			}
		}

		// 遮挡状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}

		rlImagePreviewTitle = view.findViewById(R.id.rl_image_preview_title);
		vpPreviewPhoto = view.findViewById(R.id.vp_preview_image);
		tvPreviewCount = view.findViewById(R.id.tv_image_preview_count);

		view.findViewById(R.id.iv_image_preview_back).setOnClickListener(this);
		view.findViewById(R.id.iv_image_preview_delete).setOnClickListener(this);

		iImagePreviewBiz = new ImagePreviewBiz(this, getArguments());

		setPreviewAdapter();

		updatePreviewCountUi();

		return view;
	}

	@Override public void onResume() {
		super.onResume();
		Window window = getDialog().getWindow();
		if (window != null && getActivity() != null) {
			WindowManager.LayoutParams wlp = window.getAttributes();
			wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			window.setAttributes(wlp);
		}
	}

	// 设置预览图片数据
	public void setPreviewAdapter() {
		if (getActivity() != null || vpPreviewPhoto != null) {
			if (previewFragmentAdapter == null) {
				vpPreviewPhoto.setAdapter(previewFragmentAdapter = new ImagePreviewFragmentAdapter(getChildFragmentManager(), iImagePreviewBiz.getList()));
				vpPreviewPhoto.addOnPageChangeListener(this);
			} else {
				vpPreviewPhoto.setAdapter(previewFragmentAdapter);
			}
			vpPreviewPhoto.setOffscreenPageLimit(5);
			vpPreviewPhoto.setCurrentItem(iImagePreviewBiz.getPosition());
			previewFragmentAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 获得屏幕的宽度
	 *
	 * @return
	 */
	public int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获得屏幕的高度
	 *
	 * @return
	 */
	public int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public void setOnImagePreviewListener(OnImagePreviewListener onImagePreviewListener) {
		this.onImagePreviewListener = onImagePreviewListener;
	}

	public OnImagePreviewListener getOnImagePreviewListener() {
		return onImagePreviewListener;
	}

	@Override public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismiss();
			return true;
		} else {
			return false;
		}
	}

	public interface OnImagePreviewListener {

		void onItemDeleteClick(ArrayList<String> paths, boolean isChange);
	}

	@Override public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.iv_image_preview_back) {// 返回
			dismiss();
		} else if (id == R.id.iv_image_preview_delete) {// 删除
			DeletePhotoDialogFragment.newInstance(new DeletePhotoDialogFragment.OnDeleteListener() {

				@Override public void delete() {
					iImagePreviewBiz.delete();
				}
			}).show(getChildFragmentManager(), DeletePhotoDialogFragment.class.getName());
		}
	}

	// 删除图片更新数量UI
	public void updatePreviewCountUi() {
		if (iImagePreviewBiz.getList() != null && iImagePreviewBiz.getList().size() > iImagePreviewBiz.getPosition())
			tvPreviewCount.setText(MessageFormat.format("{0}/{1}", iImagePreviewBiz.getPosition() + 1, iImagePreviewBiz.getList().size()));
	}

	// 更新title显示隐藏
	public void updateTitleAnim() {
		int animatorId;
		if (iImagePreviewBiz.isTitleHide()) {
			iImagePreviewBiz.setTitleHideState(false);
			animatorId = R.animator.alpha_show;
		} else {
			iImagePreviewBiz.setTitleHideState(true);
			animatorId = R.animator.alpha_hide;
		}
		ObjectAnimator anim1 = (ObjectAnimator) AnimatorInflater.loadAnimator(getActivity(), animatorId);
		anim1.setTarget(rlImagePreviewTitle);
		anim1.addListener(new AnimatorListenerAdapter() {

			@Override public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				rlImagePreviewTitle.setVisibility(View.VISIBLE);
			}

			@Override public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if (iImagePreviewBiz.isTitleHide()) rlImagePreviewTitle.setVisibility(View.GONE);
			}
		});
		anim1.start();
	}

	@Override public void onPageScrolled(int i, float v, int i1) {

	}

	@Override public void onPageSelected(int i) {
		iImagePreviewBiz.setPosition(i);
		updatePreviewCountUi();
	}

	@Override public void onPageScrollStateChanged(int i) {

	}

	@Override public void dismiss() {
		if (onImagePreviewListener != null && iImagePreviewBiz != null) onImagePreviewListener.onItemDeleteClick(iImagePreviewBiz.getList(), iImagePreviewBiz.isChange());
		super.dismiss();
	}

	@Override public void onDestroy() {
		super.onDestroy();
		onImagePreviewListener = null;
		if (iImagePreviewBiz != null) iImagePreviewBiz.onDetach();
		iImagePreviewBiz = null;
	}

}
