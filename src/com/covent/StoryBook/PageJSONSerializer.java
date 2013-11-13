package com.covent.StoryBook;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

public class PageJSONSerializer {
	
	private Context mContext;
	
	public PageJSONSerializer(Context context,String filename){
		mContext = context;
	}
	//TODO add filename parameter
	public void saveJSON(ArrayList<Page> pages,String filename) throws JSONException, IOException{
		//Build the JSON array
		JSONArray mJArray =  new JSONArray();
		//initialize array
		for (Page page : pages){
			mJArray.put(page.toJSON());
		}
		
		//Write to disk
		Writer mWriter = null;
		try{
			OutputStream mOut = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
			mWriter = new OutputStreamWriter(mOut);
			mWriter.write(mJArray.toString());
		} finally {
			if(mWriter != null){
				mWriter.close();
			}
		}
	}

}
