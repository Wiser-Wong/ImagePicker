package com.wiser.photo.util;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.wiser.photo.PhotoConstant;
import com.wiser.photo.model.PhotoSettingData;
import com.wiser.photo.preview.PhotoPreviewActivity;
import com.wiser.photo.select.PhotoSelectActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author Wiser
 * 
 *         压缩异步
 */
public class CompressAsyncTask extends AsyncTask<String, Void, Boolean> {

	private WeakReference<FragmentActivity>	reference;

	private PhotoSettingData				photoSettingData;

	private OnCompressListener				onCompressListener;

	private ArrayList<String>				paths;

	public CompressAsyncTask(FragmentActivity activity, OnCompressListener onCompressListener) {
		if (activity == null) return;
		reference = new WeakReference<>(activity);
		if (activity instanceof PhotoSelectActivity) {
			photoSettingData = ((PhotoSelectActivity) activity).getBiz() != null ? ((PhotoSelectActivity) activity).getBiz().getPhotoSettingData() : null;
		} else if (activity instanceof PhotoPreviewActivity) {
			photoSettingData = ((PhotoPreviewActivity) activity).getBiz() != null ? ((PhotoPreviewActivity) activity).getBiz().getPhotoSettingData() : null;
		}
		this.onCompressListener = onCompressListener;
		paths = new ArrayList<>();
	}

	@Override protected void onPreExecute() {
		super.onPreExecute();
		LoadingTool.showLoading(reference != null ? reference.get() : null, true);
	}

	@Override protected Boolean doInBackground(String... strings) {
		if (photoSettingData != null) {
			for (String string : strings) {
				paths.add(BitmapTool.bitmapToPath(photoSettingData.compressPath, new File(string).getName(), string,
						photoSettingData.compressQuality == 0 ? PhotoConstant.DEFAULT_COMPRESS_QUALITY : photoSettingData.compressQuality,
						photoSettingData.compressWidth == 0 ? PhotoConstant.DEFAULT_COMPRESS_WIDTH : photoSettingData.compressWidth,
						photoSettingData.compressHeight == 0 ? PhotoConstant.DEFAULT_COMPRESS_HEIGHT : photoSettingData.compressHeight));
			}
		}
		return true;
	}

	@Override protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
		LoadingTool.hideLoading(reference != null ? reference.get() : null);
		if (aBoolean && onCompressListener != null) {
			onCompressListener.compressSuccess(paths);
		}
		onDetach();
	}

	private void onDetach() {
		if (reference != null) reference.clear();
		reference = null;
		if (paths != null) paths.clear();
		photoSettingData = null;
		onCompressListener = null;
	}

	public interface OnCompressListener {

		void compressSuccess(ArrayList<String> paths);
	}
}
