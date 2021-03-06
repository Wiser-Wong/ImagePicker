package com.wiser.photo.grid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.wiser.photo.PhotoConstant;
import com.wiser.photo.R;
import com.wiser.photo.base.BasePhotoAdapter;
import com.wiser.photo.config.PhotoConfig;
import com.wiser.photo.dialog.ImagePreviewDialogFragment;
import com.wiser.photo.model.PhotoSettingData;
import com.wiser.photo.model.PhotoShowModel;
import com.wiser.photo.select.PhotoSelectActivity;
import com.wiser.photo.util.FileCache;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;

/**
 * @author Wiser
 * 
 *         图片带添加布局展示View
 */
public class PhotoGridView extends RecyclerView implements BasePhotoAdapter.OnPhotoItemClickListener {

	private int									addLayoutId, photoLayoutId, photoResId, deleteResId;

	private int									spanCount				= 4;

	private int									selectPhotoSpanCount	= PhotoConstant.DEFAULT_SPAN_COUNT;

	private int									addMode					= BasePhotoAdapter.HEAD;

	private int									selectPhotoMode			= PhotoConstant.CAMERA_MODE;

	private int									maxCounts				= 9;

	private boolean								isPreview;													// 是否预览

	private boolean								isCompress;													// 是否压缩

	private boolean								isCameraCrop;												// 是否拍照裁剪

	private int									compressQuality;											// 压缩质量

	private int									compressWidth;												// 压缩宽度

	private int									compressHeight;												// 压缩高度

	private int									cameraCropWidth;											// 拍照裁剪宽度

	private int									cameraCropHeight;											// 拍照裁剪高度

	private PhotoShowAdapter<PhotoShowModel>	photoAdapter;

	private ArrayList<String>					list					= new ArrayList<>();

	private OnPhotoGridListener					onPhotoGridListener;

	private static final String					COMPRESS_PATH_NAME		= "compress";

	private String								compressPath;												// 压缩路径

	public PhotoGridView(@NonNull Context context) {
		super(context);
	}

