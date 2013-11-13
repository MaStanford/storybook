package com.covent.StoryBook;

import com.covent.StoryBook.ServiceStoryBook.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
	
	public void setupNewProject(){	
		
		Page mPage = new Page(mContext,2);
		mService.addPage(mPage);
		Page mPage2 = new Page(mContext,3);
		mService.addPage(mPage2);
		Page mPage3 = new Page(mContext,0);
		mService.addPage(mPage3);
		
		Intent mIntent = new Intent(mContext, ActivityMain.class);
		startActivity(mIntent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Show my XML
		setContentView(R.layout.start_layout);
		
		mContext = this;
		
		mNewButton = (Button) findViewById(R.id.btn_new_project);
		mNewButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setupNewProject();
			}
		});
		
		mLoadButton = (Button) findViewById(R.id.btn_load_project);
		mLoadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
			Intent intent = new Intent(this, ActivityMain.class);
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
