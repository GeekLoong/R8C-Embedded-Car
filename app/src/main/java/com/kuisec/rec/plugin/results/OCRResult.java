package com.kuisec.rec.plugin.results;
import android.os.Parcel;
import android.os.Parcelable;

public class OCRResult implements Parcelable {

    private final String label;
    private final int labelIndex;
    private final float confidence;
    private int[] startTopPoint = new int[2];
    private int[] endTopPoint = new int[2];
    private int[] endBottomPoint = new int[2];
    private int[] startBottomPoint = new int[2];

    protected OCRResult(Parcel in) {
        this.label = in.readString();
        this.labelIndex = in.readInt();
        this.confidence = in.readFloat();
        in.readIntArray(startTopPoint);
        in.readIntArray(endTopPoint);
        in.readIntArray(endBottomPoint);
        in.readIntArray(startBottomPoint);
    }

    public OCRResult(String label, int labelIndex, float confidence, int[] startTopPoint, int[] endTopPoint, int[] endBottomPoint, int[] startBottomPoint) {
        this.label = label;
        this.labelIndex = labelIndex;
        this.confidence = confidence;
        this.startTopPoint = startTopPoint;
        this.endTopPoint = endTopPoint;
        this.endBottomPoint = endBottomPoint;
        this.startBottomPoint = startBottomPoint;
    }

    public static final Creator<OCRResult> CREATOR = new Creator<OCRResult>() {
        @Override
        public OCRResult createFromParcel(Parcel in) {
            return new OCRResult(in);
        }

        @Override
        public OCRResult[] newArray(int size) {
            return new OCRResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeInt(labelIndex);
        dest.writeFloat(confidence);
        dest.writeIntArray(startTopPoint);
        dest.writeIntArray(endTopPoint);
        dest.writeIntArray(endBottomPoint);
        dest.writeIntArray(startBottomPoint);
    }

    public String getLabel() {
        return label;
    }

    public int getLabelIndex() {
        return labelIndex;
    }

    public float getConfidence() {
        return confidence;
    }

    public int[] getStartTopPoint() {
        return startTopPoint;
    }

    public int[] getEndTopPoint() {
        return endTopPoint;
    }

    public int[] getEndBottomPoint() {
        return endBottomPoint;
    }

    public int[] getStartBottomPoint() {
        return startBottomPoint;
    }
}
