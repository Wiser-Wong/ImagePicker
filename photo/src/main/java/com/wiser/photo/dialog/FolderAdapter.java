package com.wiser.photo.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wiser.photo.R;
import com.wiser.photo.base.BasePhotoAdapter;
import com.wiser.photo.base.BasePhotoHolder;
import com.wiser.photo.model.PhotoFolderModel;

import java.text.MessageFormat;

/**
 * @author Wiser
 * 
 *         文件夹适配器
 */
public class FolderAdapter extends BasePhotoAdapter<PhotoFolderModel, FolderAdapter.FolderHolder> {

	private FolderDialogFragment.OnFolderClickListener onFolderClickListener;

	FolderAdapter(Context context) {
		super(context);
	}

	void setOnFolderClickListener(FolderDialogFragment.OnFolderClickListener onFolderClickListener) {
		this.onFolderClickListener = onFolderClickListener;
	}

	@Override public FolderHolder newViewHolder(ViewGroup viewGroup, int type) {
		return new FolderHolder(inflate(R.layout.photo_folder_item, viewGroup));
	}

	class FolderHolder extends BasePhotoHolder<PhotoFolderModel> {

		AppCompatImageView	ivFolderCover;

		TextView			tvFolderName;

		TextView			tvFolderPhotoCount;

		FolderHolder(@NonNull View itemView) {
			super(itemView);
			ivFolderCover = itemView.findViewById(R.id.iv_folder_photo);
			tvFolderName = itemView.findViewById(R.id.tv_folder_name);
			tvFolderPhotoCount = itemView.findViewById(R.id.tv_folder_photo_count);
		}

		@Override public void bindData(final PhotoFolderModel photoFolderModel, int position) {
			if (photoFolderModel == null) return;
			if (photoFolderModel.folderCover != null) {
				Glide.with(ivFolderCover.getContext()).load(photoFolderModel.folderCover.path).thumbnail(0.1f).centerCrop().placeholder(R.mipmap.photo_error).into(ivFolderCover);
			}
			// 文件夹名称
			tvFolderName.setText(photoFolderModel.folderName);
			// 文件夹中图片数量
			tvFolderPhotoCount.setText(MessageFormat.format("共{0}张", photoFolderModel.folderCount));

			itemView.setOnClickListener(new View.OnClickListener() {

				@Override public void onClick(View v) {
					if (onFolderClickListener != null) onFolderClickListener.onItemFolderClick(photoFolderModel);
				}
			});
		}
	}

}
