package com.wiser.photo.preview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.wiser.photo.R;
import com.wiser.photo.base.BasePhotoAdapter;
import com.wiser.photo.base.BasePhotoHolder;
import com.wiser.photo.model.PhotoSelectModel;
import com.wiser.photo.weight.SquareFrameLayout;
import com.wiser.photo.weight.SquaredImageView;

/**
 * @author Wiser
 * 
 *         预览小图片数据展示
 */
public class PreviewSmallPhotoAdapter extends BasePhotoAdapter<PhotoSelectModel, BasePhotoHolder> {

	private int index = -1;

	PreviewSmallPhotoAdapter(Context context) {
		super(context);
	}

	void setIndex(int index) {
		this.index = index;
		notifyDataSetChanged();
	}

	@Override public BasePhotoHolder newViewHolder(ViewGroup viewGroup, int type) {
		return new PreviewSmallHolder(inflate(R.layout.photo_small_item, viewGroup));
	}

	class PreviewSmallHolder extends BasePhotoHolder<PhotoSelectModel> {

		SquaredImageView ivSmallPhoto;

		SquareFrameLayout flSmallFrame;

		PreviewSmallHolder(@NonNull View itemView) {
			super(itemView);
			ivSmallPhoto = itemView.findViewById(R.id.iv_small_photo);
			flSmallFrame = itemView.findViewById(R.id.fl_small_frame);
		}

		@Override public void bindData(final PhotoSelectModel photoSelectModel, int position) {
			if (photoSelectModel == null) return;
			Glide.with(ivSmallPhoto.getContext()).load(photoSelectModel.path).thumbnail(0.1f).centerCrop().into(ivSmallPhoto);

			if (index == photoSelectModel.position) flSmallFrame.setBackgroundResource(R.drawable.shape_small_frame_st);
			else flSmallFrame.setBackgroundResource(0);

			ivSmallPhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((PhotoPreviewActivity) getContext()).setPageItem(photoSelectModel);
				}
			});
		}
	}
}
