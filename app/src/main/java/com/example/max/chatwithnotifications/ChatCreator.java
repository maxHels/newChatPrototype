package com.example.max.chatwithnotifications;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Max on 13.12.2017.
 */

public class ChatCreator {
    public static String makeChatRef(ArrayList<AppUser> users)
    {
        Collections.sort(users);
        String res="";
        for (AppUser s:
                users) {
            res+=s.Uid;
        }
        return res;
    }
}
