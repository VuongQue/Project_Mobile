package com.example.project_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mobile.R;
import com.example.project_mobile.dto.SessionResponse;

import java.util.ArrayList;
import java.util.List;

public class SessionSelectAdapter extends RecyclerView.Adapter<SessionSelectAdapter.ViewHolder> {

    private final List<SessionResponse> sessionList;
    private final List<SessionResponse> selectedSessions = new ArrayList<>();
    private final OnSelectionChangedListener selectionChangedListener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(List<SessionResponse> selectedSessions);
    }

    public SessionSelectAdapter(List<SessionResponse> sessionList, OnSelectionChangedListener listener) {
        this.sessionList = sessionList;
        this.selectionChangedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SessionResponse session = sessionList.get(position);

        holder.txtTimeIn.setText("Giờ vào: " + session.getCheckIn());
        holder.txtFee.setText(String.format("%.0f đ", session.getFee()));

        holder.checkboxPay.setOnCheckedChangeListener(null); // Clear sự kiện cũ
        holder.checkboxPay.setChecked(selectedSessions.contains(session));

        holder.checkboxPay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedSessions.contains(session)) {
                    selectedSessions.add(session);
                }
            } else {
                selectedSessions.remove(session);
            }
            selectionChangedListener.onSelectionChanged(selectedSessions);
        });
    }

    @Override
    public int getItemCount() {
        return sessionList != null ? sessionList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTimeIn, txtFee;
        CheckBox checkboxPay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTimeIn = itemView.findViewById(R.id.txtTimeIn);
            txtFee = itemView.findViewById(R.id.txtFee);
            checkboxPay = itemView.findViewById(R.id.checkboxPay);
        }
    }

    public void selectAll(boolean isChecked) {
        selectedSessions.clear();
        if (isChecked) {
            selectedSessions.addAll(sessionList);
        }
        notifyDataSetChanged();
        selectionChangedListener.onSelectionChanged(selectedSessions);
    }

    public List<SessionResponse> getSelectedSessions() {
        return selectedSessions;
    }
}