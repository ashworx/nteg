package com.example.ashworx;

import java.net.MalformedURLException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ashworx.adapters.ContactsAdapter;
import com.example.ashworx.items.Devices;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.Registration;
import com.microsoft.windowsazure.mobileservices.RegistrationCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class ContactSelectActivity extends Activity {


	/**
	 * Mobile Service Client reference
	 */
	private MobileServiceClient mClient;

	/**
	 * Mobile Service Table used to access data
	 */
	private MobileServiceTable<Devices> mDevices;

	/**
	 * x Adapter to sync the items list with the view
	 */
	private ContactsAdapter mAdapter;

	private MobileServiceUser loggedInUser;
	private String capturedValue;
	
	private Devices thisDevice;
	
	public Devices getDevice(){
		return thisDevice;
	}
	
	public MobileServiceClient getMobileServiceClient(){
		return mClient;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_select);
		
		try {
			// Create the Mobile Service Client instance, using the provided
			// Mobile Service URL and key
			mClient  = new MobileServiceClient(
					"https://ntegchatservice.azure-mobile.net/",
					"wAZWstHZqDGFKHJSpOWhXnKoWEcIie32", this);
			thisDevice = new Devices();

			NotificationsManager.handleNotifications(this, Constants.SENDER_ID, AdapterHandler.class);
			
			if(mClient.getCurrentUser()==null){
				authenticate();
			}
			
		} catch (MalformedURLException e) {
			createAndShowDialog(
					new Exception(
							"There was an error creating the Mobile Service. Verify the URL"),
					"Error");
		}
	}

	private void registerDevice() {
		mDevices.where().field("id").eq(loggedInUser.getUserId())
				.execute(new TableQueryCallback<Devices>() {
					
					public void onCompleted(List<Devices> result, int count,
							Exception exception, ServiceFilterResponse response) {
						if (exception == null) {
							if (result.size() == 0) {
								thisDevice.setDeviceHandle(AdapterHandler.getHandle());
								thisDevice.setId(loggedInUser.getUserId());
								captureValue();
								
							} else {
								thisDevice = result.get(0);
							}

						} else {
							createAndShowDialog(exception, "Error");
						}
					}
				});

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
			// refreshItemsFromTable();

			if (loggedInUser == null) {
				authenticate();
			} else {
				refreshData();
			}

		}

		return true;
	}


	private void captureValue() {
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_with_input, null);

		TextView dialogBoxMessage = (TextView) promptsView
				.findViewById(R.id.textView1);
		dialogBoxMessage.setText("Enter Login ID");

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// get user input and set it to result
						// edit text
						capturedValue = userInput.getText().toString();
						thisDevice.setDeviceUser(capturedValue);

						mDevices.insert(thisDevice,
								new TableOperationCallback<Devices>() {

									public void onCompleted(
											Devices entity,
											Exception exception,
											ServiceFilterResponse response) {

									}
								});
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
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
		if (exception.getCause() != null) {
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

	private void authenticate() {

		// Login using the Google provider.
		mClient.login(MobileServiceAuthenticationProvider.Google,
				new UserAuthenticationCallback() {

					@Override
					public void onCompleted(MobileServiceUser user,
							Exception exception, ServiceFilterResponse response) {

						if (exception == null) {
							loggedInUser = user;
							createTable();
							registerDevice();
							refreshData();
						} else {
							createAndShowDialog(
									"You must log in. Login Required", "Error");
						}
					}
				});
	}

	private void createTable() {

		// Get the Mobile Service Table instance to use
		mDevices = mClient.getTable(Devices.class);

		// Create an adapter to bind the items with the view
		mAdapter = new ContactsAdapter(this, R.layout.contact);
		ListView listViewContacts = (ListView) findViewById(R.id.listViewContact);
		listViewContacts.setAdapter(mAdapter);
	}
	
	private void refreshData(){
		mDevices.execute(new TableQueryCallback<Devices>() {
			
			@Override
			public void onCompleted(List<Devices> result, int arg1, Exception exception,
					ServiceFilterResponse arg3) {
				if(exception==null){
					int thisDevicePos = result.indexOf(thisDevice);
					if(thisDevicePos!=-1){
						result.remove(thisDevicePos);
						mAdapter.clear();
						mAdapter.addAll(result);
					} else {
						refreshData();
					}
				} else {
					createAndShowDialog(
							"Error fetching contacts", "Error");
					exception.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Registers mobile services client to receive GCM push notifications
	 * 
	 * @param gcmRegistrationId
	 *            The Google Cloud Messaging session Id returned by the call to
	 *            GoogleCloudMessaging.register in
	 *            NotificationsManager.handleNotifications
	 */
	public void registerForPush(String gcmRegistrationId) {
		mClient.getPush().register(gcmRegistrationId, null,
				new RegistrationCallback() {
					@Override
					public void onRegister(Registration registration,
							Exception exception) {
						if (exception == null) {
							thisDevice.setRegId(registration.getRegistrationId());
						}
					}
				});
	}
}