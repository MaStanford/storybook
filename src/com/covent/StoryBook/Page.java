package com.covent.StoryBook;

import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;

/**
 * Represents the page of a story book
 * Implements parceable for use with a custom view adapter - Not anymore.  Decided not to use parcelable
 * may have to impelment it later if needed but for now, at this scale it is not needed
 * Implements a JSON serializer for saving as a JSON object
 * @author mStanford
 *
 */
public class Page implements Parcelable{

	private static final String JSON_TEXT = "text";
	private static final String JSON_IMAGE= "image";
	private static final String JSON_PAINTED_TEXT = "painted_text";
	private static final String JSON_PAGE_TYPE = "page_type";


	/**
	 * Data members
	 **/
	private Bitmap mBitmapPicture = null;
	private Bitmap mBitmapText = null;
	private String mStoryText = null;
	private int mPageType = 0; //Defined in constants

	/**
	 * Create a page from a json object
	 * @param json
	 * @throws JSONException
	 */
	public Page(JSONObject json) throws JSONException{
		setBitmapPicture(getBitmapFromString(json.getString(JSON_IMAGE)));
		setStoryText(json.getString(JSON_TEXT));
		setBitmapText(getBitmapFromString(json.getString(JSON_PAINTED_TEXT)));
		setPageType(json.getInt(JSON_PAGE_TYPE));
	}

	public Page(Parcel parcellIn){
		setBitmapPicture(getBitmapFromString(parcellIn.readString()));
		setStoryText(parcellIn.readString());
		setBitmapPicture(getBitmapFromString(parcellIn.readString()));
		setPageType(parcellIn.readInt());
	} 

	/**
	 * Page type is defined in Constants.
	 * @param pageType
	 */
	public Page(Context context,int pageType) {
		setStoryText("Write text here.");
		setBitmapPicture(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher));
		setBitmapText(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher));
		setPageType(pageType);
	}

	/**
	 * Implements the Parcelable.Creator class.<p>
	 * Creates and returns a Page from a Parcel
	 * @author mStanford
	 */
	public static final Parcelable.Creator<Page> CREATOR = new Parcelable.Creator<Page>() {

		/**
		 * Creates a Page from a Parcel 
		 * @param in Parcel 
		 * @return Page created by Page(Parcel) constructor.
		 * @author mStanford
		 */
		public Page createFromParcel(Parcel in) {
			return new Page(in);
		}

		/**
		 * Returns an array of Pages.
		 * @author mstanford
		 */
		public Page[] newArray(int size) {
			return new Page[size];
		}
	};

	/**
	 * Make sure that the order you write to the parcel here is the 
	 * same order you grab in the Page(Parcel in) when writing the data members
	 * @param dest Parcel reference that is passes in.
	 * @param flags I have no idea what this is.  
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStoryText());
		dest.writeString(getStringFromBitmap(getBitmapPicture()));
		dest.writeString(getStringFromBitmap(getBitmapText()));
		dest.writeInt(getPageType());
	}


	/**
	 * Converts the Data members into a JSON Object
	 * @return JSON Object
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException{

		JSONObject mJSON = new JSONObject();

		mJSON.put(JSON_TEXT,getStoryText());
		mJSON.put(JSON_IMAGE,getStringFromBitmap(getBitmapPicture()));
		mJSON.put(JSON_PAINTED_TEXT,getStringFromBitmap(getBitmapText()));
		mJSON.put(JSON_PAGE_TYPE, getPageType());

		return mJSON;
	}

	/**
	 * Converts a Bitmap to a String
	 * @param bitmapPicture
	 * @return Encoded String
	 */
	public static String getStringFromBitmap(Bitmap bitmapPicture) {

		final int COMPRESSION_QUALITY = 100;
		String mEncodedImageString;
		ByteArrayOutputStream mByteArrayBitmapStream = new ByteArrayOutputStream();

		bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, mByteArrayBitmapStream);
		byte[] mByteArray = mByteArrayBitmapStream.toByteArray();

		mEncodedImageString = Base64.encodeToString(mByteArray, Base64.DEFAULT);

		return mEncodedImageString;
	}	

	/**
	 * Decodes a String into a bitmap
	 * @param jsonString Encoded String of a Bitmap
	 * @return Bitmap
	 */
	public static Bitmap getBitmapFromString(String jsonString) {

		byte[] mDecodedString = Base64.decode(jsonString, Base64.DEFAULT);
		Bitmap mDecodedByte = BitmapFactory.decodeByteArray(mDecodedString, 0, mDecodedString.length);
		return mDecodedByte;
	}

	/**
	 * I have no idea what this does.
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	public String getStoryText() {
		return mStoryText;
	}

	public void setStoryText(String storyText) {
		mStoryText = storyText;
	}
	
	public void setStoryText(Spanned storyText) {
		mStoryText = Html.toHtml(storyText);
	}

	public Bitmap getBitmapPicture() {
		return mBitmapPicture;
	}

	public void setBitmapPicture(Bitmap bitmapPicture) {
		mBitmapPicture = bitmapPicture;
	}

	public Bitmap getBitmapText() {
		return mBitmapText;
	}

	public void setBitmapText(Bitmap bitmapText) {
		mBitmapText = bitmapText;
	}

	/**
	 * 0-text,1-drawn,2-cover,3-toc
	 * @param pageType
	 */
	public void setPageType(int pageType){
		mPageType = pageType;
	}

	/**
	 * 0-text,1-drawn,2-cover,3-toc
	 * @return
	 */
	public int getPageType(){
		return mPageType;
	}
}
