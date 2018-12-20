package com.resonatestudios.uas_1603631_muhammad_nabillah.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.resonatestudios.uas_1603631_muhammad_nabillah.R;
import com.resonatestudios.uas_1603631_muhammad_nabillah.model.BrakeLog;

import java.util.ArrayList;

public class BrakeLogAdapter extends RecyclerView.Adapter<BrakeLogAdapter.Holder> {
    Context context;
    ArrayList<BrakeLog> brakeLogs;

    public BrakeLogAdapter(Context context) {
        this.context = context;
        brakeLogs = new ArrayList<>();
    }

    public void addToList(BrakeLog item) {
        // untuk menambahkan item dan update recyclerview
        brakeLogs.add(0, item);
        notifyItemInserted(0);
    }

    public ArrayList<BrakeLog> getBrakeLogs() {
        return brakeLogs;
    }

    public void setBrakeLogs(ArrayList<BrakeLog> brakeLogs) {
        this.brakeLogs = brakeLogs;
    }

    public void replaceBrakeLogs(ArrayList<BrakeLog> brakeLogs) {
        this.brakeLogs.addAll(brakeLogs);
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_log, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        BrakeLog item = brakeLogs.get(i);

        holder.logTimestamp.setText(item.getTimestamp().toString());

        String location =
                "Latitude: " + item.getLatitude() + "\n" +
                        "Longitude: " + item.getLongitude();
        holder.logLocation.setText(location);
    }

    @Override
    public int getItemCount() {
        return brakeLogs.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView logTimestamp;
        TextView logLocation;

        public Holder(@NonNull View itemView) {
            super(itemView);
            logTimestamp = itemView.findViewById(R.id.log_timestamp);
            logLocation = itemView.findViewById(R.id.log_location);
        }
    }
}
