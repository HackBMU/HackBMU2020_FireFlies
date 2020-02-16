package com.fireflies.govtfireflies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

	@BindView(R.id.rv_documents)
	RecyclerView rvDocuments;

	@BindView(R.id.floating_btn)
	FloatingActionButton floatingActionButton;

	@BindView(R.id.progress_bar)
	ProgressBar progressBar;

	private static Uri filePath;
	private List<Document> documentList;
	private static final String TAG = "HomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		ButterKnife.bind(this);

		progressBar.setVisibility(View.VISIBLE);
		documentList = new ArrayList<>();
		rvDocuments.setLayoutManager(new LinearLayoutManager(this));
		DocumentAdapter adapter = new DocumentAdapter(documentList, this);
		rvDocuments.setAdapter(adapter);

		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("images");
		ref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				progressBar.setVisibility(View.GONE);
				for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
					Map<String, String> map = (HashMap) snapshot.getValue();
					Document document = new Document(map.get("name"), map.get("by"), map.get("to"), String.valueOf(map.get("time")), map.get("type"));
					documentList.add(document);
				}
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				progressBar.setVisibility(View.GONE);
				Log.d(TAG, "onCancelled: " + databaseError.getMessage());
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
			filePath = data.getData();
			Log.d(TAG, "onActivityResult: " + getContentResolver().getType(filePath));

			Bundle bundle = new Bundle();
			bundle.putString("filePath", filePath.getPath());
			Log.d(TAG, "onActivityResult: " + filePath.getPath());
			PicUploadDialogFragment picUploadDialogFragment = new PicUploadDialogFragment();
			picUploadDialogFragment.setArguments(bundle);
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			picUploadDialogFragment.show(fragmentTransaction, PicUploadDialogFragment.TAG);
		}
	}

	@OnClick(R.id.floating_btn)
	void onClickAddPhoto() {
		chooseImage();
	}

	private void chooseImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
	}

	public static class PicUploadDialogFragment extends DialogFragment {

		@BindView(R.id.img_picked)
		ImageView imageView;

		private StorageReference storageReference;
		private static final String TAG = "PicUploadDialogFragment";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_pic_upload_dialog, container, false);
			ButterKnife.bind(this, view);

			FirebaseStorage storage = FirebaseStorage.getInstance();
			storageReference = storage.getReference();

			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
				imageView.setImageBitmap(bitmap);

			} catch (IOException e) {
				e.printStackTrace();
			}
			return view;
		}

		@OnClick(R.id.btn_upload_pic)
		void onClickUploadImage() {
			uploadImage();
		}

		@OnClick(R.id.btn_cancel_dialog)
		void onClickCancelDialog() {
			dismiss();
		}

		private void uploadImage() {

			if (filePath != null) {
				final ProgressDialog progressDialog = new ProgressDialog(getActivity());
				progressDialog.setTitle("Uploading...");
				progressDialog.show();

				StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
				ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
					progressDialog.dismiss();
					Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();

				}).addOnFailureListener(e -> {
					progressDialog.dismiss();
					Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
				});
			}
		}
	}

}
