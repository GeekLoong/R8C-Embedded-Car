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
    }


    /**
     * 初始化布局
     */
    private void initView() {
        binding.barBack.setOnClickListener(this);
        binding.saveShapeTh.setOnClickListener(this);
        binding.saveLPTh.setOnClickListener(this);
        binding.saveHexContent.setOnClickListener(this);
        binding.saveShapeClass.setOnClickListener(this);
        binding.saveShapeColor.setOnClickListener(this);
        binding.saveLpContent.setOnClickListener(this);
        binding.savePersonCountContent.setOnClickListener(this);
        binding.saveOcrContent.setOnClickListener(this);
        binding.saveLpRegex.setOnClickListener(this);
        binding.saveAlarmCodeContent.setOnClickListener(this);
        binding.savePowerOpenCodeContent.setOnClickListener(this);
        //阈值拖动条
        String imgTh = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.imgTh);
        binding.imgThSeekBar.setProgress((int) (Double.parseDouble(imgTh) * 100));
        binding.imgThSeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.imgThText.setText(imgTh);
        String ocrTh = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.ocrTh);
        binding.ocrThSeekBar.setProgress((int) (Double.parseDouble(ocrTh) * 100));
        binding.ocrThSeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.ocrThText.setText(ocrTh);
        String lpATh = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.LPATh);
        binding.lpAThSeekBar.setProgress((int) (Double.parseDouble(lpATh) * 100));
        binding.lpAThSeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.lpAThText.setText(lpATh);
        //TFT 固定码盘
        String tftAD = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.tftAD);
        binding.dashboardASeekBar.setProgress(Integer.parseInt(tftAD));
        binding.dashboardASeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.dashboardAText.setText(tftAD);
        String tftBD = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.tftBD);
        binding.dashboardBSeekBar.setProgress(Integer.parseInt(tftBD));
        binding.dashboardBSeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.dashboardBText.setText(tftBD);
        String tftCD = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.tftCD);
        binding.dashboardCSeekBar.setProgress(Integer.parseInt(tftCD));
        binding.dashboardCSeekBar.setOnSeekBarChangeListener(thSeekBarChangeListener);
        binding.dashboardCText.setText(tftCD);
        //添加多行单选按钮组
        new FlowRadioGroup(binding.lpItemGroup, SharedPreferencesUtil.LPColor);
        new FlowRadioGroup(binding.lpVehicleTypeItemGroup, SharedPreferencesUtil.LPVehicleType);
        new FlowRadioGroup(binding.carItemGroup, SharedPreferencesUtil.defaultVehicleType);
        new FlowRadioGroup(binding.trafficItemGroup, SharedPreferencesUtil.defaultTrafficSignTag);
        new FlowRadioGroup(binding.personOccItemGroup, SharedPreferencesUtil.personOCC);
        new FlowRadioGroup(binding.personMaskItemGroup, SharedPreferencesUtil.personMask);

        String lpRegex = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.LPRegex);
        binding.lpRegexEdit.setText(lpRegex.equals("0") ? "" : lpRegex);
        String hexContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.hexContent);
        binding.hexContentEdit.setText(hexContent.equals("0") ? "" : hexContent);
        String shapeClassHex = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.shapeClassHex);
        binding.shapeClassEdit.setText(shapeClassHex.equals("0") ? "" : shapeClassHex);
        String shapeColorHex = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.shapeColorHex);
        binding.shapeColorEdit.setText(shapeColorHex.equals("0") ? "" : shapeColorHex);
        String lpContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.lpContent);
        binding.lpContentEdit.setText(lpContent.equals("0") ? "" : lpContent);
        String personCountContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.personCountContent);
        binding.personCountContentEdit.setText(personCountContent.equals("0") ? "" : personCountContent);
        String ocrContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.ocrContent);
        binding.ocrContentEdit.setText(ocrContent.equals("0") ? "" : ocrContent);
        String alarmCodeContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.alarmCodeContent);
        binding.alarmCodeContentEdit.setText(alarmCodeContent);
        String powerOpenCodeContent = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.powerOpenCodeContent);
        binding.powerOpenCodeContentEdit.setText(powerOpenCodeContent);

        String[] shapeHSVResults = SharedPreferencesUtil.parseHSV("dark");
        binding.shapeLowHSV.setText(shapeHSVResults[0]);
        binding.shapeHighHSV.setText(shapeHSVResults[1]);
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
            case R.id.saveLpRegex:
                String lpRegex = binding.lpRegexEdit.getText().toString();
                SharedPreferencesUtil.insert(SharedPreferencesUtil.LPRegex, lpRegex);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.saveHexContent:
                String hexContent = binding.hexContentEdit.getText().toString();
                SharedPreferencesUtil.insert(SharedPreferencesUtil.hexContent, hexContent);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.saveShapeClass:
                String shapeClass = binding.shapeClassEdit.getText().toString();
                SharedPreferencesUtil.insert(SharedPreferencesUtil.shapeClassHex, shapeClass);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.saveShapeColor:
                String shapeColor = binding.shapeColorEdit.getText().toString();
                SharedPreferencesUtil.insert(SharedPreferencesUtil.shapeColorHex, shapeColor);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.saveLpContent:
                String lpContent = binding.lpContentEdit.getText().toString();
                SharedPreferencesUtil.insert(SharedPreferencesUtil.lpContent, lpContent);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.savePersonCountContent:
                String personCountContent = binding.personCountContentEdit.getText().toString();
                SharedPreferencesUtil.insert(SharedPreferencesUtil.personCountContent, personCountContent);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.saveOcrContent:
                String ocrContent = binding.ocrContentEdit.getText().toString();
                SharedPreferencesUtil.insert(SharedPreferencesUtil.ocrContent, ocrContent);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.saveAlarmCodeContent:
                String alarmCodeContent = binding.alarmCodeContentEdit.getText().toString();
                SharedPreferencesUtil.insert(SharedPreferencesUtil.alarmCodeContent, alarmCodeContent);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.savePowerOpenCodeContent:
                String powerOpenCodeContent = binding.powerOpenCodeContentEdit.getText().toString();
                SharedPreferencesUtil.insert(SharedPreferencesUtil.powerOpenCodeContent, powerOpenCodeContent);
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
                    SharedPreferencesUtil.insert(SharedPreferencesUtil.imgTh, th);
                    binding.imgThText.setText(th);
                    break;
                case R.id.ocrThSeekBar:
                    SharedPreferencesUtil.insert(SharedPreferencesUtil.ocrTh, th);
                    binding.ocrThText.setText(th);
                    break;
                case R.id.lpAThSeekBar:
                    SharedPreferencesUtil.insert(SharedPreferencesUtil.LPATh, th);
                    binding.lpAThText.setText(th);
                    break;
                case R.id.dashboardASeekBar:
                    SharedPreferencesUtil.insert(SharedPreferencesUtil.tftAD, dashboardNumber);
                    binding.dashboardAText.setText(dashboardNumber);
                    break;
                case R.id.dashboardBSeekBar:
                    SharedPreferencesUtil.insert(SharedPreferencesUtil.tftBD, dashboardNumber);
                    binding.dashboardBText.setText(dashboardNumber);
                    break;
                case R.id.dashboardCSeekBar:
                    SharedPreferencesUtil.insert(SharedPreferencesUtil.tftCD, dashboardNumber);
                    binding.dashboardCText.setText(dashboardNumber);
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