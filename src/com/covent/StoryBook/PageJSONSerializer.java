package com.covent.StoryBook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.widget.Toast;

public class PageJSONSerializer {
	
	private static final String TAG = "PageJSONSerializer";
	private Context mContext;
	
	public PageJSONSerializer(Context context,String filename){
		mContext = context;
	}
	//TODO add filename parameter
	public void saveJSON(ArrayList<Page> pages,File filename) throws JSONException, IOException{
		
		Constants.DEBUG_LOG(TAG,"Before Save.");
		//Build the JSON array
		JSONArray mJArray =  new JSONArray();
		//initialize array
		for (Page page : pages){
			mJArray.put(page.toJSON());
		}
		
		Constants.DEBUG_LOG(TAG,"Array built");
		
		//Write to disk
		Writer mWriter = null;
		try{
			Constants.DEBUG_LOG(TAG,"Trying to write to filename: " + filename.toString());
			
			//OutputStream mOut = mContext.openFileOutput(filename.getAbsolutePath() + ".json", Context.MODE_PRIVATE);
			
			FileOutputStream mFileOutputStream = new FileOutputStream(filename);
			
			Constants.DEBUG_LOG(TAG,"Before Writer");
			mWriter = new OutputStreamWriter(mFileOutputStream);
			mWriter.write(mJArray.toString());
			Constants.DEBUG_LOG(TAG,"Write complete");
		} finally {
			if(mWriter != null){
				mWriter.close();
			}
		}
	}
	
	public ArrayList<Page> loadStoryBook(File filename) throws JSONException, IOException {
		
		Toast.makeText(mContext, "Loading " + filename.getName()  + "...", Toast.LENGTH_LONG).show();
		
		Constants.DEBUG_LOG(TAG, "Load Called");
		ArrayList<Page> mStoryBook = new ArrayList<Page>();
		BufferedReader mBufferedReader = null;
		Constants.DEBUG_LOG(TAG, "Before input stream");
		try{
			//InputStream mInStream = mContext.openFileInput(filename);
			InputStream mInStream = new FileInputStream(filename);
			mBufferedReader = new BufferedReader(new InputStreamReader(mInStream));
			StringBuilder mJSONString = new StringBuilder();
			//Have a placeholder for each string
			String mLine = null;
			//read each line and append to the json string
			while((mLine = mBufferedReader.readLine()) != null ){
				mJSONString.append(mLine);
			}
			//Grab JSON tokens from string
			JSONArray mJSONArray = (JSONArray) new JSONTokener(mJSONString.toString()).nextValue();
			//build array of pages from the tokens
			for(int i = 0; i < mJSONArray.length(); i++){
				Constants.DEBUG_LOG(TAG, "adding page: " + i);
				mStoryBook.add(new Page(mJSONArray.getJSONObject(i)));
			}
			Constants.DEBUG_LOG(TAG, "Read Complete");
		} catch (FileNotFoundException mError){
			
		} finally {
			if(mBufferedReader != null){
				mBufferedReader.close();
			}
		}
		return mStoryBook;
	}
}
