package com.fireflies.govtfireflies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

	private List<Document> documentList;
	private Context context;

	private static final String TAG = "DocumentAdapter";

	public DocumentAdapter(List<Document> documentList, Context context) {
		this.documentList = documentList;
		this.context = context;
	}

	@NonNull
	@Override
	public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.row_document, parent, false);
		return new DocumentViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
		Document document = documentList.get(position);
		holder.txtDocumentName.setText(document.getName());
		holder.txtDocumentTime.setText(document.getTime());

		StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("images/" + document.getBy());

		long HALF_MEGABYTE = 512 * 512;
		imagesRef.getBytes(HALF_MEGABYTE).addOnSuccessListener(bytes -> {
			Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			holder.imgDocument.setImageBitmap(Bitmap.createScaledBitmap(bmp, holder.imgDocument.getWidth(),
					holder.imgDocument.getHeight(), false));

		}).addOnFailureListener(exception -> {
			holder.imgDocument.setImageResource(R.drawable.ic_no_image);
		});
	}

	@Override
	public int getItemCount() {
		return documentList == null ? 0 : documentList.size();
	}

	class DocumentViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.img_document)
		ImageView imgDocument;

		@BindView(R.id.txt_document_name)
		TextView txtDocumentName;

		@BindView(R.id.txt_document_time)
		TextView txtDocumentTime;

		DocumentViewHolder(@NonNull View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}
}
