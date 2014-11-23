package com.example.ashworx;

import java.net.MalformedURLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ashworx.adapters.ChatAdapter;
import com.example.ashworx.items.ChatMessage;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class ChatActivity extends Activity {
	

	/**
	 * Mobile Service Client reference
	 */
	private MobileServiceClient mClient;

	/**
	 * Mobile Service Table used to access data
	 */
	private MobileServiceTable<ChatMessage> mChatMessageTable;

	/**x	
	 * Adapter to sync the items list with the view
	 */
	private ChatAdapter mAdapter;

	/**
	 * EditText containing the "New ToDo" text
	 */
	private EditText mNewMessage;
	
	private String receiverId;
	private String thisDevice;
	

	/**
	 * Initializes the activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_to_do);
		
		// Initialize the progress bar
		
		TextView titleTextview = (TextView) findViewById(R.id.textViewTitle);
		titleTextview.setText(getIntent().getStringExtra("contactName"));
		receiverId = getIntent().getStringExtra("contactName");
		thisDevice = getIntent().getStringExtra("thisDevice");
		if(thisDevice==null){
			thisDevice = getIntent().getStringExtra("notif.thisDevice");
		}
		
		
		NotificationsManager.handleNotifications(this, Constants.SENDER_ID, AdapterHandler.class);
		
		AdapterHandler.setIntendedUser(getIntent().getStringExtra("contactName"));
		
		try {
			// Create the Mobile Service Client instance, using the provided
			// Mobile Service URL and key
			mClient  = new MobileServiceClient(
					"https://ntegchatservice.azure-mobile.net/",
					"wAZWstHZqDGFKHJSpOWhXnKoWEcIie32", this);
			
			authenticate();
			
			mNewMessage = (EditText) findViewById(R.id.textNewMessage);
			createTable();

		} catch (MalformedURLException e) {
			createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
		}
	}
	
	/**
	 * Initializes the activity menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**
	 * Select an option from the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			refreshItemsFromTable();
		}
		
		return true;
	}

	public String getReceiverId() {
		return receiverId;
	}

	/**
	 * Add a new item
	 * 
	 * @param view
	 *            The view that originated the call
	 */
	public void addItem(View view) {
		if (mClient == null) {
			return;
		}
		
		ChatMessage message = new ChatMessage();
		message.setMessage(mNewMessage.getText().toString());
		message.setSender(thisDevice);
		message.setReceiver(receiverId);
		
		mChatMessageTable.insert(message, new TableOperationCallback<ChatMessage>() {

			public void onCompleted(ChatMessage entity, Exception exception, ServiceFilterResponse response) {
				ChatMessage deliverNotif  = new ChatMessage();
				deliverNotif.setSender("");
				if(exception==null){
					deliverNotif.setMessage("(Sent)");
				} else {
					deliverNotif.setMessage("(Error)");
				}
				mAdapter.add(deliverNotif);
			}
		});
		
		mNewMessage.setText("");
		mAdapter.add(message);
	}
	
	
	/**
	 * Refresh the list with the items in the Mobile Service Table
	 */
	private void refreshItemsFromTable() {
		mAdapter.clear();
	}

	/**
	 * Creates a dialog and shows it
	 * 
	 * @param exception
	 *            The exception to show in the dialog
	 * @param title
	 *            The dialog title
	 */
	private void createAndShowDialog(Exception exception, String title) {
		Throwable ex = exception;
		if(exception.getCause() != null){
			ex = exception.getCause();
		}
		createAndShowDialog(ex.getMessage(), title);
	}

	/**
	 * Creates a dialog and shows it
	 * 
	 * @param message
	 *            The dialog message
	 * @param title
	 *            The dialog title
	 */
	private void createAndShowDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}
	
	
	private void createTable() {

	    // Get the Mobile Service Table instance to use
	    mChatMessageTable = mClient.getTable(ChatMessage.class);

	    mNewMessage = (EditText) findViewById(R.id.textNewMessage);

	    // Create an adapter to bind the items with the view
	    mAdapter = new ChatAdapter(this, R.layout.row_list_to_do);
	    mAdapter.setThisUser(thisDevice);
	    ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
	    listViewToDo.setAdapter(mAdapter);
	    AdapterHandler.setAdapter(mAdapter);

	    // Load the items from the Mobile Service
	    refreshItemsFromTable();
	}
	
	private void authenticate() {

		// Login using the Google provider.
		mClient.login(MobileServiceAuthenticationProvider.Google,
				new UserAuthenticationCallback() {

					@Override
					public void onCompleted(MobileServiceUser user,
							Exception exception, ServiceFilterResponse response) {

						if (exception == null) {	
							//Do something here
						} else {
							createAndShowDialog(
									"You must log in. Login Required", "Error");
						}
					}
				});
	}
	
	
}
