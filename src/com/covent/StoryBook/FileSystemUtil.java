
package com.covent.StoryBook;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;


public class FileSystemUtil {
	
    public static final String LOG_TAG = "FileSystemUtil";
	

    /**
     * Checks to see if the external storage is available.
     * 
     * @return true if external storage is available and mounted
     * @author Mstanford
     */
    public static boolean isExternalStorageAvailable() {
    	
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable ? true : false;
    }

    /**
     * Create output directory visible to the user and return the path.
     * 
     * @return the path to the output directory.
     * @author mstanford
     */
    public static File createExternalOutputDirectory() throws IOException {

        /* create the output directory if it doesn't exist. */
		File outputDirectory;
		outputDirectory = StoryBookApp.getApp().getExternalOutputDirectory();
		try {
			if (!outputDirectory.exists()) {
			    if (!outputDirectory.mkdirs()) {
			        throw new IllegalStateException(Constants.CREATE_OUT_DIR_ERROR + outputDirectory);
			    }
			}
		} catch (RuntimeException e) {
			Log.d(LOG_TAG,"External failed");
			e.printStackTrace();
		}
        return outputDirectory;
    }

    /**
     * Create the internal working directory that only the application knows
     * about.
     * 
     * @return the internal working directory.
     * @since Build 1
     */
    public static File createInternalOutputDirectory() {

        /* create the output directory if it doesn't exist. */
        File outputDirectory = StoryBookApp.getApp().getInternalOutputDirectory();
        try {
			if (!outputDirectory.exists()) {
			    if (!outputDirectory.mkdirs()) {
			        throw new IllegalStateException(Constants.CREATE_OUT_DIR_ERROR + outputDirectory);
			    }
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
        return outputDirectory;
    }
    
    public static File getSaveLoadFilePath(String filename){
		//Grab the writeable directory
		File mDirectory;
		try{
			mDirectory = StoryBookApp.getApp().getExternalOutputDirectory();
		} catch (IOException e){
			//No SDCard
			mDirectory = StoryBookApp.getApp().getInternalOutputDirectory();
		} 
		
		//Set the file to the directory and the filename
		File mFileName = new File(mDirectory, filename);
		
		return mFileName;
    }
}
