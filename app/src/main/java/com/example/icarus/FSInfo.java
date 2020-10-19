package com.example.icarus;

import android.widget.TextView;

public class FSInfo {
    private String FSInfoSignature;
    private String localSignature;
    private String lastKnownFreeCluster;
    private String nextFreeCluster;
    private String trailingSignature;

    public FSInfo() {
    }

    public void setFSInfoSignature(String FSInfoSignature) {
        this.FSInfoSignature = FSInfoSignature;
    }

    public void setLastKnownFreeCluster(String lastKnownFreeCluster) {
        this.lastKnownFreeCluster = lastKnownFreeCluster;
    }

    public void setLocalSignature(String localSignature) {
        this.localSignature = localSignature;
    }

    public void setNextFreeCluster(String nextFreeCluster) {
        this.nextFreeCluster = nextFreeCluster;
    }

    public void setTrailingSignature(String trailingSignature) {
        this.trailingSignature = trailingSignature;
    }

    public String getFSInfoSignature() {
        return FSInfoSignature;
    }

    public String getLastKnownFreeCluster() {
        return lastKnownFreeCluster;
    }

    public String getLocalSignature() {
        return localSignature;
    }

    public String getNextFreeCluster() {
        return nextFreeCluster;
    }

    public String getTrailingSignature() {
        return trailingSignature;
    }

    public Boolean chkFSINFOValidity(){
        if (this.getFSInfoSignature().equals("41615252") && this.getTrailingSignature().equals("0000AA55")) {
            return true;
        }
        else {
            return false;
        }
    }

    public void toString(TextView testingText) {
        testingText.append("FSINFO FSInfoSignature:" + this.getFSInfoSignature()+ "\n");
        testingText.append("FSINFO LastKnownFreeCluster:" + this.getLastKnownFreeCluster()+ "\n");
        testingText.append("FSINFO LocalSignature:" + this.getLocalSignature()+ "\n");
        testingText.append("FSINFO NextFreeCluster:" + this.getNextFreeCluster()+ "\n");
        testingText.append("FSINFO TrailingSignature:" + this.getTrailingSignature()+ "\n");
    }

}
