package net.kuisec.r8c.Bean;

import org.opencv.core.Rect;

public class RectResult {
    private String label;
    private final float confidence;
    private final Rect rect;

    public RectResult(String label, float confidence, Rect rect) {
        this.label = label;
        this.confidence = confidence;
        this.rect = rect;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getConfidence() {
        return confidence;
    }

    public Rect getRect() {
        return rect;
    }
}
