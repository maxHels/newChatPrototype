package com.example.max.chatwithnotifications;

import android.net.Uri;

/**
 * Created by Max on 17.11.2017.
 */

public class AppUser {
    String Name;
    String Uid;
    String PhotoUrl;
    public AppUser(){}
    public AppUser(String Name, String Uid,String PhotoUrl)
    {
        this.Name=Name;
        this.Uid=Uid;
        this.PhotoUrl=PhotoUrl;
    }

}
