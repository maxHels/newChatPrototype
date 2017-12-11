package com.example.max.chatwithnotifications;

/**
 * Created by Max on 01.12.2017.
 */

public class Chat {
    public Chat(String title, String reference, String lastMessage, String photoUrl) {
        Title = title;
        Reference = reference;
        LastMessage=lastMessage;
        PhotoUrl=photoUrl;
    }

    public  Chat(){}

    public String Title;
    public String Reference;
    public String LastMessage;
    public String PhotoUrl;
}
