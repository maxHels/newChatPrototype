package com.example.max.chatwithnotifications;

import android.net.Uri;

/**
 * Created by Max on 17.11.2017.
 */

public class AppUser {
    public String Name;
    public String Uid;
    public String PhotoUrl;
    public AppUser(){}
    public AppUser(String Name, String Uid,String PhotoUrl)
    {
        this.Name=Name;
        this.Uid=Uid;
        this.PhotoUrl=PhotoUrl;
    }

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
}
