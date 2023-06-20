package net.kuisec.r8c.Bean;

public class DialogItemBean {
    private final int icon;
    private final String title;

    public DialogItemBean(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
