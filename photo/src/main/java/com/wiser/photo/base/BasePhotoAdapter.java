package com.wiser.photo.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wiser
 * @param <T>
 *            数据
 * @param <V>
 *            Holder
 *
 *            base adapter
 */
@SuppressWarnings("unchecked")
public abstract class BasePhotoAdapter<T, V extends BasePhotoHolder> extends RecyclerView.Adapter<V> {

	public final static int				HEAD		= 0;

	public final static int				END			= 1;

	public final static int				OTHER		= 2;

	public int							type		= -1;

	public int							maxCounts;

	private boolean						isShowAdd	= true;

	private LayoutInflater				mInflater;

	private Context						context;

	private List<T>						mItems;

	private OnPhotoItemClickListener	onPhotoItemClickListener;

	public BasePhotoAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	public BasePhotoAdapter(Context context, int type) {
		this.context = context;
		this.type = type;
		mInflater = LayoutInflater.from(context);
	}

	public BasePhotoAdapter(Context context, int type, int maxCounts) {
		this.context = context;
		this.type = type;
		this.maxCounts = maxCounts;
		mInflater = LayoutInflater.from(context);
	}

	public abstract V newViewHolder(ViewGroup viewGroup, int type);

	public void setItems(List<T> mItems) {
		this.mItems = mItems;
		if (type == HEAD) {
			mItems.add(0, null);
		}
		notifyDataSetChanged();
	}

	public void setItems(ArrayList<T> mItems) {
		this.mItems = mItems;
		if (type == HEAD) {
			mItems.add(0, null);
		}
		notifyDataSetChanged();
	}

	public List<T> getItems() {
		return mItems;
	}

	public T getItem(int position) {
		if (mItems == null) return null;
		return mItems.get(position);
	}

	public void addList(List<T> list) {
		if (list == null || list.size() < 1 || getItems() == null) {
			return;
		}
		int position = getItemCount();
		if (mItems.size() + list.size() > maxCounts) {
			int count = mItems.size();
			for (int i = 0; i < list.size(); i++) {
				if (type == HEAD) {
					if (count + i - 1 == maxCounts) break;
				}
				if (type == END) {
					if (count + i == maxCounts) break;
				}
				mItems.add(list.get(i));
			}
		} else {
			mItems.addAll(list);
		}
		notifyItemRangeInserted(position, list.size());

		if (maxCounts > 0) {
			if (type == BasePhotoAdapter.HEAD) {
				if (getItems().size() > maxCounts) {
					setIsShowAdd(false);
				}
			} else {
				if (getItems().size() > maxCounts - 1) {
					setIsShowAdd(false);
				}
			}
		}
	}

	public void delete(int position) {
		if (getItems() == null || position < 0 || getItems().size() <= position) {
			return;
		}
		mItems.remove(position);
		if (isShowAdd()) {
			if (type == HEAD) notifyItemRangeRemoved(position, getItemCount() + 1 - position);
			else notifyItemRangeRemoved(position, getItemCount() - position);
		} else {
			setIsShowAdd(true);
		}
	}

	public void clear() {
		mItems.clear();
		notifyDataSetChanged();
	}

	public void setIsShowAdd(boolean isShow) {
		if (type == HEAD || type == END) {
			if (isShow) {
				if (type == HEAD && mItems != null) {
					mItems.add(0, null);
				}
			} else {
				if (type == HEAD && mItems != null && mItems.size() > 0) {
					mItems.remove(0);
				}
			}
			this.isShowAdd = isShow;
			notifyDataSetChanged();
		}
	}

	public boolean isShowAdd() {
		return isShowAdd;
	}

	public Context getContext() {
		return context;
	}

	public View inflate(@LayoutRes int layoutId, ViewGroup viewGroup) {
		return mInflater.inflate(layoutId, viewGroup, false);
	}

	@NonNull @Override public V onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		V v = newViewHolder(viewGroup, i);
		v.setAdapter(this);
		return v;
	}

	@Override public void onBindViewHolder(@NonNull V v, int i) {
		if (isShowAdd) {
			switch (type) {
				case HEAD:
					if (getItems() == null || i == 0) v.bindData(null, i);
					else v.bindData(getItem(i), i);
					break;
				case END:
					if (getItems() == null || getItems().size() == i) v.bindData(null, i);
					else v.bindData(getItem(i), i);
					break;
				default:
					v.bindData(getItem(i), i);
					break;
			}
		} else v.bindData(getItem(i), i);
	}

	@Override public int getItemCount() {
		if (type == HEAD || type == END) {
			if (isShowAdd) {
				if (type == END) return getItems() == null ? 1 : getItems().size() + 1;
			}
			return getItems() == null ? 0 : getItems().size();
		}
		return getItems() == null ? 0 : getItems().size();
	}

	@Override public int getItemViewType(int position) {
		if (isShowAdd) {
			switch (type) {
				case HEAD:
					if (getItems() == null || position == 0) return HEAD;
					else return OTHER;
				case END:
					if (getItems() == null || position == getItems().size()) return END;
					else return OTHER;
				default:
					return OTHER;
			}
		} else {
			return OTHER;
		}
	}

	public OnPhotoItemClickListener getOnPhotoItemClickListener() {
		return onPhotoItemClickListener;
	}

	public void setOnPhotoItemClickListener(OnPhotoItemClickListener onPhotoItemClickListener) {
		this.onPhotoItemClickListener = onPhotoItemClickListener;
	}

	public interface OnPhotoItemClickListener {

		void onAddPhotoClick(View view, int position);

		void onItemPhotoClick(View view, int position);

		void onItemDeleteClick(View view, int position);
	}

}
