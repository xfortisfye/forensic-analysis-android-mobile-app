package com.example.icarus;

import android.net.Uri;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class RootDirectory {

    private long startRootDirDec;
    private long endRootDirDec;
    private ArrayList<Long> rootDirList;
    public RootDirectory() {

    }
    public RootDirectory(long clusterNoNum, long sectPerCluster, long bytesPerSect, long startDataRegionDec, Uri uri,
                         FATable fatable, ArrayList<Long> list) throws IOException {
        this.setStartRootDirDec(clusterNoNum, sectPerCluster, bytesPerSect, startDataRegionDec);
        this.setEndRootDirDec(uri, fatable, clusterNoNum, sectPerCluster, bytesPerSect, startDataRegionDec, list);
    }

    public void setStartRootDirDec(long clusterNoNum, long sectPerCluster, long sectPerBytes, long startDataRegionDec) {
        if (clusterNoNum <= 2) {
            this.startRootDirDec = startDataRegionDec;
        }
        else {
            this.startRootDirDec = startDataRegionDec + ((clusterNoNum-2) * sectPerCluster * sectPerBytes);
        }
    }

    public void setEndRootDirDec(Uri uri, FATable fatable, long clusterNoNum, long sectPerCluster, long bytesPerSect,
                                 long startDataRegionDec, ArrayList<Long> list) throws IOException {
        this.setRootDirList(list);
        System.out.println("Size of RootDir List" + rootDirList.size());
        if (this.rootDirList.size() == 1) {
            this.endRootDirDec = startDataRegionDec + (sectPerCluster * bytesPerSect) - 1;
        }
        else {
            this.endRootDirDec = startDataRegionDec + (this.rootDirList.size() * sectPerCluster * bytesPerSect) - 1;
        }
    }

    public void setRootDirList(ArrayList<Long> rootDirList) {
        this.rootDirList = rootDirList;
    }

    public long getStartRootDirDec() {return startRootDirDec; }
    public long getEndRootDirDec() {return endRootDirDec; }
    public ArrayList<Long> getRootDirList() {return rootDirList;}

    public void toString(TextView testingText) {
        testingText.append("[     ROOT DIRECTORY REGION     ]" + "\n");
        testingText.append("Start of Root Dir (bytes): " + this.getStartRootDirDec() + "\n");
        testingText.append("End of Root Dir (bytes): " + this.getEndRootDirDec() + "\n");
        testingText.append("Root Directory List: ");
        for (int i = 0; i < this.getRootDirList().size();i++)
        {
            testingText.append(this.getRootDirList().get(i).toString());
        }
        testingText.append("\n");
    }
}
