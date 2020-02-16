package com.fireflies.govtfireflies;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

	@BindView(R.id.rv_documents)
	RecyclerView rvDocuments;

	@BindView(R.id.floating_btn)
	FloatingActionButton floatingActionButton;

	private static Uri filePath;
	private static File actualFile;

	private static String folderpath;
    static final int REQUEST_IMAGE_CAPTURE = 1;

	private static final String TAG = "HomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		ButterKnife.bind(this);

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			// Permission is not granted
			ActivityCompat.requestPermissions(HomeActivity.this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
					4);
		}

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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Toast.makeText(this, "ImageCaptured", Toast.LENGTH_SHORT).show();
			filePath = Uri.fromFile(actualFile);
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

	public void chooseImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
	}

	public void openCamera(){
		Intent camera = new Intent( );
		camera.setAction( MediaStore.ACTION_IMAGE_CAPTURE );
		String newPicFile = System.currentTimeMillis( ) + ".jpg";
		actualFile = new File( folderpath + "/" + newPicFile );
		camera.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile( actualFile ) );
		startActivityForResult( camera, REQUEST_IMAGE_CAPTURE );
	}

	void createfolder() {
		boolean success = true;
		File folder = new File( Environment.getExternalStorageDirectory( ) + File.separator + "GovtFireflies" );
		if (!folder.exists( )) {
			success = folder.mkdirs( );
		}
		if (success) {
			folderpath = folder.getAbsolutePath();
			Log.d(TAG, "createfolder: " + folderpath);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 4: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission was granted, yay! Do the
					createfolder();
					// contacts-related task you need to do.
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request.
		}
	}

	@OnClick(R.id.floating_btn)
	void onClickChooseOption() {
        FileChooserDialogFragment fileChooserDialogFragment = new FileChooserDialogFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fileChooserDialogFragment.show(fragmentTransaction, FileChooserDialogFragment.TAG);
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

    public static class FileChooserDialogFragment extends DialogFragment {

        private static final String TAG = "FileChooserDialogFragme";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragement_file_chooser_fragment, container, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @OnClick(R.id.btn_camera)
        void onClickCamera() {
            ((HomeActivity)getActivity()).openCamera();
            dismiss();
        }

        @OnClick(R.id.btn_gallery)
        void onClickGallery() {
            ((HomeActivity)getActivity()).chooseImage();
            dismiss();
        }
    }

}
