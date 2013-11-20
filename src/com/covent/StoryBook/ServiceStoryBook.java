package com.covent.StoryBook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Binder;
import android.widget.Toast;

/**
 * Service for the Story book
 * This contains the page array and operations on the array
 */
public class ServiceStoryBook extends Service {
	
	final static String TAG = "ServiceStoryBook";
	
	//Array of pages
	private ArrayList<Page> mPageArray;

	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();
	
	//Preferences
	private PreferenceManager mPreferenceManager;
	
	//json serializer
	private PageJSONSerializer mPageJSONSerializer;
	
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
			int mIndex = intent.getIntExtra(Constants.KEY_NEW_PAGE_EXTRA_POSITON, 3);
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
	
	public boolean setArray(ArrayList<Page> pageArray){
		mPageArray = pageArray;
		return true;
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
	
	public File getFileName() {
		return mPreferenceManager.getFileName();
	}

	public void setFileName(File fileName) {
		
		mPreferenceManager.setFileName(fileName);
	}
	
	/**
	 * displays a spinner with all the filenames in the local directory.
	 * I will need to also have a way for someone to search their external for the files
	 * TODO: implement this in a spinner and also get external directory involved
	 * @return
	 */
	public File[] getLoadFiles(){
		
		//File mDirectory = getFilesDir(); 
		File mDirectory;
		try{
			mDirectory = StoryBookApp.getApp().getExternalOutputDirectory();
		} catch (IOException e){
			//No SDCard
			mDirectory = StoryBookApp.getApp().getInternalOutputDirectory();
		} 
		
		File[] mSubFiles = mDirectory.listFiles();
		
		return mSubFiles;
	}
	
	public PreferenceManager getPreferenceManager() {
		return mPreferenceManager;
	}

	public void setPreferenceManager(PreferenceManager preferenceManager) {
		mPreferenceManager = preferenceManager;
	}

	/**
	 * Grabs the filename from sharedpreferences.  Automatically checks to see if external r/w and saved
	 * to external if availible or internal if not.
	 * 
	 * @return If the save is successful
	 */
	public boolean saveStoryBook(){
		
		//Set the file to the directory and the filename
		File mFileName = getFileName();
		
		try {
			//Show the user where the save is going to be
			Toast.makeText(mContext, "Saving " + mFileName.getPath() + ".JSON...", Toast.LENGTH_LONG).show();
			//Send the serializer the file and the array to save
			mPageJSONSerializer.saveJSON(mPageArray,mFileName);
			Constants.DEBUG_LOG(TAG,"StoryBook saved to file.");
			return true;
		} catch (Exception e){
			Constants.DEBUG_LOG(TAG,"Error Saving StoryBook: , "+ e);
			return false;
		}
	}
	
	/**
	 * Sets the filename in sharedpreferences and saves the file.  Automatically checks to see if external r/w and saved
	 * to external if availible or internal if not.
	 * 
	 * @return If the save is successful
	 */
	public boolean saveStoryBook(String filename){
		//Set the file to the directory and the filename
		File mFileName = FileSystemUtil.getSaveLoadFilePath(filename);
		try {
			Toast.makeText(mContext, "Saving " + mFileName.getPath() + ".JSON...", Toast.LENGTH_LONG).show();
			mPageJSONSerializer.saveJSON(mPageArray,mFileName);
			Constants.DEBUG_LOG(TAG,"StoryBook saved to file.");
			setFileName(mFileName);
			return true;
		} catch (Exception e){
			Constants.DEBUG_LOG(TAG,"Error Saving StoryBook: , "+ e);
			return false;
		}
	}
	
	/**
	 * Loads the storybook into the service
	 * Gets the filename and path from the shared preferences
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public ArrayList<Page> loadStoryBook() throws JSONException, IOException{
		//Set the file to the directory and the filename
		File mFileName = getFileName();
		//Toast.makeText(mContext, "Loading " + mFileName.getPath() + ".JSON...", Toast.LENGTH_LONG).show();
		mPageArray = mPageJSONSerializer.loadStoryBook(mFileName);
		return mPageArray;
	}
	
	/**
	 * Loads the storybook into the Service
	 * Sets the shared prefs filename
	 * Gets full path and filename from arguement
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public ArrayList<Page> loadStoryBook(File filename) throws JSONException, IOException{
		//Toast.makeText(mContext, "Loading " + filename.getPath() + ".JSON...", Toast.LENGTH_LONG).show();
		mPreferenceManager.setFileName(filename);
		mPageArray = mPageJSONSerializer.loadStoryBook(filename);
		return mPageArray;
	}
	
	public void onCreate() {
		super.onCreate();
		//Start the preference manager
		setPreferenceManager(new PreferenceManager(getApplicationContext()));
		//Set the context
		mContext=getBaseContext();
		//Create a new serializer for saving
		mPageJSONSerializer = new PageJSONSerializer(mContext, getFileName().getPath());
		//Not needed since the only time the array will be blank is if the user starts a new project and that activity will init the array
		//mPageArray = new ArrayList<Page>();
		
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
