package net.kuisec.r8c.Bean;

import org.opencv.core.Rect;

public class OCRRect {
    private String label;
    private final int rotationAngle;
    private final float confidence;
    private final Rect rect;


    public OCRRect(String label, int rotationAngle, float confidence, Rect rect) {
        this.label = label;
        this.rotationAngle = rotationAngle;
        this.confidence = confidence;
        this.rect = rect;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public float getConfidence() {
        return confidence;
    }

    public Rect getRect() {
        return rect;
    }
}
