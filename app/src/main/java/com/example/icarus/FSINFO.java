package com.example.icarus;

public class FSINFO {
    private String FSINOSignature;
    private String localSignature;
    private String lastKnownFreeCluster;
    private String nextFreeCluster;
    private String trailingSignature;

    public FSINFO() {
    }

    public void setFSINOSignature(String FSINOSignature) {
        this.FSINOSignature = FSINOSignature;
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

    public String getFSINOSignature() {
        return FSINOSignature;
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

}
