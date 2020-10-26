package com.example.icarus;

import android.net.Uri;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class FileEntry extends Grab {
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

    private ArrayList<Long> listOfClusters;
    private ArrayList<String> listOfData;
    private ArrayList<FileEntry> listOfFileAndDir;

    public FileEntry() {
    }

    public FileEntry(Uri uri, FATable fatable, ArrayList<String> listOfData) {
        // PLEASE REEDIT THIS. THIS IS SUPPOSED TO BE FOR ROOT DIRECTORY CREATION ONLY.
        // I LOST MY CODES.
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

    public void setListOfClusters(ArrayList<Long> listOfClusters) { this.listOfClusters = listOfClusters; }
    public void setListOfData(ArrayList<String> listOfData) {this.listOfData = listOfData;}
    public void setListOfFileAndDir(ArrayList<FileEntry> listOfFileAndDir) {this.listOfFileAndDir = listOfFileAndDir;}

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

    public ArrayList<Long> getListOfClusters() {return listOfClusters;}
    public ArrayList<String> getListOfData() { return listOfData; }
    public ArrayList<FileEntry> getListOfFileAndDir() { return listOfFileAndDir; }

    public void toString(TextView testingText) {
        if (this.getFileAttribute() == 15) {
            testingText.append("--- FILE ---" + "\n");
            testingText.append("SFN: " + this.getName() + "\n");
            testingText.append("SFN Ext: " + this.getNameExt() + "\n");
            testingText.append("File Attribute (Dec): " + this.getFileAttribute() + "\n");
            testingText.append("Created Time: " + this.getCreatedDate() + " " + this.getCreatedTime() + "\n");
            testingText.append("Accessed Date: " + this.getAccessedDate() + "\n");
            testingText.append("First Cluster Location: " + this.getFirstClusterLoc() + "\n");
            testingText.append("Written Time: " + this.getWrittenDate() + " " + this.getWrittenTime() + "\n");
            testingText.append("Size of File: " + this.getSizeOfFile() + "\n");
        } else if (this.getFileAttribute() == 16) {
            testingText.append("--- DIRECTORY ---" + "\n");
            testingText.append("SFN: " + this.getName() + "\n");
            testingText.append("SFN Ext: " + this.getNameExt() + "\n");
            testingText.append("File Attribute (Dec): " + this.getFileAttribute() + "\n");
            testingText.append("Created Time: " + this.getCreatedDate() + " " + this.getCreatedTime() + "\n");
            testingText.append("Accessed Date: " + this.getAccessedDate() + "\n");
            testingText.append("First Cluster Location: " + this.getFirstClusterLoc() + "\n");
            testingText.append("Written Time: " + this.getWrittenDate() + " " + this.getWrittenTime() + "\n");
            testingText.append("Size of File: " + this.getSizeOfFile() + "\n");
        } else {
            testingText.append("--- I don't know what is this ---" + "\n");
            testingText.append("SFN: " + this.getName() + "\n");
            testingText.append("SFN Ext: " + this.getNameExt() + "\n");
            testingText.append("File Attribute (Dec): " + this.getFileAttribute() + "\n");
            testingText.append("Created Time: " + this.getCreatedDate() + " " + this.getCreatedTime() + "\n");
            testingText.append("Accessed Date: " + this.getAccessedDate() + "\n");
            testingText.append("First Cluster Location: " + this.getFirstClusterLoc() + "\n");
            testingText.append("Written Time: " + this.getWrittenDate() + " " + this.getWrittenTime() + "\n");
            testingText.append("Size of File: " + this.getSizeOfFile() + "\n");
        }
    }
}
