package com.csci405.hikeshare;

import android.util.Log;

import com.csci405.hikeshare.Activities.OsmHike;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Charles on 10/25/2017.
 */

public class Lazy < T > implements AutoCloseable {
    private Object[] mArgs;
    private Constructor<T> mConstructor;
    public Lazy(Class<T> clazz, Object... args) {
        mArgs = args;
        mConstructor = (Constructor<T>) Reflect.getConstructor(clazz,args);
    }

    public Lazy() {
        mArgs = null;
        mConstructor = null;
    }

    private T mSelf = null;
    public T self() {
        if (mSelf == null) {
            synchronized (this) {
                if (mSelf == null) {
                    mSelf = create();
                }
            }
        }
        return mSelf;
    }

    public void close() {
        synchronized (this) {
            if (mSelf != null) {
                T self = mSelf;
                mSelf = null;
                close(self);
            }
        }
    }

    protected T create() {
        try {
            return (T) mConstructor.newInstance(mArgs);
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(T self) {
        if (self instanceof AutoCloseable) {
            try {
                ((AutoCloseable) self).close();
            } catch (Exception ex) {
                Log.e(OsmHike.TAG, "Exception " + ex + " closing " + self);
            }
        }
    }
}