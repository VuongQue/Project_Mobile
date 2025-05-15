package com.example.project_mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_mobile.R;
import com.example.project_mobile.dto.ParkingLotResponse;
import java.util.List;

public class ParkingLotAdapter extends RecyclerView.Adapter<ParkingLotAdapter.ViewHolder> {
    private final Context context;
    private final List<ParkingLotResponse> parkingLotResponseList;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ParkingLotResponse parkingLotResponse);
    }

    public ParkingLotAdapter(Context context, List<ParkingLotResponse> parkingLotResponseList, OnItemClickListener listener) {
        this.context = context;
        this.parkingLotResponseList = parkingLotResponseList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parking_lot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParkingLotResponse parkingLotResponse = parkingLotResponseList.get(position);
        holder.txtName.setText(parkingLotResponse.getLocation());

        if ("UNAVAILABLE".equals(parkingLotResponse.getStatus())) {
            holder.vStatus.setBackgroundResource(R.color.red);
        } else if ("AVAILABLE".equals(parkingLotResponse.getStatus())) {
            holder.vStatus.setBackgroundResource(R.color.green);
        } else {
            holder.vStatus.setBackgroundResource(R.color.gray);
        }

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(parkingLotResponse));
    }

    @Override
    public int getItemCount() {
        return parkingLotResponseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        View vStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            vStatus = itemView.findViewById(R.id.vStatus);
        }
    }
}