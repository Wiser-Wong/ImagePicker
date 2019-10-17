package com.wiser.photo.edit;

import com.bumptech.glide.Glide;
import com.wiser.photo.base.BasePhotoHolder;
import com.wiser.photo.config.PhotoConfig;
import com.wiser.photo.model.PhotoShowModel;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

/**
 * @author Wiser
 *
 *         图片数据
 */
public class PhotoHolder extends BasePhotoHolder<PhotoShowModel> {

	private AppCompatImageView	ivPhoto;

	private AppCompatImageView	ivPhotoDelete;

	public PhotoHolder(@NonNull View itemView, PhotoConfig photoConfig) {
		super(itemView);
		if (photoConfig == null) return;
		ivPhoto = itemView.findViewById(photoConfig.photoResId);
		if (photoConfig.deleteResId == -1) return;
		ivPhotoDelete = itemView.findViewById(photoConfig.deleteResId);
	}

	@Override public void bindData(PhotoShowModel model, final int position) {
		if (model == null || adapter() == null) return;
		if (model.path != null && !"".equals(model.path)) {
			Glide.with(adapter().getContext()).load(model.path).centerCrop().into(ivPhoto);
		}

		itemView.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				if (adapter().getOnPhotoItemClickListener() != null) {
					adapter().getOnPhotoItemClickListener().onItemPhotoClick(itemView, position);
				}
			}
		});

		if (ivPhotoDelete == null) return;
		ivPhotoDelete.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				if (adapter().getOnPhotoItemClickListener() != null) {
					adapter().getOnPhotoItemClickListener().onItemDeleteClick(itemView, position);
				}
			}
		});
	}
}
