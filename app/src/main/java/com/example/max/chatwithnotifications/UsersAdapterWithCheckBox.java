package com.example.max.chatwithnotifications;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

public class UsersAdapterWithCheckBox extends ArrayUsersAdapter
{
    private CheckBox checkBox;
    private ArrayList<AppUser> users;
    public UsersAdapterWithCheckBox(Context context, ArrayList<AppUser> users) {
        super(context, users);
        this.users=users;
    }


    public boolean getCheckBoxStatement(int i)
    {
        return users.get(i).box;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View temp=super.getView(i, view, viewGroup);
        checkBox=temp.findViewById(R.id.add_user);
        checkBox.setEnabled(true);
        checkBox.setClickable(true);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppUser user=users.get(i);
                if(b)
                    user.box=true;
                else
                    user.box=false;
                users.set(i,user);
            }
        });
        return temp;
    }
}
