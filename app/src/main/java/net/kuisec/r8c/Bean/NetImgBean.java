package net.kuisec.r8c.Bean;

import android.graphics.Bitmap;

/**
 * 网络图片 Bean 类
 */
public class NetImgBean {
    private final int code;
    private final Bitmap img;

    public NetImgBean(int code, Bitmap img) {
        this.code = code;
        this.img = img;
    }

    public int getCode() {
        return code;
    }

    public Bitmap getImg() {
        return img;
    }
}
