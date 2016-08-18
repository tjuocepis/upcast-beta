package com.example.titusjuocepis.upcastbeta;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by titusjuocepis on 6/4/16.
 */
public class UserManager {

    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private static User currentUser;

    private boolean userExists = false;

    private final String FIREBASE_URL = "https://upcast-beta.firebaseio.com";

    private UserManager() {
    }

    public User getCurrentUser() {
        return  currentUser;
    }

    public static String userEmail() {
        return currentUser.getEmail();
    }

    public static String key() {
        return currentUser.getKey();
    }

    public void addUser(User user) {
        currentUser = user;
        AddUserToFirebase addUserTask = new AddUserToFirebase();
        addUserTask.execute(user);
    }

    private class AddUserToFirebase extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... params) {

            Firebase usersRef = new Firebase(FIREBASE_URL+"/users");

            User user = params[0];
            String[] tokens = user.getEmail().split("\\@");

            usersRef.child(tokens[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("[FIREBASE] : ", "User exists!");
                        userExists = true;
                    }
                    else
                        Log.d("[FIREBASE] : ", "User does not exist!");
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

            if (!userExists) {
                Firebase usersRef = new Firebase(FIREBASE_URL+"/users");
                String[] tokens = user.getEmail().split("\\@");
                usersRef.push();
                usersRef.child(tokens[0]).setValue(user);
                Log.d("[FIREBASE] : ", "User finished adding to Firebase!");
            }
        }
    }
}
