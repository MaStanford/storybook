package com.covent.StoryBook;

import java.io.File;

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
		mEditor.putBoolean(Constants.SHARED_PREF_KEY, mIs);
		mEditor.commit();
	}
	
	/**
	 * template for getting a value
	 * @return value
	 */
	public int getPreset(){
		return mSharedPreferences.getInt(Constants.SHARED_PREF_KEY, 0);
	}

	public void setFileName(File filename) {
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putString(Constants.KEY_SHARED_FILENAME, filename.getPath());
		mEditor.commit();
	}
	
	public void setFileName(String filename) {
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		File mFile = FileSystemUtil.getSaveLoadFilePath(filename);
		mEditor.putString(Constants.KEY_SHARED_FILENAME, mFile.getPath());
		mEditor.commit();
	}
	
	public File getFileName(){
		File mFile = new File(mSharedPreferences.getString(Constants.KEY_SHARED_FILENAME, Constants.KEY_DEFAULT_FILENAME));
		return mFile;
	}

}
