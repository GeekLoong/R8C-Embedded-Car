package net.kuisec.r8c.CustomView;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import net.kuisec.r8c.Bean.EditDialogBean;
import net.kuisec.r8c.R;
import net.kuisec.r8c.databinding.DialogEditBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditDialog extends DialogFragment {

    DialogEditBinding binding;
    EditDialogBean bean;

    public EditDialog(EditDialogBean bean) {
        this.bean = bean;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Dialog);
        binding = DialogEditBinding.inflate(getLayoutInflater());
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        return dialog;
    }

    /**
     * 初始化弹窗
     */
    private void initData() {
        binding.editDialogTitle.setText(bean.getTitle());
        binding.editDialogContent.setInputType(bean.getInputType());
        binding.editDialogContent.setText(bean.getText());
        binding.editDialogContent.setHint(new SpannableString(bean.getHint()));
        binding.editDialogContent.setMaxLines(bean.getMaxLines());
        binding.editDialogFinish.setOnClickListener(v -> {
            bean.getEditText().setText(binding.editDialogContent.getText());
            this.dismiss();
        });
        //判断是否需要16进制处理
        if (bean.isTextHexDeal()) {
            binding.editDialogContent.setFilters(new InputFilter[] {new InputFilter.LengthFilter(bean.getMaxLength()), new InputFilter.AllCaps()});
            binding.editDialogContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String editable = binding.editDialogContent.getText().toString();
                    //只能输入16进制
                    String regEx = "[^A-F0-9 ]";
                    Pattern pattern = Pattern.compile(regEx);
                    Matcher matcher = pattern.matcher(editable);
                    //删掉不是字母或数字的字符
                    String str = matcher.replaceAll("");
                    if(!editable.equals(str)){
                        binding.editDialogContent.setText(str);
                        //因为删除了字符，要重写设置新的光标所在位置
                        binding.editDialogContent.setSelection(str.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

}
