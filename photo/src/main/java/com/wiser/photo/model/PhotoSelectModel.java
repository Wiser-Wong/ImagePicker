package com.wiser.photo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Wiser
 *
 *         图片选择数据
 */
public class PhotoSelectModel implements Parcelable {

	public String	path;

	public String	name;

	public long		time;

	public int		type;

	public boolean	isSelect;

	public int		position;

	public PhotoSelectModel() {}

	public PhotoSelectModel(String path, String name, long time, int type, int position) {
		this.path = path;
		this.name = name;
		this.time = time;
		this.type = type;
		this.position = position;
	}

	protected PhotoSelectModel(Parcel in) {
		path = in.readString();
		name = in.readString();
		time = in.readLong();
		type = in.readInt();
		position = in.readInt();
		isSelect = in.readByte() != 0;
	}

	public static final Creator<PhotoSelectModel> CREATOR = new Creator<PhotoSelectModel>() {

		@Override public PhotoSelectModel createFromParcel(Parcel in) {
			return new PhotoSelectModel(in);
		}

		@Override public PhotoSelectModel[] newArray(int size) {
			return new PhotoSelectModel[size];
		}
	};

	@Override public int describeContents() {
		return 0;
	}

	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(path);
		dest.writeString(name);
		dest.writeLong(time);
		dest.writeInt(type);
		dest.writeInt(position);
		dest.writeByte((byte) (isSelect ? 1 : 0));
	}
}
