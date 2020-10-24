package com.example.icarus;

import android.widget.TextView;

public class FATable extends Grab{
    /** In decimal **/
    private long startFirstFatSect;
    private long endFirstFatSect; // Start of Second FAT
    private long endLastFatSect;
    private long startFirstFatDec;
    private long endFirstFatDec; // Start of Second FAT
    private long endLastFatDec;
    private String FatID;
    private String endClusterMarker;

    public FATable () {
    }

    public FATable (long startFirstFatSect, long endFirstFatSect, long endLastFatSect, long bytesPerSector) {
        setStartFirstFatSect(startFirstFatSect);
        setEndFirstFatSect(endFirstFatSect);
        setEndLastFatSect(endLastFatSect);

        setStartFirstFatDec(bytesPerSector);
        setEndFirstFatDec(bytesPerSector);
        setEndLastFatDec(bytesPerSector);
    }

    public void setStartFirstFatSect(long startFirstFatSect) { this.startFirstFatSect = startFirstFatSect; }
    public void setEndFirstFatSect(long endFirstFatSect) {
        this.endFirstFatSect = endFirstFatSect;
    }
    public void setEndLastFatSect(long endLastFatSect) {
        this.endLastFatSect = endLastFatSect;
    }
    public void setStartFirstFatDec(long bytesPerSector) { this.startFirstFatDec = getStartFirstFatSect() * bytesPerSector; }
    public void setEndFirstFatDec(long bytesPerSector) { this.endFirstFatDec = (getEndFirstFatSect() * bytesPerSector) + bytesPerSector - 1; }
    public void setEndLastFatDec(long bytesPerSector) { this.endLastFatDec = (getEndLastFatSect() * bytesPerSector) + bytesPerSector - 1; }

    public void setFatID(String fatID) {
        this.FatID = fatID;
    }
    public void setEndClusterMarker(String endClusterMarker) { this.endClusterMarker = endClusterMarker; }

    public Long getStartFirstFatSect() {
        return startFirstFatSect;
    }
    public Long getEndFirstFatSect() {
        return endFirstFatSect;
    }
    public Long getEndLastFatSect() {
        return endLastFatSect;
    }
    public Long getStartFirstFatDec() {
        return startFirstFatDec;
    }
    public Long getEndFirstFatDec() { return endFirstFatDec; }
    public Long getEndLastFatDec() {
        return endLastFatDec;
    }

    public String getFatID() {
        return FatID;
    }
    public String getEndClusterMarker() {
        return endClusterMarker;
    }

    public void toString(TextView testingText) {
        testingText.append("[     FAT TABLE INFORMATION     ]" + "\n");
        testingText.append("FATID: " + this.getFatID()+ "\n");
        testingText.append("End Cluster Mark: " + this.getEndClusterMarker()+ "\n");
        testingText.append("Start of First FAT (First sect): " + this.getStartFirstFatSect() + "\n");
        testingText.append("End of First FAT (Last sect): " + this.getEndFirstFatSect() + "\n");
        testingText.append("End of Last FAT (Last sect): " + this.getEndLastFatSect() + "\n");
        testingText.append("Start of First FAT (First byte): " + this.getStartFirstFatDec() + "\n");
        testingText.append("End of First FAT (Last byte): " + this.getEndFirstFatDec() + "\n");
        testingText.append("End of Last FAT (Last byte): " + this.getEndLastFatDec() + "\n");
    }

    public Boolean chkDmgCluster(long dmgCluster) {
        if (dmgCluster == 268435447) {
            return true;
        }
        else {
            return false;
        }
    }


    public Boolean chkEOFCluster(long eofCluster) {
        if (eofCluster == 268435448 || eofCluster == 268435449 || eofCluster == 268435450 || eofCluster == 268435451
                || eofCluster == 268435452 || eofCluster == 268435453 || eofCluster == 268435454 || eofCluster == 268435455) {
            return true;
        }

        else {
            return false;
        }
    }
}

