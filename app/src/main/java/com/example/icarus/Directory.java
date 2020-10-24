package com.example.icarus;

import android.widget.TextView;

public class Directory extends Grab {
    private String name;
    private String nameExt;
    private long fileAttribute;
    private String createdTime;
    private String createdDate;
    private String accessedDate;
    private long firstClusterLoc;
    private String writtenTime;
    private String writtenDate;
    private long sizeOfFile;

    public Directory() {
    }

    public void setName(String name) { this.name = name;}
    public void setNameExt(String nameExt) { this.nameExt = nameExt;}
    public void setFileAttribute(long fileAttribute) { this.fileAttribute = fileAttribute; }
    public void setCreatedTime (long createdTime) { this.createdTime = getDecBinTime(createdTime); }
    public void setCreatedDate (long createdDate) { this.createdDate = getDecBinDate(createdDate); }
    public void setAccessedDate (long accessedDate) { this.accessedDate = getDecBinDate(accessedDate); }
    public void setFirstClusterLoc(long firstClusterLoc) { this.firstClusterLoc = firstClusterLoc; }
    public void setWrittenTime (long writtenTime) { this.writtenTime = getDecBinTime(writtenTime); }
    public void setWrittenDate (long writtenDate) { this.writtenDate = getDecBinDate(writtenDate); }
    public void setSizeOfFile(long sizeOfFile) { this.sizeOfFile = sizeOfFile; }

    public String getName() {return name;}
    public String getNameExt() {return nameExt;}
    public long getFileAttribute() {return fileAttribute;}
    public String getCreatedTime() {return createdTime;}
    public String getCreatedDate() { return createdDate; }
    public String getAccessedDate () { return accessedDate; }
    public long getFirstClusterLoc() { return firstClusterLoc; }
    public String getWrittenTime() { return writtenTime; }
    public String getWrittenDate() { return writtenDate; }
    public long getSizeOfFile() { return sizeOfFile; }

    public void toString(TextView testingText) {
        testingText.append("DIIRREECCTTORRY" + "\n");
        testingText.append("SFN: " + this.getName() + "\n");
        testingText.append("SFN Ext" + this.getNameExt() + "\n");
        testingText.append("File Attribute (Dec): " + this.getFileAttribute() + "\n");
        testingText.append("Created Time: " + this.getCreatedDate() + " " + this.getCreatedTime() + "\n");
        testingText.append("Accessed Date: " + this.getAccessedDate() + "\n");
        testingText.append("First Cluster Location: " + this.getFirstClusterLoc() + "\n");
        testingText.append("Written Time: " + this.getWrittenDate() + " " + this.getWrittenTime() + "\n");
        testingText.append("Size of File: " + this.getSizeOfFile() + "\n");
    }
}
