/**
 * 
 */
package com.covent.StoryBook;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * @author admin
 *
 */
public class StoryImageButton extends ImageButton {
	
	private final String TAG = "StoryImage";

	/**
	 * @param context
	 * @param attrs
	 */
	public StoryImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		//setBackgroundColor(17170445); //clear but null/0 works also
		setBackgroundColor(0);
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO Auto-generated method stub
       
        if(event.getAction() == android.view.MotionEvent.ACTION_DOWN){
        	 Constants.DEBUG_LOG(TAG, "Ontouch hit");
        	 //Create the intent to send
        	 Intent mIntent = new Intent(Constants.KEY_IMAGE_CLICKED_INTENT);
        	 getContext().sendBroadcast(mIntent);
        }
        return super.onTouchEvent(event);
    }
}
