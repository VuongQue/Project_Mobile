package com.example.project_mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_mobile.R;
import com.example.project_mobile.model.ParkingLot;
import java.util.List;

public class ParkingLotAdapter extends RecyclerView.Adapter<ParkingLotAdapter.ViewHolder> {
    private Context context;
    private List<ParkingLot> parkingLotList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ParkingLot parkingLot);
    }

    public ParkingLotAdapter(Context context, List<ParkingLot> parkingLotList, OnItemClickListener listener) {
        this.context = context;
        this.parkingLotList = parkingLotList;
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
        ParkingLot parkingLot = parkingLotList.get(position);
        holder.txtName.setText(parkingLot.getName());

        if ("Unavailable".equals(parkingLot.getStatus())) {
            holder.vStatus.setBackgroundResource(R.color.red);
        } else if ("Available".equals(parkingLot.getStatus())) {
            holder.vStatus.setBackgroundResource(R.color.green);
        } else {
            holder.vStatus.setBackgroundResource(R.color.gray);
        }

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(parkingLot));
    }

    @Override
    public int getItemCount() {
        return parkingLotList.size();
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