	public PhotoGridView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PhotoGridView);
		addLayoutId = ta.getResourceId(R.styleable.PhotoGridView_pgv_addLayoutId, -1);
		photoLayoutId = ta.getResourceId(R.styleable.PhotoGridView_pgv_photoLayoutId, -1);
		photoResId = ta.getResourceId(R.styleable.PhotoGridView_pgv_photoResId, -1);
		deleteResId = ta.getResourceId(R.styleable.PhotoGridView_pgv_photoDeleteResId, -1);
		spanCount = ta.getInt(R.styleable.PhotoGridView_pgv_spanCount, spanCount);
		selectPhotoSpanCount = ta.getInt(R.styleable.PhotoGridView_pgv_selectPhotoSpanCount, selectPhotoSpanCount);
		addMode = ta.getInt(R.styleable.PhotoGridView_pgv_addMode, BasePhotoAdapter.HEAD);
		selectPhotoMode = ta.getInt(R.styleable.PhotoGridView_pgv_selectPhotoMode, selectPhotoMode);
		maxCounts = ta.getInt(R.styleable.PhotoGridView_pgv_maxCounts, maxCounts);
		isPreview = ta.getBoolean(R.styleable.PhotoGridView_pgv_isPreview, false);
		isCompress = ta.getBoolean(R.styleable.PhotoGridView_pgv_isCompress, false);
		isCameraCrop = ta.getBoolean(R.styleable.PhotoGridView_pgv_isCameraCrop, false);
		compressQuality = ta.getInt(R.styleable.PhotoGridView_pgv_compressQuality, PhotoConstant.DEFAULT_COMPRESS_QUALITY);
		compressWidth = ta.getInt(R.styleable.PhotoGridView_pgv_compressWidth, PhotoConstant.DEFAULT_COMPRESS_WIDTH);
		compressHeight = ta.getInt(R.styleable.PhotoGridView_pgv_compressHeight, PhotoConstant.DEFAULT_COMPRESS_HEIGHT);
		cameraCropWidth = ta.getInt(R.styleable.PhotoGridView_pgv_cameraCropWidth, PhotoConstant.DEFAULT_CROP_WIDTH);
		cameraCropHeight = ta.getInt(R.styleable.PhotoGridView_pgv_cameraCropHeight, PhotoConstant.DEFAULT_CROP_HEIGHT);

		ta.recycle();

		initData();
	}

	private PhotoConfig photoConfig() {
		if (addLayoutId == -1 || photoLayoutId == -1) throw new InflateException("请设置展示的添加布局或者图片布局ID");
		if (photoResId == -1) throw new InflateException("请设置展示的图片布局内部图片控件ID");
		PhotoConfig photoConfig = new PhotoConfig();
		photoConfig.addLayoutId = addLayoutId;
		photoConfig.photoLayoutId = photoLayoutId;
		photoConfig.photoResId = photoResId;
		photoConfig.deleteResId = deleteResId;
		return photoConfig;
	}

	private void initData() {

		photoConfig();

		setLayoutManager(new GridLayoutManager(getContext(), spanCount));
		setAdapter(photoAdapter = new PhotoShowAdapter<>(getContext(), photoConfig(), addMode, maxCounts));

		photoAdapter.setOnPhotoItemClickListener(this);
		photoAdapter.setItems(new ArrayList<PhotoShowModel>());

		if (isCompress) {
			// Android-data-包名-files-compressPhoto-
			compressPath = FileCache.configureFileDir(getContext()) + File.separator + COMPRESS_PATH_NAME + File.separator;
			if (!new File(compressPath).exists()) FileCache.createFilesDirFolder(getContext(), COMPRESS_PATH_NAME);
		}

	}

	public void setItems(List<PhotoShowModel> list) {
		if (photoAdapter != null) photoAdapter.setItems(list);
	}

	public PhotoShowAdapter<PhotoShowModel> adapter() {
		return photoAdapter;
	}

	public void setOnPhotoGridListener(OnPhotoGridListener onPhotoGridListener) {
		this.onPhotoGridListener = onPhotoGridListener;
	}

	public void setCompressPath(String compressPath) {
		this.compressPath = compressPath;
		FileCache.createFolder(compressPath);
	}

	// 设置数据
	public void setPhotoData(ArrayList<String> list) {
		if (list == null) return;
		this.list.addAll(list);
		if (photoAdapter != null) photoAdapter.addList(getModels(list));
	}

	// 删除
	public void delete(int position) {
		if (list == null || adapter() == null) return;
		list.remove(position);
		adapter().delete(position);
	}

	// 清楚压缩的图片
	public void clearCompressPhoto() {
		FileCache.clearFolder(compressPath);
	}

	private ArrayList<PhotoShowModel> getModels(ArrayList<String> list) {
		if (list == null || list.size() == 0) return new ArrayList<>();
		ArrayList<PhotoShowModel> models = new ArrayList<>();
		for (String s : list) {
			PhotoShowModel model = new PhotoShowModel();
			model.path = s;
			models.add(model);
		}
		return models;
	}

	public void setMaxCounts(int maxCounts) {
		this.maxCounts = maxCounts;
		if (photoAdapter != null) photoAdapter.maxCounts = maxCounts;
	}

	public int getMaxCounts() {
		return maxCounts;
	}

	public ArrayList<String> getList() {
		return list;
	}

	public int getCount() {
		return list != null ? list.size() : 0;
	}

	public boolean isCompress() {
		return isCompress;
	}

	public void setCompress(boolean compress) {
		isCompress = compress;
	}

	public boolean isCameraCrop() {
		return isCameraCrop;
	}

	public void setCameraCrop(boolean cameraCrop) {
		isCameraCrop = cameraCrop;
	}

	public void setCompressQuality(int compressQuality) {
		this.compressQuality = compressQuality;
	}

	public int getCompressQuality() {
		return compressQuality;
	}

	public void setCompressWidth(int compressWidth) {
		this.compressWidth = compressWidth;
	}

	public int getCompressWidth() {
		return compressWidth;
	}

	public void setCompressHeight(int compressHeight) {
		this.compressHeight = compressHeight;
	}

	public int getCompressHeight() {
		return compressHeight;
	}

	public void setCameraCropWidth(int cameraCropWidth) {
		this.cameraCropWidth = cameraCropWidth;
	}

	public int getCameraCropWidth() {
		return cameraCropWidth;
	}

	public void setCameraCropHeight(int cameraCropHeight) {
		this.cameraCropHeight = cameraCropHeight;
	}

	public int getCameraCropHeight() {
		return cameraCropHeight;
	}

	@Override public void onAddPhotoClick(View view, int position) {
		if (onPhotoGridListener != null) onPhotoGridListener.onAddClick(view, position);
		else {
			if (isCompress) {
				PhotoSelectActivity.intent((FragmentActivity) getContext(), new PhotoSettingData(maxCounts > getCount() ? maxCounts - getCount() : 0, selectPhotoSpanCount, selectPhotoMode,
						compressPath, isCompress, compressQuality, compressWidth, compressHeight, isCameraCrop, cameraCropWidth, cameraCropHeight));
			} else {
				PhotoSelectActivity.intent((FragmentActivity) getContext(), maxCounts > getCount() ? maxCounts - getCount() : 0, selectPhotoSpanCount, selectPhotoMode);
			}
		}
	}

	@Override public void onItemPhotoClick(View view, int position) {
		if (onPhotoGridListener != null) onPhotoGridListener.onItemClick(view, position);
		else if (isPreview) ImagePreviewDialogFragment.newInstance(list, position, new ImagePreviewDialogFragment.OnImagePreviewListener() {

			@Override public void onItemDeleteClick(ArrayList<String> paths, boolean isChange) {
				if (!isChange) return;
				if (adapter() != null) {
					if (paths != null && paths.size() < maxCounts && (addMode == BasePhotoAdapter.HEAD || addMode == BasePhotoAdapter.END)) adapter().setIsShowAdd(true);
					else adapter().setIsShowAdd(false);
					adapter().setItems(getModels(paths));
				}
			}
		}).show(((FragmentActivity) getContext()).getSupportFragmentManager(), ImagePreviewDialogFragment.class.getName());
	}

	@Override public void onItemDeleteClick(View view, int position) {
		if (onPhotoGridListener != null) onPhotoGridListener.onDeleteClick(view, position);
		else {
			delete(position);
		}
	}

	public void onDetach() {
		if (list != null) list.clear();
		list = null;
		clearCompressPhoto();
	}

	public interface OnPhotoGridListener {

		void onAddClick(View view, int position);

		void onItemClick(View view, int position);

		void onDeleteClick(View view, int position);
	}

}
