package com.example.project_mobile.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mobile.R;
import com.example.project_mobile.dto.NotificationResponse;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private final Context context;
    private final List<NotificationResponse> notificationResponseList;
    private final NotificationAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(NotificationResponse notificationResponse, int position);
    }

    public NotificationAdapter(Context context, List<NotificationResponse> notificationResponseList, NotificationAdapter.OnItemClickListener listener) {
        this.context = context;
        this.notificationResponseList = notificationResponseList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        NotificationResponse notification = notificationResponseList.get(position);

        holder.txtTitle.setText(notification.getTitle());
        holder.txtMessage.setText(notification.getMessage());
        holder.txtCreatedAt.setText(notification.getFormattedDate());
        if (!notification.isRead()) {
            holder.txtTitle.setTypeface(null, Typeface.BOLD);
        }
        else {
            holder.txtTitle.setTypeface(null, Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(notification, position));
    }

    @Override
    public int getItemCount() {
        return notificationResponseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMessage, txtCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
        }

    }
}
