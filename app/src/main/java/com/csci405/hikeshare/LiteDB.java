package com.csci405.hikeshare;

/**
 * Created by Charles on 10/25/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static com.csci405.hikeshare.Util.eq;


public class LiteDB extends SQLiteOpenHelper implements AutoCloseable {
    public static class Lazy extends com.csci405.hikeshare.Lazy < LiteDB > {
        Lazy(Context context) {
            super(LiteDB.class, context);
        }
    }

    static class SCHEMA {
        public static final String DATABASE_NAME = "lite.db";
        public static final int DATABASE_VERSION = 1;

        public static class COURSES implements BaseColumns {
            public static final String TABLE_NAME = "courses";
            public static final String COLUMN_ID = _ID;
            public static final String COLUMN_DEPARTMENT = "department";
            public static final String COLUMN_NUMBER = "number";

            public static final String SQL_CREATE =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            COLUMN_ID + " INTEGER PRIMARY KEY" + "," +
                            COLUMN_DEPARTMENT + " TEXT" + "," +
                            COLUMN_NUMBER + " INTEGER)";

            public static final String SQL_DROP =
                    "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }

    private SQLiteDatabase mWritableDatabase = null;


    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (mWritableDatabase == null) {
            synchronized (this) {
                if (mWritableDatabase == null) {
                    if (mReadableDatabase != null) {
                        mReadableDatabase.close();
                    }
                    mWritableDatabase = super.getWritableDatabase();
                    mReadableDatabase = mWritableDatabase;
                }
            }
        }
        return mWritableDatabase;
    }

    private SQLiteDatabase mReadableDatabase = null;


    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (mReadableDatabase == null) {
            synchronized (this) {
                if (mReadableDatabase == null) {
                    mReadableDatabase = super.getReadableDatabase();
                }
            }
        }
        return mReadableDatabase;
    }

    @Override
    public void close() {
        synchronized (this) {
            if (mReadableDatabase != mWritableDatabase) {
                if (mReadableDatabase != null) mReadableDatabase.close();
            }
            if (mWritableDatabase != null) mWritableDatabase.close();

            mReadableDatabase = null;
            mWritableDatabase = null;
        }
    }


    class Courses extends SCHEMA.COURSES {
        void create(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
        }

        void create() {
            create(getWritableDatabase());
        }

        void drop(SQLiteDatabase db) {
            db.execSQL(SQL_DROP);
        }

        void drop() {
            drop(getWritableDatabase());
        }

        void reset(SQLiteDatabase db) {
            drop(db);
            create(db);
        }

        void reset() {
            drop(getWritableDatabase());
            create(getWritableDatabase());
        }

        /*
        void insert(SQLiteDatabase db, Course course) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DEPARTMENT, course.mDepartment);
            values.put(COLUMN_NUMBER, course.mNumber);
            course.mId = db.insert(TABLE_NAME, null, values);
        } */

        /*
        void insert(Course course) {
            insert(getWritableDatabase(), course);
        }
        */

        /*
        void delete(SQLiteDatabase db, Course course) {
            if (course.mId == -1) {
                String selection = COLUMN_DEPARTMENT + " = ? AND " + COLUMN_NUMBER + " = ?";
                String[] selectionArgs = { course.mDepartment, Long.toString(course.mNumber) };
                db.delete(TABLE_NAME, selection, selectionArgs);
            } else {
                delete(db,course.mId);
            }
        }
        */

        /*
        void delete(Course course) {
            delete(getWritableDatabase(),course);
        }
        */

        void delete(SQLiteDatabase db, long id) {
            String selection = COLUMN_ID + " = ?";
            String[] selectionArgs = { Long.toString(id) };
            db.delete(TABLE_NAME, selection, selectionArgs);
        }

        void delete(long id) {
            delete(getWritableDatabase(),id);
        }

        /*
        ArrayList<Course> all(SQLiteDatabase db) {
            ArrayList<Course> ans = new ArrayList<Course>();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    COLUMN_ID,
                    COLUMN_DEPARTMENT,
                    COLUMN_NUMBER};

            Cursor cursor = db.query(
                    TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String department = cursor.getString(1);
                long number = cursor.getLong(2);
                ans.add(new Course(id, department, number));
            }

            cursor.close();
            return ans;
        }
        */

        /*
        ArrayList<Course> all() {
            return all(getReadableDatabase());
        }
        */
    }


    Courses mCourses = null;

    Courses courses() {
        if (mCourses == null) {
            synchronized (this) {
                if (mCourses == null) {
                    mCourses = new Courses();
                }
            }
        }
        return mCourses;
    }

    public LiteDB(Context context) {
        super(context, SCHEMA.DATABASE_NAME, null, SCHEMA.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        courses().create(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        courses().drop(db);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}