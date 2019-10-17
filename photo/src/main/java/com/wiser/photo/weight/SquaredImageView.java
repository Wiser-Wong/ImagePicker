package com.wiser.photo.weight;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author Wiser
 * 自定义IV
 */
public class SquaredImageView extends android.support.v7.widget.AppCompatImageView {

	public SquaredImageView(Context context) {
		super(context);
	}

	public SquaredImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}
}
