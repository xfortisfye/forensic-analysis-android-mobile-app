package com.example.icarus;

public class FATable {
    private String startSector;
    private String FatID;
    private String endClusterMarker;

    public FATable () {
    }

    public void setStartSector(){
        this.startSector = startSector;
    }

    public void setFatID(String fatID) {
        this.FatID = fatID;
    }

    public void setEndClusterMarker(String endClusterMarker) {
        this.endClusterMarker = endClusterMarker;
    }

    public String getStartSector() {
        return startSector;
    }

    public String getFatID() {
        return FatID;
    }

    public String getEndClusterMarker() {
        return endClusterMarker;
    }
}

