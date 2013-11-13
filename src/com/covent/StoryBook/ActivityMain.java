/**
 * 
 */
package com.covent.StoryBook;

import java.util.ArrayList;

import com.covent.StoryBook.ServiceStoryBook.LocalBinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * @author mStanford
 *
 */
public class ActivityMain extends Activity {
	
	protected static final String TAG = "ActivityMain";
	// make some buttons
	private Button btn_prev, btn_next;
	// Grab the textViews for the gallery
	private TextView mTextPageNumber;
	private Bitmap mBitMapPicture;
	// set position
	private int mPosition = 0;
	//ViewFlipper
	private ViewAdapter mViewAdapter;
	private ViewFlipper mViewFlipper;
	//Service
	private ServiceStoryBook mService;
	boolean mBound = false;
	//context
	Context mContext;


	/***************************************************
	 * Services
	 *************************************************/

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			//Show the array
			if(mService.getArray().isEmpty()){
				updateAdapter();
			}
		}
		public void onServiceDisconnected(ComponentName className) {
			mBound = false;
		}
	};
		

	/*
	 * Updates the mViewadapter
	 */
	public void updateAdapter(){
		if(mBound){
			//mService.setArray(storybook);
			//Sets the updates array to the adapter
			mViewAdapter.setArray(mService.getArray());
			//notifies that there are changes in the adapter
			mViewAdapter.notifyDataSetChanged();
			//Sets the flipper to show the current position
			mViewFlipper.setDisplayedChild(mPosition);
			//set the readout
			switch(mPosition){
			case 0:
				mTextPageNumber.setText("Cover");
				break;
			case 1:
				mTextPageNumber.setText("ToC");
				break;
			default:
				mTextPageNumber.setText("Page " + (mPosition-1));
				break;
			}
		}
	}

	/***************************************************
	 * BROADCAST RECEIVER
	 ***************************************************/

	/**
	 * Grabs all the broadcasts sent out in this context.
	 * To send broadcasts here, you need to pass a reference to the context and then
	 * call the sendBroacast method in the context object.
	 * The onReceive is called by the context when a broadcast is sent.
	 * Do work in the onRecieve method.
	 */
	private BroadcastReceiver RefreshAdapterReciever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Constants.DEBUG_LOG(TAG, "Broadcast recieved");
			receivedBroadcast(intent);
		}
	};
	
	/**
	 * Receives intents from the activities when the user changes text
	 */
	private BroadcastReceiver TextChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Constants.DEBUG_LOG(TAG, "Text Broadcast recieved");
			receivedTextBroadcast(intent);
		}
	};
	
	private void receivedTextBroadcast(Intent intent){
		Constants.DEBUG_LOG("ReceieveBroadcast", "Received Intent" + intent.getAction());
		
		mService.getPage(mPosition).setStoryText(intent.getStringExtra(Constants.KEY_TEXT_CHANGE_INTENT_EXTRA));
	}

	/**
	 * Called from the broadcast receiver.  
	 * I'm grabbing the intent from the receiver and checking its action.
	 * @param intent
	 */
	private void receivedBroadcast(Intent intent) {

		Constants.DEBUG_LOG("ReceieveBroadcast", "Received Intent" + intent.getAction());
		
		if (intent.getAction().equals(Constants.KEY_REFRESH_ADAPTER_INTENT)) {
			//Send an empty message to the handler to 
			mHandler.sendEmptyMessage(0);
			updateAdapter();
		}
	}

	/**
	 * Handler override. Intercepts handler messages and updates ViewFlipper
	 * position
	 * @author mstanford
	 */
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//Updates the UI.
			updateAdapter();
			setupListeners();
		}
	};

	/**********************************************************************
	 * ONCLICK LISTENERS
	 ********************************************************************/
	public Button.OnClickListener next_ocl = new Button.OnClickListener() {
		public void onClick(View v) {
			if (mPosition < mViewAdapter.getCount() - 1) {
				mPosition++;
				mHandler.sendEmptyMessage(0);
			}
		}
	};

	public Button.OnClickListener prev_ocl = new Button.OnClickListener() {
		public void onClick(View v) {
			if (mPosition > 0) {
				mPosition--;
				mHandler.sendEmptyMessage(0);
			}
		}
	};
	
	public void setupListeners(){
		
	}

	/**
	 * Overwrite the onFling OnFling sent parameters from the OnTouchEvent()
	 * The onfling needs to check for getting off path, so I check the Y axis
	 * and discard flings that move veritcally too much.
	 * I then measure the amount of movement either left or right and measure it against
	 * a min distance and then make sure it was enough speed, so you cant do 5 minute
	 * flings.
	 * 
	 * @author mstanford
	 */
	private class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
				float velocityY) {
			// If swipe goes off path
			if (Math.abs(event1.getY() - event2.getY()) > Constants.SWIPE_MAX_OFF_PATH)
				return true;
			// If swipe goes from from right to left.
			if (event1.getX() - event2.getX() > Constants.SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > Constants.SWIPE_THRESHOLD_VELOCITY) {
				Constants.DEBUG_LOG("Swipe", "Next");
				next_ocl.onClick(null);
				// If swipe goes from left to right.
			} else if (event2.getX() - event1.getX() > Constants.SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > Constants.SWIPE_THRESHOLD_VELOCITY) {
				Constants.DEBUG_LOG("Swipe", "Prev");
				prev_ocl.onClick(null);
			}
			return true;
		}
	}
	
	/**
	 * Gets a bitmap from the image file.
	 */
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         
        if (requestCode == Constants.CODE_RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
 
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            try {
				mBitMapPicture = BitmapFactory.decodeFile(picturePath);
				mService.getPage(mPosition).setBitmapPicture(mBitMapPicture);
				mHandler.sendEmptyMessage(0);
			}
			catch (Exception e) {
				Toast.makeText(getBaseContext(), "Image File Not Compatible", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
        }
    }
	
	/**
	 * Load an image
	 */
	public void loadImage(){
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, Constants.CODE_RESULT_LOAD_IMAGE);
	}


	/**
	 * Save the position of the ViewFlipper
	 * @author mstanford
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(Constants.KEY_BUNDLE_POSITION, mPosition);
		outState.putParcelableArrayList(Constants.KEY_BUNDLE_ARRAY, mService.getArray());
	}

	/**
	 * Recall the position of the ViewFlipper
	 * @author mstanford
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mPosition = savedInstanceState.getInt(Constants.KEY_BUNDLE_POSITION);
		ArrayList<Page> mPageArray = savedInstanceState.getParcelableArrayList(Constants.KEY_BUNDLE_ARRAY);
		mService.setArray(mPageArray);
	}

	@Override
	protected void onStart(){
		super.onStart();
		/****************************************
		 * SERVICES
		 ***************************************/
		// Bind to LocalService
		Intent intent = new Intent(this, ServiceStoryBook.class);
		//http://developer.android.com/guide/components/bound-services.html
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		Constants.DEBUG_LOG(TAG, "Service: " + mService);
		
		mHandler.sendEmptyMessage(0);
	}

	@Override
	protected void onResume() {
		super.onResume();

		/*****************************************
		 *BROADCAST RECEIEVER  
		 *****************************************/
		// Register broadcast recievers
		IntentFilter mIntent = new IntentFilter();
		//Main view
		mIntent.addAction(Constants.KEY_NEW_PAGE_INTENT);
		registerReceiver(RefreshAdapterReciever, mIntent);
		//Textchanged receiver
		IntentFilter mIntentText = new IntentFilter();
		mIntentText.addAction(Constants.KEY_TEXT_CHANGE_INTENT);
		registerReceiver(TextChangeReceiver, mIntentText);

		mHandler.sendEmptyMessage(0);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop(){
		super.onStop();
		// Unregister the Broadcast reciever
		unregisterReceiver(RefreshAdapterReciever);
		unregisterReceiver(TextChangeReceiver);
		//Unbind the Services
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	/*****************************************************************
	 * ONCREATE
	 * 
	 * @author mstanford
	 *****************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Show my XML
		setContentView(R.layout.main_layout);
		
		mContext = getBaseContext();

		// Grab my references for main view
		btn_next = (Button) findViewById(R.id.btn_new_project);
		btn_prev = (Button) findViewById(R.id.btn_load_project);
		btn_next.setOnClickListener(next_ocl);
		btn_prev.setOnClickListener(prev_ocl);
		
		//Page readout
		mTextPageNumber = (TextView) findViewById(R.id.text_page_number);

		// ViewFlipper
		mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		mViewFlipper.setInAnimation(this, android.R.anim.fade_in);
		//mViewFlipper.setOutAnimation(this, android.R.anim.fade_out);
		mViewAdapter = new ViewAdapter(this, mViewFlipper);

		/**
		 * Create a gesture detector. Construct with context and a gesture
		 * listener.
		 */
		final GestureDetector mGestureDetector = new GestureDetector(this, new GestureListener());
		mViewFlipper.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Constants.DEBUG_LOG("OnTouch", "Before swipe");
				mGestureDetector.onTouchEvent(event);
				return true;
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home.
			Intent intent = new Intent(this, ActivityMain.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_add_page:
			//Make new page
			if(mPosition != 0){
				Page mNewPage = new Page(mContext,0);
				mService.addPage(mNewPage, mPosition+1);
				mHandler.sendEmptyMessage(0);
			}
//			//I cant get the parcelable to work properly - mStanford
//			//Send page to the service.  Service will update UI when it processes the parcell
//			Intent mAddPageIntent = new Intent(Constants.KEY_NEW_PAGE_INTENT_POSITION);
//			mAddPageIntent.putExtra(Constants.KEY_PARCEL_PAGE, mNewPage);
//			mAddPageIntent.putExtra(Constants.KEY_EXTRA_POSITON, mPosition + 1);
//			Constants.DEBUG_LOG("New page selected", "pos: " + (mPosition + 1));
//			Constants.DEBUG_LOG("Total Pages", "#" + mService.getArray().size());
//			sendBroadcast(mAddPageIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
