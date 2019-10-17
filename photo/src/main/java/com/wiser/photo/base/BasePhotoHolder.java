package com.wiser.photo.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Wiser
 * Holder base
 */
public abstract class BasePhotoHolder<T> extends RecyclerView.ViewHolder {

    private BasePhotoAdapter adapter;

    public BasePhotoHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setAdapter(BasePhotoAdapter adapter) {
        this.adapter = adapter;
    }

    public BasePhotoAdapter adapter() {
        return adapter;
    }

    public abstract void bindData(T t, int position);

}
