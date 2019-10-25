package com.wiser.photo.preview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.wiser.photo.PhotoConstant;
import com.wiser.photo.R;
import com.wiser.photo.dialog.ImagePreviewDialogFragment;
import com.wiser.photo.model.PhotoSelectModel;
import com.wiser.photo.photoview.PhotoView;
import com.wiser.photo.photoview.PhotoViewAttache;

/**
 * @author Wiser
 *
 *         图片加载
 */
public class PhotoPreviewFragment extends Fragment {

	private FragmentActivity activity;

	@Override public void onAttach(Context context) {
		super.onAttach(context);
		activity = (FragmentActivity) context;
	}

	public static PhotoPreviewFragment newInstance(PhotoSelectModel model) {
		PhotoPreviewFragment fragment = new PhotoPreviewFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(PhotoConstant.PREVIEW_PHOTO_PATH_KEY, model);
		fragment.setArguments(bundle);
		return fragment;
	}

	public static PhotoPreviewFragment newInstance(String path) {
		PhotoPreviewFragment fragment = new PhotoPreviewFragment();
		Bundle bundle = new Bundle();
		bundle.putString(PhotoConstant.PREVIEW_IMAGE_PATH_KEY, path);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.photo_preview_frg, container, false);
		if (getArguments() != null && getActivity() != null) {
			PhotoSelectModel model = getArguments().getParcelable(PhotoConstant.PREVIEW_PHOTO_PATH_KEY);
			if (model != null) {
				Glide.with(getActivity()).load(model.path != null ? model.path : "").thumbnail(0.1f).into((PhotoView) view.findViewById(R.id.iv_preview_photo));
			} else {
				String path = getArguments().getString(PhotoConstant.PREVIEW_IMAGE_PATH_KEY);
				Glide.with(getActivity()).load(path != null ? path : "").thumbnail(0.1f).into((PhotoView) view.findViewById(R.id.iv_preview_photo));
			}
		}

		((PhotoView) view).setOnViewTapListener(new PhotoViewAttache.OnViewTapListener() {

			@Override public void onViewTap(View view, float x, float y) {
				if (activity != null) {
					if (activity instanceof PhotoPreviewActivity) {
						((PhotoPreviewActivity) activity).updateTitleAnim();
					} else {
						ImagePreviewDialogFragment imagePreviewDialogFragment = (ImagePreviewDialogFragment) activity.getSupportFragmentManager().findFragmentByTag(ImagePreviewDialogFragment.class.getName());
						if (imagePreviewDialogFragment != null) {
							imagePreviewDialogFragment.updateTitleAnim();
						}
					}
				}
			}
		});
		return view;
	}
}
