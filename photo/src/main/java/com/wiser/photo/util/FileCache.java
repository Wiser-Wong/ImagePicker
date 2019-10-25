package com.wiser.photo.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

/**
 * @author Wiser
 * @version 版本
 */
public class FileCache extends BaseFile {

	private final String ENCODING = "utf8";

	/**
	 * @param path
	 */
	public static void initConfigureFile(String path) {
		if (!TextUtils.isEmpty(path)) {
			createFolder(path);
		}
	}

	/**
	 * @param path
	 */
	public void initConfigureCache(String path) {
		if (!TextUtils.isEmpty(path)) {
			createFolder(path);
		}
	}

	/**
	 * @param path
	 * @param folderName
	 */
	public void initStorageDirectoryFolder(String path, String folderName) {
		if (!TextUtils.isEmpty(path)) {
			createFolder(path + File.separator + folderName);
		}
	}

	/**
	 * 获取/storage/emulated/0/Android/data/com.wiser.frame/files路径
	 * 
	 * @param context
	 * @return
	 */
	public static String configureFileDir(Context context) {
		// 文件初始化
		File filesDir;
		if (isMounted()) {
			// We can read and write the media
			filesDir = context.getExternalFilesDir(null);
		} else {
			// Load another directory, probably local memory
			filesDir = context.getFilesDir();
		}
		if (filesDir != null) {
			return filesDir.getAbsolutePath();
		}
		return null;
	}

	/**
	 * 获取/storage/emulated/0/Android/data/com.wiser.frame/cache路径
	 *
	 * @param context
	 * @return
	 */
	public String configureCacheDir(Context context) {
		// 文件初始化
		File filesDir;
		if (isMounted()) {
			// We can read and write the media
			filesDir = context.getExternalCacheDir();
		} else {
			// Load another directory, probably local memory
			filesDir = context.getCacheDir();
		}
		if (filesDir != null) {
			return filesDir.getAbsolutePath();
		}
		return null;
	}

	/**
	 * 获取/storage/emulated/0文件夹 就是 我的文件/内部存储 的路径
	 *
	 * @return
	 */
	public String configureStorageDir() {
		// 文件初始化
		File filesDir = null;
		if (isMounted()) {
			// We can read and write the media
			filesDir = Environment.getExternalStorageDirectory();
		}
		if (filesDir != null) {
			return filesDir.getAbsolutePath();
		}
		return null;
	}

	/**
	 * @param filePath
	 * @param nameSuffix
	 *            参数
	 * @return 返回值
	 */
	private String pathForCacheEntry(String filePath, String nameSuffix) {
		return filePath + File.separator + nameSuffix;
	}

	public static void delete(String path) {
		File file = new File(path);
		if (file.exists()) file.delete();
	}

	/**
	 * check if there is a cache file with fileName
	 *
	 * @param filePath
	 * @param fileName
	 *            the name of the file
	 * @return true if the file exits, false otherwise
	 */
	public boolean hasCache(String filePath, String fileName) {
		return new File(pathForCacheEntry(filePath, fileName)).exists();
	}
}
