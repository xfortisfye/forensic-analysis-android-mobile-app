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
        if (hexData.toString().equals("80")) {
            this.bootableStatus = "Bootable";
        }

        if (hexData.toString().equals("00")) {
            this.bootableStatus = "Non-Bootable";
        }
    }

    public void setPartitionType(StringBuilder hexData) {
        /***** Someone may fill this up *****/
        if (hexData.toString().equals("00")) {
            this.partitionType = "Empty";
        }

        else if (hexData.toString().equals("01")) {
            this.partitionType = "FAT12";
        }

        else if (hexData.toString().equals("02")) {
            this.partitionType = "XENIX root";
        }

        else if (hexData.toString().equals("03")) {
            this.partitionType = "XENIX usr";
        }

        else if (hexData.toString().equals("04")) {
            this.partitionType = "FAT16 <32M";
        }

        else if (hexData.toString().equals("05")) {
            this.partitionType = "Extended";
        }

        else if (hexData.toString().equals("06")) {
            this.partitionType = "FAT16";
        }

        else if (hexData.toString().equals("0A")) {
            this.partitionType = "OS/2 Boot Manager";
        }

        else if (hexData.toString().equals("0B")) {
            this.partitionType = "W95 FAT32";
        }

        else if (hexData.toString().equals("0C")) {
            this.partitionType = "W95 FAT32 (LBA)";
        }

        else if (hexData.toString().equals("EE")) {
            this.partitionType = "GPT";
        }

        else {
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


