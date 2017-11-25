package com.csci405.hikeshare;

/**
 * Created by Charles on 10/25/2017.
 */



import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import static com.csci405.hikeshare.Util.*;


public class Prefs implements AutoCloseable {
    public static class Lazy extends com.csci405.hikeshare.Lazy < Prefs > {
        Lazy(Context context) {
            super(Prefs.class, context);
        }
    }

    public static final String PREFERENCES = "preferences";

    private Context mContext;
    Prefs(Context context) { mContext = context; }

    private SharedPreferences mPreferences = null;
    private SharedPreferences.Editor mEditor = null;

    private SharedPreferences preferences() {
        if (mPreferences == null) {
            synchronized(this) {
                if (mPreferences == null) {
                    mPreferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                }
            }
        }
        return mPreferences;
    }

    private SharedPreferences.Editor editor() {
        if (mEditor == null) {
            synchronized (this) {
                if (mEditor == null) {
                    mEditor = preferences().edit();
                }
            }
        }
        return mEditor;
    }

    public static final String AUTHENTICATED = "authenticated";
    public static final boolean AUTHENTICATED_DEFAULT = false;

    public boolean authenticated() {
        return preferences().getBoolean(AUTHENTICATED,AUTHENTICATED_DEFAULT);
    }

    public void authenticated(boolean value) {
        if (authenticated() != value) {
            editor().putBoolean(AUTHENTICATED, value);
        }
    }

    public static final String OFFLINE = "offline";
    public static final boolean OFFLINE_DEFAULT = false;
    public boolean offline() {return preferences().getBoolean(OFFLINE,OFFLINE_DEFAULT);}

    public static final String COMPASS = "compass";
    public static final boolean COMPASS_DEFAULT = true;
    public boolean compass() {return preferences().getBoolean(COMPASS,COMPASS_DEFAULT);}

    public static final String POWERSAVE = "powersave";
    public static final boolean POWERSAVE_DEFAULT = false;
    public boolean powersave(){return preferences().getBoolean(POWERSAVE,POWERSAVE_DEFAULT);}

    public static final String MARKERS = "markers";
    public static final boolean MARKERS_DEFAULT = false;
    public boolean markers() {return preferences().getBoolean(MARKERS,MARKERS_DEFAULT);}

    public static final String MAPSOURCE = "mapsource";
    public static final String MAPSOURCE_DEFAULT = "USGS National Map (TOPO)";
    public String mapsource(){return preferences().getString(MAPSOURCE,MAPSOURCE_DEFAULT);}

    public static final String USER = "user";
    public static final String USER_DEFAULT = null;
    public String user() {
        return preferences().getString(USER, USER_DEFAULT);
    }


    public void user(String value) {
        if (!eq(user(),value)) {
            editor().putString(USER, value);
        }
    }

    public static final String GROUPS = "groups";
    public static final Set<String> GROUPS_DEFAULT = new HashSet<String>();
    public Set<String> groups() {
        return preferences().getStringSet(GROUPS,GROUPS_DEFAULT);
    }

    public void groups(Set<String> value) {
        if (!groups().equals(value)) {
            editor().putStringSet(GROUPS, value);
        }
    }

    public void groups(String... values) {
        HashSet<String> valueAsSet = new HashSet<String>();
        for (String group : values) { valueAsSet.add(group); }
        groups(valueAsSet);
    }

    public void cancel() {
        if (mEditor != null) {
            synchronized (this) {
                if (mEditor != null) {
                    mEditor = null;
                }
            }
        }

    }

    public void save() {
        if (mEditor != null) {
            synchronized (this) {
                if (mEditor != null) {
                    mEditor.commit();
                    mEditor = null;
                }
            }
        }
    }

    @Override
    public void close() {
        save();
    }
}
