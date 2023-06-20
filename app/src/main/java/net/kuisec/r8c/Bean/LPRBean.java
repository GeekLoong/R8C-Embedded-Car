package net.kuisec.r8c.Bean;

/**
 * 车牌识别结果返回 Bean 类
 */
public class LPRBean {
    private int minX = 0, minY = 0, width = 0, height = 0;
    private boolean findSuccess = false;

    public boolean isFindSuccess() {
        return findSuccess;
    }
    public void setFindSuccess(boolean findSuccess) {
        this.findSuccess = findSuccess;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
