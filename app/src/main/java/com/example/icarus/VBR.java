package com.example.icarus;

public class VBR {

    private long VBRSector;
    private String OEM;

    public VBR() {
    }

//    public VBR(Partition partition) {
//        VBRSector = partition.getStartOfPartition();
//    }


    public void setVBRSector(Long startOfPartition) { long VBRSector = startOfPartition; }

    public long getVBRSector() { return VBRSector; }

    public void setOEM(String hexData) { this.OEM = hexData; }

    public String getOEM() { return this.OEM; }

}


