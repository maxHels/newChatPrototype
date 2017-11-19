package com.example.max.chatwithnotifications;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Max on 17.11.2017.
 */

public class ArrayUsersAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<AppUser> users;

    public ArrayUsersAdapter(Context context, ArrayList<AppUser> users)
    {
        this.context=context;
        this.users=users;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.users.size();
    }

    @Override
    public Object getItem(int i) {
        return this.users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View temp=view;
        if(temp==null)
            temp=inflater.inflate(R.layout.user_temp,viewGroup,false);

        AppUser user=(AppUser) getItem(i);

        TextView textView = (TextView) temp.findViewById(R.id.user_name);
        textView.setText(user.Name);
        ImageView imageView=(ImageView)temp.findViewById(R.id.user_photo);
        Uri uri=Uri.parse(user.PhotoUrl);
        imageView.setImageURI(uri);
        return temp;
    }
}
