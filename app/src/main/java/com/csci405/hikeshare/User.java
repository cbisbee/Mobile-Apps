package com.csci405.hikeshare;

/**
 * Created by Charles on 10/25/2017.
 */


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import static com.csci405.hikeshare.Utilities.Util.*;

public class User {

    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

    public static class Lazy extends com.csci405.hikeshare.Lazy < User > {
        Lazy(Prefs.Lazy prefs) {
            super(User.class, prefs);
        }
    }

    String name() { return fbUser.getDisplayName(); }

    Prefs.Lazy mPrefs;
    User(Prefs.Lazy prefs) {
        mPrefs=prefs;
    }

    Prefs prefs() { return mPrefs.self(); }

    boolean exists(String user) {
        // valid users must be readable as an unathenticated user for this to work (firebase->db console->database [rules tab]
        //DatabaseReference mDatabase;
        // Write a message to the database
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");

        //myRef.setValue("Hello, World!");
        return false;
    }

    boolean authenticate(String user, String password) {
        switch(user) {
            case "foo@example.com": return eq("hello",password);
            case "bar@example.com": return eq("world",password);
            case "admin@localhost": return eq("secret", password);
        }
        return false;
    }

    boolean login(String user) {
        switch(user) {
            case "foo@example.com":
            case "bar@example.com":
                prefs().authenticated(true);
                prefs().user(user);
                prefs().groups("users");
                prefs().save();
                break;
            case "admin@localhost":
                prefs().authenticated(true);
                prefs().user("admin@localhost");
                prefs().groups("admins","users");
                prefs().save();
                break;
            default:
                logout();
        }
        return prefs().authenticated();
    }

    void logout() {
        prefs().authenticated(false);
        prefs().user(null);
        prefs().groups(new String[] {});
        prefs().save();
    }

    boolean authenticated() { return prefs().authenticated(); }
    String user() { return prefs().user(); }
    boolean isUser() { return prefs().groups().contains("users"); }
    boolean isAdmin() { return prefs().groups().contains("admins"); }
}