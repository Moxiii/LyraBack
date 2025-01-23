package com.moxi.lyra.Calendar.Event.Tags;

public class TagsUtils {
public static boolean isCustomTag(String tag) {
	return tag.startsWith(Tags.CUSTOM.name()+ ":");
}
public static String getCustomTag(String tag) {
	if(isCustomTag(tag)){
		return tag.split(":")[1];
	}
	return null;
}
}
