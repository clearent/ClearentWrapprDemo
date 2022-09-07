package com.example.clearentwrapperdemo.feature;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clearent.idtech.android.wrapper.model.ReaderStatus;
import com.example.clearentwrapperdemo.R;

import java.util.List;

public class ReadersListAdapter extends RecyclerView.Adapter<ReadersListAdapter.ViewHolder> {

    private final List<ReaderStatus> mData;
    private final ItemClickListener mClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.reader_name);

            int pos = getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            view.setOnClickListener(v -> mClickListener.onItemClick(getItem(pos)));
        }
    }

    public ReadersListAdapter(List<ReaderStatus> data, ItemClickListener itemClickListener) {
        this.mData = data;
        this.mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reader_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        ReaderStatus reader = mData.get(position);
        viewHolder.textView.setText(reader.getDisplayName());
    }

    ReaderStatus getItem(int id) {
        return mData.get(id);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface ItemClickListener {
        void onItemClick(ReaderStatus readerStatus);
    }
}
