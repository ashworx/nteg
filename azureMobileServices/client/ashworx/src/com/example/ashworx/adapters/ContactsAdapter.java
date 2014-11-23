package com.example.ashworx.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ashworx.ChatActivity;
import com.example.ashworx.ContactSelectActivity;
import com.example.ashworx.R;
import com.example.ashworx.items.Devices;

/**
 * Adapter to bind a ToDoItem List to a view
 */
public class ContactsAdapter extends ArrayAdapter<Devices> {

	/**
	 * Adapter context
	 */
	Context mContext;
	

	/**
	 * Adapter View layout
	 */
	int mLayoutResourceId;

	public ContactsAdapter(Context context, int layoutResourceId) {
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

		final Devices currentItem = getItem(position);

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}

		row.setTag(currentItem);
		final TextView checkBox =  (TextView) row.findViewById(R.id.contact);
		checkBox.setText(currentItem.getDeviceUser());
		checkBox.setEnabled(true);
		
		row.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent startChat = new Intent(mContext, ChatActivity.class);
				startChat.putExtra("contactName", currentItem.getDeviceUser());
				if(mContext instanceof ContactSelectActivity){
					startChat.putExtra("thisDevice", ((ContactSelectActivity)mContext).getDevice().getDeviceUser());
				}
				if (startChat.resolveActivity(mContext.getPackageManager()) != null) {
					mContext.startActivity(startChat);
				}
				
			}
		});

		return row;
	}

}
