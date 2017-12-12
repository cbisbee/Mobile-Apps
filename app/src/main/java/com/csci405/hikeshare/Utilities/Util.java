package com.csci405.hikeshare.Utilities;

/**
 * Created by Charles on 10/25/2017.
 */

import android.util.Log;

import com.csci405.hikeshare.CoreActivity;


public class Util {
    public static boolean eq(Object a, Object b) {
        if (a == null || b == null) { return a == b; }
        if (a.getClass().isInstance(b)) return a.equals(b);
        if (b.getClass().isInstance(a)) return b.equals(a);
        return false;
    }

    public static boolean eq(CharSequence a, CharSequence b) {
        if (a == null || b == null) { return a == b; }
        return a.equals(b);
    }

    public static int cmp(CharSequence a, CharSequence b) {
        if (a == null || b == null) {
            if (a == null && b != null) return -1;
            if (a != null && b == null) return  1;
            return 0;
        }

        int i = 0;
        int na = a.length();
        int nb = b.length();
        int n = Math.min(na,nb);
        for (;;) {
            int cpA = Character.codePointAt(a,i);
            int cpB = Character.codePointAt(b,i);
            if (cpA != cpB) { return cpA < cpB ? -1 : 1; }
            i = i + Character.charCount(cpA);
            if (i >= n) break;
        }
        if (na < nb) return -1;
        if (nb < na) return  1;
        return 0;
    }

    public static void i(String message) {
        Log.i(CoreActivity.TAG,message);
    }
    public static void e(String message) {
        Log.e(CoreActivity.TAG,message);
    }
    public static void w(String message) {
        Log.w(CoreActivity.TAG,message);
    }

    public static void d(String message) {
        Log.d(CoreActivity.TAG,message);
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            i("sleep interrupted");
        }
    }
}