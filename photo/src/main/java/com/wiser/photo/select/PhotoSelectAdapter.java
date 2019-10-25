package com.wiser.photo.select;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wiser.photo.PhotoConstant;
import com.wiser.photo.R;
import com.wiser.photo.base.BasePhotoAdapter;
import com.wiser.photo.base.BasePhotoHolder;
import com.wiser.photo.model.PhotoSelectModel;
import com.wiser.photo.preview.PhotoPreviewActivity;
import com.wiser.photo.weight.SquareFrameLayout;
import com.wiser.photo.weight.SquaredImageView;

import java.util.ArrayList;

/**
 * @author Wiser
 * 
 *         图片选择适配器
 */
public class PhotoSelectAdapter extends BasePhotoAdapter<PhotoSelectModel, BasePhotoHolder> {

	private int							count;

	private ArrayList<PhotoSelectModel>	selectData;

	private int							surplusCount;

	public ArrayList<PhotoSelectModel> getSelectData() {
		return selectData;
	}

	public int getCount() {
		return count;
	}

	// 重置数据
	public void resetData() {
		this.count = 0;
		if (selectData != null) selectData.clear();
	}

	public PhotoSelectAdapter(Context context) {
		super(context);
		if (this.selectData == null) this.selectData = new ArrayList<>();
		count = selectData.size();
		surplusCount = ((PhotoSelectActivity) context).getBiz().getSurplusCount();
	}

	public void setData(ArrayList<PhotoSelectModel> models, ArrayList<PhotoSelectModel> selectData, int count) {
		this.selectData = selectData;
		if (this.selectData == null) this.selectData = new ArrayList<>();
		this.count = count;
		setItems(models);
	}

	public void setSelectData(ArrayList<PhotoSelectModel> selectData) {
		this.selectData = selectData;
	}

	@Override public int getItemViewType(int position) {
		if (getItem(position).type == PhotoConstant.CAMERA_MODE) {
			return PhotoConstant.CAMERA_MODE;
		}
		return super.getItemViewType(position);
	}

	@Override public BasePhotoHolder<PhotoSelectModel> newViewHolder(ViewGroup viewGroup, int type) {
		if (type == PhotoConstant.CAMERA_MODE) return new CameraHolder(inflate(R.layout.photo_camera_layout, viewGroup));
		return new PhotoSelectHolder(inflate(R.layout.photo_select_item, viewGroup));
	}

	class PhotoSelectHolder extends BasePhotoHolder<PhotoSelectModel> {

		AppCompatImageView	ivSelectPhoto;

		SquaredImageView	ivPhotoSelectCheck;

		SquareFrameLayout	flCover;

		PhotoSelectHolder(@NonNull View itemView) {
			super(itemView);
			ivSelectPhoto = itemView.findViewById(R.id.iv_select_photo);
			ivPhotoSelectCheck = itemView.findViewById(R.id.iv_photo_select_check);
			flCover = itemView.findViewById(R.id.fl_cover);
		}

		@Override public void bindData(final PhotoSelectModel photoSelectModel, final int position) {
			if (photoSelectModel == null) return;

			final IPhotoSelectBiz iPhotoSelectBiz = ((PhotoSelectActivity) getContext()).getBiz();
			if (iPhotoSelectBiz != null) {
				iPhotoSelectBiz.getModels().get(position).position = position;

				// 预览图片
				ivSelectPhoto.setOnClickListener(new View.OnClickListener() {

					@Override public void onClick(View v) {
						PhotoPreviewActivity.intent((FragmentActivity) getContext(), (ArrayList<PhotoSelectModel>) getItems(), selectData, surplusCount, position, PhotoConstant.PREVIEW_PHOTO_MODE,
								iPhotoSelectBiz.isCamera(), iPhotoSelectBiz.getPhotoSettingData());
					}
				});
			}

			Glide.with(ivSelectPhoto.getContext()).load(photoSelectModel.path).thumbnail(0.1f).centerCrop().into(ivSelectPhoto);

			if (photoSelectModel.isSelect) {
				ivPhotoSelectCheck.setBackgroundResource(R.mipmap.photo_selected);
				flCover.setVisibility(View.GONE);
			} else {
				ivPhotoSelectCheck.setBackgroundResource(R.mipmap.photo_unselected);
				if (((PhotoSelectActivity) getContext()).getBiz().getSurplusCount() == count) {
					flCover.setVisibility(View.VISIBLE);
				} else {
					flCover.setVisibility(View.GONE);
				}
			}

			if (surplusCount <= 0) return;
			ivPhotoSelectCheck.setOnClickListener(new View.OnClickListener() {

				@Override public void onClick(View v) {
					if (count >= surplusCount && !photoSelectModel.isSelect) {
						Toast.makeText(getContext(), "你最多只能选择" + surplusCount + "张图片", Toast.LENGTH_SHORT).show();
						return;
					}
					PhotoSelectModel photoSelectModel1 = getItem(getAdapterPosition());
					if (photoSelectModel1.isSelect) {
						photoSelectModel1.isSelect = false;
						if (count > 0) {
							count--;
						}
						remove(photoSelectModel1);
					} else {
						photoSelectModel1.isSelect = true;
						count++;
						selectData.add(photoSelectModel1);
					}
					getItems().set(position, photoSelectModel1);
					notifyItemChanged(position);
					((PhotoSelectActivity) getContext()).updateBtnStateUi(count);

					// 当剩余可选 等于正在选的数量时 更新使其他置灰不可选
					if (surplusCount == count) {
						notifyDataSetChanged();
						// 这步的作用是减少全部刷新 只有当已经剩余等于选择的时候 再次点击反选的时候更新全部，其他情况不会更新全部¬
					} else if (!photoSelectModel1.isSelect && surplusCount == count + 1) {
						notifyDataSetChanged();
					}
				}
			});

		}
	}

	// 相册
	class CameraHolder extends BasePhotoHolder<PhotoSelectModel> {

		TextView tvCamera;

		CameraHolder(@NonNull View itemView) {
			super(itemView);
			tvCamera = itemView.findViewById(R.id.tv_camera_photo);
		}

		@Override public void bindData(PhotoSelectModel photoSelectModel, int position) {
			itemView.setOnClickListener(new View.OnClickListener() {

				@Override public void onClick(View v) {
					((PhotoSelectActivity) getContext()).requestCameraPermission();
				}
			});
		}
	}

	public void onDetach() {
		if (selectData != null) selectData.clear();
		selectData = null;
	}

	// 移除选择
	private void remove(PhotoSelectModel photoSelectModel) {
		if (photoSelectModel == null || selectData == null || selectData.size() == 0) return;
		for (PhotoSelectModel model : selectData) {
			if (model == null || model.path == null) continue;
			if (model.path.equals(photoSelectModel.path)) {
				selectData.remove(model);
				break;
			}
		}
	}

}
