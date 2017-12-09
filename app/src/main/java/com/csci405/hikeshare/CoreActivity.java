package com.csci405.hikeshare;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Charles on 10/25/2017.
 */

public class CoreActivity extends AppCompatActivity implements AutoCloseable {
    private static CoreActivity activity;
    public static CoreActivity activity() { return activity; }
    public static final String TAG = "HikeShare";
    Model.Lazy mModel = new Model.Lazy(this);
    @Override public void close() { mModel.close(); }
    @Override public void onDestroy() {
        close();
        super.onDestroy();
    }
    //public StorageReference mStorageRef;


    public Prefs prefs() { return mModel.self().prefs(); }
    LiteDB liteDB() { return mModel.self().liteDB(); }
    User user() { return mModel.self().user(); }
    //Courses courses() { return mModel.self().courses(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        initFilesIfMissing();
        mAuth = FirebaseAuth.getInstance();
        //mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignIn();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    onSignOut();
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    void onSignIn() {

    }

    void onSignOut() {

    }
    public FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthListener;


    void initFile(int id, String file)  {
        InputStream in = null;
        OutputStream out = null;
        try {
            File dir = new File(getApplicationInfo().dataDir);
            File partFile = new File(dir, file + ".part");
            out = new FileOutputStream(partFile);
            in = getResources().openRawResource(id);
            byte[] block = new byte[1024];
            for (; ; ) {
                int status = in.read(block);
                if (status < 0) break;
                out.write(block,0,status);
            }
            out.close();
            partFile.renameTo(new File(dir,file));
            Log.i(TAG,"created file " + file + " in " + dir);
        } catch (IOException ex) {
            Log.i(TAG, "io error on file " + file + " id " + id + ": " + ex);
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                Log.i(TAG, "io error on file " + file + " id " + id + ": " + e);
            }
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                Log.i(TAG, "io error on file " + file + " id " + id + ": " + e);
            }
        }
    }
    void initFileIfMissing(int id, String file) {
        File dir = new File(getApplicationInfo().dataDir);
        if (!new File(dir, file).exists()) {
            initFile(id,file);
        }
    }

    void initFilesIfMissing() {
        //initFileIfMissing(R.raw.beep,"beep.m4a");
    }
}