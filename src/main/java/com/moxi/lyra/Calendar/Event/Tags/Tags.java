package com.moxi.lyra.Calendar.Event.Tags;

public enum Tags {
	WORK, PERSONAL,CUSTOM;
	public static String custom(String value){
		return CUSTOM.name()+ ":" + value.toLowerCase();
	}
}
