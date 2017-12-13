package com.example.max.chatwithnotifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Created by Max on 26.11.2017.
 */

public class UserLoader {
    public static FirebaseUser getCurrentFirebaseUser(Context context, Activity activity)
    {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if(user==null)
        {
            context.startActivity(new Intent(context,GoogleSignIn.class));
            activity.finish();
        }
        return user;
    }

    @NonNull
    public static AppUser getCurrentAppUser(@NonNull FirebaseUser firebaseUser)
    {
        return new AppUser(firebaseUser.getDisplayName(),firebaseUser.getUid(),firebaseUser.getPhotoUrl().toString());
    }

    public static void signOutFromFirebase(Context context,
                                    FragmentActivity activity, GoogleApiClient.OnConnectionFailedListener listener)
    {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.signOut();
        getCurrentFirebaseUser(context,activity);
    }

    public static ArrayList<AppUser> getAllAppUsers(DataSnapshot snapshot)
    {
        ArrayList<AppUser> users= new ArrayList<>();
        for (DataSnapshot userSnapshot:
             snapshot.getChildren()) {
            users.add(userSnapshot.getValue(AppUser.class));
        }
        return users;
    }
}
