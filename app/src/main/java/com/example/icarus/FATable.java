package com.example.icarus;

public class FATable {
    private String FatID;
    private String endClusterMarker;

    public FATable () {
    }

    public void setFatID(String fatID) {
        this.FatID = fatID;
    }

    public void setEndClusterMarker(String endClusterMarker) {
        this.endClusterMarker = endClusterMarker;
    }

    public String getFatID() {
        return FatID;
    }

    public String getEndClusterMarker() {
        return endClusterMarker;
    }
}

