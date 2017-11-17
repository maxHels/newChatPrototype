package com.example.max.chatwithnotifications;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private final String USERS_TABLE="USERS_TABLE";
    private DatabaseReference databaseReference;
    private ArrayList<AppUser> users;
    private AppUser user;
    private ListView usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth=FirebaseAuth.getInstance();
        mFirebaseUser=mFirebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        usersList= findViewById(R.id.users_list);
        users= new ArrayList<>();
        ValueEventListener usersListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                      users.add(dataSnapshot.getValue(AppUser.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(usersListener);

        if(mFirebaseUser==null)
        {
            startActivity(new Intent(this,GoogleSignIn.class));
            finish();
            return;
        }
        else
        {

        }

        user=new AppUser(mFirebaseUser.getDisplayName(),mFirebaseUser.getUid(),
                mFirebaseUser.getPhotoUrl().toString());

        if(!users.contains(user)) {
            databaseReference.child(USERS_TABLE).push().setValue(user);
            users.add(user);
        }
        usersList.setAdapter(new ArrayUsersAdapter(this,users));
    }
}
