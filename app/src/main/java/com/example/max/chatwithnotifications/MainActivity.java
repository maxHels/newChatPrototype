package com.example.max.chatwithnotifications;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    private ArrayList<String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth=FirebaseAuth.getInstance();
        mFirebaseUser=mFirebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        users= new ArrayList<>();
        users.add(mFirebaseUser.getUid());
        /*ValueEventListener usersListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                      users.add(dataSnapshot.getValue(FirebaseUser.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(usersListener);
    */
        if(!users.contains(mFirebaseUser.getUid())) {
            databaseReference.child(USERS_TABLE).push().setValue(mFirebaseUser.getUid());
        }

        if(mFirebaseUser==null)
        {
            startActivity(new Intent(this,GoogleSignIn.class));
            finish();
            return;
        }
        else
        {
            Toast.makeText(this,"Already signed",Toast.LENGTH_SHORT);
        }
    }
}
