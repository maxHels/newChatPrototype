package com.example.max.chatwithnotifications.DbConnector;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Max on 19.11.2017.
 */

final class  FirebaseUserLoader {
    static FirebaseUser getCurrentFirebaseUser()
    {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
