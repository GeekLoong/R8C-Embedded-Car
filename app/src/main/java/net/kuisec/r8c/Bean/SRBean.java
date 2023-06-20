package net.kuisec.r8c.Bean;

import android.graphics.Bitmap;

public class SRBean {
    private boolean success = false;
    private Bitmap bitmap = null;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
