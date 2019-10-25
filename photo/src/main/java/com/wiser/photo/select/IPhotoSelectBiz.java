package com.wiser.photo.select;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.wiser.photo.PhotoConstant;
import com.wiser.photo.model.PhotoFolderModel;
import com.wiser.photo.model.PhotoSelectModel;
import com.wiser.photo.model.PhotoSettingData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Wiser
 * 
 *         选择图片业务类
 */
public interface IPhotoSelectBiz {

	/**
	 * 表 - 列名
	 */
	String[]	IMAGE_PROJECTION	= { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.MIME_TYPE,
			MediaStore.Images.Media.SIZE, MediaStore.Images.Media._ID };

	/**
	 * ARGS
	 */
	String[]	SELECTION_ARGS		= { "image/jpeg", "image/png" };

	void loadAllPhoto(PhotoSelectActivity activity);

	int getSurplusCount();

	int getSpanCount();

	PhotoSettingData getPhotoSettingData();

	ArrayList<PhotoSelectModel> getModels();

	void setModels(ArrayList<PhotoSelectModel> models);

	ArrayList<PhotoFolderModel> getFolderModels();

	void setCurrentFolderPathType(String folderPath);

	String getCurrentFolderPathType();

	HashMap<String, PhotoSelectModel> getSelectMap();

	void setSelectMap(HashMap<String, PhotoSelectModel> map);

	boolean isCamera();

	ArrayList<String> covertSelectDataList(ArrayList<PhotoSelectModel> models);

	String[] covertSelectDataStrings(ArrayList<PhotoSelectModel> models);

	String getOutFilePath();

	void setOutFilePath(String outFilePath);

	void onDetach();

}

class PhotoSelectBiz implements IPhotoSelectBiz {

	private final static String					ALL						= "ALL";

	private ArrayList<PhotoSelectModel>			images					= new ArrayList<>();

	private ArrayList<PhotoFolderModel>			folderPhotos			= new ArrayList<>();

	private HashMap<String, PhotoSelectModel>	selectMap				= new HashMap<>();

	private int									surplusCount;

	private int									spanCount				= PhotoConstant.DEFAULT_SPAN_COUNT;

	private boolean								isCamera;

	private String								currentFolderPathType	= ALL;								// 文件夹路径 用于解决 不同文件夹下位置选中与未选中

	private String								outFilePath;

	private PhotoSettingData					photoSettingData;

	PhotoSelectBiz() {}

	PhotoSelectBiz(int surplusCount, int spanCount, boolean isCamera) {
		this.surplusCount = surplusCount;
		this.spanCount = spanCount;
		this.isCamera = isCamera;
	}

	PhotoSelectBiz(PhotoSettingData photoSettingData) {
		if (photoSettingData != null) {
			this.photoSettingData = photoSettingData;
			this.surplusCount = photoSettingData.surplusCount;
			this.isCamera = (photoSettingData.type == PhotoConstant.CAMERA_MODE);
		}
	}

	@Override public int getSurplusCount() {
		return surplusCount;
	}

	@Override public int getSpanCount() {
		return spanCount;
	}

	@Override public PhotoSettingData getPhotoSettingData() {
		return photoSettingData;
	}

	@Override public ArrayList<PhotoSelectModel> getModels() {
		return images;
	}

	@Override public void setModels(ArrayList<PhotoSelectModel> models) {
		this.images = models;
	}

	@Override public ArrayList<PhotoFolderModel> getFolderModels() {
		return folderPhotos;
	}

	@Override public void setCurrentFolderPathType(String folderPath) {
		this.currentFolderPathType = folderPath;
	}

	@Override public String getCurrentFolderPathType() {
		return currentFolderPathType;
	}

	@Override public HashMap<String, PhotoSelectModel> getSelectMap() {
		return selectMap;
	}

	@Override public void setSelectMap(HashMap<String, PhotoSelectModel> map) {
		this.selectMap = map;
	}

	@Override public boolean isCamera() {
		return isCamera;
	}

