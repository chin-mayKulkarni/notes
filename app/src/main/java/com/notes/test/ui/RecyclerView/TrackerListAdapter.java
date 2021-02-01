package com.notes.test.ui.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.notes.test.R;

import java.util.List;

public class TrackerListAdapter extends RecyclerView.Adapter<TrackerListAdapter.ViewHolder> {

    LayoutInflater inflater;
    //private MyListData[] listdata;
    List<TrackerListData> myListMainData ;

    // RecyclerView recyclerView;
    public TrackerListAdapter(Context ctx, List<TrackerListData> listdata) {
        this.inflater = LayoutInflater.from(ctx);
        this.myListMainData = listdata;
    }

    @NonNull
    @Override
    public TrackerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.tracker_list_item, parent, false);
        //  ViewHolder viewHolder = new ViewHolder(listItem);
        return new TrackerListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackerListAdapter.ViewHolder holder, int position) {

        holder.textView.setText(myListMainData.get(position).getDescription());
        holder.numberOfDownloads.setText(myListMainData.get(position).getNumberOfDownloads());
        holder.textViewHeader.setText(myListMainData.get(position).getHeader());

    }

    @Override
    public int getItemCount() {
        return myListMainData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewHeader;
        public TextView textView, numberOfDownloads, id;
        public CardView relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewHeader = itemView.findViewById(R.id.textViewHeaderTracker);
            this.textView = itemView.findViewById(R.id.textViewTracker);
            this.numberOfDownloads = itemView.findViewById(R.id.numberOfDownloadsTracker);
            relativeLayout = itemView.findViewById(R.id.relativeLayoutTracker);
        }
    }
}
