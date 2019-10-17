package com.wiser.photo.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import com.wiser.photo.R;
import com.wiser.photo.model.PhotoFolderModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wiser
 * 
 *         文件夹列表弹窗
 */
public class FolderDialogFragment extends DialogFragment {

	public static final String		FOLDER_DATA_KEY	= "FOLDER_DATA_KEY";

	private OnFolderClickListener	onFolderClickListener;

	public static FolderDialogFragment newInstance(ArrayList<PhotoFolderModel> folderModels, OnFolderClickListener onFolderClickListener) {
		FolderDialogFragment folderDialogFragment = new FolderDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(FOLDER_DATA_KEY, folderModels);
		folderDialogFragment.setArguments(bundle);
		folderDialogFragment.setOnFolderClickListener(onFolderClickListener);
		return folderDialogFragment;
	}

	@Override public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.DefaultDialogTheme);
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (getDialog() != null) {
			// 去除标题栏
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		View view = inflater.inflate(R.layout.photo_folder_dialog, container, false);

		if (getDialog() != null) {
			// 设置点击空白处是否关闭Dialog
			getDialog().setCanceledOnTouchOutside(true);
		}

		RecyclerView rlvFolder = view.findViewById(R.id.rlv_folder);
		rlvFolder.setLayoutManager(new LinearLayoutManager(getActivity()));
		FolderAdapter folderAdapter;
		rlvFolder.setAdapter(folderAdapter = new FolderAdapter(getActivity()));
		if (getArguments() != null){
			ArrayList<PhotoFolderModel> folderModels = getArguments().getParcelableArrayList(FOLDER_DATA_KEY);
			folderAdapter.setItems(getArguments() != null ?  folderModels : null);
		}
		folderAdapter.setOnFolderClickListener(onFolderClickListener);

		return view;
	}

	public void setOnFolderClickListener(OnFolderClickListener onFolderClickListener) {
		this.onFolderClickListener = onFolderClickListener;
	}

	@Override public void onResume() {
		super.onResume();
		Window window = getDialog().getWindow();
		if (window != null && getActivity() != null) {
			WindowManager.LayoutParams wlp = window.getAttributes();
			wlp.width = (int) (getScreenWidth(getActivity()) * 0.7f);
			wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			wlp.gravity = Gravity.START;
			window.setAttributes(wlp);
		}
	}

	/**
	 * 获得屏幕的宽度
	 *
	 * @return
	 */
	public int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获得屏幕的高度
	 *
	 * @return
	 */
	public int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public interface OnFolderClickListener {

		void onItemFolderClick(PhotoFolderModel folderModel);
	}

	@Override public void onDestroy() {
		super.onDestroy();
		onFolderClickListener = null;
	}
}
