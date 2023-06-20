package net.kuisec.r8c.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import net.kuisec.r8c.R;
import net.kuisec.r8c.Utils.CommunicationUtil;
import net.kuisec.r8c.databinding.CarStateFragmentBinding;

import java.util.HashMap;

/**
 * 小车状态碎片
 */
public class CarStateFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    CarStateFragmentBinding binding;
    HashMap<String, Integer> hashMap;

    public CarStateFragment() {
    }

    public CarStateFragmentBinding getBinding() {
        return this.binding;
    }

    public static CarStateFragment newInstance() {
        return new CarStateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = CarStateFragmentBinding.inflate(getLayoutInflater());
        initData();
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        binding.forwardButton.setOnClickListener(this);
        binding.forwardButton.setOnLongClickListener(v -> {
            hashMap = new HashMap<>();
            hashMap.put("speed", binding.speedNumber.getProgress());
            CommunicationUtil.movingCar("findWay", hashMap);
            return false;
        });
        binding.turnLeftButton.setOnClickListener(this);
        binding.turnRightButton.setOnClickListener(this);
        binding.stopButton.setOnClickListener(this);
        binding.speedNumber.setOnSeekBarChangeListener(this);
        binding.coderNumber.setOnSeekBarChangeListener(this);
        binding.speedText.setText("速度值：" + binding.speedNumber.getProgress());
        binding.coderText.setText("码盘值：" + binding.coderNumber.getProgress());
    }

    @SuppressLint({"NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forwardButton:
                hashMap = new HashMap<>();
                hashMap.put("coder", binding.coderNumber.getProgress());
                hashMap.put("speed", binding.speedNumber.getProgress());
                CommunicationUtil.movingCar("forward", hashMap);
                break;
            case R.id.turnLeftButton:
                hashMap = new HashMap<>();
                hashMap.put("angle", getChecked());
                CommunicationUtil.movingCar("turnLeft", hashMap);
                break;
            case R.id.turnRightButton:
                hashMap = new HashMap<>();
                hashMap.put("angle", getChecked());
                CommunicationUtil.movingCar("turnRight", hashMap);
                break;
            case R.id.stopButton:
                CommunicationUtil.stopCar();
                break;
            default:
                break;
        }
    }


    /**
     * 获得角度选取组的TAG
     * @return 返回ID
     */
    public int getChecked() {
        int childCount = binding.angleCheckGroup.getChildCount();
        int tagID = 0;
        for (int i = 0; i < childCount; i++) {
            RadioButton radioButton = (android.widget.RadioButton) binding.angleCheckGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                tagID = Integer.parseInt(radioButton.getTag().toString());
                break;
            }
        }
        return tagID;
    }


    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.speedNumber:
                binding.speedText.setText("速度值：" + progress);
                break;
            case R.id.coderNumber:
                binding.coderText.setText("码盘值：" + progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}