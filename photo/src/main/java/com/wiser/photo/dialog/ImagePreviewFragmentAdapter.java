package com.wiser.photo.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wiser.photo.preview.PhotoPreviewFragment;

import java.util.ArrayList;

/**
 * @author Wiser
 * 
 *         预览图片适配器
 */
public class ImagePreviewFragmentAdapter extends FragmentStatePagerAdapter {

	private ArrayList<String> paths;

	public ImagePreviewFragmentAdapter(FragmentManager fm, ArrayList<String> paths) {
		super(fm);
		this.paths = paths;
	}

	@Override public Fragment getItem(int i) {
		return PhotoPreviewFragment.newInstance(paths != null ? paths.get(i) : null);
	}

	@Override public int getCount() {
		return paths != null ? paths.size() : 0;
	}

}
