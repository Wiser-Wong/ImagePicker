package com.wiser.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wiser.photo.PhotoConstant;
import com.wiser.photo.grid.PhotoGridView;
import com.wiser.photo.select.PhotoSelectActivity;

public class PhotoAct extends AppCompatActivity {

	private PhotoGridView gvPhoto;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gvPhoto = findViewById(R.id.gv_photo);

		gvPhoto.setMaxCounts(9);

		gvPhoto.setCompressPath(Environment.getExternalStorageDirectory()+"/compress");

//		gvPhoto.setOnPhotoGridListener(new PhotoGridView.OnPhotoGridListener() {
//			@Override
//			public void onAddClick(View view, int position) {
//
//			}
//
//			@Override
//			public void onItemClick(View view, int position) {
//
//			}
//
//			@Override
//			public void onDeleteClick(View view, int position) {
//
//			}
//		});
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PhotoConstant.SELECT_PHOTO) {
			if (data != null) gvPhoto.setPhotoData(data.getStringArrayListExtra(PhotoConstant.INTENT_SELECT_PHOTO_KEY));
		}
	}

	public void openPhoto(View view) {
		PhotoSelectActivity.intent(this, 10, 4,PhotoConstant.CAMERA_MODE);
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		gvPhoto.onDetach();
	}
}
