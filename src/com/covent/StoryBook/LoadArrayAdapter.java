/**
 * Was thinking about using a custom array adapter for the load file file[] array.  
 * Might as well try using the default android adapter first, for once.
 * 
 * @author mStanford
 */
package com.covent.StoryBook;

import android.content.Context;
import android.widget.ArrayAdapter;

public class LoadArrayAdapter<T> extends ArrayAdapter<T> {

	public LoadArrayAdapter(Context context, int resource) {
		super(context, resource);
	}
}
