package com.wiser.photo.edit;

import android.support.annotation.NonNull;
import android.view.View;

import com.bumptech.glide.Glide;
import com.wiser.photo.R;
import com.wiser.photo.base.BasePhotoHolder;
import com.wiser.photo.model.PhotoShowModel;
import com.wiser.photo.weight.SquaredImageView;

/**
 * @author Wiser
 *
 *         图片数据
 */
public class PhotoHolder extends BasePhotoHolder<PhotoShowModel> {

	private SquaredImageView	ivPhoto;

	private SquaredImageView	ivPhotoDelete;

	public PhotoHolder(@NonNull View itemView) {
		super(itemView);
		ivPhoto = itemView.findViewById(R.id.iv_photo);
		ivPhotoDelete = itemView.findViewById(R.id.iv_photo_delete);
	}

	@Override public void bindData(PhotoShowModel model, final int position) {
		if (model == null) return;
		if (model.path != null && !"".equals(model.path)) {
			Glide.with(ivPhoto.getContext()).load(model.path).centerCrop().into(ivPhoto);
		}

		itemView.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				if (adapter().getOnPhotoItemClickListener() != null) {
					adapter().getOnPhotoItemClickListener().onItemPhotoClick(itemView, position);
				}
			}
		});

		ivPhotoDelete.setOnClickListener(new View.OnClickListener() {

			@Override public void onClick(View v) {
				if (adapter().getOnPhotoItemClickListener() != null) {
					adapter().getOnPhotoItemClickListener().onItemDeleteClick(itemView, position);
				}
			}
		});
	}
}
