package com.kuisec.rec.plugin.results;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class RecResult implements Parcelable {

    private final String label;
    private final float confidence;
    private int[] startTopPoint = new int[2];
    private int[] endTopPoint = new int[2];
    private int[] endBottomPoint = new int[2];
    private int[] startBottomPoint = new int[2];

    protected RecResult(Parcel in) {
        this.label = in.readString();
        this.confidence = in.readFloat();
        in.readIntArray(startTopPoint);
        in.readIntArray(endTopPoint);
        in.readIntArray(endBottomPoint);
        in.readIntArray(startBottomPoint);
    }

    public RecResult(String label, float confidence, int[] startTopPoint, int[] endTopPoint, int[] endBottomPoint, int[] startBottomPoint) {
        this.label = label;
        this.confidence = confidence;
        this.startTopPoint = startTopPoint;
        this.endTopPoint = endTopPoint;
        this.endBottomPoint = endBottomPoint;
        this.startBottomPoint = startBottomPoint;
    }

    public static final Creator<RecResult> CREATOR = new Creator<RecResult>() {
        @Override
        public RecResult createFromParcel(Parcel in) {
            return new RecResult(in);
        }

        @Override
        public RecResult[] newArray(int size) {
            return new RecResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeFloat(confidence);
        dest.writeIntArray(startTopPoint);
        dest.writeIntArray(endTopPoint);
        dest.writeIntArray(endBottomPoint);
        dest.writeIntArray(startBottomPoint);
    }

    public String getLabel() {
        return label;
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
