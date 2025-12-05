package com.example.diagsmartv2.sensors;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SensorInfo implements Parcelable {

    private String name;
    private String vendor;
    private String type;
    private String power;
    private String resolution;
    private String maxRange;
    private String wakeupSensor;
    private String dynamicSensor;

    public SensorInfo(String name, String vendor, String type, String power, String resolution, String maxRange, String wakeupSensor, String dynamicSensor) {
        this.name = name;
        this.vendor = vendor;
        this.type = type;
        this.power = power;
        this.resolution = resolution;
        this.maxRange = maxRange;
        this.wakeupSensor = wakeupSensor;
        this.dynamicSensor = dynamicSensor;
    }

    protected SensorInfo(Parcel in) {
        name = in.readString();
        vendor = in.readString();
        type = in.readString();
        power = in.readString();
        resolution = in.readString();
        maxRange = in.readString();
        wakeupSensor = in.readString();
        dynamicSensor = in.readString();
    }

    public static final Creator<SensorInfo> CREATOR = new Creator<SensorInfo>() {
        @Override
        public SensorInfo createFromParcel(Parcel in) {
            return new SensorInfo(in);
        }

        @Override
        public SensorInfo[] newArray(int size) {
            return new SensorInfo[size];
        }
    };

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPower() { return power; }
    public void setPower(String power) { this.power = power; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public String getMaxRange() { return maxRange; }
    public void setMaxRange(String maxRange) { this.maxRange = maxRange; }
    public String getWakeupSensor() { return wakeupSensor; }
    public void setWakeupSensor(String wakeupSensor) { this.wakeupSensor = wakeupSensor; }
    public String getDynamicSensor() { return dynamicSensor; }
    public void setDynamicSensor(String dynamicSensor) { this.dynamicSensor = dynamicSensor; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(vendor);
        dest.writeString(type);
        dest.writeString(power);
        dest.writeString(resolution);
        dest.writeString(maxRange);
        dest.writeString(wakeupSensor);
        dest.writeString(dynamicSensor);
    }
}

