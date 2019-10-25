package com.wiser.photo.preview;

import android.os.Bundle;
import android.widget.Toast;

import com.wiser.photo.PhotoConstant;
import com.wiser.photo.model.PhotoSelectModel;
import com.wiser.photo.model.PhotoSettingData;

import java.util.ArrayList;

/**
 * @author Wiser
 * 
 *         图片预览业务
 */
public interface IPhotoPreviewBiz {

	ArrayList<PhotoSelectModel> getList();

	ArrayList<PhotoSelectModel> getSelectData();

	void setPosition(int position);

	int getPosition();

	int getCount();

	int getSurplusCount();

	int getType();

	int matchIndex(PhotoSelectModel model);

	boolean isSelect(int position);

	boolean isCamera();

	boolean isCompress();

	boolean isNoBtnPreview();

	boolean isTitleHide();

	void setTitleHideState(boolean isTitleHide);

	void initSelectPhoto();

	void selectPhotoClick(int position);

	PhotoSettingData getPhotoSettingData();

	String[] covertSelectDataStrings(ArrayList<PhotoSelectModel> models);

	void onDetach();

}

class PhotoPreviewBiz implements IPhotoPreviewBiz {

	private PhotoPreviewActivity		activity;

	private ArrayList<PhotoSelectModel>	list;

	private int							position;

	private int							count;

	private ArrayList<PhotoSelectModel>	selectData;

	private int							surplusCount;

	private boolean						isCamera;

	private PhotoSettingData			photoSettingData;

	private boolean						isNoBtnPreview;

	private int							type	= PhotoConstant.PREVIEW_PHOTO_MODE;

	private boolean						isTitleHide;

	PhotoPreviewBiz(PhotoPreviewActivity activity, Bundle bundle) {
		this.activity = activity;
		if (bundle != null) {
			this.list = bundle.getParcelableArrayList(PhotoConstant.INTENT_SELECT_PHOTO_KEY);
			this.position = bundle.getInt(PhotoConstant.PREVIEW_PHOTO_INDEX_KEY);
			this.selectData = bundle.getParcelableArrayList(PhotoConstant.PREVIEW_PHOTO_SELECT_DATA_KEY);
			this.surplusCount = bundle.getInt(PhotoConstant.SURPLUS_COUNT_KEY);
			this.type = bundle.getInt(PhotoConstant.PREVIEW_MODE_KEY, PhotoConstant.PREVIEW_PHOTO_MODE);
			isCamera = bundle.getBoolean(PhotoConstant.SHOW_MODE_KEY, false);
			photoSettingData = bundle.getParcelable(PhotoConstant.SETTING_DATA_KEY);
			this.count = selectData != null ? selectData.size() : 0;
			this.isNoBtnPreview = type != PhotoConstant.PREVIEW_BTN_MODE;
			if (isCamera && isNoBtnPreview) {
				if (this.list != null && this.list.size() > 0) this.list.remove(0);
				if (position > 0) position--;
			}
		}
	}

	@Override public ArrayList<PhotoSelectModel> getList() {
		return list;
	}

	@Override public ArrayList<PhotoSelectModel> getSelectData() {
		return selectData;
	}

	@Override public void setPosition(int position) {
		this.position = position;
	}

	@Override public int getPosition() {
		return position;
	}

	@Override public int getCount() {
		return count;
	}

	@Override public int getSurplusCount() {
		return surplusCount;
	}

	@Override public int getType() {
		return type;
	}

	@Override public int matchIndex(PhotoSelectModel model) {
		if (model == null || selectData == null || selectData.size() == 0) return 0;
		for (int i = 0; i < selectData.size(); i++) {
			if (model.equals(selectData.get(i))) return i;
		}
		return 0;
	}

	// 是否选中
	@Override public boolean isSelect(int position) {
		if (list == null || list.size() == 0) return false;
		PhotoSelectModel photoSelectModel = list.get(position);
		if (photoSelectModel == null) return false;
		return photoSelectModel.isSelect;
	}

	@Override public boolean isCamera() {
		return isCamera;
	}

	@Override public boolean isCompress() {
		return photoSettingData != null && photoSettingData.isCompress;
	}

	@Override public boolean isNoBtnPreview() {
		return isNoBtnPreview;
	}

	@Override public boolean isTitleHide() {
		return isTitleHide;
	}

	@Override public void setTitleHideState(boolean isTitleHide) {
		this.isTitleHide = isTitleHide;
	}

	// 初始化选择图片
	@Override public void initSelectPhoto() {
		if (list == null || list.size() == 0) return;
		PhotoSelectModel photoSelectModel = list.get(position);
		activity.updateUi(photoSelectModel.isSelect);
	}

	// 选择点击
	@Override public void selectPhotoClick(int position) {
		if (activity == null || list == null || list.size() == 0) return;
		PhotoSelectModel photoSelectModel = list.get(position);
		if (count >= surplusCount && !photoSelectModel.isSelect) {
			Toast.makeText(activity, "你最多只能选择" + surplusCount + "张图片", Toast.LENGTH_SHORT).show();
			return;
		}
		PhotoSelectModel photoSelectModel1 = list.get(position);
		if (photoSelectModel1.isSelect) {
			photoSelectModel1.isSelect = false;
			if (count > 0) {
				count--;
			}
			remove(photoSelectModel1);
		} else {
			photoSelectModel1.isSelect = true;
			count++;
			selectData.add(photoSelectModel1);
		}
		list.set(position, photoSelectModel1);
		activity.updateUi(photoSelectModel1.isSelect);

	}

	@Override public PhotoSettingData getPhotoSettingData() {
		return photoSettingData;
	}

	@Override public String[] covertSelectDataStrings(ArrayList<PhotoSelectModel> models) {
		if (models == null) return null;
		String[] selectData = new String[models.size()];
		for (int i = 0; i < models.size(); i++) {
			if (models.get(i) == null) continue;
			selectData[i] = models.get(i).path;
		}
		return selectData;
	}

	// 移除选择
	private void remove(PhotoSelectModel photoSelectModel) {
		if (photoSelectModel == null || selectData == null || selectData.size() == 0) return;
		for (PhotoSelectModel model : selectData) {
			if (model == null || model.path == null) continue;
			if (model.path.equals(photoSelectModel.path)) {
				selectData.remove(model);
				break;
			}
		}
	}

	@Override public void onDetach() {
		if (list != null) list.clear();
		list = null;
		if (selectData != null) selectData.clear();
		selectData = null;
		activity = null;
		photoSettingData = null;
	}

}
