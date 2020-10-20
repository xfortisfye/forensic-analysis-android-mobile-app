package com.example.icarus;

import android.widget.TextView;

public class FATable {
    /** In decimal **/
    private long startOfFirstFat;
    private long endOfFirstFat; // Start of Second FAT
    private long endOfLastFat;
    private String FatID;
    private String endClusterMarker;

    public FATable () {
    }

    public FATable (long startOfFirstFat, long endOfFirstFat, long endOfLastFat) {
        setStartOfFirstFat(startOfFirstFat);
        setEndOfFirstFat(endOfFirstFat);
        setEndOfLastFat(endOfLastFat);
    }

    public void setStartOfFirstFat(long startOfFirstFat) {
        this.startOfFirstFat = startOfFirstFat;
    }

    public void setEndOfFirstFat(long endOfFirstFat) {
        this.endOfFirstFat = endOfFirstFat;
    }

    public void setEndOfLastFat(long endOfLastFat) {
        this.endOfLastFat = endOfLastFat;
    }

    public void setFatID(String fatID) {
        this.FatID = fatID;
    }

    public void setEndClusterMarker(String endClusterMarker) {
        this.endClusterMarker = endClusterMarker;
    }

    public Long getStartOfFirstFat() {
        return startOfFirstFat;
    }

    public Long getEndOfFirstFat() {
        return endOfFirstFat;
    }

    public Long getEndOfLastFat() {
        return endOfLastFat;
    }

    public String getFatID() {
        return FatID;
    }

    public String getEndClusterMarker() {
        return endClusterMarker;
    }

    public void toString(TextView testingText) {
        testingText.append("FATABLE INFORMATION: " + "\n");
        testingText.append("    FATID: " + this.getFatID()+ "\n");
        testingText.append("    End Cluster Mark: " + this.getEndClusterMarker()+ "\n");
        testingText.append("    Start of First FAT (First byte): " + this.getStartOfFirstFat() + "\n");
        testingText.append("    End of First FAT (Last byte):" + this.getEndOfFirstFat() + "\n");
        testingText.append("    End of Last FAT:" + this.getEndOfLastFat() + "\n");
    }
}

