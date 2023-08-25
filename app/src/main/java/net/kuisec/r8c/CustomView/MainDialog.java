package net.kuisec.r8c.CustomView;

import static net.kuisec.r8c.Const.HeaderConst.CAR_FLAG;
import static net.kuisec.r8c.Const.HeaderConst.END_FLAG;
import static net.kuisec.r8c.Const.HeaderConst.REPLY_FLAG;
import static net.kuisec.r8c.Const.SignConst.A_FLAG;
import static net.kuisec.r8c.Const.SignConst.B_FLAG;
import static net.kuisec.r8c.Const.SignConst.C_FLAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kuisec.r8c.Adapter.DialogItemAdapter;
import net.kuisec.r8c.Bean.DialogItemBean;
import net.kuisec.r8c.Bean.EditDialogBean;
import net.kuisec.r8c.Bean.OCRRect;
import net.kuisec.r8c.Bean.RectResult;
import net.kuisec.r8c.Ints.TextWatcherCallBack;
import net.kuisec.r8c.MainActivity;
import net.kuisec.r8c.R;
import net.kuisec.r8c.Utils.CommunicationUtil;
import net.kuisec.r8c.Utils.FileUtil;
import net.kuisec.r8c.Utils.FloatWindowUtil;
import net.kuisec.r8c.Utils.HandlerUtil;
import net.kuisec.r8c.Utils.ImgPcsUtil;
import net.kuisec.r8c.Utils.LogUtil;
import net.kuisec.r8c.Utils.SharedPreferencesUtil;
import net.kuisec.r8c.Utils.ThreadUtil;
import net.kuisec.r8c.databinding.DialogMainBinding;

import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 对话框类
 *
 * @author Jinsn
 */
public class MainDialog extends DialogFragment {

