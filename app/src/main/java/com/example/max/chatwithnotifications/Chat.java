package com.example.max.chatwithnotifications;

import java.util.ArrayList;

/**
 * Created by Max on 01.12.2017.
 */

public class Chat {
    public Chat(String title, String reference, String lastMessage, String photoUrl,
                ArrayList<AppUser>chatParticipants) {
        Title = title;
        Reference = reference;
        LastMessage=lastMessage;
        PhotoUrl=photoUrl;
        ChatParticipants=chatParticipants;
    }

    public  Chat(){}

    public String Title;
    public String Reference;
    public String LastMessage;
    public String PhotoUrl;
    public ArrayList<AppUser> ChatParticipants;
}
