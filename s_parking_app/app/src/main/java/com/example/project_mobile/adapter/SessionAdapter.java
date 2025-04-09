package com.example.project_mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_mobile.R;
import com.example.project_mobile.model.Session;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
    private Context context;
    private List<Session> sessionList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Session session);
    }

    public SessionAdapter(Context context, List<Session> sessionList, SessionAdapter.OnItemClickListener listener) {
        this.context = context;
        this.sessionList = sessionList;
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
        Session session = sessionList.get(position);
        if (session != null) {
            holder.txtTimeIn.setText(session.getCheckIn()+"");
            holder.txtFee.setText(session.getFee()+"đ");
            holder.txtPaid.setText(session.getIdPayment() > 0 ? "Paid" : "Unpaid");
        }
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(session));
    }

    @Override
    public int getItemCount() {
        return sessionList != null ? sessionList.size() : 0;
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