package com.hangbui.booktrade;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {

    private String id;
    private String email;
    private String name;
    private String photoUrl;
    private String university;

    public User(String id, String email, String name, String photoUrl, String university) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
        this.university = university;
    }

    protected User(Parcel in) {
        id = in.readString();
        email = in.readString();
        name = in.readString();
        photoUrl = in.readString();
        university = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User() {
        id = "";
        email = "";
        name = "";
        photoUrl = "";
        university = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(photoUrl);
        parcel.writeString(university);
    }
}


