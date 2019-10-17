package com.wiser.photo.preview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wiser.photo.model.PhotoSelectModel;

import java.util.ArrayList;

/**
 * @author Wiser
 * 
 *         预览图片适配器
 */
public class PhotoPreviewFragmentAdapter extends FragmentStatePagerAdapter {

	private ArrayList<PhotoSelectModel> models;

	PhotoPreviewFragmentAdapter(FragmentManager fm, ArrayList<PhotoSelectModel> models) {
		super(fm);
		this.models = models;
	}

	@Override public Fragment getItem(int i) {
		return PhotoPreviewFragment.newInstance(models != null ? models.get(i) : null);
	}

	@Override public int getCount() {
		return models != null ? models.size() : 0;
	}
}
