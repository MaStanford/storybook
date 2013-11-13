package com.covent.StoryBook;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
	
	SharedPreferences mSharedPreferences;
	//Need a context for the shared preferences
	Context mContext;
	
	/**
	 * Pass a context for the shared preferences.
	 * @param context
	 */
	PreferenceManager(Context context){
		mContext = context;
		loadPreferences();
	}
	
	/**
	 * Grabs reference to the shared preferences in private mode
	 * @author mStanford
	 */
	private void loadPreferences(){
		mSharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);
	}
	
	/**
	 * template for setting
	 * @param mIs
	 */
	public void set(boolean mIs){
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(Constants.KEY, mIs);
		mEditor.commit();
	}
	
	/**
	 * template for getting a value
	 * @return value
	 */
	public int getPreset(){
		return mSharedPreferences.getInt(Constants.KEY, 0);
	}

}
