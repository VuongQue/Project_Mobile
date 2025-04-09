package com.example.project_mobile.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project_mobile.HistoryActivity;
import com.example.project_mobile.ParkingActivity;
import com.example.project_mobile.R;
import com.example.project_mobile.adapter.SliderAdapter;
import com.example.project_mobile.databinding.FragmentHomeBinding;
import com.example.project_mobile.model.Image;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private SliderAdapter adapter;
    private List<Image> imageList;
    private ArrayList<Integer> arrayList;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        SwitchActivity();
        ConfigSliderView();
        // recyclerView

        return binding.getRoot();
    }

    private void ConfigSliderView() {
        arrayList = new ArrayList<>();

        // Thêm ảnh vào danh sách
        arrayList.add(R.drawable.notif1);
        arrayList.add(R.drawable.notif2);
        arrayList.add(R.drawable.notif3);

        adapter = new SliderAdapter(requireContext(), arrayList);
        binding.imageSlider.setSliderAdapter(adapter);

        // Cấu hình sliderView
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSlider.setIndicatorSelectedColor(getResources().getColor(R.color.red));
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.startAutoCycle();
        binding.imageSlider.setScrollTimeInSec(5);
    }

    private void SwitchActivity() {
        if (binding != null) {
            binding.booking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ParkingActivity.class);
                    startActivity(intent);
                }
            });

            binding.history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

}
