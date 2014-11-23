package com.example.ashworx.items;

public class Devices {

	@com.google.gson.annotations.SerializedName("id")
	private String id;
	
	@com.google.gson.annotations.SerializedName("userId")
	private String deviceUser;
	
	@com.google.gson.annotations.SerializedName("handle")
	private String deviceHandle;
	
	@com.google.gson.annotations.SerializedName("hubregistrationid")
	private String regId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceUser() {
		return deviceUser;
	}

	public void setDeviceUser(String deviceUser) {
		this.deviceUser = deviceUser;
	}

	public String getDeviceHandle() {
		return deviceHandle;
	}

	public void setDeviceHandle(String deviceHandle) {
		this.deviceHandle = deviceHandle;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deviceHandle == null) ? 0 : deviceHandle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Devices other = (Devices) obj;
		if (deviceHandle == null) {
			if (other.deviceHandle != null)
				return false;
		} else if (!deviceHandle.equals(other.deviceHandle))
			return false;
		return true;
	}
	
	
	
	
}
