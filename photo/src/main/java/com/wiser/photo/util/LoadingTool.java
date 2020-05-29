package com.wiser.photo.util;

import android.support.v4.app.FragmentActivity;

import com.wiser.photo.loading.LoadingDialogFragment;

/**
 * @author Wiser
 *
 * 			loading
 */
public class LoadingTool {

	/**
	 * 显示loading
	 *
	 * @param isClose
	 *            是否可以关闭弹窗
	 */
	public static void showLoading(FragmentActivity activity, boolean... isClose) {
		if (activity == null) return;
		if (isClose.length > 0) {
			LoadingDialogFragment.showLoadingDialog(activity, isClose[0]);
		} else {
			LoadingDialogFragment.showLoadingDialog(activity, false);
		}
	}

	/**
	 * 隐藏loading
	 */
	public static void hideLoading(FragmentActivity activity) {
		if (activity == null) return;
		LoadingDialogFragment loadingDialogFragment = (LoadingDialogFragment) activity.getSupportFragmentManager().findFragmentByTag(LoadingDialogFragment.class.getName());
		if (loadingDialogFragment != null) loadingDialogFragment.dismiss();
	}

}
