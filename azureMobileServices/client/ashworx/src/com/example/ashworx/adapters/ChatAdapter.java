package com.example.ashworx.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ashworx.R;
import com.example.ashworx.items.ChatMessage;

/**
 * Adapter to bind a ToDoItem List to a view
 */
public class ChatAdapter extends ArrayAdapter<ChatMessage> {

	/**
	 * Adapter context
	 */
	Context mContext;
	
	String thisUser;
	
	public void setThisUser(String user){
		thisUser = user;
	}

	/**
	 * Adapter View layout
	 */
	int mLayoutResourceId;

	public ChatAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);

		mContext = context;
		mLayoutResourceId = layoutResourceId;
	}

	/**
	 * Returns the view for a specific item on the list
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		final ChatMessage currentItem = getItem(position);

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}

		row.setTag(currentItem);
		final TextView checkBox =  (TextView) row.findViewById(R.id.message);
		String text = "".equals(currentItem.getSender())?currentItem.getMessage():currentItem.getSender() + " : " + currentItem.getMessage();
		checkBox.setText(text);
		checkBox.setEnabled(true);

		return row;
	}

}
