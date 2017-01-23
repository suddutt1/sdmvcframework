package com.ibm.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonUtil {

	private static final Gson _SERIALIZER = new GsonBuilder().serializeNulls()
			.setPrettyPrinting().create();
	
	public static String toJson(Object obj)
	{
		return (obj!=null ? _SERIALIZER.toJson(obj): "{}");
	}
}
