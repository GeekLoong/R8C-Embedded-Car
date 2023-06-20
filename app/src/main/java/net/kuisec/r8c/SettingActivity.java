package net.kuisec.r8c;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.kuisec.r8c.CustomView.FlowRadioGroup;
import net.kuisec.r8c.Utils.SharedPreferencesUtil;
import net.kuisec.r8c.Utils.ThemeUtil;
import net.kuisec.r8c.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //设置状态栏主题
        ThemeUtil.setDarkTheme(this);
        initView();
        initData();
    }


    /**
     * 初始化布局
     */
    private void initView() {
        binding.barBack.setOnClickListener(this);
        binding.handoffTh.setOnClickListener(this);
        binding.saveShapeTh.setOnClickListener(this);
        binding.saveLPTh.setOnClickListener(this);
        //阈值拖动条
        String imgTh = SharedPreferencesUtil.queryKey2Value("imgTh");
        binding.imgThSeekBar.setProgress((int) (Double.parseDouble(imgTh) * 100));
        binding.imgThSeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.imgThText.setText(imgTh);
        String ocrTh = SharedPreferencesUtil.queryKey2Value("ocrTh");
        binding.ocrThSeekBar.setProgress((int) (Double.parseDouble(ocrTh) * 100));
        binding.ocrThSeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.ocrThText.setText(ocrTh);
        String lpATh = SharedPreferencesUtil.queryKey2Value("lpATh");
        binding.lpAThSeekBar.setProgress((int) (Double.parseDouble(lpATh) * 100));
        binding.lpAThSeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.lpAThText.setText(lpATh);
        //TFT 固定码盘
        String tftAD = SharedPreferencesUtil.queryKey2Value("tftAD");
        binding.dashboardASeekBar.setProgress(Integer.parseInt(tftAD));
        binding.dashboardASeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.dashboardAText.setText(tftAD);
        String tftBD = SharedPreferencesUtil.queryKey2Value("tftBD");
        binding.dashboardBSeekBar.setProgress(Integer.parseInt(tftBD));
        binding.dashboardBSeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.dashboardBText.setText(tftBD);
        //添加多行单选按钮组
        new FlowRadioGroup(binding.lpItemGroup, "lpColor");
        new FlowRadioGroup(binding.carItemGroup, "carModel");
        new FlowRadioGroup(binding.trafficItemGroup, "trafficFlag");
    }


    /**
     * 初始化数据
     */
    private void initData() {
        String hsvModel = SharedPreferencesUtil.queryKey2Value("HSVModel");
        String[] results = SharedPreferencesUtil.parseHSV(hsvModel);
        binding.shapeLowHSV.setText(results[0]);
        binding.shapeHighHSV.setText(results[1]);
        if ("dark".equals(hsvModel)) {
            binding.handoffTh.setText("现阈值 DARK");
        } else {
            binding.handoffTh.setText("现阈值 LIGHT");
        }
        String[] lpHSVResult = SharedPreferencesUtil.parseHSV("lp");
        binding.lpLowHSV.setText(lpHSVResult[0]);
        binding.lpHighHSV.setText(lpHSVResult[1]);
    }


    /**
     * 点击事件
     *
     * @param view 组件
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_back:
                finish();
                break;
            case R.id.handoffTh:
                String hsvModel = SharedPreferencesUtil.queryKey2Value("HSVModel");
                if ("dark".equals(hsvModel)) {
                    SharedPreferencesUtil.insert("HSVModel", "light");
                    hsvModel = SharedPreferencesUtil.queryKey2Value("HSVModel");
                    String[] results = SharedPreferencesUtil.parseHSV(hsvModel);
                    binding.shapeLowHSV.setText(results[0]);
                    binding.shapeHighHSV.setText(results[1]);
                    binding.handoffTh.setText("现阈值 LIGHT");
                } else {
                    SharedPreferencesUtil.insert("HSVModel", "dark");
                    hsvModel = SharedPreferencesUtil.queryKey2Value("HSVModel");
                    String[] results = SharedPreferencesUtil.parseHSV(hsvModel);
                    binding.shapeLowHSV.setText(results[0]);
                    binding.shapeHighHSV.setText(results[1]);
                    binding.handoffTh.setText("现阈值 DARK");
                }
                break;
            case R.id.saveShapeTh:
                String lowShapeText = binding.shapeLowHSV.getText().toString().replace("\n", "!");
                SharedPreferencesUtil.saveShapeHSV("Low", lowShapeText);
                String highShapeText = binding.shapeHighHSV.getText().toString().replace("\n", "!");
                SharedPreferencesUtil.saveShapeHSV("High", highShapeText);
                Toast.makeText(this, "阈值保存成功！", Toast.LENGTH_SHORT).show();
                //更新阈值
                SharedPreferencesUtil.initShapeHSVColorTh();
                break;
            case R.id.saveLPTh:
                String lowLPText = binding.lpLowHSV.getText().toString().replace("\n", "!");
                SharedPreferencesUtil.saveLPHSV("Low", lowLPText);
                String highLPText = binding.lpHighHSV.getText().toString().replace("\n", "!");
                SharedPreferencesUtil.saveLPHSV("High", highLPText);
                Toast.makeText(this, "阈值保存成功！", Toast.LENGTH_SHORT).show();
                //更新阈值
                SharedPreferencesUtil.initLPHSVColorTh();
                break;
            case R.id.saveShapeClass:
                String maxShapeCount = binding.shapeClassEdit.getText().toString();
                SharedPreferencesUtil.insert("maxShapeClass", maxShapeCount);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /**
     * 定义阈值监听
     */
    SeekBar.OnSeekBarChangeListener thSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            @SuppressLint("DefaultLocale") String th = String.format("%.2f", progress * 0.01);
            String dashboardNumber = String.valueOf(progress);
            switch (seekBar.getId()) {
                case R.id.imgThSeekBar:
                    SharedPreferencesUtil.insert("imgTh", th);
                    binding.imgThText.setText(th);
                    break;
                case R.id.ocrThSeekBar:
                    SharedPreferencesUtil.insert("ocrTh", th);
                    binding.ocrThText.setText(th);
                    break;
                case R.id.lpAThSeekBar:
                    SharedPreferencesUtil.insert("lpATh", th);
                    binding.lpAThText.setText(th);
                    break;
                case R.id.dashboardASeekBar:
                    SharedPreferencesUtil.insert("tftAD", dashboardNumber);
                    binding.dashboardAText.setText(dashboardNumber);
                    break;
                case R.id.dashboardBSeekBar:
                    SharedPreferencesUtil.insert("tftBD", dashboardNumber);
                    binding.dashboardBText.setText(dashboardNumber);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}