package com.example.icarus;

public class VBR {

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
    private long bit32SectorsOfFat;
    private long rootCluster;
    private long FSInfoSector;



    public VBR() {
    }

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

    public void setBit32SectorsOfFat(long bit32SectorsOfFat) { this.bit32SectorsOfFat = bit32SectorsOfFat; }

    public void setRootCluster(long rootCluster) { this.rootCluster = rootCluster; }

    public void setFSInfoSector(long FSInfoSector) { this.FSInfoSector = FSInfoSector; }



    public String getOEM() { return this.OEM; }

    public long getBytesPerSector() { return bytesPerSector; }

    public long getSectorsPerCluster() { return sectorsPerCluster; }

    public long getReservedAreaSize() { return reservedAreaSize; }

    public long getNumOfFats() { return numOfFats; }

    public long getBit16Sectors() { return bit16Sectors; }

    public long getMaxRootFiles() { return maxRootFiles; }

    public String getMediaType() { return mediaType; }

    public long getOffset() { return offset; }

    public long getBit32Sectors() { return bit32Sectors; }

    public long getBit32SectorsOfFat() { return bit32SectorsOfFat; }

    public long getRootCluster() { return rootCluster; }

    public long getFSInfoSector() { return FSInfoSector; }

    public String toString() {
        String VBRInfo = ("VBR Info:"
                + "     OEM: " + this.OEM
                + "\n     Bytes Per Sector: " + this.bytesPerSector
                + "\n     Sectors Per Cluster: " + this.sectorsPerCluster
                + "\n     Reserved Area Size (in sector)" + this.reservedAreaSize
                + "\n     Number of Fats: " + this.numOfFats
                + "\n     16-bit value of number of sectors: " + this.bit16Sectors
                + "\n     Media Type: " + this.mediaType
                + "\n     Number of sectors before start of partition: " + this.offset
                + "\n     32-bit value of number of sectors: " + this.bit32Sectors
                + "\n     32-bit value of 1 FAT: " + this.bit32SectorsOfFat
                + "\n     Cluster of Root Director: " + this.rootCluster
                + "\n     Sector of FSINFO: " + this.FSInfoSector + "\n"
        );

        return VBRInfo;
    }

}

