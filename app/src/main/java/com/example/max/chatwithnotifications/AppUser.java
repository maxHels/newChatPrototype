package com.example.max.chatwithnotifications;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Contacts;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Max on 17.11.2017.
 */

public class AppUser implements Parcelable, Comparable<AppUser> {
    public String Name;
    //variable used only for checkBox
    public boolean box=false;
    public String Uid;
    public String PhotoUrl;
    public AppUser(){}
    public AppUser(String Name, String Uid,String PhotoUrl)
    {
        this.Name=Name;
        this.Uid=Uid;
        this.PhotoUrl=PhotoUrl;
    }
    public AppUser(FirebaseUser user)
    {
        Name=user.getDisplayName();
        Uid=user.getUid();
        PhotoUrl=user.getPhotoUrl().toString();
    }

    protected AppUser(Parcel in) {
        Name = in.readString();
        Uid = in.readString();
        PhotoUrl = in.readString();
    }

    public static final Creator<AppUser> CREATOR = new Creator<AppUser>() {
        @Override
        public AppUser createFromParcel(Parcel in) {
            return new AppUser(in);
        }

        @Override
        public AppUser[] newArray(int size) {
            return new AppUser[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AppUser)
        {
        AppUser user=(AppUser)obj;
        return Uid.equals(user.Uid);
        }
        return false;
    }



    @Override
    public String toString() {
        return Name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeString(Uid);
        parcel.writeString(PhotoUrl);
    }

    @Override
    public int compareTo(@NonNull AppUser appUser) {
        return this.Uid.compareTo(appUser.Uid);
    }
}
