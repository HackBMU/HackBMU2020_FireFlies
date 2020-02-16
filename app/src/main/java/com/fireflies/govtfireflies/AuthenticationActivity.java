package com.fireflies.govtfireflies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AuthenticationActivity extends AppCompatActivity {

	private static final String TAG = "AuthenticationActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);

		Log.d(TAG, "onCreate: " + new AuthPreferences(this).getUserId());
		AuthPreferences authPreferences = new AuthPreferences(this);
		if (authPreferences.getUserId() != null) {
			startActivity(new Intent(this, HomeActivity.class));
			finish();
		}

		getSupportActionBar().setTitle("Login");

		if (findViewById(R.id.fragment_container) != null) {

			if (savedInstanceState != null) {
				return;
			}

			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new LoginFragment()).commit();
		}
	}

	public void setToolbarTitle(int title) {
		getSupportActionBar().setTitle(title);
	}
}
