package com.example.icarus;

public class VBR {

    private long VBRSector;
    private String OEM;
    private long bytesPerSector;
    private long sectorsPerCluster;
    private long reservedAreaSize; // In sectors
    private long numOfFats;
    private long bit16Sectors;
    private long maxRootFiles;
    private String mediaType;
    private long offset; // Number of sectors before start of partition
    private long bit32Sectors;
    private long rootCluster;
    private long FSInfoSector;

    public VBR() {
    }

//    public VBR(Partition partition) {
//        VBRSector = partition.getStartOfPartition();
//    }


    public void setVBRSector(Long startOfPartition) { long VBRSector = startOfPartition; }

    public void setOEM(String hexData) { this.OEM = hexData; }

    public void setBytesPerSector(long BytesPerSector) { this.bytesPerSector = bytesPerSector; }

    public void setSectorsPerCluster(long sectorsPerCluster) { this.sectorsPerCluster = sectorsPerCluster; }

    public void setReservedAreaSize(long reservedAreaSize) { this.reservedAreaSize = reservedAreaSize; }

    public void setNumOfFats(long numOfFats) { this.numOfFats = numOfFats; }

    public void setBit16Sectors(long bit16Sectors) { this.bit16Sectors = bit16Sectors; }

    public void setMaxRootFiles(long maxRootFiles) { this.maxRootFiles = maxRootFiles; }

    public void setMediaType(StringBuilder hexData) {
        String hexDataString;
        switch(hexData.toString()) {
            case "F8":
                mediaType =  "Fixed Disk";
                break;
            case "F0":
                mediaType = "Removable";
                break;
        }
    }

    public void setOffset(long offset) { this.offset = offset; }

    public void setBit32Sectors(long bit32Sectors) { this.bit32Sectors = bit32Sectors; }

    public void setRootCluster(long rootCluster) { this.rootCluster = rootCluster; }

    public void setFSInfoSector(long FSInfoSector) { this.FSInfoSector = FSInfoSector; }

    public String getOEM() { return this.OEM; }

    public long getVBRSector() { return VBRSector; }


}


