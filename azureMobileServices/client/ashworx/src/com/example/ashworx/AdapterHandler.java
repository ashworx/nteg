package com.example.ashworx;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.ashworx.adapters.ChatAdapter;
import com.example.ashworx.items.ChatMessage;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

public class AdapterHandler extends NotificationsHandler {

	public static final int NOTIFICATION_ID = 1;
	NotificationCompat.Builder builder;
	Context ctx;

	private NotificationManager mNotificationManager;

	@com.google.gson.annotations.SerializedName("handle")
	private static String mHandle;

	private static ChatAdapter listAdapter;

	private static String intendedUser;

	public static String getHandle() {
		return mHandle;
	}

	public static final void setHandle(String handle) {
		mHandle = handle;
	}

	public static void setAdapter(ChatAdapter adapter) {
		AdapterHandler.listAdapter = adapter;
	}

	public static void setIntendedUser(String intendedUser) {
		AdapterHandler.intendedUser = intendedUser;
	}

	@Override
	public void onRegistered(Context context, String gcmRegistrationId) {
		super.onRegistered(context, gcmRegistrationId);
		setHandle(gcmRegistrationId);
		if (context instanceof ContactSelectActivity)
			((ContactSelectActivity) context)
					.registerForPush(gcmRegistrationId);
	}

	@Override
	public void onReceive(Context context, Bundle bundle) {
		// super.onReceive(context, bundle);
		ChatMessage message = new ChatMessage();
		// String receiver = bundle.getString("reciever");
		// if(receiver.equals(intendedUser)){
		message.setSender(bundle.getString("sender"));
		message.setMessage(bundle.getString("message"));
		message.setReceiver(bundle.getString("receiver"));

		if (intendedUser != null && listAdapter != null) {
			if (message.getSender().equals(intendedUser)) {
				listAdapter.add(message);
			}

		} else {
			intendedUser = bundle.getString("sender");
		}

		ctx = context;

		sendNotification(bundle);

	}

	private void sendNotification(Bundle bundle) {
		mNotificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent startChat = new Intent(ctx, ChatActivity.class);
		startChat.putExtra("notif.thisDevice", bundle.getString("receiver"));
		startChat.putExtra("contactName", intendedUser);

		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
				startChat, 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				ctx)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Notification Hub Demo")
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(bundle
								.getString("message")))
				.setContentText(bundle.getString("message"));

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
