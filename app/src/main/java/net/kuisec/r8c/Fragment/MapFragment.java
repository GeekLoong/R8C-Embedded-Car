package net.kuisec.r8c.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kuisec.r8c.R;
import net.kuisec.r8c.databinding.MapFragmentBinding;

/**
 * 地图碎片
 * 负责竞赛平台自动化跑图
 */
public class MapFragment extends Fragment {

    MapFragmentBinding binding;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MapFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}