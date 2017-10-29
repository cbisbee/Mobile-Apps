package com.csci405.hikeshare;

/**
 * Created by Charles on 10/25/2017.
 */

import android.content.Context;

public class Model implements AutoCloseable {
    public static class Lazy extends com.csci405.hikeshare.Lazy < Model > {
        Lazy(Context context) {
            super(Model.class, context);
        }
    }

    Context mContext;
    Model(Context context) {
        mContext = context;
        mPrefs = new Prefs.Lazy(mContext);
        mLiteDB = new LiteDB.Lazy(mContext);
        mUser = new User.Lazy(mPrefs);
        //mCourses = new Courses.Lazy(mLiteDB);
    }

    Context context() { return mContext; }
    Prefs.Lazy mPrefs;
    Prefs prefs() { return mPrefs.self(); }
    LiteDB.Lazy mLiteDB;
    LiteDB liteDB() { return mLiteDB.self(); }
    User.Lazy mUser;
    User user() { return mUser.self(); }
    //Courses.Lazy mCourses;
    //Courses courses() { return mCourses.self(); }



    @Override
    public void close() {
        mLiteDB.close();
        mPrefs.close();
    }
}