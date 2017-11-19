package com.example.max.chatwithnotifications;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private final String USERS_TABLE="USERS_TABLE";
    private DatabaseReference databaseReference;
    private  FirebaseDatabase database;
    private ArrayList<AppUser> users;
    private AppUser user;
    private ListView usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database=FirebaseDatabase.getInstance();
        mFirebaseAuth=FirebaseAuth.getInstance();
        mFirebaseUser=mFirebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference(USERS_TABLE);
        usersList= findViewById(R.id.users_list);

        if(mFirebaseUser==null)
        {
            startActivity(new Intent(this,GoogleSignIn.class));
            finish();
            database.setPersistenceEnabled(true);
            return;
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new ArrayList<AppUser>();
                try {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        AppUser user = postSnapshot.getValue(AppUser.class);
                        users.add(user);
                    }
                    //users = (HashMap<Object, AppUser>)dataSnapshot.getChildren();
                    updateUI();
                }
                catch (Exception e)
                {
                    Log.d("d",e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void updateUI()
    {
        user=new AppUser(mFirebaseUser.getDisplayName(),mFirebaseUser.getUid(),
                mFirebaseUser.getPhotoUrl().toString());

        /*if(users==null)
            users=new HashMap<Object, AppUser>();
        ArrayList<AppUser> otherUsers= new ArrayList<AppUser>();
        otherUsers.addAll(users.values());
        */
        /*if(!otherUsers.contains(user))
        {
            databaseReference.child(USERS_TABLE).push().setValue(user);
        }
        else
        {
            //otherUsers.remove(user);
        }*/

        usersList.setAdapter(new ArrayUsersAdapter(this,users));
    }
}
