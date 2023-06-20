package net.kuisec.r8c.Bean;

import android.widget.EditText;

public class EditDialogBean {
    private final String title;
    private final String hint;
    private final String text;
    private final int maxLines;
    private final int maxLength;
    private final int inputType;
    private final EditText eT;
    private boolean textHexDeal;

    public EditDialogBean(EditText eT) {
        this.title = "编辑内容";
        this.hint = eT.getHint().toString();
        this.text = eT.getText().toString();
        this.maxLines = eT.getMaxLines();
        this.maxLength = eT.getMaxEms();
        this.inputType = eT.getInputType();
        this.eT = eT;
    }

    public EditDialogBean(EditText eT, boolean textHexDeal) {
        this.title = "编辑内容";
        this.hint = eT.getHint().toString();
        this.text = eT.getText().toString();
        this.maxLines = eT.getMaxLines();
        this.maxLength = eT.getMaxEms();
        this.inputType = eT.getInputType();
        this.eT = eT;
        this.textHexDeal = textHexDeal;
    }

    public EditDialogBean(String title, EditText eT) {
        this.title = title;
        this.hint = eT.getHint().toString();
        this.text = eT.getText().toString();
        this.maxLines = eT.getMaxLines();
        this.maxLength = eT.getMaxEms();
        this.inputType = eT.getInputType();
        this.eT = eT;
    }

    public String getTitle() {
        return title;
    }

    public String getHint() {
        return hint;
    }

    public String getText() {
        return text;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getInputType() {
        return inputType;
    }

    public EditText getEditText() {
        return eT;
    }

    public boolean isTextHexDeal() {
        return textHexDeal;
    }
}
