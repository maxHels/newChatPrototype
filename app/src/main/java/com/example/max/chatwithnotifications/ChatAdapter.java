package com.example.max.chatwithnotifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Max on 10.12.2017.
 */

public class ChatAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Chat> chats;
    private Context context;

    public ChatAdapter(ArrayList<Chat> chats, Context context) {
        this.chats = chats;
        this.context = context;
        inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int i) {
        return chats.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View temp=view;
        if(temp==null)
            temp=inflater.inflate(R.layout.chat_item,viewGroup,false);
        Chat chat=(Chat)getItem(i);
        TextView name=temp.findViewById(R.id.nameTv);
        name.setText(chat.Title);
        TextView message=temp.findViewById(R.id.messageTv);
        if(chat.LastMessage!=null)
            message.setText(chat.LastMessage);
        if(chat.PhotoUrl!=null) {
            ImageView img = temp.findViewById(R.id.photoIv);
            new ImageDownloader(img).execute(chat.PhotoUrl);
        }
        return temp;
    }
}
