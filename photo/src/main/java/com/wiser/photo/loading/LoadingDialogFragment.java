package com.wiser.photo.loading;

import com.wiser.photo.R;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * @author Wiser
 * 
 *         loading
 */
public class LoadingDialogFragment extends DialogFragment implements DialogInterface.OnKeyListener {

	private boolean isClose;

	public static LoadingDialogFragment showLoadingDialog(FragmentActivity activity, boolean isClose) {
		if (activity == null) return null;
		LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isClose", isClose);
		loadingDialogFragment.setArguments(bundle);
		loadingDialogFragment.show(activity.getSupportFragmentManager(), LoadingDialogFragment.class.getName());
		return loadingDialogFragment;
	}

	@Override public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.DefaultDialogTheme);
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) isClose = savedInstanceState.getBoolean("isClose");
		if (getDialog() != null && getDialog().getWindow() != null) {
			// 去除标题栏
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			// 设置点击空白处是否关闭Dialog
			getDialog().setCanceledOnTouchOutside(isClose);

			// 设置返回键点击是否关闭Dialog
			if (!isClose) getDialog().setOnKeyListener(this);
		}
		if (savedInstanceState != null) {
			isClose = savedInstanceState.getBoolean("isClose", false);
		}
		return inflater.inflate(R.layout.dialog_loading, container, false);
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		return event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK;
	}

	/**
	 * 判断弹窗是否显示
	 *
	 * @return
	 */
	public boolean isShowing() {
		return getDialog() != null && getDialog().isShowing();
	}

	@Override public void dismiss() {
		if (isShowing()) {
			super.dismissAllowingStateLoss();
		}
	}

	@Override public void show(FragmentManager manager, String tag) {
		try {
			super.show(manager, tag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
