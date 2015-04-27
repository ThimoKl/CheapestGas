package de.einfachtanken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

public class KeyValueStorage {

	private static SharedPreferences mSharedPref = null;
	private static Gson GSON = new Gson();
	
	public static void init(Context context) {
		mSharedPref = context.getSharedPreferences(
		        "config_shared_preferences", Context.MODE_PRIVATE);
	}
	
	public static boolean getBoolean(String key, boolean defaultValue) {
		return mSharedPref.getBoolean(key, defaultValue);
	}

	public static void setBoolean(String key, boolean value) {
		Editor editor = mSharedPref.edit();
		editor.putBoolean(key, value);		
		editor.commit();
	}

	public static void setInt(String key, int value) {
		Editor editor = mSharedPref.edit();
		editor.putInt(key, value);		
		editor.commit();
	}

	public static int getInt(String key, int defaultValue) {
		return mSharedPref.getInt(key, defaultValue);
	}

    public static void setLong(String key, long value) {
        Editor editor = mSharedPref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLong(String key, long defaultValue) {
        return mSharedPref.getLong(key, defaultValue);
    }

	public static void setString(String key, String value) {
		Editor editor = mSharedPref.edit();
		editor.putString(key, value);		
		editor.commit();
	}

	public static String getString(String key, String defaultValue) {
		return mSharedPref.getString(key, defaultValue);
	}

	public static void remove(String key) {
		Editor editor = mSharedPref.edit();
		editor.remove(key);	
		editor.commit();
	}
	
	public static void setObject(String key, Object object) {
		if(object == null){
			throw new IllegalArgumentException("object is null");
		}
		
		if(key.equals("") || key == null){
			throw new IllegalArgumentException("key is empty or null");
		}
		Editor editor = mSharedPref.edit();
		editor.putString(key, GSON.toJson(object));
		editor.commit();
	}

	public static <T> T getObject(String key, Class<T> a) {
	
		String gson = KeyValueStorage.getString(key, null);
		if (gson == null) {
			return null;
		} else {
			try{
				return GSON.fromJson(gson, a);
			} catch (Exception e) {
				throw new IllegalArgumentException("Object storaged with key " + key + " is instanceof other class");				
			}
		}
	}
	
	public static boolean contains(String key) {
		return mSharedPref.contains(key);
	}

}
