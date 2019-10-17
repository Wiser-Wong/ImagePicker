package com.wiser.photo.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * @author Wiser
 *
 *         文件路径
 */
public class FileTool {

	private static final String	JPEG_FILE_PREFIX			= "IMG_";

	private static final String	JPEG_FILE_SUFFIX			= ".jpg";

	private static final String	EXTERNAL_STORAGE_PERMISSION	= "android.permission.WRITE_EXTERNAL_STORAGE";

	// 创建临时文件
	public static File createTmpFile(Context context) {
		File dir;
		if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
			dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			if (!dir.exists()) {
				dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
				if (!dir.exists()) {
					dir = getCacheDirectory(context, true);
				}
			}
		} else {
			dir = getCacheDirectory(context, true);
		}
		try {
			return File.createTempFile(JPEG_FILE_PREFIX, JPEG_FILE_SUFFIX, dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File getCacheDirectory(Context context, boolean preferExternal) {
		File appCacheDir = null;
		String externalStorageState;
		try {
			externalStorageState = Environment.getExternalStorageState();
		} catch (NullPointerException | IncompatibleClassChangeError e) { // (sh)it happens (Issue #660)
			externalStorageState = "";
		}
		if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		if (appCacheDir == null) {
			@SuppressLint("SdCardPath")
			String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
			appCacheDir = new File(cacheDirPath);
		}
		return appCacheDir;
	}

	private static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				return null;
			}
			try {
				// noinspection ResultOfMethodCallIgnored
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException ignored) {
			}
		}
		return appCacheDir;
	}

	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * 获取URI
	 * 
	 * @param context
	 * @param data
	 * @return
	 */
	public static Uri getImageUri(Context context, Intent data) {
		String imagePath = null;
		Uri uri = data.getData();
		if (Build.VERSION.SDK_INT >= 19) {
			if (DocumentsContract.isDocumentUri(context, uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
					String id = docId.split(":")[1];
					String selection = MediaStore.Images.Media._ID + "=" + id;
					imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
				} else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
					Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
					imagePath = getImagePath(context, contentUri, null);
				}
			} else if ("content".equalsIgnoreCase(uri.getScheme())) {
				imagePath = getImagePath(context, uri, null);
			} else if ("file".equalsIgnoreCase(uri.getScheme())) {
				imagePath = uri.getPath();
			}
		} else {
			uri = data.getData();
			imagePath = getImagePath(context, uri, null);
		}
		File file = new File(imagePath);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = FileProvider.getUriForFile(context, "com.example.mypet.fileprovider", file);
		} else {
			uri = Uri.fromFile(file);
		}

		return uri;
	}

	private static String getImagePath(Context context, Uri uri, String selection) {
		String path = null;
		Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			}
			cursor.close();
		}
		return path;
	}
}
