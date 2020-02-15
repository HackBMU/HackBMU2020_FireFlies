package com.fireflies.govtfireflies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.storage.StorageMetadata;

import butterknife.BindView;

public class HomeActivity extends AppCompatActivity {

	@BindView(R.id.rv_documents)
	RecyclerView rvDocuments;

	private static final String TAG = "HomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}
}
