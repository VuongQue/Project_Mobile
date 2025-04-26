package com.example.project_mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mobile.R;
import com.example.project_mobile.dto.BookingResponse;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder>{
    private final Context context;
    private final List<BookingResponse> bookingResponseList;

    public BookingAdapter(Context context, List<BookingResponse> bookingResponseList) {
        this.context = context;
        this.bookingResponseList = bookingResponseList;
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_history, parent, false);
        return new BookingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, int position) {
        BookingResponse bookingResponse = bookingResponseList.get(position);
        holder.tvLocationName.setText(bookingResponse.getLocation());
        holder.tvBookingDate.setText(String.format("%s", bookingResponse.getDate()));
        holder.tvFee.setText(String.valueOf(bookingResponse.getFee()));
    }

    @Override
    public int getItemCount() {
        return bookingResponseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocationName, tvBookingDate, tvFee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvFee = itemView.findViewById(R.id.tvFee);
        }
    }
}
