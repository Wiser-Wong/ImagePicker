package com.wiser.photo.select;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wiser.photo.R;
import com.wiser.photo.model.PhotoFolderModel;
import com.wiser.photo.weight.PhotoSelectFolderFrameLayout;

import java.util.ArrayList;

/**
 * @author Wiser
 *
 *         图片文件夹选择
 */
public class PhotoSelectFolderFragment extends Fragment {

    public static final String		FOLDER_DATA_KEY	= "FOLDER_DATA_KEY";

    private OnFolderClickListener onFolderClickListener;

    private PhotoSelectFolderFrameLayout flFolder;

    public static PhotoSelectFolderFragment newInstance(ArrayList<PhotoFolderModel> folderModels, OnFolderClickListener onFolderClickListener) {
        PhotoSelectFolderFragment fragment = new PhotoSelectFolderFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(FOLDER_DATA_KEY, folderModels);
        fragment.setArguments(bundle);
        fragment.setOnFolderClickListener(onFolderClickListener);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_select_folder_frg,container,false);
        init(view);
        return view;
    }

    private void init(View view){
        flFolder = view.findViewById(R.id.fl_folder);
        if (getArguments() != null){
            ArrayList<PhotoFolderModel> folderModels = getArguments().getParcelableArrayList(FOLDER_DATA_KEY);
            flFolder.setData(folderModels);
            flFolder.setOnFolderClickListener(onFolderClickListener);
        }
    }

    public void handleAnim(){
        if (flFolder != null) flFolder.handleAnim();
    }

    public void setOnFolderClickListener(OnFolderClickListener onFolderClickListener) {
        this.onFolderClickListener = onFolderClickListener;
    }

    public interface OnFolderClickListener {

        void onItemFolderClick(PhotoFolderModel folderModel);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFolderClickListener = null;
    }
}
