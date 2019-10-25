package com.wiser.photo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Wiser
 * 
 *         图片设置数据
 */
public class PhotoSettingData implements Parcelable {

	public int		surplusCount;

	public int		spanCount;

	public int		type;

	public String	compressPath;

	public boolean	isCompress;

	public int		compressWidth;

	public int		compressHeight;

	public int		compressQuality;

	public boolean	isCameraCrop;

	public int		cropWidth;

	public int		cropHeight;

	public PhotoSettingData(int surplusCount, int spanCount, int type, String compressPath, boolean isCompress, int compressQuality, int compressWidth, int compressHeight, boolean isCameraCrop,
			int cropWidth, int cropHeight) {
		this.surplusCount = surplusCount;
		this.spanCount = spanCount;
		this.type = type;
		this.compressPath = compressPath;
		this.isCompress = isCompress;
		this.compressQuality = compressQuality;
		this.compressWidth = compressWidth;
		this.compressHeight = compressHeight;
		this.isCameraCrop = isCameraCrop;
		this.cropWidth = cropWidth;
		this.cropHeight = cropHeight;
	}

	public PhotoSettingData() {}

	private PhotoSettingData(Parcel in) {
		surplusCount = in.readInt();
		spanCount = in.readInt();
		type = in.readInt();
		compressPath = in.readString();
		isCompress = in.readByte() != 0;
		compressWidth = in.readInt();
		compressHeight = in.readInt();
		compressQuality = in.readInt();
		isCameraCrop = in.readByte() != 0;
		cropWidth = in.readInt();
		cropHeight = in.readInt();
	}

	public static final Creator<PhotoSettingData> CREATOR = new Creator<PhotoSettingData>() {

		@Override public PhotoSettingData createFromParcel(Parcel in) {
			return new PhotoSettingData(in);
		}

		@Override public PhotoSettingData[] newArray(int size) {
			return new PhotoSettingData[size];
		}
	};

	@Override public int describeContents() {
		return 0;
	}

	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(surplusCount);
		dest.writeInt(spanCount);
		dest.writeInt(type);
		dest.writeString(compressPath);
		dest.writeByte((byte) (isCompress ? 1 : 0));
		dest.writeInt(compressWidth);
		dest.writeInt(compressHeight);
		dest.writeInt(compressQuality);
		dest.writeByte((byte) (isCameraCrop ? 1 : 0));
		dest.writeInt(cropWidth);
		dest.writeInt(cropHeight);
	}
}
