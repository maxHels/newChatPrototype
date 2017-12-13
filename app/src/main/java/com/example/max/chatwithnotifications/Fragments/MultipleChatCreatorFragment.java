package com.example.max.chatwithnotifications.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max.chatwithnotifications.Act;
import com.example.max.chatwithnotifications.AppUser;
import com.example.max.chatwithnotifications.ArrayUsersAdapter;
import com.example.max.chatwithnotifications.Chat;
import com.example.max.chatwithnotifications.ChatCreator;
import com.example.max.chatwithnotifications.R;
import com.example.max.chatwithnotifications.UserLoader;
import com.example.max.chatwithnotifications.UsersAdapterWithCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultipleChatCreatorFragment extends Fragment {

    private final static String imageUrl="http://bipbap.ru/wp-content/uploads/2017/04/555946b1b027f7626674951f6f236e9198f06211.jpg";
    private ListView usersLv;
    private TextView chatTitle;
    private Button createChatButton;
    private ArrayList<AppUser> users;
    private AppUser user;
    private final static String USERS_REF="USERS_TABLE";
    private FirebaseDatabase database;
    private DatabaseReference userRef, chatsRef;

    public MultipleChatCreatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new AppUser(FirebaseAuth.getInstance().getCurrentUser());
        database=FirebaseDatabase.getInstance();
        userRef=database.getReference(USERS_REF);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = UserLoader.getAllAppUsers(dataSnapshot);
                users.remove(user);
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUI() {
        usersLv.setAdapter(new UsersAdapterWithCheckBox(getContext(),users));
    }

    private int getItemsCount()
    {
        int count=0;
        for (int i=0;i<usersLv.getCount();i++) {
            AppUser user=(AppUser) usersLv.getItemAtPosition(i);
            if(user.box)
                count++;
        }
        return count;
    }

    private ArrayList<AppUser> getAllCheckedItems()
    {
        ArrayList<AppUser>allUs=new ArrayList<>();
        for (int i=0;i<usersLv.getCount();i++) {
            AppUser user=(AppUser) usersLv.getItemAtPosition(i);
            if(user.box)
                allUs.add(user);
        }
        return allUs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_multiple_chat_creator, container, false);
        usersLv=v.findViewById(R.id.usersToSelect);
        chatTitle=v.findViewById(R.id.chatTitle);
        createChatButton=v.findViewById(R.id.create_chat);
        createChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChat();
            }
        });
        return v;
    }

    private void createChat() {
        ArrayList<AppUser> users=getAllCheckedItems();
        users.add(new AppUser(FirebaseAuth.getInstance().getCurrentUser()));
        Chat chat=new Chat(chatTitle.getText().toString(), ChatCreator.makeChatRef(users),
                null,imageUrl,users);
        launchAct(chat);
    }

    private void launchAct(Chat chat) {
        Intent intent=new Intent(getContext(), Act.class);
        intent.putExtra("chat",chat);
        getContext().startActivity(intent);
    }

}
