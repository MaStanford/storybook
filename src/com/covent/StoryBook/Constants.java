package com.covent.StoryBook;

import android.util.Log;

public class Constants {
	
		//Debug, set true to have log output and debug output
		static boolean DEBUG = true;
		
		//Keys for shared preferences
		public static final String SHARED_PREF_KEY = "covent.shared.key";
		public static final String KEY = "key";
		
		//Gesture globals
	    public static final int SWIPE_MIN_DISTANCE = 100;
	    public static final int SWIPE_MAX_OFF_PATH = 250;
	    public static final int SWIPE_THRESHOLD_VELOCITY = 80;
	    
	    //Codes
	    
		//result code for loading an image
		public static final int CODE_RESULT_LOAD_IMAGE = 1337;

	    //Misc Keys
	    
	    //Key for save bundle
		public static final String KEY_BUNDLE_POSITION = "covent.position";
		//Key for parcelable extra
		public static final String KEY_PARCEL_PAGE = "covent_page_parcel";
		//Intent action for adding a new page
		public static final String KEY_NEW_PAGE_INTENT = "covent_new_page_intent";
		//Key for adding a page with a position set
		public static final String KEY_NEW_PAGE_INTENT_POSITION = "covent_new_page_intent_position";
		//Intent action for replacing/updating current page	
		public static final String KEY_UPDATE_PAGE_INTENT = "covent_update_page_intent";
		//Intent action for refreshing the adapter
		public static final String KEY_REFRESH_ADAPTER_INTENT = "covent_refresh_adapter";
		//Key for adding a new page at set index
		public static final String KEY_EXTRA_POSITON = "covent_position_key";
		//Key for the text changed intent
		public static final String KEY_TEXT_CHANGE_INTENT = "covent_text_change_intent";
		//Key for the extra in the changed text intent
		public static final String KEY_TEXT_CHANGE_INTENT_EXTRA = "covent_text_change_intent_extra";
		//Key for bundle array
		public static final String KEY_BUNDLE_ARRAY = "covent_key_bundle_array";

		//Log output using debug logic
		static void DEBUG_LOG(String tag,String msg){
			if(DEBUG)
				Log.d(tag,msg);
		}
}
