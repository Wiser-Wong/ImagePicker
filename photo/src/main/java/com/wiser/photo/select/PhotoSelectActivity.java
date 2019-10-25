package com.wiser.photo.select;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wiser.photo.PhotoConstant;
import com.wiser.photo.R;
import com.wiser.photo.dialog.FolderDialogFragment;
import com.wiser.photo.model.PhotoFolderModel;
import com.wiser.photo.model.PhotoSelectModel;
import com.wiser.photo.model.PhotoSettingData;
import com.wiser.photo.preview.PhotoPreviewActivity;
import com.wiser.photo.util.CameraTools;
import com.wiser.photo.util.CompressAsyncTask;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wiser
 * 
 *         选择图片界面
 */
public class PhotoSelectActivity extends FragmentActivity implements View.OnClickListener, CompressAsyncTask.OnCompressListener {

	private TextView			tvPhotoSelectFinish;

	private TextView			tvPhotoSelectPreview;

	private IPhotoSelectBiz		iPhotoSelectBiz;

	private PhotoSelectAdapter	photoSelectAdapter;

	public static void intent(FragmentActivity activity, int surplusCount, int spanCount, int type) {
		if (activity == null) return;
		Intent intent = new Intent(activity, PhotoSelectActivity.class);
		intent.putExtra(PhotoConstant.SURPLUS_COUNT_KEY, surplusCount);
		intent.putExtra(PhotoConstant.SPAN_COUNT_KEY, spanCount);
		intent.putExtra(PhotoConstant.SHOW_MODE_KEY, type);
		intent.putExtra("isObj", false);
		activity.startActivityForResult(intent, PhotoConstant.SELECT_PHOTO);
		activity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
	}

	public static void intent(FragmentActivity activity, int surplusCount, int spanCount) {
		if (activity == null) return;
		Intent intent = new Intent(activity, PhotoSelectActivity.class);
		intent.putExtra(PhotoConstant.SURPLUS_COUNT_KEY, surplusCount);
		intent.putExtra(PhotoConstant.SPAN_COUNT_KEY, spanCount);
		intent.putExtra("isObj", false);
		activity.startActivityForResult(intent, PhotoConstant.SELECT_PHOTO);
		activity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
	}

	public static void intent(FragmentActivity activity, PhotoSettingData photoSettingData) {
		if (activity == null) return;
		Intent intent = new Intent(activity, PhotoSelectActivity.class);
		intent.putExtra("isObj", true);
		Bundle bundle = new Bundle();
		bundle.putParcelable(PhotoConstant.SETTING_DATA_KEY, photoSettingData);
		intent.putExtras(bundle);
		activity.startActivityForResult(intent, PhotoConstant.SELECT_PHOTO);
		activity.overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_select_act);

		RecyclerView rlvPhotoSelect = findViewById(R.id.rlv_photo_select);
		tvPhotoSelectFinish = findViewById(R.id.tv_photo_select_finish);
		TextView tvPhotoCancel = findViewById(R.id.tv_photo_select_cancel);
		tvPhotoSelectPreview = findViewById(R.id.tv_photo_select_preview);
		TextView tvAllPhotoFolder = findViewById(R.id.tv_all_photo_folder);

		tvPhotoCancel.setOnClickListener(this);
		tvPhotoSelectPreview.setOnClickListener(this);
		tvPhotoSelectFinish.setOnClickListener(this);
		tvAllPhotoFolder.setOnClickListener(this);

		if (getIntent() != null) {
			if (!getIntent().getBooleanExtra("isObj", false)) {
				iPhotoSelectBiz = new PhotoSelectBiz(getIntent() != null ? getIntent().getIntExtra(PhotoConstant.SURPLUS_COUNT_KEY, 0) : 0,
						getIntent() != null ? getIntent().getIntExtra(PhotoConstant.SPAN_COUNT_KEY, PhotoConstant.DEFAULT_SPAN_COUNT) : PhotoConstant.DEFAULT_SPAN_COUNT,
						getIntent() != null && getIntent().getIntExtra(PhotoConstant.SHOW_MODE_KEY, -1) == PhotoConstant.CAMERA_MODE);
			} else {
				iPhotoSelectBiz = new PhotoSelectBiz(
						(getIntent() != null && getIntent().getExtras() != null) ? (PhotoSettingData) (getIntent().getExtras().getParcelable(PhotoConstant.SETTING_DATA_KEY)) : null);
			}
		} else iPhotoSelectBiz = new PhotoSelectBiz();

