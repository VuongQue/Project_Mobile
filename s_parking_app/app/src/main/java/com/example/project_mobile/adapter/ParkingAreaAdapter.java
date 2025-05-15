package com.example.project_mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mobile.R;
import com.example.project_mobile.dto.ParkingAreaResponse;

import java.util.List;

public class ParkingAreaAdapter extends RecyclerView.Adapter<ParkingAreaAdapter.ViewHolder>{

    private final Context context;
    private final List<ParkingAreaResponse> parkingAreaResponseList;

    public ParkingAreaAdapter(Context context, List<ParkingAreaResponse> parkingAreaResponseList) {
        this.context = context;
        this.parkingAreaResponseList = parkingAreaResponseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_bar_parking_area, parent, false);
        return new ParkingAreaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingAreaAdapter.ViewHolder holder, int position) {
        ParkingAreaResponse parkingAreaResponse = parkingAreaResponseList.get(position);
        holder.tvId.setText(parkingAreaResponse.getIdArea());

        int maxCapacity = parkingAreaResponse.getMaxCapacity();
        int availableSlots = parkingAreaResponse.getAvailableSlots();

        holder.progressBar.setMax(maxCapacity);
        holder.progressBar.setProgress(availableSlots);
    }


    @Override
    public int getItemCount() {
        return parkingAreaResponseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
