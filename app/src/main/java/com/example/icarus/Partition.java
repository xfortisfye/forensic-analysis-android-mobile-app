package com.example.icarus;

public class Partition {
    private String bootableStatus;
    private String partitionType;
    private Long startOfPartition;
    private Long endOfPartition;
    private Long lenOfPartition;
    public Partition() {
    }

    public void setBootableStatus(StringBuilder hexData) {
        String hexDataString;
        switch(hexDataString = hexData.toString()) {
            case "80":
                this.bootableStatus = "Bootable";
                break;
            case "00":
                this.bootableStatus = "Non-Bootable";
                break;
        }
    }

    public void setPartitionType(StringBuilder hexData) {
        String hexDataString;
        switch(hexDataString = hexData.toString()) {
            case "00":
                this.partitionType = "Empty";
                break;
            case "01":
                this.partitionType = "FAT12";
                break;
            case "02":
                this.partitionType = "XENIX root";
                break;
            case "03":
                this.partitionType = "XENIX usr";
                break;
            case "04":
                this.partitionType = "FAT16 <32M";
                break;
            case "05":
                this.partitionType = "Extended";
                break;
            case "06":
                this.partitionType = "FAT16";
                break;
            case "0A":
                this.partitionType = "OS/2 Boot Manager";
                break;
            case "0B":
                this.partitionType = "W95 FAT32";
                break;
            case "0C":
                this.partitionType = "W95 FAT32 (LBA)";
                break;
            case "EE":
                this.partitionType = "GPT";
                break;
            default:
                this.partitionType = "Invalid Partition Type";
        }
    }

    public void setStartOfPartition(Long startOfPartition) {
        this.startOfPartition = startOfPartition;
    }

    public void setEndOfPartition() {
        this.endOfPartition = this.startOfPartition + this.lenOfPartition - 1;
    }

    public void setLenOfPartition(Long lenOfPartition){
        this.lenOfPartition = lenOfPartition;
    }

    public String getBootableStatus() {
        return bootableStatus;
    }

    public String getPartitionType() {
        return partitionType;
    }

    public Long getStartOfPartition() {
        return startOfPartition;
    }

    public Long getEndOfPartition() {
        return endOfPartition;
    }

    public Long getLenOfPartition(){
        return lenOfPartition;
    }

}


