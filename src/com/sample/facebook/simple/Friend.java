package com.sample.facebook.simple;

import com.google.gson.annotations.SerializedName;

public class Friend {

	private String name;
	@SerializedName("friend_count")
	
	private String friendCount;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFriendCount() {
		return friendCount;
	}
	public void setFriendCount(String friendCount) {
		this.friendCount = friendCount;
	}
}
