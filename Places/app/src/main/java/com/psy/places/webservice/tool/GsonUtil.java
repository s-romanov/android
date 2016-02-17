package com.psy.places.webservice.tool;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by sromanov on 16/02/2016.
 */
public class GsonUtil {
	
	private static Gson DEFAULT_GSON = new Gson();
	
	private GsonUtil() {
	}
	
	public static <T> T jsonToObject(Gson gson, String jsonString, Type resultType) {
		Gson g = gson == null ? DEFAULT_GSON : gson;
		return g.fromJson(jsonString, resultType);
	}
}
