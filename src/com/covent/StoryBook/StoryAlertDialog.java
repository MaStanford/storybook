package com.covent.StoryBook;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class StoryAlertDialog extends Activity {

	AlertDialog alert;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);

		Button connect = new Button(this);
		connect.setText("Don't push me");

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Profile");
		alertBuilder.setView(connect);


		connect.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				alert.dismiss();
			}
		});

		alert = alertBuilder.create();
	}
}


