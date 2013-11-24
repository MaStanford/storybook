/**
 * Storybook Main Activity.  It contains the ViewAdapter that displays the pages in the page array.  
 * Fragments are overkill for most applications.  It is a lot simpler to use a custom view adapter and display content in that manner.
 * @author mStanford
 */
package com.covent.StoryBook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import com.covent.StoryBook.ServiceStoryBook.LocalBinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

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
	//
	AlertDialog mDialog;


	/***************************************************
	 * Services
	 *************************************************/

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}
		public void onServiceDisconnected(ComponentName className) {
			mBound = false;
		}
	};


	/**
	 * Updates the mViewadapter
	 * @author mstanford
	 */
	public void updateViewAdapter(){
		if(mBound){
			Constants.DEBUG_LOG(TAG, "Update adapter called");
			//Some debugging stuffs
			Constants.DEBUG_LOG(TAG, "Array Size Service: " + mService.getArray().size());
			for(int i = 0; i < mService.getArray().size(); i++){
				Constants.DEBUG_LOG(TAG, "Page: " + i + " Text: " + mService.getPage(i).getStoryText());
			}
			//Set the array in the flipper.
			mViewAdapter.setArray(mService.getArray());
			//notifies that there are changes in the adapter
			mViewAdapter.notifyDataSetChanged();
			//Sets the flipper to show the current position
			mViewFlipper.setDisplayedChild(mPosition);

			//set the readout
			switch(mPosition){
			case Constants.PAGE_COVER:
				mTextPageNumber.setText(Html.fromHtml("<b>Cover</b>"));
				break;
			case Constants.PAGE_TOC:
				mTextPageNumber.setText(Html.fromHtml("<b>ToC</b>"));
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
	 * 
	 */
	private BroadcastReceiver ActivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Constants.DEBUG_LOG(TAG, "Broadcast recieved");
			receivedBroadcast(intent);
		}
	};

	/**
	 * Called from the broadcast receiver.  
	 * I'm grabbing the intent from the receiver and checking its action.
	 * @param intent
	 * @author mstanford
	 */
	private void receivedBroadcast(Intent intent) {

		Constants.DEBUG_LOG("ReceieveBroadcast", "Received Intent" + intent.getAction());

		//Intent to refresh UI, currently not being used. Was going to be part of parcell implemeentaion
		if (intent.getAction().equals(Constants.KEY_REFRESH_ADAPTER_INTENT)) {
			//Send an empty message to the handler to 
			mHandler.sendEmptyMessage(0);
			return;
		}

		//Intent send when the editText object receives a textchanged callback
		if (intent.getAction().equals(Constants.KEY_TEXT_CHANGE_INTENT)) {
			TextView mText = (TextView) mViewFlipper.getCurrentView().findViewById(R.id.tv_story_text);
			Constants.DEBUG_LOG("Receieved text", mText.getText().toString());
			//Tried to use HTML to mark text but decided to do spannable instead - mStanford
			//mService.getPage(mPosition).setStoryText(Html.fromHtml(mText.getText().toString()));
			mService.getPage(mPosition).setStoryText(mText.getText().toString());
			return;
		}

		//Intent when the Imageview object receives a gesture == touch down
		if (intent.getAction().equals(Constants.KEY_IMAGE_CLICKED_INTENT)) {
			loadImage();
			return;
		}
	}

	/**
	 * Handler override. Intercepts handler messages and updates ViewFlipper
	 * position.  
	 * @author mstanford
	 */
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//Updates the UI.
			updateViewAdapter();
		}
	};

	/**********************************************************************
	 * ONCLICK LISTENERS
	 ********************************************************************/
	public Button.OnClickListener next_ocl = new Button.OnClickListener() {
		public void onClick(View v) {
			if (mPosition < mViewAdapter.getCount() - 1) {
				mPosition++;
				Constants.DEBUG_LOG(TAG, "Position = " + mPosition);
				mHandler.sendEmptyMessage(0);
			}
		}
	};

	public Button.OnClickListener prev_ocl = new Button.OnClickListener() {
		public void onClick(View v) {
			if (mPosition > 0) {
				mPosition--;
				Constants.DEBUG_LOG(TAG, "Position = " + mPosition);
				mHandler.sendEmptyMessage(0);
			}
		}
	};

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
	 * Creates the dialogs used for saving and loading.
	 * @author mstanford
	 */
	protected AlertDialog onAlertDialog(int dialogtype) {
		final AlertDialog.Builder buildDialog = new AlertDialog.Builder(this);
		switch(dialogtype){
		case Constants.DIALOG_SAVE_AS:
			buildDialog.setTitle(getString(R.string.dialog_save_title));
			final EditText mUserInput = new EditText(this);
			buildDialog.setView(mUserInput);
			buildDialog.setPositiveButton(R.string.dialog_save_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Editable mUserTextInput = (Editable)mUserInput.getText(); 
					String mSaveName = mUserTextInput.toString();
					mSaveName = mSaveName.replaceAll("[^a-zA-Z0-9\\s]", "");
					//Save the project, also renames the shared preference
					mService.saveStoryBook(mSaveName);
				}
			});
			buildDialog.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			break;
		case Constants.DIALOG_LOAD_PROJECT:
			buildDialog.setTitle(getString(R.string.dialog_load_title));
			ListView mTextViewFileList = new ListView(this);
			//Grabs a list of all the files in the directory we are saving to
			final File[] mFileList = mService.getLoadFiles();
			//Array of filenames in string form
			ArrayList<String> mTextList = new ArrayList<String>();
			if (true)//(mFileList != null)
			{				
				for (int i =0; i < mFileList.length; i++)
				{
					mTextList.add(i,mFileList[i].getName());
					Constants.DEBUG_LOG(TAG, "Filename: " + mFileList[i].getPath());
				}
			}
			//Array Adapter for the ListView
			ArrayAdapter<String> mFileAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mTextList);
			mTextViewFileList.setAdapter(mFileAdapter);
			mTextViewFileList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view,int position, long id) {
					try {
						mService.loadStoryBook(mFileList[position]);
						mHandler.sendEmptyMessage(0);
						mDialog.dismiss();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});
			buildDialog.setView(mTextViewFileList);
			buildDialog.setNegativeButton(R.string.dialog_back_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			break;
		}
		
		mDialog = buildDialog.create();
		return mDialog;
	}

	/**
	 * Gets a bitmap from the image file.
	 * @author mstanford
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Constants.DEBUG_LOG(TAG, "Picture Activity return");

		if (requestCode == Constants.CODE_RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			try {
				Constants.DEBUG_LOG(TAG, "Picture Path: " + picturePath);
				mBitMapPicture = BitmapFactory.decodeFile(picturePath);
				//Check to see if the picture is too large
				if(mBitMapPicture.getHeight() > Constants.MAX_BITMAP_X || mBitMapPicture.getWidth() > Constants.MAX_BITMAP_y){
					Toast.makeText(mContext, "Images must be 4096x4096 or smaller.", Toast.LENGTH_LONG).show();
				}
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
	 * The onActivityResult grabs the results from the intent
	 * @author mstanford
	 */
	public void loadImage(){
		Intent mIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(mIntent, Constants.CODE_RESULT_LOAD_IMAGE);
	}
	
	/**
	 * Saves the book.  
	 * @param code Set in Constants
	 * @author mstanford
	 */
	public void saveStoryBook(int code){
		switch(code){
		case Constants.CODE_SAVE_AS: //Save as
			Dialog saveDialog = onAlertDialog(Constants.DIALOG_SAVE_AS);
			saveDialog.show();
			break;
		case Constants.CODE_SAVE: //save current filename
			mService.saveStoryBook();
			break;
		default:
			Toast.makeText(mContext, "Invalid Save Code!", Toast.LENGTH_LONG).show();
			break;
		}
	}
	
	public void loadStoryBook(){
		Dialog loadDialog = onAlertDialog(1);
		loadDialog.show();
	}

	/**
	 * Creates a new page.
	 * 
	 * @param pageType Look at Page class to see the page types.
	 * @author mstanford
	 */
	public void createNewPage(int pageType){
		//Make new page
		if(mPosition != 0){
			Constants.DEBUG_LOG(TAG, "New page hit");
			mPosition++;
			Page mNewPage = new Page(mContext,pageType); 
			Constants.DEBUG_LOG(TAG, "Attempting to add page");
			mService.addPage(mNewPage, mPosition);
			Constants.DEBUG_LOG(TAG, "Page added at " + mPosition);
			mHandler.sendEmptyMessage(0);
		}
	}

	/**
	 * Returns a boolean array.  Used for toggle buttons or what not
	 *
	 * TODO: implement this so I can have a toggle button for formatting.  This need to be called
	 * everytime the page is updated
	 * @return bool array 1-bold, 2-italics, 3-normal
	 * @author mstanford
	 */
	public boolean[] isTextFormated(String string){
		boolean[] mBool = {false,false,false};
		return mBool;
	}

	/**
	 * I originally was going to use HTML tags for formatting texts but I ran into too many issues
	 * parsing them and removing them that I decided to use spannable :( I guess function trumps form for this.
	 * 
	 * @author mstanford
	 */
	public void onTextFormat(int formatType){

		TextView mTextView = (TextView) mViewFlipper.getCurrentView().findViewById(R.id.tv_story_text);
		String mFormattedString = (String) mTextView.getText();

		int startSelection=mTextView.getSelectionStart();
		int endSelection=mTextView.getSelectionEnd();

		SpannableStringBuilder mStringBuilder = new SpannableStringBuilder(mFormattedString);

		switch (formatType){
		case Constants.CODE_FORMAT_BOLD:
			StyleSpan mBold = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
			mStringBuilder.setSpan(mBold, startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			break;
		case Constants.CODE_FORMAT_ITALIC:
			StyleSpan mItalic = new StyleSpan(android.graphics.Typeface.ITALIC); // Span to make text italic                                     
			mStringBuilder.setSpan(mItalic, startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			break;
		case Constants.CODE_FORMAT_NORMAL:
			StyleSpan mHeader = new StyleSpan(android.graphics.Typeface.NORMAL); // Span to make text italic                                     
			mStringBuilder.setSpan(mHeader, startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			break;
		}

		mTextView.setText(mStringBuilder);
	}


	/**
	 * Save the position of the ViewFlipper
	 * @author mstanford
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(Constants.KEY_SAVE_BUNDLE_POSITION, mPosition);
	}

	/**
	 * Recall the position of the ViewFlipper
	 * @author mstanford
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mPosition = savedInstanceState.getInt(Constants.KEY_SAVE_BUNDLE_POSITION);
	}

	@Override
	protected void onStart(){
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		/****************************************
		 * SERVICES
		 ***************************************/
		// Bind to LocalService
		Intent intent = new Intent(this, ServiceStoryBook.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		Constants.DEBUG_LOG(TAG, "Service: " + mService);

		if(mService != null && mService.getArray() == null){
			try {
				Constants.DEBUG_LOG(TAG, "onResume : Loading Storybook ");
				//Load the arraylist into adapter, side effect is array gets loaded into service.
				mViewAdapter.setArray(mService.loadStoryBook());
			} catch (JSONException e) {
				Constants.DEBUG_LOG(TAG, "onResume: " + "JSON Error");
				e.printStackTrace();
			} catch (IOException e) {
				Constants.DEBUG_LOG(TAG, "onResume: " + "IO Error");
				e.printStackTrace();
			}
		}

		mHandler.sendEmptyMessage(0);

		/*****************************************
		 *BROADCAST RECEIEVER  
		 *****************************************/
		// Register broadcast recievers
		IntentFilter mIntent = new IntentFilter();
		mIntent.addAction(Constants.KEY_REFRESH_ADAPTER_INTENT);
		mIntent.addAction(Constants.KEY_TEXT_CHANGE_INTENT);
		mIntent.addAction(Constants.KEY_IMAGE_CLICKED_INTENT);
		registerReceiver(ActivityReceiver, mIntent);
		mHandler.sendEmptyMessage(0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mService.saveStoryBook();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//Unbind the Services
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	protected void onStop(){
		super.onStop();
		// Unregister the Broadcast reciever
		unregisterReceiver(ActivityReceiver);
	}

	/*****************************************************************
	 * ONCREATE
	 * 
	 * @author mstanford
	 *****************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		mTextPageNumber.setGravity(Gravity.CENTER_HORIZONTAL);

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
			return true;
		case R.id.menu_add_page:
			createNewPage(Constants.PAGE_TYPE_TEXT);
			return true;
		case R.id.menu_save:
			saveStoryBook(Constants.CODE_SAVE);
			return true;
		case R.id.menu_load:
			loadStoryBook();
			return true;
		case R.id.menu_save_as:
			saveStoryBook(Constants.CODE_SAVE_AS);
			return true;
		case R.id.menu_delete_page:
			return true;
		case R.id.menu_new_project:
			Intent mNewIntent = new Intent(mContext,StartActivity.class);
			startActivity(mNewIntent);
			return true;
		case R.id.menu_email:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