		rlvPhotoSelect.setLayoutManager(new GridLayoutManager(this, iPhotoSelectBiz.getSpanCount()));
		rlvPhotoSelect.setAdapter(photoSelectAdapter = new PhotoSelectAdapter(this));

		requestPermission();

	}

	// 申请权限
	private void requestPermission() {
		// 申请权限
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
					&& ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
				iPhotoSelectBiz.loadAllPhoto(this);
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE }, PhotoConstant.PERMISSION_REQUEST_CODE);
			}
		} else {
			iPhotoSelectBiz.loadAllPhoto(this);
		}
	}

	// 请求拍照权限
	public void requestCameraPermission() {
		// 申请权限
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
				camera();
			} else {
				ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, PhotoConstant.PERMISSION_CAMERA_REQUEST_CODE);
			}
		} else {
			camera();
		}
	}

	// 打开拍照
	private void camera() {
		File file = CameraTools.skipCamera(this);
		iPhotoSelectBiz.setOutFilePath(file != null ? file.getAbsolutePath() : "");
	}

	public IPhotoSelectBiz getBiz() {
		return iPhotoSelectBiz;
	}

	// 设置本地图片列表
	public void setPhotoData(List<PhotoSelectModel> list) {
		photoSelectAdapter.setItems(list);
	}

	@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PhotoConstant.PERMISSION_REQUEST_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				iPhotoSelectBiz.loadAllPhoto(this);
			} else {
				Toast.makeText(this, "请打开权限", Toast.LENGTH_SHORT).show();
			}
		}
		// 拍照
		if (requestCode == PhotoConstant.PERMISSION_CAMERA_REQUEST_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				camera();
			} else {
				Toast.makeText(this, "请打开拍照权限", Toast.LENGTH_SHORT).show();
			}
		}
	}

	// 更新选择图片底部按钮UI
	public void updateBtnStateUi(int count) {
		// 预览和完成
		if (photoSelectAdapter != null && photoSelectAdapter.getCount() > 0) {
			// 预览
			tvPhotoSelectPreview.setEnabled(true);
			tvPhotoSelectPreview.setTextColor(Color.WHITE);
			tvPhotoSelectPreview.setText(MessageFormat.format("预览({0})", photoSelectAdapter.getCount()));
			// 完成
			tvPhotoSelectFinish.setEnabled(true);
			tvPhotoSelectFinish.setBackgroundResource(R.drawable.photo_select_finish_shape_st);
			tvPhotoSelectFinish.setText(MessageFormat.format("完成({0}/{1})", count, iPhotoSelectBiz.getSurplusCount()));
			tvPhotoSelectFinish.setTextColor(Color.WHITE);
		} else {
			// 预览
			tvPhotoSelectPreview.setEnabled(false);
			tvPhotoSelectPreview.setTextColor(Color.parseColor("#50ffffff"));
			tvPhotoSelectPreview.setText("预览");
			// 完成
			tvPhotoSelectFinish.setEnabled(false);
			tvPhotoSelectFinish.setBackgroundResource(R.drawable.photo_select_finish_shape_df);
			tvPhotoSelectFinish.setText("完成");
			tvPhotoSelectFinish.setTextColor(Color.parseColor("#50ffffff"));
		}
	}

	@Override public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.tv_photo_select_cancel) {// 取消
			this.finish();
			overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
		} else if (id == R.id.tv_photo_select_preview) {// 预览
			PhotoPreviewActivity.intent(this, photoSelectAdapter.getSelectData(), photoSelectAdapter.getSelectData(), iPhotoSelectBiz.getSurplusCount(), 0, PhotoConstant.PREVIEW_BTN_MODE,
					iPhotoSelectBiz.isCamera(), iPhotoSelectBiz.getPhotoSettingData());
		} else if (id == R.id.tv_photo_select_finish) {// 完成
			judgeExecuteCompress();
		} else if (id == R.id.tv_all_photo_folder) {// 全部相册
			FolderDialogFragment.newInstance(iPhotoSelectBiz.getFolderModels(), new FolderDialogFragment.OnFolderClickListener() {

				@Override public void onItemFolderClick(PhotoFolderModel folderModel) {
					if (folderModel == null) return;
					if (!iPhotoSelectBiz.getCurrentFolderPathType().equals(folderModel.folderPath)) {
						iPhotoSelectBiz.setCurrentFolderPathType(folderModel.folderPath);
						if (photoSelectAdapter != null) {
							for (int i = 0; i < photoSelectAdapter.getSelectData().size(); i++) {
								PhotoSelectModel photoSelectModel = photoSelectAdapter.getSelectData().get(i);
								if (photoSelectModel != null) {
									photoSelectModel.isSelect = false;
									iPhotoSelectBiz.getModels().set(photoSelectModel.position, photoSelectModel);
								}
							}
							photoSelectAdapter.resetData();
							updateBtnStateUi(photoSelectAdapter.getCount());
							photoSelectAdapter.setItems(folderModel.folderPhotos);
						}
					}
					iPhotoSelectBiz.setModels(folderModel.folderPhotos);
					FolderDialogFragment folderDialogFragment = (FolderDialogFragment) getSupportFragmentManager().findFragmentByTag(FolderDialogFragment.class.getName());
					if (folderDialogFragment != null) folderDialogFragment.dismiss();
				}
			}).show(getSupportFragmentManager(), FolderDialogFragment.class.getName());
		}
	}

	// 执行压缩
	private void judgeExecuteCompress() {
		if (iPhotoSelectBiz.getPhotoSettingData() != null && iPhotoSelectBiz.getPhotoSettingData().isCompress) {
			new CompressAsyncTask(PhotoSelectActivity.this, this).execute(iPhotoSelectBiz.covertSelectDataStrings(photoSelectAdapter.getSelectData()));
		} else {
			complete();
		}
	}

	// 完成
	private void complete(ArrayList<String> paths) {
		Log.d(PhotoSelectActivity.class.getName(), "选择的路径：-->>" + (paths != null ? Arrays.toString(paths.toArray()) : ""));
		Intent intent = new Intent();
		intent.putStringArrayListExtra(PhotoConstant.INTENT_SELECT_PHOTO_KEY, paths);
		setResult(PhotoConstant.SELECT_PHOTO, intent);
		this.finish();
	}

	// 完成
	private void complete() {
		Log.d(PhotoSelectActivity.class.getName(), "选择的路径：-->>" + Arrays.toString(photoSelectAdapter.getSelectData().toArray()));
		Intent intent = new Intent();
		intent.putStringArrayListExtra(PhotoConstant.INTENT_SELECT_PHOTO_KEY, iPhotoSelectBiz.covertSelectDataList(photoSelectAdapter.getSelectData()));
		setResult(PhotoConstant.SELECT_PHOTO, intent);
		this.finish();
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PhotoConstant.PREVIEW_PHOTO) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					if (bundle.getInt(PhotoConstant.PREVIEW_PHOTO_COMPLETE_KEY, -1) == PhotoConstant.PREVIEW_PHOTO_COMPLETE_VALUE) {
						ArrayList<PhotoSelectModel> selectData = bundle.getParcelableArrayList(PhotoConstant.PREVIEW_PHOTO_SELECT_DATA_KEY);
						if (photoSelectAdapter != null) photoSelectAdapter.setSelectData(selectData);
						if (iPhotoSelectBiz.getPhotoSettingData() != null && iPhotoSelectBiz.getPhotoSettingData().isCompress) {
							complete(bundle.getStringArrayList(PhotoConstant.INTENT_SELECT_PHOTO_KEY));
						} else {
							complete();
						}
					} else {
						ArrayList<PhotoSelectModel> models = bundle.getParcelableArrayList(PhotoConstant.INTENT_SELECT_PHOTO_KEY);
						ArrayList<PhotoSelectModel> selectData = bundle.getParcelableArrayList(PhotoConstant.PREVIEW_PHOTO_SELECT_DATA_KEY);
						int count = bundle.getInt(PhotoConstant.SELECT_PHOTO_COUNT_KEY);
						// 添加相机
						if (iPhotoSelectBiz.isCamera() && bundle.getInt(PhotoConstant.PREVIEW_MODE_KEY) != PhotoConstant.PREVIEW_BTN_MODE && models != null)
							models.add(0, new PhotoSelectModel("", "", 0, PhotoConstant.CAMERA_MODE, 0));
						// 图片点击预览
						if (bundle.getInt(PhotoConstant.PREVIEW_MODE_KEY) == PhotoConstant.PREVIEW_PHOTO_MODE) {
							if (photoSelectAdapter != null) photoSelectAdapter.setData(models, selectData, count);
							// 点击预览按钮
						} else if (bundle.getInt(PhotoConstant.PREVIEW_MODE_KEY) == PhotoConstant.PREVIEW_BTN_MODE) {
							if (models != null && models.size() > 0) {
								for (PhotoSelectModel model : models) {
									if (model == null) continue;
									iPhotoSelectBiz.getModels().set(model.position, model);
								}
							}
							if (photoSelectAdapter != null) photoSelectAdapter.setData(iPhotoSelectBiz.getModels(), selectData, count);
						}
						updateBtnStateUi(count);
					}
				}
			}
		}
		// 拍照
		if (resultCode == RESULT_OK && requestCode == PhotoConstant.CAMERA_REQUEST_CODE) {
			if (iPhotoSelectBiz.getPhotoSettingData() != null && iPhotoSelectBiz.getPhotoSettingData().isCameraCrop) {
				Uri uri;
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) uri = Uri.fromFile(new File(iPhotoSelectBiz.getOutFilePath()));
				else uri = FileProvider.getUriForFile(this, PhotoConstant.AUTHORITY, new File(iPhotoSelectBiz.getOutFilePath()));
				CameraTools.cropPhoto(this, uri, new File(iPhotoSelectBiz.getOutFilePath()),
						iPhotoSelectBiz.getPhotoSettingData() != null ? iPhotoSelectBiz.getPhotoSettingData().cropWidth : PhotoConstant.DEFAULT_CROP_WIDTH,
						iPhotoSelectBiz.getPhotoSettingData() != null ? iPhotoSelectBiz.getPhotoSettingData().cropHeight : PhotoConstant.DEFAULT_CROP_HEIGHT, PhotoConstant.CROP_REQUEST_CODE);
			} else {
				ArrayList<String> photos = new ArrayList<>();
				photos.add(iPhotoSelectBiz.getOutFilePath());
				Intent intent = new Intent();
				intent.putStringArrayListExtra(PhotoConstant.INTENT_SELECT_PHOTO_KEY, photos);
				setResult(PhotoConstant.SELECT_PHOTO, intent);
				this.finish();
			}

		}

		// 裁剪
		if (requestCode == PhotoConstant.CROP_REQUEST_CODE) {
			ArrayList<String> photos = new ArrayList<>();
			photos.add(iPhotoSelectBiz.getOutFilePath());
			Intent intent = new Intent();
			intent.putStringArrayListExtra(PhotoConstant.INTENT_SELECT_PHOTO_KEY, photos);
			setResult(PhotoConstant.SELECT_PHOTO, intent);
			this.finish();
		}
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		if (iPhotoSelectBiz != null) iPhotoSelectBiz.onDetach();
		iPhotoSelectBiz = null;
		if (photoSelectAdapter != null) photoSelectAdapter.onDetach();
		photoSelectAdapter = null;
	}

	// 压缩完成
	@Override public void compressSuccess(ArrayList<String> paths) {
		complete(paths);
	}
}
