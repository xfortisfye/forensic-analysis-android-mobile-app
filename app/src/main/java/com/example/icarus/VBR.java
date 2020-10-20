package com.example.icarus;

import android.widget.TextView;

public class VBR {

    private String OEM;
    private long bytesPerSector;
    private long sectorsPerCluster;
    private long reservedAreaSize; // In sectors
    private long numOfFats;
    private long bit16Sectors; // Applicable to FAT16 only.
    private long maxRootFiles;
    private String mediaType;
    private long offset; // Number of sectors before start of partition
    private long bit32Sectors; // Number of total sectors in the partition
    private long bit32SectorsOfFat; // Number of total sectors in the FAT
    private long rootCluster;
    private long FSInfoSector;


    public VBR() {
    }

    public void setOEM(String hexData) {
        this.OEM = hexData;
    }

    public void setBytesPerSector(long bytesPerSector) {
        this.bytesPerSector = bytesPerSector;
    }

    public void setSectorsPerCluster(long sectorsPerCluster) {
        this.sectorsPerCluster = sectorsPerCluster;
    }

    public void setReservedAreaSize(long reservedAreaSize) {
        this.reservedAreaSize = reservedAreaSize;
    }

    public void setNumOfFats(long numOfFats) {
        this.numOfFats = numOfFats;
    }

    public void setBit16Sectors(long bit16Sectors) {
        this.bit16Sectors = bit16Sectors;
    }

    public void setMaxRootFiles(long maxRootFiles) {
        this.maxRootFiles = maxRootFiles;
    }

    public void setMediaType(StringBuilder hexData) {
        switch (hexData.toString()) {
            case "F8":
                mediaType = "Fixed Disk";
                break;
            case "F0":
                mediaType = "Removable";
                break;
            default:
                this.mediaType = "Invalid Media Type";
                break;
        }
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setBit32Sectors(long bit32Sectors) {
        this.bit32Sectors = bit32Sectors;
    }

    public void setBit32SectorsOfFat(long bit32SectorsOfFat) {
        this.bit32SectorsOfFat = bit32SectorsOfFat;
    }

    public void setRootCluster(long rootCluster) {
        this.rootCluster = rootCluster;
    }

    public void setFSInfoSector(long FSInfoSector) {
        this.FSInfoSector = FSInfoSector;
    }


    public String getOEM() {
        return this.OEM;
    }

    public long getBytesPerSector() {
        return bytesPerSector;
    }

    public long getSectorsPerCluster() {
        return sectorsPerCluster;
    }

    public long getReservedAreaSize() {
        return reservedAreaSize;
    }

    public long getNumOfFats() {
        return numOfFats;
    }

    public long getBit16Sectors() {
        return bit16Sectors;
    }

    public long getMaxRootFiles() {
        return maxRootFiles;
    }

    public String getMediaType() {
        return mediaType;
    }

    public long getOffset() {
        return offset;
    }

    public long getBit32Sectors() {
        return bit32Sectors;
    }

    public long getBit32SectorsOfFat() {
        return bit32SectorsOfFat;
    }

    public long getRootCluster() {
        return rootCluster;
    }

    public long getFSInfoSector() {
        return FSInfoSector;
    }

    public void toString(TextView testingText) {
        testingText.append("VBR Info: " + "\n");
        testingText.append("    OEM: " + this.getOEM() + "\n");
        testingText.append("    Bytes Per Sector: " + this.getBytesPerSector() + "\n");
        testingText.append("    Sectors Per Cluster: " + this.getSectorsPerCluster() + "\n");
        testingText.append("    Reserved Area Size (in sector)" + this.getReservedAreaSize() + "\n");
        testingText.append("    Number of Fats: " + this.getNumOfFats() + "\n");
        testingText.append("    16-bit value of number of sectors: " + this.getBit16Sectors() + "\n");
        testingText.append("    Media Type: " + this.getMediaType() + "\n");
        testingText.append("    Number of sectors before start of partition: " + this.getOffset() + "\n");
        testingText.append("    32-bit value of number of sectors: " + this.getBit32Sectors() + "\n");
        testingText.append("    32-bit value of 1 FAT: " + this.getBit32SectorsOfFat() + "\n");
        testingText.append("    Cluster of Root Directory: " + this.getRootCluster() + "\n");
        testingText.append("    Sector of FSINFO: " + this.getFSInfoSector() + "\n");
    }
}

