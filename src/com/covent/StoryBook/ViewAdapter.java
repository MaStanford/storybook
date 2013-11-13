package com.covent.StoryBook;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

/**
 * Extends the BaseAdapter class for our use of the ViewFlipper
 * @author mstanford
 */
public class ViewAdapter extends BaseAdapter {

	// Make an array of my data objects
	public ArrayList<Page> storybook;
	private Context mContext;
	private LayoutInflater mInflater;
	private ViewFlipper mViewFlipper;

	/**
	 * The default constructor
	 * @author mstanford
	 */
	public ViewAdapter(Context context,ViewFlipper parent) {
		mContext = context;
		mViewFlipper = parent;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Sets the array
	 *TODO use this when the intent UI refresh
	 */
	public void setArray(ArrayList<Page> page){
		storybook = page;
	}

	/**
	 * @return The count of how many objects in the dataset.
	 * @author mstanford
	 */
	public int getCount() {
		Constants.DEBUG_LOG("View", "getCount()");
		return storybook.size();
	}

	/**
	 * Returns an Object from the array that
	 * is pointed to by the supplied Index value.
	 * 
	 * @param  position Index value for desired dataobject
	 * @return Object pointed to by Index value.  
	 * @author mstanford
	 */
	public Object getItem(int position) {
		Constants.DEBUG_LOG("View", "getItem(int)");

		return storybook.get(position);
	}

	/**
	 * Doesn't do anything. 
	 * 
	 * @author mstanford
	 */
	public long getItemId(int position) {
		Constants.DEBUG_LOG("View", "getItemId(int)");
		return position;
	}

	/**
	 * @author mStanford
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		Constants.DEBUG_LOG("View", "getView()");

		switch(storybook.get(position).getPageType()){
		case 0: //The page is a text page.  Inflate the text page xml

			if(position % 2 == 0){
				convertView = mInflater.inflate(R.layout.page_layout_text_even, null);
			}else{ 
				convertView = mInflater.inflate(R.layout.page_layout_text_odd, null);
			}

			ImageButton mImageView1 = (ImageButton) convertView.findViewById(R.id.image_button_text_page);

			if(storybook.get(position).getBitmapPicture() != null)
				mImageView1.setImageBitmap(storybook.get(position).getBitmapPicture());

			EditText mEditText1 = (EditText) convertView.findViewById(R.id.tv_story_text);

			if(storybook.get(position).getStoryText() != null)
				mEditText1.setText(storybook.get(position).getStoryText());

			break;
		case 1: //Drawn page with drawn bitmap
			//TODO: for drawn page
			break;
		case 2: //The page is a cover page. Inflate the cover xml

			convertView = mInflater.inflate(R.layout.page_layout_cover, null);

			ImageButton mImageView = (ImageButton) convertView.findViewById(R.id.cover_image);

			if(storybook.get(position).getBitmapPicture() != null)
				mImageView.setImageBitmap(storybook.get(position).getBitmapPicture());

			EditText mEditText = (EditText) convertView.findViewById(R.id.cover_text);

			if(storybook.get(position).getStoryText() != null)
				mEditText.setText(storybook.get(position).getStoryText());
			break;
		case 3: //The page is a table of contents. Inflate the TOC xml
			convertView = mInflater.inflate(R.layout.page_layout_toc, null);

			EditText mEditText2 = (EditText) convertView.findViewById(R.id.toc_text);

			if(storybook.get(position).getStoryText() != null)
				mEditText2.setText(storybook.get(position).getStoryText());
			break;
		default:
			convertView = mInflater.inflate(R.layout.layout_error, null);
			break;
		}
		return convertView;
	}
	/**
	 * 
	 */
	@Override
	public void notifyDataSetChanged() {
		Constants.DEBUG_LOG("View", "notifyDataSetChanged()");
		super.notifyDataSetChanged();
		mViewFlipper.removeAllViews();
		for (int i = 0; i < getCount(); i++) {
			mViewFlipper.addView(getView(i, null, null));
		}
	}


}
