package com.fireflies.govtfireflies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

	private List list;
	private Context context;

	public DocumentAdapter(List list, Context context) {
		this.list = list;
		this.context = context;
	}

	@NonNull
	@Override
	public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return null;
	}

	@Override
	public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {

	}

	@Override
	public int getItemCount() {
		return 0;
	}

	class DocumentViewHolder extends RecyclerView.ViewHolder {
		public DocumentViewHolder(@NonNull View itemView) {
			super(itemView);
		}
	}
}
