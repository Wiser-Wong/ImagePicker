package com.wiser.photo.dialog;

import com.wiser.photo.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author Wiser
 * 
 *         删除图片弹窗
 */
public class DeletePhotoDialogFragment extends DialogFragment {

	private OnDeleteListener onDeleteListener;

	public static DeletePhotoDialogFragment newInstance(OnDeleteListener onDeleteListener) {
		DeletePhotoDialogFragment deletePhotoDialogFragment = new DeletePhotoDialogFragment();
		deletePhotoDialogFragment.setOnDeleteListener(onDeleteListener);
		return deletePhotoDialogFragment;
	}

	@Override public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.DeleteDialogTheme);
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (getDialog() != null) {
			// 去除标题栏
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		View view = inflater.inflate(R.layout.photo_delete_dialog, container, false);

		if (getDialog() != null) {
			// 设置点击空白处是否关闭Dialog
			getDialog().setCanceledOnTouchOutside(true);
		}

		if (getDialog() != null && getDialog().getWindow() != null) {
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}

		view.findViewById(R.id.tv_delete_photo).setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				if (onDeleteListener != null) onDeleteListener.delete();
				dismiss();
			}
		});

		view.findViewById(R.id.tv_delete_photo_cancel).setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				dismiss();
			}
		});

		return view;
	}

	@Override public void onResume() {
		super.onResume();
		Window window = getDialog().getWindow();
		if (window != null && getActivity() != null) {
			WindowManager.LayoutParams wlp = window.getAttributes();
			wlp.width = getScreenWidth(getActivity());
			wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			wlp.gravity = Gravity.BOTTOM;
			window.setAttributes(wlp);
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

	public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
		this.onDeleteListener = onDeleteListener;
	}

	public interface OnDeleteListener {

		void delete();
	}

	@Override public void onDestroy() {
		super.onDestroy();
		onDeleteListener = null;
	}
}
