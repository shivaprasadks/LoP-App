package com.think.myopencart.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Banner implements Parcelable {

    private String banner_image_id;
    private String banner_id;
    private String link;
    private String title;
    private String image;

    protected Banner(Parcel in) {
        banner_image_id = in.readString();
        banner_id = in.readString();
        link = in.readString();
        title = in.readString();
        image = in.readString();
    }

    public static final Creator<Banner> CREATOR = new Creator<Banner>() {
        @Override
        public Banner createFromParcel(Parcel in) {
            return new Banner(in);
        }

        @Override
        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };

    public String getLink() {
        return link;
    }

    public String getBanner_image_id() {
        return banner_image_id;
    }

    public String getBanner_id() {
        return banner_id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(banner_image_id);
        dest.writeString(banner_id);
        dest.writeString(link);
        dest.writeString(title);
        dest.writeString(image);
    }
}
