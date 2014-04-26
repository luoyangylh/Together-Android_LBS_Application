package com.example.map;

import android.app.Application;

public class Data extends Application {
	private String user_id;
	
	public String getID() {
		return this.user_id;
	}
	
	public void setID(String id) {
		this.user_id = id;
	}
	
	@Override
	public void onCreate() {
		user_id = "1";
		super.onCreate();
	}
}
