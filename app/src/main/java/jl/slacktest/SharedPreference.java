package jl.slacktest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Android1 on 8/18/2016.
 */
public class SharedPreference {

    public static final String TAG = SharedPreference.class.getName();
    public static final String SHARED_PREF_NAME = "device";
    private static final int PRIVATE_MODE = 0;
    private static SharedPreferences _pref;
    private static SharedPreference _instance;

    private SharedPreference() {
    }

    public static SharedPreference getInstance(Context context) {
        if (_pref == null) {
            _pref = context
                    .getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        }
        if (_instance == null) {
            _instance = new SharedPreference();
        }
        return _instance;
    }

    public void clear() {
        SharedPreferences.Editor editor = _pref.edit();
        editor.clear();
        editor.commit();
    }

    public void setServiceStarted(String value) {
        setString(Keys.STARTED.getLabel(), value);
    }

    public String getServiceStarted(String defaultValue) {
        return getString(Keys.STARTED.getLabel(), defaultValue);
    }



    private void setString(String key, String value) {
        if (key != null && value != null) {
            try {
                if (_pref != null) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.putString(key, value);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to set " + key + "= " + value
                        + "in shared preference", e);
            }
        }
    }

    private void setInt(String key, int value) {
        if (key != null) {
            try {
                if (_pref != null) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.putInt(key, value);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to set " + key + "= " + value
                        + "in shared preference", e);
            }
        }
    }

    private void setFloat(String key, float value) {
        if (key != null) {
            try {
                if (_pref != null) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.putFloat(key, value);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to set " + key + "= " + value
                        + "in shared preference", e);
            }
        }
    }

    private String getString(String key, String defaultValue) {
        if (_pref != null && key != null && _pref.contains(key)) {
            return _pref.getString(key, defaultValue);
        }
        return defaultValue;
    }

    private int getInt(String key, int defaultValue) {
        if (_pref != null && key != null && _pref.contains(key)) {
            return _pref.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    private float getFloat(String key, float defaultValue) {
        if (_pref != null && key != null && _pref.contains(key)) {
            return _pref.getFloat(key, defaultValue);
        }
        return defaultValue;
    }

    private void setBoolean(String key, boolean value) {
        if (key != null) {
            try {
                if (_pref != null) {
                    SharedPreferences.Editor editor = _pref.edit();
                    editor.putBoolean(key, value);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to set " + key + "= " + value
                        + "in shared preference", e);
            }
        }
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        if (_pref != null && key != null && _pref.contains(key)) {
            return _pref.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    private enum Keys {
        STARTED("started");

        private String label;

        private Keys(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
