package com.wiser.photo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @author Wiser
 * 
 *         图片文件夹
 */
public class PhotoFolderModel implements Parcelable {

	public String						folderName;

	public String						folderPath;

	public int							folderCount;

	public PhotoSelectModel				folderCover;

	public ArrayList<PhotoSelectModel>	folderPhotos;

	public PhotoFolderModel() {}

	private PhotoFolderModel(Parcel in) {
		folderName = in.readString();
		folderPath = in.readString();
		folderCount = in.readInt();
		folderCover = in.readParcelable(PhotoSelectModel.class.getClassLoader());
		folderPhotos = in.createTypedArrayList(PhotoSelectModel.CREATOR);
	}

	public static final Creator<PhotoFolderModel> CREATOR = new Creator<PhotoFolderModel>() {

		@Override public PhotoFolderModel createFromParcel(Parcel in) {
			return new PhotoFolderModel(in);
		}

		@Override public PhotoFolderModel[] newArray(int size) {
			return new PhotoFolderModel[size];
		}
	};

	@Override public int describeContents() {
		return 0;
	}

	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(folderName);
		dest.writeString(folderPath);
		dest.writeInt(folderCount);
		dest.writeParcelable(folderCover, flags);
		dest.writeTypedList(folderPhotos);
	}
}
