package com.example.project_mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_mobile.R;
import com.example.project_mobile.dto.SessionResponse;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
    private Context context;
    private List<SessionResponse> sessionResponseList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(SessionResponse sessionResponse);
    }

    public SessionAdapter(Context context, List<SessionResponse> sessionResponseList, SessionAdapter.OnItemClickListener listener) {
        this.context = context;
        this.sessionResponseList = sessionResponseList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        SessionResponse sessionResponse = sessionResponseList.get(position);
        if (sessionResponse != null) {
            holder.txtTimeIn.setText(sessionResponse.getCheckIn()+"");
            holder.txtFee.setText(sessionResponse.getFee()+"đ");
            holder.txtPaid.setText(sessionResponse.getIdPayment() > 0 ? "Paid" : "Unpaid");
        }
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(sessionResponse));
    }

    @Override
    public int getItemCount() {
        return sessionResponseList != null ? sessionResponseList.size() : 0;
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTimeIn, txtFee, txtPaid;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTimeIn = itemView.findViewById(R.id.txtTimeIn);
            txtFee = itemView.findViewById(R.id.txtFee);
            txtPaid = itemView.findViewById(R.id.txtPaid);
        }
    }
}