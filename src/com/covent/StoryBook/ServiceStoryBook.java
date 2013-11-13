package com.covent.StoryBook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Binder;

/**
 * Service for the Story book
 * This contains the page array and operations on the array
 */
public class ServiceStoryBook extends Service {
	
	final String TAG = "ServiceStoryBook";
	
	//Array of pages
	private ArrayList<Page> mPageArray = new ArrayList<Page>();

	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();
	
	//Preferences
	private PreferenceManager mPreferenceManager;
	
	//json serializer
	private PageJSONSerializer mPageJSONSerializer;
	
	//Filename to save, this will be pulled from shared preferences later on
	private String mFileName = "myStoryBook.json";
	
	//Context
	Context mContext;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public ServiceStoryBook getService() {
			return ServiceStoryBook.this;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		return START_STICKY;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
//		// Unregister the Broadcast reciever
//		unregisterReceiver(NewPageReceiver);
		return super.onUnbind(intent);
	}
	
	/***************************************************
	 * BROADCAST RECEIVER
	 ***************************************************/

	/**
	 * Receives intents from the activities when the user adds a new page
	 */
	private BroadcastReceiver PageChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Constants.DEBUG_LOG(TAG, "Page Broadcast recieved");
			receivedPageBroadcast(intent);
		}
	};
	
	
	/**
	 * Called from the broadcast receiver.  
	 * I'm grabbing the intent from the receiver and checking its action.
	 * @param intent
	 */
	private void receivedPageBroadcast(Intent intent) {

		Constants.DEBUG_LOG("ReceieveBroadcast", "Received Intent" + intent.getAction());
		//The user creates a new page and intent with extra new page intent is sent
		if (intent.getAction().equals(Constants.KEY_NEW_PAGE_INTENT)) {
			
			//Create a new page object from a parcel grabbed from an intent
			Page mNewPage = intent.getParcelableExtra(Constants.KEY_PARCEL_PAGE);
			//Add the page at the end
			addPage(mNewPage);
			
			//Array was altered intent.  Updates Adapter and UI
			Intent UIstop = new Intent(Constants.KEY_REFRESH_ADAPTER_INTENT);
			this.sendBroadcast(UIstop);
		//User has made a change to the current page
		} else if(intent.getAction().equals(Constants.KEY_NEW_PAGE_INTENT_POSITION)){
			
			Constants.DEBUG_LOG("Service Recieved new page", "Before new page from parcel");
			
			//Create a page from the parceable extra and replace the pointed to page
			Page mNewPage = intent.getParcelableExtra(Constants.KEY_PARCEL_PAGE);
			int mIndex = intent.getIntExtra(Constants.KEY_EXTRA_POSITON, 3);
			setPage(mIndex,mNewPage);
			
			Constants.DEBUG_LOG("Service Recieved new page", "After new page from parcel");
			
			//Array was altered intent.  Updates Adapter and UI
			Intent UIstop = new Intent(Constants.KEY_REFRESH_ADAPTER_INTENT);
			this.sendBroadcast(UIstop);
		}
	}
	public void setContext(Context context){
		mContext = context;
	}
	
	public void setArray(ArrayList<Page> pageArray){
		//mPageArray.clear();
		mPageArray = pageArray;
	}
	
	public ArrayList<Page> getArray(){
		return mPageArray;
	}
	
	public int setPage(int index,Page page){
		mPageArray.set(index, page);
		return 1;
	}
	
	public int addPage(Page page,int index){
		mPageArray.add(index, page);
		return 1;
	}
	
	public int addPage(Page page){
		mPageArray.add(page);
		return 1;
	}
	
	public Page getPage(int index){
		return mPageArray.get(index);
	}
	
	public int getNumberOfPages(){
		return mPageArray.size();
	}
	
	public String getFileName() {
		return mFileName;
	}

	public void setFileName(String fileName) {
		mFileName = fileName;
	}
	
	public PreferenceManager getPreferenceManager() {
		return mPreferenceManager;
	}

	public void setPreferenceManager(PreferenceManager preferenceManager) {
		mPreferenceManager = preferenceManager;
	}

	public boolean saveStoryBook(){
		try {
			mPageJSONSerializer.saveJSON(mPageArray,mFileName);
			Constants.DEBUG_LOG(TAG,"StoryBook saved to file.");
			return true;
		} catch (Exception e){
			Constants.DEBUG_LOG(TAG,"Error Saving StoryBook: , + e");
			return false;
		}
	}
	
	public ArrayList<Page> loadStoryBook() throws JSONException, IOException {
		ArrayList<Page> mStoryBook = new ArrayList<Page>();
		BufferedReader mBufferedReader = null;
		try{
			InputStream mInStream = mContext.openFileInput(getFileName());
			mBufferedReader = new BufferedReader(new InputStreamReader(mInStream));
			StringBuilder mJSONString = new StringBuilder();
			//Have a placeholder for each string
			String mLine = null;
			//read each line and append to the json string
			while((mLine = mBufferedReader.readLine()) != null ){
				mJSONString.append(mLine);
			}
			//Grab JSON tokens from string
			JSONArray mJSONArray = (JSONArray) new JSONTokener(mJSONString.toString()).nextValue();
			//build array of pages from the tokens
			for(int i = 0; i < mJSONArray.length(); i++){
				mStoryBook.add(new Page(mJSONArray.getJSONObject(i)));
			}
		} catch (FileNotFoundException mError){
			
		} finally {
			if(mBufferedReader != null){
				mBufferedReader.close();
			}
		}
		return mStoryBook;
	}	
	
	public void onCreate() {
		super.onCreate();
		//Start the preference manager
		setPreferenceManager(new PreferenceManager(getApplicationContext()));
		//Create a new serializer for saving
		mPageJSONSerializer = new PageJSONSerializer(mContext, getFileName());
		
		/*****************************************
		 *BROADCAST RECEIEVER  
		 *****************************************/
		// Register broadcast recievers
		IntentFilter mIntentPage = new IntentFilter();
		//Main view
		mIntentPage.addAction(Constants.KEY_NEW_PAGE_INTENT);
		mIntentPage.addAction(Constants.KEY_NEW_PAGE_INTENT_POSITION);
		registerReceiver(PageChangeReceiver, mIntentPage);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Constants.DEBUG_LOG(TAG, "onDestroy called");
		unregisterReceiver(PageChangeReceiver);
	}

	@Override
	public ComponentName startService(Intent service) {
		// TODO Auto-generated method stub
		return super.startService(service);
	}

	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		return super.stopService(name);
	}
	
	
	
}