    DialogMainBinding binding;
    MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Dialog);
        binding = DialogMainBinding.inflate(getLayoutInflater());
        initData();
    }

    /**
     * 创建视图根
     *
     * @param inflater           视图对象
     * @param container          容器
     * @param savedInstanceState 保存的实例状态
     * @return 返回一个布局
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return binding.getRoot();
    }

    /**
     * 布局内容
     *
     * @param savedInstanceState 保存的状态
     * @return 返回一个Dialog弹窗
     */
    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.Theme_Dialog_Animations);
        WindowManager.LayoutParams params = window.getAttributes();
        params.y = 50;
        return dialog;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        activity = (MainActivity) getActivity();
        List<DialogItemBean> list = new ArrayList<>();
        list.add(new DialogItemBean(R.drawable.broadcasting, "语音播报标志物"));
        list.add(new DialogItemBean(R.drawable.tft_display, "智能 TFT 显示标志物"));
        list.add(new DialogItemBean(R.drawable.hsv, "HSV 动态调节和展示"));
        list.add(new DialogItemBean(R.drawable.photo_graph, "竞赛平台拍照系统"));
        list.add(new DialogItemBean(R.drawable.terminal, "万能通信指令"));
        list.add(new DialogItemBean(R.drawable.camera, "摄像头视角控制"));
        list.add(new DialogItemBean(R.drawable.car_start, "启动主车"));
        list.add(new DialogItemBean(R.drawable.sync_img, "加载本地图像到 DEBUG"));
        list.add(new DialogItemBean(R.drawable.qr_code, "二维码识别"));
        list.add(new DialogItemBean(R.drawable.ocr, "文字识别"));
        list.add(new DialogItemBean(R.drawable.cocr, "中文识别"));
        list.add(new DialogItemBean(R.drawable.lpr, "车牌内容识别"));
        list.add(new DialogItemBean(R.drawable.traffic_lights, "交通灯识别"));
        list.add(new DialogItemBean(R.drawable.image_rec, "查找交通标志后识别多边形"));
        list.add(new DialogItemBean(R.drawable.image_rec, "交通标志查找和识别"));
        list.add(new DialogItemBean(R.drawable.image_rec, "多边形查找和识别"));
        list.add(new DialogItemBean(R.drawable.image_rec, "车型车牌识别"));
        list.add(new DialogItemBean(R.drawable.image_rec, "口罩识别"));
        list.add(new DialogItemBean(R.drawable.image_rec, "行人识别"));
        list.add(new DialogItemBean(R.drawable.cocr, "TFT 中文查找和识别"));
        list.add(new DialogItemBean(R.drawable.lpr, "TFT 车牌查找和识别"));

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerviewDialog.setLayoutManager(manager);
        DialogItemAdapter dialogItemAdapter = new DialogItemAdapter(list);
        binding.recyclerviewDialog.setAdapter(dialogItemAdapter);
        binding.recyclerviewDialog.addOnItemTouchListener(new OnItemTouchListener(binding.recyclerviewDialog) {
            @SuppressLint({"InflateParams", "SetTextI18n", "NonConstantResourceId"})
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                switch (viewHolder.getLayoutPosition()) {
                    //语音播报
                    case 0:
                        FloatWindowUtil voiceBroadcastFloatWindow = new FloatWindowUtil(requireActivity());
                        View voiceBroadcastLayout = voiceBroadcastFloatWindow.create(R.layout.float_voice_broadcast);
                        EditText voiceBroadcastEdit = voiceBroadcastLayout.findViewById(R.id.voiceBroadcastEdit);
                        Button sendVoiceBroadcastContent = voiceBroadcastLayout.findViewById(R.id.sendVoiceBroadcastContent);
                        voiceBroadcastEdit.setOnClickListener(view -> {
                            EditDialog dialog = new EditDialog(new EditDialogBean(voiceBroadcastEdit, false));
                            dialog.show(activity.getSupportFragmentManager(), "Edit Dialog");
                        });
                        sendVoiceBroadcastContent.setOnClickListener(view -> {
                            String voiceBroadcastContent = voiceBroadcastEdit.getText().toString();
                            if (voiceBroadcastContent.isEmpty()) {
                                HandlerUtil.sendMsg("播报内容不能为空！");
                            } else {
                                CommunicationUtil.voiceBroadcast(voiceBroadcastContent);
                            }
                        });
                        break;
                    //智能 TFT 显示标志物
                    case 1:
                        FloatWindowUtil tftFloatWindow = new FloatWindowUtil(requireActivity());
                        View tftLayout = tftFloatWindow.create(R.layout.float_tft);
                        //将三个单选小组定为一个大组
                        RadioGroup[] rG = {tftLayout.findViewById(R.id.floating_tft1), tftLayout.findViewById(R.id.floating_tft2), tftLayout.findViewById(R.id.floating_tft3)};
                        //给每个单选按钮设置点击事件
                        for (RadioGroup r : rG) {
                            for (int i = 0; i <= r.getChildCount() - 1; i++) {
                                RadioButton modeButton = (RadioButton) r.getChildAt(i);
                                modeButton.setOnClickListener(new RadioOnClickListener(rG, modeButton));
                            }
                        }
                        //将视图与单选按钮绑定
                        RadioButton showImgMode = tftLayout.findViewById(R.id.showImgMode);
                        showImgMode.setOnCheckedChangeListener(new BindChecked(tftLayout.findViewById(R.id.showImgModeLayout)));
                        RadioButton showLPMode = tftLayout.findViewById(R.id.showLPMode);
                        showLPMode.setOnCheckedChangeListener(new BindChecked(tftLayout.findViewById(R.id.showLPModeLayout)));
                        RadioButton showTimerMode = tftLayout.findViewById(R.id.showTimerMode);
                        showTimerMode.setOnCheckedChangeListener(new BindChecked(tftLayout.findViewById(R.id.showTimerModeLayout)));
                        RadioButton showHEXMode = tftLayout.findViewById(R.id.showHEXMode);
                        showHEXMode.setOnCheckedChangeListener(new BindChecked(tftLayout.findViewById(R.id.showHEXModeLayout)));
                        RadioButton showDistanceMode = tftLayout.findViewById(R.id.showDistanceMode);
                        showDistanceMode.setOnCheckedChangeListener(new BindChecked(tftLayout.findViewById(R.id.showDistanceModeLayout)));
                        RadioButton showTrafficMode = tftLayout.findViewById(R.id.showTrafficMode);
                        showTrafficMode.setOnCheckedChangeListener(new BindChecked(tftLayout.findViewById(R.id.showTrafficModeLayout)));
                        //将输入框定为一个大组
                        EditText[] eTG = {tftLayout.findViewById(R.id.tftImgContent), tftLayout.findViewById(R.id.tftLPContent), tftLayout.findViewById(R.id.tftHEXContent), tftLayout.findViewById(R.id.tftDistanceContent)};
                        for (EditText e : eTG) {
                            //获得焦点时打开编辑窗口
                            e.setOnClickListener(view -> {
                                EditDialog dialog = new EditDialog(new EditDialogBean(e));
                                dialog.show(activity.getSupportFragmentManager(), "Edit Dialog");
                            });
                            e.setOnFocusChangeListener((view, b) -> {
                                if (b) {
                                    EditDialog dialog = new EditDialog(new EditDialogBean(e));
                                    dialog.show(activity.getSupportFragmentManager(), "Edit Dialog");
                                }
                            });
                        }
                        RadioButton checkClassA = tftLayout.findViewById(R.id.tft_class_a);
                        RadioButton checkClassB = tftLayout.findViewById(R.id.tft_class_b);
                        //指令和内容
                        HashMap<String, String> hashMap = new HashMap<>();

                        //图片显示模式
                        Button tftJump = tftLayout.findViewById(R.id.tftJump);
                        tftJump.setOnClickListener(view -> {
                            hashMap.put("ctrl", "跳转");
                            hashMap.put("content", eTG[0].getText().toString());
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        Button tftPrevious = tftLayout.findViewById(R.id.tftPrevious);
                        tftPrevious.setOnClickListener(view -> {
                            hashMap.put("ctrl", "上一张");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        Button tftPlay = tftLayout.findViewById(R.id.tftPlay);
                        AtomicBoolean autoIsRun = new AtomicBoolean(false);
                        AtomicInteger tftCount = new AtomicInteger(1);
                        tftPlay.setOnClickListener(view -> {
                            if (!autoIsRun.get()) {
                                autoIsRun.set(true);
                                tftPlay.setText("停止");
                                ThreadUtil.createThread(() -> {
                                    while (autoIsRun.get()) {
                                        hashMap.put("ctrl", "下一张");
                                        if (checkClassA.isChecked())
                                            CommunicationUtil.tftCmd(A_FLAG, hashMap);
                                        else if (checkClassB.isChecked())
                                            CommunicationUtil.tftCmd(B_FLAG, hashMap);
                                        else
                                            CommunicationUtil.tftCmd(C_FLAG, hashMap);
                                        LogUtil.printLog("TFT 自动播放：正在播放第" + tftCount + "轮");
                                        tftCount.getAndIncrement();
                                        ThreadUtil.sleep(2250);
                                    }
                                });
                            } else {
                                autoIsRun.set(false);
                                tftCount.set(1);
                                tftPlay.setText("自动播放");
                            }
                        });
                        Button tftNext = tftLayout.findViewById(R.id.tftNext);
                        tftNext.setOnClickListener(view -> {
                            hashMap.put("ctrl", "下一张");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        //监听编辑窗口返回的内容
                        eTG[0].addTextChangedListener(new FloatEditTextWatcher(() -> {
                            int count = eTG[0].getText().length() == 0 ? 0 : Integer.parseInt(eTG[0].getText().toString());
                            if (!(count >= 1 && count <= 20)) {
                                HandlerUtil.sendMsg("输入的值不符合规范，请修改");
                                tftJump.setEnabled(false);
                                tftJump.setText("输入的值不符合规范，请修改");
                            } else {
                                tftJump.setEnabled(true);
                                tftJump.setText("跳转到第【" + count + "】张");
                            }
                        }));

                        //车牌显示模式
                        Button tftLPButton = tftLayout.findViewById(R.id.tftLPButton);
                        tftLPButton.setOnClickListener(view -> {
                            hashMap.put("ctrl", "车牌显示");
                            hashMap.put("content", eTG[1].getText().toString());
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        //监听编辑窗口返回的内容
                        eTG[1].addTextChangedListener(new FloatEditTextWatcher(() -> {
                            if (eTG[1].getText().length() != 6) {
                                HandlerUtil.sendMsg("输入的值不符合规范，请修改");
                                tftLPButton.setEnabled(false);
                                tftLPButton.setText("输入的值不符合规范，请修改");
                            } else {
                                tftLPButton.setEnabled(true);
                                tftLPButton.setText("显示车牌【" + eTG[1].getText().toString() + "】");
                            }
                        }));

                        //计时器模式
                        Button tftTimerOff = tftLayout.findViewById(R.id.tftTimerOff);
                        tftTimerOff.setOnClickListener(view -> {
                            hashMap.put("ctrl", "暂停计时");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        Button tftTimerReset = tftLayout.findViewById(R.id.tftTimerReset);
                        tftTimerReset.setOnClickListener(view -> {
                            hashMap.put("ctrl", "重置计时");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        Button tftTimerOn = tftLayout.findViewById(R.id.tftTimerOn);
                        tftTimerOn.setOnClickListener(view -> {
                            hashMap.put("ctrl", "开始计时");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });

                        //16进制显示模式
                        Button tftHEXButton = tftLayout.findViewById(R.id.tftHEXButton);
                        tftHEXButton.setOnClickListener(view -> {
                            hashMap.put("ctrl", "HEX显示");
                            hashMap.put("content", eTG[2].getText().toString());
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        //监听编辑窗口返回的内容
                        eTG[2].addTextChangedListener(new FloatEditTextWatcher(() -> {
                            boolean ok = true;
                            char[] cs = eTG[2].getText().toString().toCharArray();
                            for (char c : cs) {
                                if (!((c >= 48 && c <= 57) || (c >= 65 && c <= 70))) {
                                    ok = false;
                                    break;
                                }
                            }
                            if (eTG[2].getText().length() != 6 || !ok) {
                                HandlerUtil.sendMsg("输入的值不符合规范，请修改");
                                tftHEXButton.setEnabled(false);
                                tftHEXButton.setText("输入的值不符合规范，请修改");
                            } else {
                                tftHEXButton.setEnabled(true);
                                tftHEXButton.setText("显示HEX【" + eTG[2].getText().toString() + "】");
                            }
                        }));

                        //距离显示模式
                        Button tftDistanceButton = tftLayout.findViewById(R.id.tftDistanceButton);
                        tftDistanceButton.setOnClickListener(view -> {
                            hashMap.put("ctrl", "距离显示");
                            hashMap.put("content", eTG[3].getText().toString());
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        //监听编辑窗口返回的内容
                        eTG[3].addTextChangedListener(new FloatEditTextWatcher(() -> {
                            if (eTG[3].getText().length() == 0) {
                                HandlerUtil.sendMsg("输入的值不符合规范，请修改");
                                tftDistanceButton.setEnabled(false);
                                tftDistanceButton.setText("输入的值不符合规范，请修改");
                            } else {
                                tftDistanceButton.setEnabled(true);
                                tftDistanceButton.setText("显示距离【JL-" + eTG[3].getText().toString() + "mm】");
                            }
                        }));

                        //交通标志显示模式
                        Button tftForward = tftLayout.findViewById(R.id.tftForward);
                        tftForward.setOnClickListener(view -> {
                            hashMap.put("ctrl", "直行");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        Button tftTurnLeft = tftLayout.findViewById(R.id.tftTurnLeft);
                        tftTurnLeft.setOnClickListener(view -> {
                            hashMap.put("ctrl", "左转");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        Button tftTurnRight = tftLayout.findViewById(R.id.tftTurnRight);
                        tftTurnRight.setOnClickListener(view -> {
                            hashMap.put("ctrl", "右转");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        Button tftBack = tftLayout.findViewById(R.id.tftBack);
                        tftBack.setOnClickListener(view -> {
                            hashMap.put("ctrl", "掉头");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        Button tftNoForward = tftLayout.findViewById(R.id.tftNoForward);
                        tftNoForward.setOnClickListener(view -> {
                            hashMap.put("ctrl", "禁止直行");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        Button tftNoEnter = tftLayout.findViewById(R.id.tftNoEnter);
                        tftNoEnter.setOnClickListener(view -> {
                            hashMap.put("ctrl", "禁止通行");
                            if (checkClassA.isChecked())
                                CommunicationUtil.tftCmd(A_FLAG, hashMap);
                            else if (checkClassB.isChecked())
                                CommunicationUtil.tftCmd(B_FLAG, hashMap);
                            else
                                CommunicationUtil.tftCmd(C_FLAG, hashMap);
                        });
                        break;
                    //HSV 动态调节和展示
                    case 2:
                        FloatWindowUtil imgFloatWindow = new FloatWindowUtil(requireActivity());
                        View floatImgLayout = imgFloatWindow.create(R.layout.float_img);
                        floatImgLayout.findViewById(R.id.floating_close).setVisibility(View.VISIBLE);
                        ImageView img = floatImgLayout.findViewById(R.id.floating_img);
                        FloatWindowUtil hsvFloatWindow = new FloatWindowUtil(requireActivity());
                        View floatHsvLayout = hsvFloatWindow.create(R.layout.float_hsv);
                        SeekBar[] hsvSeekBars;
                        hsvSeekBars = new SeekBar[]{floatHsvLayout.findViewById(R.id.floating_HMin), floatHsvLayout.findViewById(R.id.floating_SMin), floatHsvLayout.findViewById(R.id.floating_VMin), floatHsvLayout.findViewById(R.id.floating_HMax), floatHsvLayout.findViewById(R.id.floating_SMax), floatHsvLayout.findViewById(R.id.floating_VMax)};
                        TextView[] hsvTextViews;
                        hsvTextViews = new TextView[]{floatHsvLayout.findViewById(R.id.HMin_Text), floatHsvLayout.findViewById(R.id.SMin_Text), floatHsvLayout.findViewById(R.id.VMin_Text), floatHsvLayout.findViewById(R.id.HMax_Text), floatHsvLayout.findViewById(R.id.SMax_Text), floatHsvLayout.findViewById(R.id.VMax_Text)};
                        for (int position = 0; position <= hsvSeekBars.length - 1; position++) {
                            hsvSeekBars[position].setOnSeekBarChangeListener(new OnSeekBarChangeListener(position, hsvSeekBars, hsvTextViews, img));
                            //剪切板功能
                            if (position < 3) {
                                hsvTextViews[position].setOnLongClickListener(view -> {
                                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("th", hsvTextViews[0].getText().toString() + hsvTextViews[1].getText().toString() + hsvTextViews[2].getText().toString());
                                    clipboard.setPrimaryClip(clipData);
                                    Toast.makeText(activity, "已复制阈值到剪贴板", Toast.LENGTH_SHORT).show();
                                    return true;
                                });
                            } else {
                                hsvTextViews[position].setOnLongClickListener(view -> {
                                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("th", hsvTextViews[3].getText().toString() + hsvTextViews[4].getText().toString() + hsvTextViews[5].getText().toString());
                                    clipboard.setPrimaryClip(clipData);
                                    Toast.makeText(activity, "已复制阈值到剪贴板", Toast.LENGTH_SHORT).show();
                                    return true;
                                });
                            }
                            switch (position) {
                                case 0:
                                case 1:
                                case 3:
                                case 4:
                                    hsvTextViews[position].setText(hsvSeekBars[position].getProgress() + ", ");
                                    break;
                                case 2:
                                case 5:
                                    hsvTextViews[position].setText(String.valueOf(hsvSeekBars[position].getProgress()));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    //竞赛平台拍照系统
                    case 3:
                        FloatWindowUtil cameraFloatWindow = new FloatWindowUtil(requireActivity());
                        View floatCameraLayout = cameraFloatWindow.create(R.layout.float_camera);
                        RadioGroup cameraGroup = floatCameraLayout.findViewById(R.id.floating_camera);
                        LinearLayout cameraTFTClassLayout = floatCameraLayout.findViewById(R.id.camera_tft_class);
                        RadioButton checkClassA2 = floatCameraLayout.findViewById(R.id.tft_class_a);
                        RadioButton checkClassB2 = floatCameraLayout.findViewById(R.id.tft_class_b);
                        AtomicReference<String> pgCmd = new AtomicReference<>("拍立得");
                        cameraGroup.setOnCheckedChangeListener((group, checkedId) -> {
                            if (checkedId == R.id.ones_camera) pgCmd.set("一秒一拍");
                            if (checkedId == R.id.click_camera) pgCmd.set("拍立得");
                            if (checkedId == R.id.tft_camera) {
                                pgCmd.set("TFT模式");
                                cameraTFTClassLayout.setVisibility(View.VISIBLE);
                            } else {
                                cameraTFTClassLayout.setVisibility(View.GONE);
                            }
                        });
                        Button tftStopCamera = floatCameraLayout.findViewById(R.id.tftStopCamera);
                        //停止拍照
                        tftStopCamera.setOnClickListener(view -> {
                            if (FileUtil.isCameraPlayFlag()) {
                                HandlerUtil.sendMsg("拍照任务停止");
                                FileUtil.setCameraPlayFlag(false);
                            } else {
                                HandlerUtil.sendMsg("目前没有拍照任务");
                            }
                        });
                        Button tftStartCamera = floatCameraLayout.findViewById(R.id.tftStartCamera);
                        //开始拍照
                        tftStartCamera.setOnClickListener(view -> {
                            if (!FileUtil.isCameraPlayFlag()) {
                                HandlerUtil.sendMsg("开始拍照任务, 任务名称：" + pgCmd.get());
                                FileUtil.setCameraPlayFlag(true);
                                byte classID;
                                if (checkClassA2.isChecked())
                                    classID = A_FLAG;
                                else if (checkClassB2.isChecked())
                                    classID = B_FLAG;
                                else
                                    classID = C_FLAG;
                                FileUtil.camera(classID, pgCmd.get());
                            } else {
                                HandlerUtil.sendMsg("拍照任务已经开始，请勿重复开始任务");
                            }
                        });
                        break;
                    //万能通信指令
                    case 4:
                        FloatWindowUtil floatTerminalWindow = new FloatWindowUtil(requireActivity());
                        View floatTerminalLayout = floatTerminalWindow.create(R.layout.float_terminal);
                        //设置通信指令主指令选择
                        GridLayout mainCmdGroup = floatTerminalLayout.findViewById(R.id.main_cmd_item_group);
                        new FlowRadioGroup(mainCmdGroup, SharedPreferencesUtil.mainCmd);
                        EditText terminalEditText = floatTerminalLayout.findViewById(R.id.hexTerminal);
                        //获得焦点时打开编辑窗口
                        terminalEditText.setOnClickListener(view -> {
                            EditDialog dialog = new EditDialog(new EditDialogBean(terminalEditText, true));
                            dialog.show(activity.getSupportFragmentManager(), "Edit Dialog");
                        });
                        terminalEditText.setOnFocusChangeListener((view, b) -> {
                            if (b) {
                                EditDialog dialog = new EditDialog(new EditDialogBean(terminalEditText, true));
                                dialog.show(activity.getSupportFragmentManager(), "Edit Dialog");
                            }
                        });
                        AtomicReference<String> unCmd = new AtomicReference<>("get");
                        RadioGroup terminalMethod = floatTerminalLayout.findViewById(R.id.terminal_method);
                        terminalMethod.setOnCheckedChangeListener((group, checkedId) -> {
                            if (checkedId == R.id.get_method) unCmd.set("get");
                            if (checkedId == R.id.post_method) unCmd.set("post");
                            if (checkedId == R.id.not_method) unCmd.set("");
                        });
                        Button sendHexTerminalData = floatTerminalLayout.findViewById(R.id.sendHexTerminalData);
                        sendHexTerminalData.setOnClickListener(view -> {
                            //发送万能指令数据到竞赛平台
                            if (!terminalEditText.getText().toString().isEmpty()) {
                                String mainCmd = SharedPreferencesUtil.queryKey2Value(SharedPreferencesUtil.mainCmd);
                                switch (mainCmd) {
                                    case "无\nNOT":
                                        mainCmd = "";
                                        break;
                                    case "从车启动\n(0xEE)":
                                        mainCmd = "EE";
                                        break;
                                    case "从车任务完成\n(0xFF)":
                                        mainCmd = "FF";
                                        break;
                                    case "主车路径\n(0xB6)":
                                        mainCmd = "B6";
                                        break;
                                    case "RFID密钥B\n(0xA3)":
                                        mainCmd = "A3";
                                        break;
                                    case "RFID扇区\n(0xA4)":
                                        mainCmd = "A4";
                                        break;
                                    case "开启标志位\n(0xB9)\n(0x01)RFID\n(0x02)地形":
                                        mainCmd = "B9";
                                        break;
                                }
                                List<String> text = new ArrayList<>();
                                if (!mainCmd.isEmpty())
                                    text.add(mainCmd);
                                String[] cmdContent = String.valueOf(terminalEditText.getText()).split(" ");
                                Collections.addAll(text, cmdContent);
                                try {
                                    byte[] data = new byte[text.size()];
                                    for (int i = 0; i < data.length; i++) {
                                        data[i] = (byte) Integer.parseInt(text.get(i), 16);
                                    }
                                    if (unCmd.get().isEmpty()) {
                                        CommunicationUtil.sendData(data);
                                    } else {
                                        CommunicationUtil.sendData(unCmd.get(), REPLY_FLAG, data);
                                    }
                                } catch (NumberFormatException e) {
                                    HandlerUtil.sendMsg("没有选择主指令类型");
                                }
                            }
                        });
                        Button sendHexDataForAndroid = floatTerminalLayout.findViewById(R.id.sendHexDataForAndroid);
                        sendHexDataForAndroid.setOnClickListener(v -> {
                            //发送万能指令数据到安卓本身
                            if (!terminalEditText.getText().toString().isEmpty()) {
                                String[] text = String.valueOf(terminalEditText.getText()).split(" ");
                                byte[] data = new byte[text.length];
                                for (int i = 0; i < data.length; i++) {
                                    data[i] = (byte) Integer.parseInt(text[i], 16);
                                }
                                HandlerUtil.sendMsg(HandlerUtil.DATA_PARSE_FLAG, data);
                                HandlerUtil.sendMsg(HandlerUtil.DATA_PARSE_FLAG, new byte[]{CAR_FLAG, REPLY_FLAG, END_FLAG});
                            } else {
                                HandlerUtil.sendMsg("发送至安卓本身，输入框请勿留空");
                            }
                        });
                        break;
                    //摄像头角度控制
                    case 5:
                        FloatWindowUtil cameraVaFloatWindow = new FloatWindowUtil(requireActivity());
                        View floatCameraVaLayout = cameraVaFloatWindow.create(R.layout.float_camera_va);
                        RadioGroup vaCameraGroup = floatCameraVaLayout.findViewById(R.id.va_camera);
                        AtomicReference<String> vaCmd = new AtomicReference<>("toggle");
                        vaCameraGroup.setOnCheckedChangeListener((group, checkedId) -> {
                            if (checkedId == R.id.va_set) vaCmd.set("set");
                            if (checkedId == R.id.va_toggle) vaCmd.set("toggle");
                        });
                        Button[] vaButtons = {floatCameraVaLayout.findViewById(R.id.va_default1_button), floatCameraVaLayout.findViewById(R.id.va_default2_button), floatCameraVaLayout.findViewById(R.id.va_default3_button), floatCameraVaLayout.findViewById(R.id.va_default4_button)};
                        for (int i = 0; i < vaButtons.length; i++) {
                            int cmdID = i + 1;
                            vaButtons[i].setOnClickListener(v -> {
                                ThreadUtil.createThread(() -> CommunicationUtil.moveCamera(vaCmd.get() + cmdID));
                            });
                        }
                        floatCameraVaLayout.findViewById(R.id.va_reset_button).setOnClickListener(v -> {
                            ThreadUtil.createThread(() -> CommunicationUtil.moveCamera("reset"));
                        });
                        break;
                    //启动主车
                    case 6:
                        CommunicationUtil.startCar();
                        break;
                    //加载本地图像到 DEBUG
                    case 7:
                        FileUtil.selectImageFile();
                        break;
                    //二维码识别
                    case 8:
                        ImgPcsUtil.recQRCode(A_FLAG, false);
                        break;
                    //文字识别
                    case 9:
                        ThreadUtil.createThread(() -> {
                            List<OCRRect> results = ImgPcsUtil.ocr(ImgPcsUtil.equalScaleImage(ImgPcsUtil.getImg()), false);
                            results.forEach(result -> {
                                LogUtil.printLog("文字识别：" + result.getLabel() + " 置信率：" + result.getConfidence());
                            });
                        });
                        break;
                    //中文识别
                    case 10:
                        ImgPcsUtil.recOnceChineseText(A_FLAG);
                        break;
                    //车牌内容识别
                    case 11:
                        ThreadUtil.createThread(() -> {
                            ImgPcsUtil.findLP(ImgPcsUtil.getImg());
                        });
                        break;
                    //交通灯识别
                    case 12:
                        ThreadUtil.createThread(() -> {
                            Bitmap recImg = ImgPcsUtil.getImg();
                            List<RectResult> rectResults = ImgPcsUtil.imageRecognition(recImg, ImgPcsUtil.TLR);
                            ImgPcsUtil.drawLabels(rectResults, recImg, false);
                        });
                        break;
                    //查找交通标志后识别多边形
                    case 13:
                        ImgPcsUtil.recTrafficSignsAndShapes(A_FLAG, true, true, false);
                        break;
                    //交通标志查找和识别
                    case 14:
                        ImgPcsUtil.recTrafficSignsAndShapes(A_FLAG, true, false, false);
                        break;
                    //多边形查找和识别
                    case 15:
                        ImgPcsUtil.recTrafficSignsAndShapes(A_FLAG, false, true, false);
                        break;
                    //车型车牌识别
                    case 16:
                        ThreadUtil.createThread(() -> {
                            Bitmap recImg = ImgPcsUtil.getImg();
                            List<RectResult> rectResults = ImgPcsUtil.imageRecognition(recImg, ImgPcsUtil.VTR);
                            ImgPcsUtil.drawLabels(rectResults, recImg, false);
                        });
                        break;
                    //口罩识别
                    case 17:
                        ThreadUtil.createThread(() -> {
                            Bitmap recImg = ImgPcsUtil.getImg();
                            List<RectResult> rectResults = ImgPcsUtil.imageRecognition(recImg, ImgPcsUtil.MR);
                            ImgPcsUtil.drawLabels(rectResults, recImg, false);
                        });
                        break;
                    //行人识别
                    case 18:
                        ImgPcsUtil.recPerson(A_FLAG);
                        break;
                    //TFT 中文查找和识别
                    case 19:
                        ImgPcsUtil.recTftChineseTextRecognition(A_FLAG);
                        break;
                    //TFT 车牌查找和识别
                    case 20:
                        ImgPcsUtil.recLP(A_FLAG);
                        break;
                    default:
                        break;
                }
                //关闭 Dialog
                dismiss();
            }

            /**
             * 长按操作
             * @param viewHolder 视图
             */
            @Override
            public void onItemLongClick(RecyclerView.ViewHolder viewHolder) {
            }
        });
    }


    /**
     * HSV 动态调节 SeekBar监听类
     */
    static class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        int position;
        SeekBar[] seekBars;
        TextView[] textViews;
        ImageView img;

        public OnSeekBarChangeListener(int position, SeekBar[] seekBars, TextView[] textViews, ImageView img) {
            this.position = position;
            this.seekBars = seekBars;
            this.textViews = textViews;
            this.img = img;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (position) {
                case 0:
                    if (progress > seekBars[3].getProgress()) {
                        seekBar.setProgress(seekBars[3].getProgress());
                    }
                    textViews[position].setText(seekBar.getProgress() + ", ");
                    ImgPcsUtil.realTimeHsv(new Scalar(seekBars[0].getProgress(), seekBars[1].getProgress(), seekBars[2].getProgress()), new Scalar(seekBars[3].getProgress(), seekBars[4].getProgress(), seekBars[5].getProgress()), img);
                    break;
                case 1:
                    if (progress > seekBars[4].getProgress()) {
                        seekBar.setProgress(seekBars[4].getProgress());
                    }
                    textViews[position].setText(seekBar.getProgress() + ", ");
                    ImgPcsUtil.realTimeHsv(new Scalar(seekBars[0].getProgress(), seekBars[1].getProgress(), seekBars[2].getProgress()), new Scalar(seekBars[3].getProgress(), seekBars[4].getProgress(), seekBars[5].getProgress()), img);
                    break;
                case 2:
                    if (progress > seekBars[5].getProgress()) {
                        seekBar.setProgress(seekBars[5].getProgress());
                    }
                    textViews[position].setText(String.valueOf(seekBar.getProgress()));
                    ImgPcsUtil.realTimeHsv(new Scalar(seekBars[0].getProgress(), seekBars[1].getProgress(), seekBars[2].getProgress()), new Scalar(seekBars[3].getProgress(), seekBars[4].getProgress(), seekBars[5].getProgress()), img);
                    break;
                case 3:
                    if (progress < seekBars[0].getProgress()) {
                        seekBar.setProgress(seekBars[0].getProgress());
                    }
                    textViews[position].setText(seekBar.getProgress() + ", ");
                    ImgPcsUtil.realTimeHsv(new Scalar(seekBars[0].getProgress(), seekBars[1].getProgress(), seekBars[2].getProgress()), new Scalar(seekBars[3].getProgress(), seekBars[4].getProgress(), seekBars[5].getProgress()), img);
                    break;
                case 4:
                    if (progress < seekBars[1].getProgress()) {
                        seekBar.setProgress(seekBars[1].getProgress());
                    }
                    textViews[position].setText(seekBar.getProgress() + ", ");
                    ImgPcsUtil.realTimeHsv(new Scalar(seekBars[0].getProgress(), seekBars[1].getProgress(), seekBars[2].getProgress()), new Scalar(seekBars[3].getProgress(), seekBars[4].getProgress(), seekBars[5].getProgress()), img);
                    break;
                case 5:
                    if (progress < seekBars[2].getProgress()) {
                        seekBar.setProgress(seekBars[2].getProgress());
                    }
                    textViews[position].setText(String.valueOf(seekBar.getProgress()));
                    ImgPcsUtil.realTimeHsv(new Scalar(seekBars[0].getProgress(), seekBars[1].getProgress(), seekBars[2].getProgress()), new Scalar(seekBars[3].getProgress(), seekBars[4].getProgress(), seekBars[5].getProgress()), img);
                    break;
                default:
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

    /**
     * 多行单选组点击事件监听类
     */
    static class RadioOnClickListener implements View.OnClickListener {

        RadioGroup[] rG;
        RadioButton modeButton;

        public RadioOnClickListener(RadioGroup[] rG, RadioButton modeButton) {
            this.rG = rG;
            this.modeButton = modeButton;
        }

        /**
         * 关闭其他单选按钮
         */
        private void shutdownOther() {
            boolean flag = false;
            for (RadioGroup r : rG) {
                for (int i = 0; i <= r.getChildCount() - 1; i++) {
                    if (r.getChildAt(i) == modeButton) {
                        flag = true;
                    }
                }
                if (!flag) {
                    r.clearCheck();
                } else {
                    flag = false;
                }
            }
        }

        @Override
        public void onClick(View view) {
            shutdownOther();
        }
    }

    /**
     * 悬浮窗输入框监听器
     */
    static class FloatEditTextWatcher implements TextWatcher {

        TextWatcherCallBack callBack;

        public FloatEditTextWatcher(TextWatcherCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            callBack.callBack();
        }
    }

    /**
     * 绑定单选事件和指定View
     */
    static class BindChecked implements CompoundButton.OnCheckedChangeListener {

        TableLayout layout;

        public BindChecked(TableLayout layout) {
            this.layout = layout;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                layout.setVisibility(View.VISIBLE);
            } else {
                layout.setVisibility(View.GONE);
            }
        }
    }

}


