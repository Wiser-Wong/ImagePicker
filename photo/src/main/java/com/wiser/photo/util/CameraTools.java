package com.wiser.photo.util;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;

import com.wiser.photo.PhotoConstant;

import java.io.File;

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

	/**
	 * 调用系统照相机拍照
	 *
	 * @param outPath
	 *            输出路径String
	 * @param authority
	 *            7.0以上需要
	 * @param requestCode
	 *            请求码
	 * @return 返回文件绝对路径 file.getAbsolutePath();
	 */
	public static String intentCamera(FragmentActivity activity, String outPath, String authority, int requestCode) {
		if (activity == null) return "";
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File outDir = new File(outPath);
			if (!outDir.exists()) {
				outDir.mkdirs();
			}
			File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
			else {
				intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity, authority, outFile));
			}
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			activity.startActivityForResult(intent, requestCode);
			return outFile.getAbsolutePath();
		}
		return "";
	}

	/**
	 * 截图方法
	 *
	 * @param uri
	 *            uri
	 * @param requestCode
	 *            请求吗
	 */
	public static void cropPhoto(FragmentActivity activity, Uri uri, int requestCode) {
		if (activity == null) return;
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// intent.putExtra("scale", true);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		// intent.putExtra("outputFormat",
		// Bitmap.CompressFormat.JPEG.toString());
		// intent.putExtra("noFaceDetection", true); // no face detection
		activity.startActivityForResult(intent, requestCode);
	}

}
