package com.wiser.photo.dialog;

import android.os.Bundle;

import com.wiser.photo.PhotoConstant;

import java.util.ArrayList;

/**
 * @author Wiser
 * 
 *         预览图片业务
 */
public interface IImagePreviewBiz {

	ArrayList<String> getList();

	int getPosition();

	void setPosition(int position);

	void delete();

	boolean isChange();

	void onDetach();

}

class ImagePreviewBiz implements IImagePreviewBiz {

	private ImagePreviewDialogFragment	fragment;

	private ArrayList<String>			list;

	private int							position;

	private boolean						isChange;

	public ImagePreviewBiz(ImagePreviewDialogFragment f, Bundle bundle) {
		this.fragment = f;
		if (bundle != null) {
			position = bundle.getInt(PhotoConstant.PREVIEW_PHOTO_INDEX_KEY);
			list = bundle.getStringArrayList(PhotoConstant.PREVIEW_PHOTO_PATH_KEY);
		}
	}

	@Override public ArrayList<String> getList() {
		return list;
	}

	@Override public int getPosition() {
		return position;
	}

	@Override public void setPosition(int position) {
		this.position = position;
	}

	@Override public void delete() {
		this.isChange = true;
		if (list.size() > position) {
			list.remove(position);
			if (position > 0) position--;
		}
		if (fragment != null) {
			fragment.setPreviewAdapter();
			fragment.updatePreviewCountUi();
		}
		if ((list == null || list.size() == 0) && fragment != null) {
			fragment.dismiss();
		}
	}

	@Override public boolean isChange() {
		return isChange;
	}

	@Override public void onDetach() {
		fragment = null;
		isChange = false;
	}
}
