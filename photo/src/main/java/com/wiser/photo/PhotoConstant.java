package com.wiser.photo;

/**
 * @author Wiser
 * 
 *         常量
 */
public interface PhotoConstant {

	// 权限请求码
	int		PERMISSION_REQUEST_CODE			= 1100;

	// 拍照权限请求码
	int		PERMISSION_CAMERA_REQUEST_CODE	= 1101;

	// 剩余可选数量Key
	String	SURPLUS_COUNT_KEY				= "SURPLUS_COUNT_KEY";

	// 选择图片展示列数
	String	SPAN_COUNT_KEY					= "SPAN_COUNT_KEY";

	// 选择的图片结合数据Key
	String	INTENT_SELECT_PHOTO_KEY			= "INTENT_SELECT_PHOTO_KEY";

	// 预览图片地址key
	String	PREVIEW_PHOTO_PATH_KEY			= "PREVIEW_PHOTO_PATH_KEY";

	// 预览图片地址key
	String	PREVIEW_IMAGE_PATH_KEY			= "PREVIEW_IMAGE_PATH_KEY";

	// 选择图片数量key
	String	SELECT_PHOTO_COUNT_KEY			= "SELECT_PHOTO_COUNT_KEY";

	// 预览图片剩余选择图片集合key
	String	PREVIEW_PHOTO_SELECT_DATA_KEY	= "PREVIEW_PHOTO_SELECT_DATA_KEY";

	// 预览图片地址位置key
	String	PREVIEW_PHOTO_INDEX_KEY			= "PREVIEW_PHOTO_INDEX_KEY";

	// 完成Key
	String	PREVIEW_PHOTO_COMPLETE_KEY		= "PREVIEW_PHOTO_COMPLETE_KEY";

	// 预览模式key
	String	PREVIEW_MODEL_KEY				= "PREVIEW_MODEL_KEY";

	// 展示模式key
	String	SHOW_MODEL_KEY					= "SHOW_MODEL_KEY";

	// 完成Value
	int		PREVIEW_PHOTO_COMPLETE_VALUE	= 109999;

	// 跳转图片选择页请求码
	int		SELECT_PHOTO					= 1001;

	// 跳转图片预览页请求码
	int		PREVIEW_PHOTO					= 1002;

	// 默认图片选择器列数
	int		DEFAULT_SPAN_COUNT				= 3;

	// 预览图片模式
	int		PREVIEW_PHOTO_MODEL				= 111110029;

	// 预览按钮模式
	int		PREVIEW_BTN_MODEL				= 222112440;

	// 相机模式
	int		CAMERA_MODEL					= 11002;

	// 相册模式
	int		PHOTO_MODEL						= 11003;

	// 相机拍照请求码
	int		CAMERA_REQUEST_CODE				= 119;

	// 裁剪请求码
	int		CROP_REQUEST_CODE				= 120;

	String	AUTHORITY						= "com.wiser.photo.fileprovider";
}
