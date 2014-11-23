package com.example.ashworx.items;

public class ChatMessage {
	
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	
	@com.google.gson.annotations.SerializedName("message")
	private String message;
	
	@com.google.gson.annotations.SerializedName("sender")
	private String sender;
	
	@com.google.gson.annotations.SerializedName("receiver")
	private String receiver;
	
	public String getId() {
		return id;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getReceiver() {
		return receiver;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}

}