	@Override public ArrayList<String> covertSelectDataList(ArrayList<PhotoSelectModel> models) {
		ArrayList<String> selectData = new ArrayList<>();
		for (PhotoSelectModel model : models) {
			if (model == null) continue;
			selectData.add(model.path);
		}
		return selectData;
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

	@Override public String getOutFilePath() {
		return outFilePath;
	}

	@Override public void setOutFilePath(String outFilePath) {
		this.outFilePath = outFilePath;
	}

	@Override public void loadAllPhoto(PhotoSelectActivity activity) {
		int position = 0;
		if (isCamera) position = 1;

		StringBuilder like = new StringBuilder();
		like.append(IMAGE_PROJECTION[4]);
		like.append(">0 AND ");
		like.append(IMAGE_PROJECTION[3]);
		like.append("=? OR ");
		like.append(IMAGE_PROJECTION[3]);
		like.append("=? ");

		ContentResolver contentResolver = activity.getContentResolver();

		String order = IMAGE_PROJECTION[2] + " DESC";
		Cursor query = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, like.toString(), SELECTION_ARGS, order);

		assert query != null;
		if (query.moveToFirst()) {
			do {
				String path = query.getString(query.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
				String name = query.getString(query.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
				long dateTime = query.getLong(query.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
				PhotoSelectModel photoSelectModel = null;
				if (fileExist(path)) {
					photoSelectModel = new PhotoSelectModel(path, name, dateTime, PhotoConstant.PHOTO_MODE, position++);
					images.add(photoSelectModel);
				}
				// 获取文件夹数据
				File folderFile = new File(path).getParentFile();
				if (folderFile != null && folderFile.exists()) {
					String fp = folderFile.getAbsolutePath();
					PhotoFolderModel f = getFolderByPath(fp);
					if (f == null) {
						PhotoFolderModel folder = new PhotoFolderModel();
						folder.folderName = folderFile.getName();
						folder.folderPath = fp;
						folder.folderCover = photoSelectModel;
						ArrayList<PhotoSelectModel> imageList = new ArrayList<>();
						imageList.add(photoSelectModel);
						folder.folderPhotos = imageList;
						folder.folderCount = 1;
						folderPhotos.add(folder);
						if (isCamera) {
							// 添加相机
							PhotoSelectModel camera = new PhotoSelectModel();
							camera.type = PhotoConstant.CAMERA_MODE;
							camera.position = 0;
							folder.folderPhotos.add(0, camera);
						}
					} else {
						if (f.folderPhotos != null) f.folderPhotos.add(photoSelectModel);
					}
					if (f != null) f.folderCount = f.folderPhotos != null && f.folderPhotos.size() > 0 ? (isCamera() ? f.folderPhotos.size() - 1 : f.folderPhotos.size()) : 0;
				}
			} while (query.moveToNext());
		}
		query.close();

		// 添加相机
		if (isCamera) {
			PhotoSelectModel camera = new PhotoSelectModel();
			camera.type = PhotoConstant.CAMERA_MODE;
			camera.position = 0;
			images.add(0, camera);
		}

		activity.setPhotoData(images);

		// 添加所有图片文件夹
		PhotoFolderModel folder = new PhotoFolderModel();
		folder.folderName = "所有图片";
		folder.folderCount = images != null && images.size() > 0 ? (isCamera() ? images.size() - 1 : images.size()) : 0;
		folder.folderPhotos = images;
		folder.folderPath = ALL;
		folder.folderCover = isCamera ? ((images != null && images.size() > 1) ? images.get(1) : null) : ((images != null && images.size() > 0) ? images.get(0) : null);
		folderPhotos.add(0, folder);
	}

	/**
	 * 检查文件夹是否存在
	 *
	 * @param path
	 *            路径
	 * @return 文件夹
	 */
	private PhotoFolderModel getFolderByPath(String path) {
		for (PhotoFolderModel folder : folderPhotos) {
			if (TextUtils.equals(folder.folderPath, path)) {
				return folder;
			}
		}
		return null;
	}

	/**
	 * 判断文件存在不存在
	 *
	 * @param path
	 *            路径
	 * @return 结果
	 */
	private boolean fileExist(String path) {
		return !TextUtils.isEmpty(path) && new File(path).exists();
	}

	@Override public void onDetach() {
		if (images != null) images.clear();
		images = null;
		if (folderPhotos != null) folderPhotos.clear();
		folderPhotos = null;
	}
}
