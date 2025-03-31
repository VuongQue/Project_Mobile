package com.example.project_mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.project_mobile.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderHolder> {
    private Context context;
    private ArrayList<Integer> arrayList;

    public SliderAdapter(Context context, ArrayList<Integer> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public SliderHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images, parent,  false);
        return new SliderHolder(view);
    }

    @Override
    public void onBindViewHolder(SliderHolder viewHolder, int position) {
        Glide.with(context).load(arrayList.get(position)).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    public class SliderHolder extends SliderViewAdapter.ViewHolder {
        private ImageView imageView;

        public SliderHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_auto_image_slider);
        }
    }
}

