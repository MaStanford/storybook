package com.covent.StoryBook;

import android.util.Log;

public class Constants {
	
		//Debug, set true to have log output and debug output
		static boolean DEBUG = true;
		
		//Directory
		public static final String INTERNAL_FILE_DIR = "StoryBook";
		public static final String EXTERNAL_FILE_DIR = "StoryBook";
		
		//Default file names
		public static final String KEY_DEFAULT_FILENAME = "UntitledStory";

		
		//Keys for shared preferences
		public static final String SHARED_PREF_KEY = "covent.shared.key";
		
		//Gesture globals
	    public static final int SWIPE_MIN_DISTANCE = 100;
	    public static final int SWIPE_MAX_OFF_PATH = 250;
	    public static final int SWIPE_THRESHOLD_VELOCITY = 80;
	    
	    //Max size for bitmaps
	    public static final int MAX_BITMAP_X = 4096;
	    public static final int MAX_BITMAP_y = 4096;
	    
	    //Codes
		//result code for loading an image
		public static final int CODE_RESULT_LOAD_IMAGE = 1337;
		//Save codes
		public static final int CODE_SAVE = 0;
		public static final int CODE_SAVE_AS = 1;
		//Format codes
		public static final int CODE_FORMAT_BOLD = 6;
		public static final int CODE_FORMAT_ITALIC = 7;
		public static final int CODE_FORMAT_NORMAL = 8;
		//Page types for prinitng out the page number
		public static final int PAGE_COVER = 0;
		public static final int PAGE_TOC = 1;
		public static final int PAGE = 2;
		
		//Page types for displaying and creating
		public static final int PAGE_TYPE_TEXT = 0;
		public static final int PAGE_TYPE_IMAGE = 1;
		public static final int PAGE_TYPE_COVER = 2;
		public static final int PAGE_TYPE_TOC = 3;

	    //Misc Keys
		public static final String KEY_SAVE_BUNDLE_POSITION = "covent_bundle_position";
		public static final String KEY_PARCEL_PAGE = "covent_page_parcel";
		public static final String KEY_NEW_PAGE_EXTRA_POSITON = "covent_position_key";
		public static final String EXTERNAL_FILE_DIR_KEY = "covent_external_file_key";
		public static final String KEY_BUNDLE_ARRAY = "covent_key_bundle_array";
		public static final String KEY_SHARED_FILENAME = "covent_shared_filename_key";
		
		
		//Intents
		
		//Intent action for adding a new page
		public static final String KEY_NEW_PAGE_INTENT = "covent_new_page_intent";
		//Key for adding a page with a position set
		public static final String KEY_NEW_PAGE_INTENT_POSITION = "covent_new_page_intent_position";
		//Intent action for refreshing the adapter
		public static final String KEY_REFRESH_ADAPTER_INTENT = "covent_refresh_adapter";
		//Key for the text changed intent
		public static final String KEY_TEXT_CHANGE_INTENT = "covent_text_change_intent";
		//key for when the imageview is clicked
		public static final String KEY_IMAGE_CLICKED_INTENT = "covent_image_clicked_intent";

		
		//Errors
		public static final String CREATE_OUT_DIR_ERROR = "CREATE_OUT_DIR_ERROR";
		public static final String EXTERNAL_DIR_NOT_AVAIL = "EXTERNAL_DIR_NOT_AVAIL";
		public static final String STORYBOOK_NOT_CREATE = "STORYBOOK_NOT_CREATE";
		
		
		//Dialogs
		public static final int DIALOG_NEW_PROJECT_NAME = 0;
		public static final int DIALOG_LOAD_PROJECT = 1;
		public static final int DIALOG_SAVE_AS = 2;


		
		//Log output using debug logic
		static void DEBUG_LOG(String tag,String msg){
			if(DEBUG)
				Log.d(tag,msg);
		}
}
