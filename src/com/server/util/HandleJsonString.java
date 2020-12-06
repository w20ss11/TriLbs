package com.server.util;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class HandleJsonString {
	static Gson gson;
	public static List<Map<String, String>> readJsonString(String gsonString) {
		List<Map<String, String>> readList = null;
		gson = new Gson();
		if (gson != null) {
			readList = gson.fromJson(gsonString,
					new TypeToken<List<Map<String, String>>>() {
					}.getType());
		}
		return readList;
	}
}
