package com.covent.StoryBook;

import java.util.ArrayList;

import android.annotation.SuppressLint;
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
@SuppressLint("CutPasteId")
public class ViewAdapter extends BaseAdapter {

	// Make an array of my data objects
	public ArrayList<Page> storybook;
	private LayoutInflater mInflater;
	private ViewFlipper mViewFlipper;

	/**
	 * The default constructor
	 * @author mstanford
	 */
	public ViewAdapter(Context context,ViewFlipper parent) {
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
		Constants.DEBUG_LOG("View", "getCount(): " + storybook.size());
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
		case Constants.PAGE_TYPE_TEXT: //The page is a text page.  Inflate the text page xml
			if(position % 2 == 0){
				convertView = mInflater.inflate(R.layout.page_layout_text_even, null);
			}else{ 
				convertView = mInflater.inflate(R.layout.page_layout_text_odd, null);
			}
			ImageButton mImageView1 = (ImageButton) convertView.findViewById(R.id.image_main_view);
			if(storybook.get(position).getBitmapPicture() != null)
				mImageView1.setImageBitmap(storybook.get(position).getBitmapPicture());
			EditText mEditText = (EditText) convertView.findViewById(R.id.tv_story_text);
			if(storybook.get(position).getStoryText() != null){
				//I originally used html to mark text, but switched it to spannable - mStanford
				//mEditText2.setText(Html.fromHtml(storybook.get(position).getStoryText()));
				mEditText.setText(storybook.get(position).getStoryText());
			}
			break;
			
			
		case Constants.PAGE_TYPE_IMAGE: //Drawn page with drawn bitmap
			//TODO: for drawn page
			break;
			
			
		case Constants.PAGE_TYPE_COVER: //The page is a cover page. Inflate the cover xml
			convertView = mInflater.inflate(R.layout.page_layout_cover, null);
			ImageButton mImageView = (ImageButton) convertView.findViewById(R.id.image_main_view);
			if(storybook.get(position).getBitmapPicture() != null)
				mImageView.setImageBitmap(storybook.get(position).getBitmapPicture());
			EditText mCoverEditText = (EditText) convertView.findViewById(R.id.tv_story_text);
			if(storybook.get(position).getStoryText() != null){
				//I originally used html to mark text, but switched it to spannable - mStanford
				//mEditText2.setText(Html.fromHtml(storybook.get(position).getStoryText()));
				mCoverEditText.setText(storybook.get(position).getStoryText());
			}
			break;
			
			
		case Constants.PAGE_TYPE_TOC: //The page is a table of contents. Inflate the TOC xml
			convertView = mInflater.inflate(R.layout.page_layout_toc, null);
			EditText mTOCEditText = (EditText) convertView.findViewById(R.id.tv_story_text);
			if(storybook.get(position).getStoryText() != null){
				//I originally used html to mark text, but switched it to spannable - mStanford
				//mEditText2.setText(Html.fromHtml(storybook.get(position).getStoryText()));
				mTOCEditText.setText(storybook.get(position).getStoryText());
			}
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
