package com.covent.StoryBook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import com.covent.StoryBook.ServiceStoryBook.LocalBinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class StartActivity extends Activity {
	final String TAG = "StartActivity";
	//Buttons
	Button mNewButton;
	Button mLoadButton;
	//Service
	private ServiceStoryBook mService;
	boolean mBound = false;
	//context
	Context mContext;
	//Preferences
	PreferenceManager mPreferenceManager;


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
	 * Called when the user wants to start a new project.
	 */
	public void startNewProject(){	
		Dialog mDialog = onCreateProjectDialog(Constants.DIALOG_NEW_PROJECT_NAME);
		mDialog.show();
	}

	/**
	 * Sets up the ArrayList of pages.  Makes a cover, Table of Contents and a first page
	 */
	public void setupNewPageArray(){
		{
			ArrayList<Page> mArrayList = new ArrayList<Page>();
			Page mPage = new Page(mContext,2);
			mArrayList.add(mPage);
			Page mPage2 = new Page(mContext,3);
			mArrayList.add(mPage2);
			Page mPage3 = new Page(mContext,0);
			mArrayList.add(mPage3);
			mService.setArray(mArrayList);
		}
		launchMainActivity();
	}

	protected void launchMainActivity(){
		Intent mIntent = new Intent(mContext, ActivityMain.class);
		startActivity(mIntent);
	}

	/**
	 * Called when the user wants to load a project
	 */
	public void loadProject(){	
		Dialog mDialog = onCreateProjectDialog(Constants.DIALOG_LOAD_PROJECT);
		mDialog.show();
	}

	/*
	 * Dialogs
	 */
	protected AlertDialog onCreateProjectDialog(int dialogId) {
		final AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
		switch (dialogId){
		case Constants.DIALOG_NEW_PROJECT_NAME:
			mDialogBuilder.setTitle(getString(R.string.dialog_project_title));
			final EditText mUserInput = new EditText(this);
			mDialogBuilder.setView(mUserInput);
			mDialogBuilder.setPositiveButton(R.string.dialog_start_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Editable mUserTextInput = (Editable)mUserInput.getText(); 
					String mSaveName = mUserTextInput.toString();
					mSaveName = mSaveName.replaceAll("[^a-zA-Z0-9\\s]", "");
					//Set the name as shared preference
					mPreferenceManager.setFileName(mSaveName);
					//Set up the array and start the new project
					setupNewPageArray();
				}
			});
			mDialogBuilder.setNegativeButton(R.string.dialog_back_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			break;

		case Constants.DIALOG_LOAD_PROJECT:
			mDialogBuilder.setTitle(getString(R.string.dialog_load_title));
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
						launchMainActivity();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});
			mDialogBuilder.setView(mTextViewFileList);
			mDialogBuilder.setNegativeButton(R.string.dialog_back_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			break;
		default:
			break;
		}

		//Sets the alert dialog and returns it
		AlertDialog mDialog = mDialogBuilder.create();
		return mDialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Show my XML
		setContentView(R.layout.start_layout);

		mContext = this;

		mPreferenceManager = new PreferenceManager(mContext);

		mNewButton = (Button) findViewById(R.id.btn_new_project);
		mNewButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startNewProject();
			}
		});

		mLoadButton = (Button) findViewById(R.id.btn_load_project);
		mLoadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadProject();
			}
		});
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
	}


	@Override
	protected void onStop(){
		super.onStop();
		//Unbind the Services
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
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
			Intent intent = new Intent(this, StartActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_add_page:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
