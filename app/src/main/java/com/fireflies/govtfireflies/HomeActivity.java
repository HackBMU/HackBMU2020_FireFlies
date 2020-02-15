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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.floating_btn)
    FloatingActionButton floatingActionButton;

    private static Uri filePath;

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
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

        private FirebaseStorage storage;
        private StorageReference storageReference;

        private static final String TAG = "PicUploadDialogFragment";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_pic_upload_dialog, container, false);
            ButterKnife.bind(this, view);

            storage = FirebaseStorage.getInstance();
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
