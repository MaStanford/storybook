package com.covent.StoryBook;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

public class StoryBookApp extends Application {

	/**
     * The application singleton. Gets set by onCreate(). The function
     * {@link #isCreated() isCreated()} returns true if this member is not null.
     * 
     */
    private static StoryBookApp THIS_APP = null;
    public static final String LOG_TAG = "StoryBookApp";
    
    @Override
	public void onCreate() {

		super.onCreate();
		THIS_APP = this;
		
		//Create the dir and check for errors
		
		FileSystemUtil.createInternalOutputDirectory();
		Constants.DEBUG_LOG(LOG_TAG, "Internal Created");
		try {
			FileSystemUtil.createExternalOutputDirectory();
			Constants.DEBUG_LOG(LOG_TAG, "External Created");
			Constants.DEBUG_LOG(LOG_TAG, "IsExternal? " + FileSystemUtil.isExternalStorageAvailable());
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Returns the external directory name from preferences. 
     * 
     * @return String of the output directory.
     * @author mstanford
     */
    public String getExternalFileDir(){
    	SharedPreferences sharedprefs = getSharedPreferences(Constants.SHARED_PREF_KEY,MODE_PRIVATE);
    	return sharedprefs.getString(Constants.EXTERNAL_FILE_DIR_KEY, Constants.EXTERNAL_FILE_DIR);
    }
    

    /**
     * Returns the external output directory. It calls the getExternalFileDir and isExternalStorageAvailiable
     * If we can read and write to external then it returns the external file directory set in shared preferences
     * which getExternalFileDir retrieves.
     * 
     * @return the output directory to the application that is used on the
     *         SDCARD
     * @throws IOException if the directory cannot be obtained or is not
     *             available.
     * @author mstanford
     */
    public File getExternalOutputDirectory() throws IOException {
    	try{
        if (FileSystemUtil.isExternalStorageAvailable())
            return new File(Environment.getExternalStorageDirectory(),  getExternalFileDir());
    	}catch (RuntimeException e){
    		e.printStackTrace();
    	}
    	throw new IOException(Constants.EXTERNAL_DIR_NOT_AVAIL);
    }

    /**
     * Returns the internal output directory
     * 
     * @return the output directory to the application that is used internally
     *         by the app.
     * @author mstanford
     */
    public File getInternalOutputDirectory() {
        return new File(getFilesDir(), Constants.INTERNAL_FILE_DIR);
    }

    /**
     * Returns the static singleton of this application so the application can
     * have global application context when it is needed.
     * 
     * @return a reference to this class singleton.
     * @throws IllegalStateException if this function is called before
     *             {@link #onCreate() onCreate()} is called.
     */
    public static StoryBookApp getApp() {
    	
        try {
			if (THIS_APP == null)
			    throw new IllegalStateException(Constants.STORYBOOK_NOT_CREATE);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
        return THIS_APP;
    }
}
