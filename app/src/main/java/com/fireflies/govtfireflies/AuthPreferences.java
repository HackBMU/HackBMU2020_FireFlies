package com.fireflies.govtfireflies;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthPreferences {

	private static String KEY_USER_ID = "user_id";
	private static String KEY_EMAIL = "email";

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	public AuthPreferences(Context context) {
		String PREF_NAME = "CorruptEnd";
		sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editor.apply();
	}

	public void setEmail(String email) {
		editor.putString(KEY_EMAIL, email);
		editor.apply();
	}

	public String getEmail() {
		return sharedPreferences.getString(KEY_EMAIL, null);
	}

	public void setUserId(String userId) {
		editor.putString(KEY_USER_ID, userId);
		editor.apply();
	}

	public String getUserId() {
		return sharedPreferences.getString(KEY_USER_ID, null);
	}

}
