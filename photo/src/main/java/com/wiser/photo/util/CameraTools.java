package com.wiser.photo.util;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.wiser.photo.PhotoConstant;

import java.io.File;
import java.util.Locale;

/**
 * @author Wiser
 * 
 *         拍照工具
 */
public class CameraTools {

	/**
	 * 跳转拍照
	 * 
	 * @param activity
	 * @return
	 */
	public static File skipCamera(FragmentActivity activity) {
		if (activity == null) return null;
		File mTmpFile = null;
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
			// 设置系统相机拍照后的输出路径
			// 创建临时文件
			mTmpFile = FileTool.createTmpFile(activity);
			if (mTmpFile != null && mTmpFile.exists()) {

				/* 获取当前系统的android版本号 */
				int currentapiVersion = Build.VERSION.SDK_INT;
				if (currentapiVersion < 24) {
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
					activity.startActivityForResult(cameraIntent, PhotoConstant.CAMERA_REQUEST_CODE);
				} else {
					ContentValues contentValues = new ContentValues(1);
					contentValues.put(MediaStore.Images.Media.DATA, mTmpFile.getAbsolutePath());
					Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					activity.startActivityForResult(cameraIntent, PhotoConstant.CAMERA_REQUEST_CODE);
				}
			}
		}
		return mTmpFile;
	}

	// 图片裁剪
	public static Uri cropPhoto(FragmentActivity activity, Uri uri, File imgFile, int cropWidth, int cropHeight, int requestCode) {
		Uri mCutUri;
		Intent intent = new Intent("com.android.camera.action.CROP"); // 打开系统自带的裁剪图片的intent

		// 注意一定要添加该项权限，否则会提示无法裁剪
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

		intent.setDataAndType(uri, "image/*");
		intent.putExtra("scale", true);
		// 设置裁剪区域的宽高比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 设置裁剪区域的宽度和高度
		intent.putExtra("outputX", cropWidth);
		intent.putExtra("outputY", cropHeight);
		// 取消人脸识别
		intent.putExtra("noFaceDetection", true);
		// 图片输出格式
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		// 若为false则表示不返回数据
		intent.putExtra("return-data", false);
		// 指定裁剪完成以后的图片所保存的位置,pic info显示有延时
		// if (fromCapture) {
		// 如果是使用拍照，那么原先的uri和最终目标的uri一致,注意这里的uri必须是Uri.fromFile生成的
		mCutUri = Uri.fromFile(imgFile);
		// }
		// else { // 从相册中选择，那么裁剪的图片保存在take_photo中
		// String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new
		// Date());
		// String fileName = "photo_" + time;
		// File mCutFile = new File(Environment.getExternalStorageDirectory() +
		// "/take_photo", fileName + ".jpeg");
		// if (!mCutFile.getParentFile().exists()) {
		// mCutFile.getParentFile().mkdirs();
		// }
		// mCutUri = Uri.fromFile(mCutFile);
		// }
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCutUri);
		// 以广播方式刷新系统相册，以便能够在相册中找到刚刚所拍摄和裁剪的照片
		Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intentBc.setData(uri);
		activity.sendBroadcast(intentBc);
		activity.startActivityForResult(intent, requestCode); // 设置裁剪参数显示图片至ImageVie

		return mCutUri;
	}

}
