package net.kuisec.r8c.CustomView;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.RadioButton;

import net.kuisec.r8c.Utils.SharedPreferencesUtil;

public class FlowRadioGroup implements View.OnClickListener {
    GridLayout group;
    RadioButton[] buttons;

    public FlowRadioGroup(GridLayout group, String saveKey) {
        this.group = group;
        buttons = new RadioButton[group.getChildCount()];
        for (int i = 0; i < group.getChildCount(); i++) {
            buttons[i] = (RadioButton) group.getChildAt(i);
            buttons[i].setOnClickListener(this);
            buttons[i].setOnCheckedChangeListener(RadioButtonCheckedChangedListener.Builder(saveKey));
        }
        showRadioButtonCheckedView(group, saveKey);
    }

    @Override
    public void onClick(View view) {
        RadioButton clickedButton = (RadioButton) view;
        for (RadioButton button : buttons) {
            if (clickedButton == button) {
                clickedButton.setChecked(true);
            } else {
                button.setChecked(false);
            }
        }
    }

    /**
     * 从存储中查找指定内容，并对比 RadioButton 的 Text
     * @param group 存放多个 RadioButton 的 GridLayout 布局
     * @param key 从存储中查找的内容
     */
    private void showRadioButtonCheckedView(GridLayout group, String key) {
        String value = SharedPreferencesUtil.queryKey2Value(key);
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton showButton = (RadioButton)group.getChildAt(i);
            if (showButton.getText().toString().equals(value)) {
                showButton.setChecked(true);
                break;
            }
        }
    }


    /**
     * 当自定义 RadioButton 状态改变时更新存储的键值
     */
    static class RadioButtonCheckedChangedListener implements CompoundButton.OnCheckedChangeListener {
        public String saveKey;

        public static RadioButtonCheckedChangedListener Builder(String saveKey) {
            RadioButtonCheckedChangedListener radioButtonCheckedChangedListener = new RadioButtonCheckedChangedListener();
            radioButtonCheckedChangedListener.saveKey = saveKey;
            return radioButtonCheckedChangedListener;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                SharedPreferencesUtil.insert(saveKey, compoundButton.getText().toString());
            }
        }
    }
}
