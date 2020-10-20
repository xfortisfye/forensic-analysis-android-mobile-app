package com.example.icarus;

import android.widget.TextView;

public class DataRegion {
    /** In decimal **/
    private long startOfDataRegion;
    private long endOfDataRegion;
    public DataRegion(){

    }

    public DataRegion(long startOfDataRegion, long endOfDataRegion){
        this.setStartOfDataRegion(startOfDataRegion);
        this.setEndOfDataRegion(endOfDataRegion);
    }

    public void setStartOfDataRegion(long startOfDataRegion) {
        this.startOfDataRegion = startOfDataRegion;
    }

    public void setEndOfDataRegion(long endOfDataRegion) {
        this.endOfDataRegion = endOfDataRegion;
    }

    public long getStartOfDataRegion() {
        return startOfDataRegion;
    }

    public long getEndOfDataRegion() {
        return endOfDataRegion;
    }

    public void toString(TextView testingText) {
        testingText.append("DATA REGION: " + "\n");
        testingText.append("    Start of Data Region:" + this.getStartOfDataRegion() + "\n");
        testingText.append("    End  of Data Region:" + this.getEndOfDataRegion() + "\n");
    }
}
