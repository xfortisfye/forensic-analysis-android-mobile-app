package com.example.icarus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button startAnalyseButton;
    TextView testingText;
    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*** FOR HIDING TOP BAR ***/
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        /*** Detect Start Analyse Button ***/
        startAnalyseButton = (Button) findViewById(R.id.startAnalyseButton);
        startAnalyseButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                Intent openFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                openFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                openFileIntent.setType("*/*");
                startActivityForResult(openFileIntent, READ_REQUEST_CODE);
            }
        });
    }

    /*** Detect File input ***/
    /*** Detect File input ***/
    /*** Detect File input ***/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();

                testingText = (TextView) findViewById(R.id.testingText);
                testingText.setText("");
                int partitionCounter = 0;
                long startCount = 0L;
                Boolean validMBR = false;
                Boolean extendedPartExist = false;
                MBR mbr = new MBR();

                try {
                    /*** Insert all partition reading functions here ***/
                    /*** Insert all partition reading functions here ***/
                    /*** Insert all partition reading functions here ***/
                    mbr = getMBR(uri, startCount + 0); // Instantiate new MBR object

                    if (mbr.chkMBRValidity(testingText)) {

                        mbr.setPartition1(getMBR_PartitionInfo(uri, startCount + 446));
                        mbr.setPartition2(getMBR_PartitionInfo(uri, startCount + 462));
                        mbr.setPartition3(getMBR_PartitionInfo(uri, startCount + 478));
                        mbr.setPartition4(getMBR_PartitionInfo(uri, startCount + 494));

                        mbr.getPartition1().setEndOfPartition();
                        mbr.getPartition2().setEndOfPartition();
                        mbr.getPartition3().setEndOfPartition();
                        mbr.getPartition4().setEndOfPartition();

                        validMBR = true;
                    } else {
                        validMBR = false;
                        testingText.append("No MBR found. " + "\n\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Unable to read file");
                }

                if (validMBR == true) {
                    try {
                        Partition[] partitionAvailability = {mbr.getPartition1(), mbr.getPartition2(),
                                mbr.getPartition3(), mbr.getPartition4()};
                        for (Partition partition : partitionAvailability) {
                            if (partition.getPartitionType().equals("Extended") || partition.getPartitionType().equals("W95 Ext'd (LBA)")) {

                                extendedPartExist = true;
                                testingText.append("Extended found! : " + extendedPartExist + "\n\n");
                                Boolean loopedAllExtPartitions = false;
                                long priExtPartitionStart = partition.getStartOfPartition();

                                do {
                                    Boolean validExtMBR = false;
                                    ExtMBR extmbr = new ExtMBR();
                                    try {
                                        extmbr = getExtMBR(uri, partition.getStartOfPartition() * 512);
                                        if (extmbr.chkExtMBRValidity(testingText)) {
                                            extmbr.setExtPartition(getExtMBR_PartitionInfo(uri, (partition.getStartOfPartition() * 512) + 446, partition.getStartOfPartition(), priExtPartitionStart));
                                            extmbr.getExtPartition().setEndOfPartition();

                                            testingText.append("Ext MBR found. " + "\n\n");
                                            validExtMBR = true;
                                        } else {
                                            testingText.append("No Ext MBR found. " + "\n\n");
                                            validExtMBR = false;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        System.out.println("Unable to read file");
                                    }

                                    if (validExtMBR == true) {
                                        try {
                                            if (!extmbr.getExtPartition().getPartitionType().equals("Empty")) {
                                                partitionCounter++;

                                                extmbr.getExtPartition().setVBR(getVBRInfo(uri, extmbr.getExtPartition().getStartOfPartition() * 512));
                                                extmbr.getExtPartition().setPartitionName("[     EXT PARTITION " + partitionCounter + ": " +
                                                        extmbr.getExtPartition().getVBR().getVolumeLabel() + " (" + extmbr.getExtPartition().getVBR().getFileSystemLabel() + ")     ]");

                                                long startFirstFatSect, endFirstFatSect, endLastFatSect, startDataRegionSect, endDataRegionSect;

                                                startFirstFatSect = extmbr.getExtPartition().getStartOfPartition() + extmbr.getExtPartition().getVBR().getReservedAreaSize();
                                                endFirstFatSect = startFirstFatSect + extmbr.getExtPartition().getVBR().getBit32SectorsOfFat() - 1;

                                                startDataRegionSect = startFirstFatSect;

                                                for (int index = 0; index < extmbr.getExtPartition().getVBR().getNumOfFats(); index++) {
                                                    startDataRegionSect = startDataRegionSect + extmbr.getExtPartition().getVBR().getBit32SectorsOfFat();
                                                }
                                                endLastFatSect = startDataRegionSect - 1;
                                                endDataRegionSect = extmbr.getExtPartition().getStartOfPartition() + extmbr.getExtPartition().getVBR().getBit32Sectors() - 1;

                                                FATable fat = new FATable(startFirstFatSect, endFirstFatSect, endLastFatSect,
                                                        extmbr.getExtPartition().getVBR().getBytesPerSector());
                                                extmbr.getExtPartition().setFAT(getFATInfo(uri, fat.getStartFirstFatDec(), fat));

                                                DataRegion dataRegion = new DataRegion(startDataRegionSect, endDataRegionSect,
                                                        extmbr.getExtPartition().getVBR().getBytesPerSector());
                                                extmbr.getExtPartition().setDataRegion(dataRegion);

                                                ArrayList<StringBuilder> listOfRootDirData = new ArrayList<StringBuilder>();
                                                listOfRootDirData = getListOfDirDataTraverse(uri, fat, dataRegion,
                                                        getListOfClusterTraverse(uri, fat, extmbr.getExtPartition().getVBR().getRootCluster()),
                                                        extmbr.getExtPartition().getVBR().getBytesPerCluster());
                                                FileEntry rootDirectory = new FileEntry(getListOfClusterTraverse(uri, fat,
                                                        extmbr.getExtPartition().getVBR().getRootCluster()), getListOfDirDataTraverse(uri, fat, dataRegion,
                                                                getListOfClusterTraverse(uri, fat, extmbr.getExtPartition().getVBR().getRootCluster()),
                                                                extmbr.getExtPartition().getVBR().getBytesPerCluster()));
                                                //acc

                                                ArrayList<FileEntry> listOfFileAndDir = new ArrayList<FileEntry>();
                                                rootDirectory.setListOfFileAndDir(traverseDirectory(uri, fat, dataRegion, extmbr.getExtPartition()
                                                        .getVBR().getBytesPerCluster(), listOfRootDirData, listOfFileAndDir));
                                                extmbr.getExtPartition().setRootDirectory(rootDirectory);



                                                /*** Generation of Report ***/
                                                extmbr.getExtPartition().toString(testingText);
                                                extmbr.getExtPartition().getVBR().toString(testingText);
                                                extmbr.getExtPartition().getFAT().toString(testingText);
                                                extmbr.getExtPartition().getDataRegion().toString(testingText);
                                                printAllFileAndDir(extmbr.getExtPartition().getRootDirectory().getListOfFileAndDir(), testingText);
                                                partition.setStartOfPartition(extmbr.getExtPartition().getCalExt2MBR());

                                            } else {
                                                loopedAllExtPartitions = true;
                                                partition.setStartOfPartition(priExtPartitionStart);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            System.out.println("Unable to read file");
                                        }
                                    }
                                    if (extmbr.getExtPartition().getExt2Offset() == 0L) {
                                        loopedAllExtPartitions = true;
                                        partition.setStartOfPartition(priExtPartitionStart);
                                    }
                                } while (loopedAllExtPartitions == false);
                            } else {
                                if (!partition.getPartitionType().equals("Empty")) {

                                    partitionCounter++;

                                    partition.setVBR(getVBRInfo(uri, partition.getStartOfPartition() * 512));
                                    partition.setPartitionName("[     PARTITION " + partitionCounter + ": " +
                                            partition.getVBR().getVolumeLabel() + " (" + partition.getVBR().getFileSystemLabel() + ")     ]");

                                    long startFirstFatSect, endFirstFatSect, endLastFatSect, startDataRegionSect, endDataRegionSect;

                                    startFirstFatSect = partition.getStartOfPartition() + partition.getVBR().getReservedAreaSize();
                                    endFirstFatSect = startFirstFatSect + partition.getVBR().getBit32SectorsOfFat() - 1;

                                    startDataRegionSect = startFirstFatSect;

                                    for (int index = 0; index < partition.getVBR().getNumOfFats(); index++) {
                                        startDataRegionSect = startDataRegionSect + partition.getVBR().getBit32SectorsOfFat();
                                    }
                                    endLastFatSect = startDataRegionSect - 1;
                                    endDataRegionSect = partition.getStartOfPartition() + partition.getVBR().getBit32Sectors() - 1;

                                    FATable fat = new FATable(startFirstFatSect, endFirstFatSect, endLastFatSect, partition.getVBR().getBytesPerSector());
                                    partition.setFAT(getFATInfo(uri, fat.getStartFirstFatDec(), fat));

                                    DataRegion dataRegion = new DataRegion(startDataRegionSect, endDataRegionSect, partition.getVBR().getBytesPerSector());
                                    partition.setDataRegion(dataRegion);

                                    ArrayList<StringBuilder> listOfRootDirData = new ArrayList<StringBuilder>();
                                    listOfRootDirData = getListOfDirDataTraverse(uri, fat, dataRegion,
                                            getListOfClusterTraverse(uri, fat, partition.getVBR().getRootCluster()), partition.getVBR().getBytesPerCluster());
                                    FileEntry rootDirectory = new FileEntry(getListOfClusterTraverse(uri, fat, partition.getVBR().getRootCluster()),
                                            getListOfDirDataTraverse(uri, fat, dataRegion,
                                                    getListOfClusterTraverse(uri, fat, partition.getVBR().getRootCluster()), partition.getVBR().getBytesPerCluster()));
                                    //acc

                                    ArrayList<FileEntry> listOfFileAndDir = new ArrayList<FileEntry>();
                                    rootDirectory.setListOfFileAndDir(traverseDirectory(uri, fat, dataRegion, partition.getVBR().getBytesPerCluster(), listOfRootDirData, listOfFileAndDir));
                                    partition.setRootDirectory(rootDirectory);



//                                    /*** Carving of file ***/
//                                    System.out.println("===============================================================");
//                                    System.out.println("Fat table 1 " + partition.getFAT().getStartFirstFatDec());
//                                    System.out.println("Fat table 2 " + partition.getFAT().getEndFirstFatDec());
//                                    System.out.println("Root Dir " + partition.getDataRegion().getStartDataRegionDec());
//                                    System.out.println("===============================================================");
//                                    long rootDirDec = partition.getDataRegion().getStartDataRegionDec();
//                                    long sectPerCluster = partition.getVBR().getSectorsPerCluster();
//                                    long bytesPerSector = partition.getVBR().getBytesPerSector();
//                                    long rootDirSect = partition.getDataRegion().getStartDataRegionSect();
//                                    long startClusterDec = 0, fileSizeDec = 0;
//                                    long startDirBlockDec;
//                                    String DirName;
//
////                                    System.out.println(partition.getVBR().getSectorsPerCluster());
////                                    System.out.println(test);
////                                    System.out.println(test+partition.getVBR().getSectorsPerCluster()*512);
//                                    long sectDec = sectPerCluster * 512;
//                                    ArrayList<String> fileInfo;
//                                    ArrayList<String> fileDateTime;
//                                    long test = rootDirDec+sectDec;
//                                    while(rootDirDec != test){
//
//                                        startClusterDec = getHexToDecimal(getLEHexData(uri, rootDirDec+26, rootDirDec + 27));
//                                        fileSizeDec = getHexToDecimal(getLEHexData(uri, rootDirDec+28, rootDirDec + 31));
//                                        System.out.println(getLEHexData(uri, rootDirDec + 11, rootDirDec + 11).toString());
////                                        if(getLEHexData(uri, rootDirDec + 11, rootDirDec + 11).toString().equals("0F")){
////                                            System.out.println("Long file name found");
////                                            System.out.println(getHexToASCII(getBEHexData(uri, rootDirDec, rootDirDec + 10)));
////                                        }
//                                        if(getLEHexData(uri, rootDirDec + 11, rootDirDec + 11).toString().equals("10")){
//                                            System.out.println("===============================================================");
//                                            System.out.println("Directory found");
//                                            System.out.println("===============================================================");
//
//                                            DirName = getHexToASCII(getBEHexData(uri, rootDirDec, rootDirDec + 7));
//
//                                            fileDateTime = getFileDateTime(uri, rootDirDec);
//
//                                            System.out.println("===============================================================");
//                                            System.out.println("Time Directory created (24hr) " + fileDateTime.get(0));
//                                            System.out.println("Date Directory created " + fileDateTime.get(1));
//                                            System.out.println("Last Accessed Date " +  fileDateTime.get(2));
//                                            System.out.println("Last Write Time " +  fileDateTime.get(3));
//
//                                            startDirBlockDec = Long.parseLong(getStartLocOfBlock(rootDirSect, startClusterDec, sectPerCluster, bytesPerSector));
//
//                                            //System.out.println("Start Location of Directory Block " + startDirBlockDec);
//                                            System.out.println("===============================================================");
//                                            System.out.println("===============================================================");
//                                            System.out.println("Jumping to " + DirName + " Directory ");
//                                            System.out.println("===============================================================");
//
//
//
//                                            long startDirBlockSect = startDirBlockDec/512;
//                                            long test2 = startDirBlockDec+sectDec;
//
//
//                                            while(startDirBlockDec != test2) {
//
//                                                long startDirClusterDec = getHexToDecimal(getLEHexData(uri, startDirBlockDec+26, startDirBlockDec + 27));
//                                                long fileSizeInDirDec = getHexToDecimal(getLEHexData(uri, startDirBlockDec+28, startDirBlockDec + 31));
//
//                                                if (getLEHexData(uri, startDirBlockDec + 11, startDirBlockDec + 11).toString().equals("20")) {
//                                                    System.out.println("===============================================================");
//                                                    System.out.println("File found in " + DirName);
//                                                    System.out.println("===============================================================");
//
//                                                    fileDateTime = getFileDateTime(uri, rootDirDec);
//
//                                                    System.out.println("===============================================================");
//                                                    System.out.println("Time File created (24hr) " + fileDateTime.get(0));
//                                                    System.out.println("Date File created " + fileDateTime.get(1));
//                                                    System.out.println("Last Accessed Date " + fileDateTime.get(2));
//                                                    System.out.println("Last Write Time " + fileDateTime.get(3));
//                                                    System.out.println("===============================================================");
//
//                                                    fileInfo = getFileInfo(uri, startDirBlockDec, rootDirSect, startDirClusterDec, sectPerCluster, bytesPerSector, fileSizeInDirDec);
//
//                                                    System.out.println("===============================================================");
//                                                    System.out.println("File Name " + fileInfo.get(0));
//                                                    System.out.println("File extension " + fileInfo.get(1));
//                                                    System.out.println("Start Location of File Block " + fileInfo.get(2));
//                                                    System.out.println("End Location of File Block " + fileInfo.get(3));
//                                                    System.out.println("===============================================================");
//
//                                                }
//                                                startDirBlockDec += 16;
//                                            }
//                                        }
//
//                                        if(getLEHexData(uri, rootDirDec + 11, rootDirDec + 11).toString().equals("20")){
//                                            System.out.println("===============================================================");
//                                            System.out.println("File found in root dir");
//                                            System.out.println("===============================================================");
//
//                                            fileDateTime = getFileDateTime(uri, rootDirDec);
//
//                                            System.out.println("===============================================================");
//                                            System.out.println("Time File created (24hr) " + fileDateTime.get(0));
//                                            System.out.println("Date File created " + fileDateTime.get(1));
//                                            System.out.println("Last Accessed Date " +  fileDateTime.get(2));
//                                            System.out.println("Last Write Time " +  fileDateTime.get(3));
//                                            System.out.println("===============================================================");
//
//                                            fileInfo = getFileInfo(uri, rootDirDec, rootDirSect, startClusterDec, sectPerCluster, bytesPerSector, fileSizeDec);
//
//                                            System.out.println("===============================================================");
//                                            System.out.println("File name " + fileInfo.get(0));
//                                            System.out.println("File extension " + fileInfo.get(1));
//                                            System.out.println("Start Location of File Block " + fileInfo.get(2));
//                                            System.out.println("End Location of File Block " + fileInfo.get(3));
//                                            System.out.println("===============================================================");
//
//                                        }
//
//                                        rootDirDec+=16;
//
//                                    }


                                    /*** Generation of Report ***/
                                    partition.toString(testingText);
                                    partition.getVBR().toString(testingText);
                                    partition.getFAT().toString(testingText);
                                    partition.getDataRegion().toString(testingText);
                                    printAllFileAndDir(partition.getRootDirectory().getListOfFileAndDir(), testingText);
                                } else {
                                    //Ignore
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Unable to read file");
                    }
                }
            }
        }
    }

    public void printAllFileAndDir (ArrayList<FileEntry> listOfFileAndDir, TextView testingText) {
        for (int i = 0; i < listOfFileAndDir.size(); i++) {
            listOfFileAndDir.get(i).toString(testingText);
            if (listOfFileAndDir.get(i).getFileAttribute() == 16) {
                printAllFileAndDir(listOfFileAndDir.get(i).getListOfFileAndDir(), testingText);
            }
        }
    }

    /***** ***** ***** ***** FUNCTIONS TO CARVE DATA ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO CARVE DATA ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO CARVE DATA ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO CARVE DATA ***** ***** ***** *****/

    /*** Getting File Name / extension / block  ***/
    public ArrayList<String> getFileInfo(Uri uri, long rootDirDec, long rootDirSect, long startClusterDec, long sectPerCluster, long bytesPerSector, long fileSizeDec) throws IOException {

        String fileName, fileExt, StartLockOfFileBlock, EndLocOfFileBlock;

        ArrayList<String> fileInfo = new ArrayList<String>();

        fileName = getHexToASCII(getBEHexData(uri, rootDirDec, rootDirDec + 7));
        fileExt = getHexToASCII(getBEHexData(uri, rootDirDec + 8, rootDirDec + 10));

        StartLockOfFileBlock = getStartLocOfBlock(rootDirSect, startClusterDec, sectPerCluster, bytesPerSector); // Element 0 = start Loc, Element 1 = end Loc

        EndLocOfFileBlock = getEndLocOfBlock(Long.parseLong(StartLockOfFileBlock), fileSizeDec);

        fileInfo.add(fileName);
        fileInfo.add(fileExt);
        fileInfo.add(StartLockOfFileBlock);
        fileInfo.add(EndLocOfFileBlock);

        return fileInfo;
    }


    /*** Getting Date & Time from either file or directory  ***/ //Long is used in scenario when number is too huge.
    public ArrayList<String> getFileDateTime(Uri uri, long rootDirDec) throws IOException {

        String timeCreated, dateCreated, lastAccDate, lastWriteTime, timeCreatedBin, dateCreatedBin, lastAccDateBin, lastWriteTimeBin;
        ArrayList<String> fileDateTime = new ArrayList<String>();

        timeCreatedBin = getDecToBin(getHexToDecimal(getLEHexData(uri, rootDirDec + 14, rootDirDec + 15)));
        dateCreatedBin = getDecToBin(getHexToDecimal(getLEHexData(uri, rootDirDec + 16, rootDirDec + 17)));
        lastAccDateBin = getDecToBin(getHexToDecimal(getLEHexData(uri, rootDirDec + 18, rootDirDec + 19)));
        lastWriteTimeBin = getDecToBin(getHexToDecimal(getLEHexData(uri, rootDirDec + 20, rootDirDec + 21)));

        timeCreated = getBinToTime(timeCreatedBin);
        lastWriteTime = getBinToTime(lastWriteTimeBin);
        lastAccDate = getBinToDate(lastAccDateBin);
        dateCreated = getBinToDate(dateCreatedBin);

        fileDateTime.add(timeCreated);
        fileDateTime.add(dateCreated);
        fileDateTime.add(lastAccDate);
        fileDateTime.add(lastWriteTime);

        return fileDateTime;
    }

    /*** Change Decimal to Binary ***/ //Long is used in scenario when number is too huge.
    public String getDecToBin(long decValue) {

        String binValue = Long.toBinaryString(decValue);

        if (binValue.length() != 16) {
            int pad = 16 - binValue.length();               // Padding binary to allow conversion for date/time
            StringBuilder sb = new StringBuilder();

            while (sb.length() < pad) {
                sb.append('0');
            }

            sb.append(binValue);

            return sb.toString();

        } else {

            return binValue;

        }
    }

    /*** Change Binary to Date ***/ //Long is used in scenario when number is too huge.
    public String getBinToDate(String binDate) {

        String Date, Year, Month, Day, yearBin, monthBin, dayBin;

        yearBin = binDate.substring(0, 7);
        monthBin = binDate.substring(7, 11);
        dayBin = binDate.substring(11, 16);

        Year = String.valueOf(Integer.parseInt(yearBin, 2) + 1980);
        Month = String.valueOf(Integer.parseInt(monthBin, 2));
        Day = String.valueOf(Integer.parseInt(dayBin, 2));

        Date = String.join("-", Day, Month, Year);

        return Date;
    }

    /*** Change Binary to Date ***/ //Long is used in scenario when number is too huge.
    public String getBinToTime(String binTime) {

        String Time, Hour, Min, Sec, hourBin, minBin, secBin;
        if (binTime.equals("0000000000000000")) {
            return "Nil";
        }

        hourBin = binTime.substring(0, 5);
        minBin = binTime.substring(5, 11);
        secBin = binTime.substring(11, 16);

        Hour = String.valueOf(Integer.parseInt(hourBin, 2));
        Min = String.valueOf(Integer.parseInt(minBin, 2));
        Sec = String.valueOf(Integer.parseInt(secBin, 2) * 2);

        Time = String.join(":", Hour, Min, Sec);

        return Time;
    }

    /*** Get the Starting location of block ***/ //Long is used in scenario when number is too huge.
    public String getStartLocOfBlock(long rootDirSec, long startCluster, long clusterSize, long bytesPerSec) {
        String startLocDec;

        startLocDec = Long.toString(((rootDirSec + ((startCluster - 2) * clusterSize)) * bytesPerSec));
//        System.out.println(rootDirSec);
//        System.out.println(startCluster);
//        System.out.println(clusterSize);
//        System.out.println(bytesPerSec);
        return startLocDec;
    }

    /*** Get the Ending location of block ***/ //Long is used in scenario when number is too huge.
    public String getEndLocOfBlock(long startLocDec, long fileSizeDec) {
        String endLocDec;

        endLocDec = Long.toString(startLocDec + fileSizeDec);

        return endLocDec;
    }

    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/

    /*** Get Hex Data String in Big Endian Mode ***/
    public StringBuilder getBEHexData(Uri uri, long startCount, long endCount) throws IOException {
        int decimalValue;
        StringBuilder hexString = new StringBuilder();

        try {
            InputStream file1 = getContentResolver().openInputStream(uri);
            file1.skip(startCount);
            for (long i = startCount; i <= endCount; i++) {
                decimalValue = file1.read();
                hexString.append(String.format("%02X", decimalValue));
            }

            file1.close();
            return hexString;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*** Get Hex Data String in Little Endian Mode ***/
    public StringBuilder getLEHexData(Uri uri, long startCount, long endCount) throws IOException {
        int decimalValue;
        StringBuilder hexString = new StringBuilder();

        try {
            InputStream file1 = getContentResolver().openInputStream(uri);
            file1.skip(startCount);
            for (long i = startCount; i <= endCount; i++) {
                decimalValue = file1.read();
                hexString.append(String.format("%02X", decimalValue));
            }

            StringBuilder hexLE = new StringBuilder();
            for (int j = hexString.length(); j != 0; j -= 2) {
                hexLE.append(hexString.substring(j - 2, j));
            }

            file1.close();
            return hexLE;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /*** Change Hex to Decimal ***/ //Long is used in scenario when number is too huge.
    public long getHexToDecimal(StringBuilder hexString) {
        long decValue = Long.parseLong(String.valueOf(hexString), 16);
        return decValue;
    }

    /*** Change Hex to LE to Decimal ***/
    public long getHexLEDec(Uri uri, long startCount, long endCount) throws IOException {
        return getHexToDecimal(getLEHexData(uri, startCount, endCount));
    }

    /*** Convert Hex to ASCII String  ***/
    public String getHexToASCII(StringBuilder hexString) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            temp.append((char) Integer.parseInt(str, 16));
        }
        return temp.toString();
    }

    /*** Concat two Strings of Hex ***/
    public StringBuilder concatHex(StringBuilder firstHex, StringBuilder secondHex) {
        StringBuilder concatHex = new StringBuilder();
        concatHex.append(firstHex).append(secondHex);
        return concatHex;
    }

    /*** Get Hex Data String in Big Endian Mode (STRING VERSION) ***/
    public StringBuilder getBEHexData(ArrayList<StringBuilder> hexData, long startCount, long endCount)  {
        StringBuilder hexString = new StringBuilder();

            for (long i = startCount; i <= endCount; i++) {
                hexString.append(hexData.get((int)i));
            }

            return hexString;
    }

    /*** Get Hex Data String in Little Endian Mode (STRING VERSION) ***/
    public StringBuilder getLEHexData(ArrayList<StringBuilder> hexData, long startCount, long endCount) {
        StringBuilder hexString = new StringBuilder();

        for (long i = startCount; i <= endCount; i++) {
            hexString.append(hexData.get((int)i));
        }
        StringBuilder hexLE = new StringBuilder();
            for (int j = hexString.length(); j != 0; j -= 2) {
                hexLE.append(hexString.substring(j - 2, j));
            }
            return hexLE;
    }

    /*** Change Hex to LE to Decimal ***/
    public long getHexLEDec(ArrayList<StringBuilder> hexData, long startCount, long endCount) throws IOException {
        return getHexToDecimal(getLEHexData(hexData, startCount, endCount));
    }


    /***** ***** ***** ***** START OF GRABBING RECORDS ***** ***** ***** *****/
    /***** ***** ***** ***** START OF GRABBING RECORDS ***** ***** ***** *****/
    /***** ***** ***** ***** START OF GRABBING RECORDS ***** ***** ***** *****/
    /***** ***** ***** ***** START OF GRABBING RECORDS ***** ***** ***** *****/

    /*** Create new MBR object with its' information ***/
    public MBR getMBR(Uri uri, long startCount) throws IOException {
        MBR mbr = new MBR(getLEHexData(uri, startCount + 440, startCount + 444).toString());
        mbr.setSignatureType(getLEHexData(uri, startCount + 510, startCount + 511).toString());
        return mbr;
    }

    /*** Create new Partition object with its' information ***/
    public Partition getMBR_PartitionInfo(Uri uri, long startCount) throws IOException {
        Partition partition = new Partition();
        partition.setBootableStatus(getLEHexData(uri, startCount + 0, startCount + 0));
        partition.setPartitionType(getLEHexData(uri, startCount + 4, startCount + 4));
        partition.setStartOfPartition(getHexLEDec(uri, startCount + 8, startCount + 11));
        partition.setLenOfPartition(getHexLEDec(uri, startCount + 12, startCount + 15));
        return partition;
    }

    /*** Create new Volume Boot Record object with its' information ***/
    public VBR getVBRInfo(Uri uri, long startCount) throws IOException {
        VBR vbr = new VBR();
        vbr.setOEM(getHexToASCII(getBEHexData(uri, startCount + 3, startCount + 10)));
        vbr.setBytesPerSector(getHexLEDec(uri, startCount + 11, startCount + 12));
        vbr.setSectorsPerCluster(getHexLEDec(uri, startCount + 13, startCount + 13));
        vbr.setReservedAreaSize(getHexLEDec(uri, startCount + 14, startCount + 15));
        vbr.setNumOfFats(getHexLEDec(uri, startCount + 16, startCount + 16));
        vbr.setMaxRootFiles(getHexLEDec(uri, startCount + 17, startCount + 18));
        vbr.setBit16Sectors(getHexLEDec(uri, startCount + 19, startCount + 20));
        vbr.setMediaType(getLEHexData(uri, startCount + 21, startCount + 21));
        vbr.setOffset(getHexLEDec(uri, startCount + 28, startCount + 31));
        vbr.setBit32Sectors(getHexLEDec(uri, startCount + 32, startCount + 35));
        vbr.setBit32SectorsOfFat(getHexLEDec(uri, startCount + 36, startCount + 39));
        vbr.setRootCluster(getHexLEDec(uri, startCount + 44, startCount + 47));
        vbr.setVolumeLabel(getHexToASCII(getBEHexData(uri, startCount + 71, startCount + 81)));
        vbr.setFileSystemLabel(getHexToASCII(getBEHexData(uri, startCount + 82, startCount + 89)));
        vbr.setBytesPerCluster();
        return vbr;
    }

    /*** Create new FAT object with its' information ***/
    public FATable getFATInfo(Uri uri, long startCount, FATable fat) throws IOException {
        fat.setFatID(getLEHexData(uri, startCount + 0, startCount + 3).toString());
        fat.setEndClusterMarker(getLEHexData(uri, startCount + 4, startCount + 7).toString());
        return fat;
    }

    /*** Create new ExtMBR object with its' information ***/
    public ExtMBR getExtMBR(Uri uri, long startCount) throws IOException {
        ExtMBR extmbr = new ExtMBR();
        extmbr.setSignatureType(getLEHexData(uri, startCount + 510, startCount + 511).toString());
        return extmbr;
    }

    /*** Create new ExtPartition object with its' information ***/
    public ExtPartition getExtMBR_PartitionInfo(Uri uri, long startCount, long startExtMBR, long priExtPartitionStart) throws IOException {
        ExtPartition extPartition = new ExtPartition(startExtMBR);
        extPartition.setExtBootableStatus(getLEHexData(uri, startCount + 0, startCount + 0));
        extPartition.setPartitionType(getLEHexData(uri, startCount + 4, startCount + 4));
        extPartition.setPriExtPartitionStart(priExtPartitionStart);
        extPartition.setStartOfPartition(getHexLEDec(uri, startCount + 8, startCount + 11));
        extPartition.setLenOfPartition(getHexLEDec(uri, startCount + 12, startCount + 15));
        extPartition.setExt2Offset(getHexLEDec(uri, startCount + 24, startCount + 27));
        extPartition.setCalExt2MBR();
        return extPartition;
    }

    //Get List of Clusters link to the whole data
    public ArrayList<Long> getListOfClusterTraverse(Uri uri, FATable fat, Long clusterNum) throws IOException {
        ArrayList<Long> clusterNumlist = new ArrayList<Long>();
        clusterNumlist.add(clusterNum);
        boolean endOfClusterReach = false;
        do {
            long clusterHex = getHexLEDec(uri, (fat.getStartFirstFatDec() + (4 * (clusterNum))), (fat.getStartFirstFatDec() + ((4 * (clusterNum)) + 3)));

            if ((!fat.chkDmgCluster(clusterHex)) && !fat.chkEOFCluster(clusterHex)) {
                clusterNumlist.add(clusterHex);
                clusterNum = clusterNumlist.get(clusterNumlist.size() - 1);
            } else {
                endOfClusterReach = true;
            }
        } while (endOfClusterReach == false);

        return clusterNumlist;
    }


    // Collecting all the data for Directory
    public ArrayList<StringBuilder> getListOfDirDataTraverse(Uri uri, FATable fat, DataRegion dataRegion,
                                                      ArrayList<Long> clusterNumList, long bytesPerCluster) throws IOException {
        ArrayList<StringBuilder> dirContent = new ArrayList<StringBuilder>();
        long totalFileSize = clusterNumList.size() * bytesPerCluster;

        for (int i = 0; i < clusterNumList.size(); i++) {
            if (totalFileSize >= bytesPerCluster) {
                totalFileSize = totalFileSize - bytesPerCluster;
                for (long j=((clusterNumList.get(i) - 2) * bytesPerCluster); j<((clusterNumList.get(i) - 1) * bytesPerCluster - 1); j++) {
                    dirContent.add(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + j),
                            (dataRegion.getStartDataRegionDec() + j)));
                }
            } else {
                for (long j=(clusterNumList.get(i) - 2) * bytesPerCluster; j<((clusterNumList.get(i) - 2) * bytesPerCluster + totalFileSize); j++) {
                    dirContent.add(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + j),
                            (dataRegion.getStartDataRegionDec() + j)));
                }
            }
        }

        return dirContent;
    }

    // Collecting all the data for File
    public ArrayList<StringBuilder> getListOfFileDataTraverse(Uri uri, FATable fat, DataRegion dataRegion,
                                                       ArrayList<Long> clusterNumList, long bytesPerCluster, long totalFileSize) throws IOException {
        ArrayList<StringBuilder> fileContent = new ArrayList<StringBuilder>();
        for (int i = 0; i < clusterNumList.size(); i++) {
            totalFileSize = totalFileSize - (bytesPerCluster);
            if (totalFileSize >= bytesPerCluster) {
                fileContent.add(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster),
                        (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 1) * bytesPerCluster - 1)));
            } else {
                fileContent.add(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster),
                        (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster + totalFileSize)));
            }
        }

        return fileContent;
    }

    //acc
    public ArrayList<FileEntry> traverseDirectory(Uri uri, FATable fat, DataRegion dataRegion, long bytesPerCluster,
                                  ArrayList<StringBuilder> listOfDirData, ArrayList<FileEntry> listOfFileAndDir) throws IOException {

        int numOfLFNentries;
        long startCount = 0;
        long endCount = listOfDirData.size()-1;

        while (startCount < endCount) {
            numOfLFNentries = 0;

            if (getHexLEDec(listOfDirData, startCount + 11, startCount + 11) == 0) {
                // EMPTY
                startCount = startCount + 32;
            } else if (getHexLEDec(listOfDirData, startCount + 11, startCount + 11) == 15) {
                // LONG FILE NAME DETECTED
                if (getHexLEDec(listOfDirData, startCount, startCount) == 0 || (
                        getHexLEDec(listOfDirData, startCount, startCount) == 46 &&
                                getHexLEDec(listOfDirData, startCount+11, startCount+11) == 16)) {
                    // Skip as entry is 0 OR a '.' Directory;
                    startCount = startCount + 32;
                } else if (getHexLEDec(listOfDirData, startCount, startCount) == 229) {
                    // File OR Directory is deleted.
                    startCount = startCount + 32;
                } else {
                    // LONG FILE
                    // File is present
                    numOfLFNentries = Integer.parseInt(getBEHexData(listOfDirData, startCount, startCount).toString());
                    numOfLFNentries = numOfLFNentries - 40;

                    // Generate long file name
                    String fullLFname = "";
                    for (int i = 0; i < numOfLFNentries; i++) {
                        String tempLFname = "";
                        if (!getBEHexData(listOfDirData, startCount + 1, startCount + 1).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 1, startCount + 1));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 3, startCount + 3).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 3, startCount + 3));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 5, startCount + 5).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 5, startCount + 5));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 7, startCount + 7).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 7, startCount + 7));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 9, startCount + 9).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 9, startCount + 9));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 14, startCount + 14).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 14, startCount + 14));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 16, startCount + 16).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 16, startCount + 16));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 18, startCount + 18).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 18, startCount + 18));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 20, startCount + 20).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 20, startCount + 20));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 22, startCount + 22).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 22, startCount + 22));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 24, startCount + 24).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 24, startCount + 24));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 28, startCount + 28).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 28, startCount + 28));
                        }
                        if (!getBEHexData(listOfDirData, startCount + 30, startCount + 30).equals("FF")) {
                            tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 30, startCount + 30));
                        }
                        fullLFname = tempLFname + fullLFname;
                        startCount = startCount + 32;
                    }

                    if (getHexLEDec(listOfDirData, startCount + 11, startCount + 11) == 32) {
                        // FILE ONLY
                        FileEntry fileEntry = new FileEntry();
                        fileEntry.setLFname(fullLFname.replace("ÿ", ""));
                        fileEntry.setSFname(getHexToASCII(getBEHexData(listOfDirData, startCount, startCount + 7)));
                        fileEntry.setNameExt(getHexToASCII(getBEHexData(listOfDirData, startCount + 8, startCount + 10))
                                .replace("ÿ", "").replace(" ", ""));
                        fileEntry.setFileAttribute(getHexLEDec(listOfDirData, startCount + 11, startCount + 11));
                        // 13th is time in tenths of seconds.
                        fileEntry.setCreatedTime(getHexLEDec(listOfDirData, startCount + 14, startCount + 15));
                        fileEntry.setCreatedDate(getHexLEDec(listOfDirData, startCount + 16, startCount + 17));
                        fileEntry.setAccessedDate(getHexLEDec(listOfDirData, startCount + 18, startCount + 19));
                        fileEntry.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(listOfDirData, startCount + 20, startCount + 21),
                                getLEHexData(listOfDirData, startCount + 26, startCount + 27))));
                        fileEntry.setWrittenTime(getHexLEDec(listOfDirData, startCount + 22, startCount + 23));
                        fileEntry.setWrittenDate(getHexLEDec(listOfDirData, startCount + 24, startCount + 25));
                        fileEntry.setSizeOfFile(getHexLEDec(listOfDirData, startCount + 28, startCount + 31));
                        fileEntry.setListOfClusters(getListOfClusterTraverse(uri, fat, fileEntry.getFirstClusterLoc()));
                        listOfFileAndDir.add(fileEntry);
                        startCount = startCount + 32;
                    }
                    else if (getHexLEDec(listOfDirData, startCount + 11, startCount + 11) == 16){
                        //DIRECTORY ONLY
                        FileEntry fileEntry = new FileEntry();
                        fileEntry.setLFname(fullLFname.replace("ÿ", ""));
                        fileEntry.setSFname(getHexToASCII(getBEHexData(listOfDirData, startCount, startCount + 7)));
                        fileEntry.setNameExt(getHexToASCII(getBEHexData(listOfDirData, startCount + 8, startCount + 10))
                                .replace("ÿ", "").replace(" ", ""));
                        fileEntry.setFileAttribute(getHexLEDec(listOfDirData, startCount + 11, startCount + 11));
                        // 13th is time in tenths of seconds.
                        fileEntry.setCreatedTime(getHexLEDec(listOfDirData, startCount + 14, startCount + 15));
                        fileEntry.setCreatedDate(getHexLEDec(listOfDirData, startCount + 16, startCount + 17));
                        fileEntry.setAccessedDate(getHexLEDec(listOfDirData, startCount + 18, startCount + 19));
                        fileEntry.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(listOfDirData, startCount + 20, startCount + 21),
                                getLEHexData(listOfDirData, startCount + 26, startCount + 27))));
                        fileEntry.setWrittenTime(getHexLEDec(listOfDirData, startCount + 22, startCount + 23));
                        fileEntry.setWrittenDate(getHexLEDec(listOfDirData, startCount + 24, startCount + 25));
                        fileEntry.setSizeOfFile(getHexLEDec(listOfDirData, startCount + 28, startCount + 31));
                        fileEntry.setListOfClusters(getListOfClusterTraverse(uri, fat, fileEntry.getFirstClusterLoc()));
                        fileEntry.setListOfData(getListOfDirDataTraverse(uri, fat, dataRegion, fileEntry.getListOfClusters(), bytesPerCluster));
                        ArrayList<FileEntry> listOfAnotherFileAndDir = new ArrayList<FileEntry>();
                        fileEntry.setListOfFileAndDir(traverseDirectory(uri, fat, dataRegion, bytesPerCluster, fileEntry.getListOfData(), listOfAnotherFileAndDir));
                        listOfFileAndDir.add(fileEntry);
                        startCount = startCount + 32;

                    }
                    else {
                        //INVALID FILE
//                        FileEntry fileEntry = new FileEntry();
//                        fileEntry.setLFname(fullLFname);
//                        fileEntry.setSFname(getHexToASCII(getBEHexData(listOfDirData, startCount, startCount + 7)));
//                        fileEntry.setNameExt(getHexToASCII(getBEHexData(listOfDirData, startCount + 8, startCount + 10)));
//                        fileEntry.setFileAttribute(getHexLEDec(listOfDirData, startCount + 11, startCount + 11));
//                        // 13th is time in tenths of seconds.
//                        fileEntry.setCreatedTime(getHexLEDec(listOfDirData, startCount + 14, startCount + 15));
//                        fileEntry.setCreatedDate(getHexLEDec(listOfDirData, startCount + 16, startCount + 17));
//                        fileEntry.setAccessedDate(getHexLEDec(listOfDirData, startCount + 18, startCount + 19));
//                        fileEntry.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(listOfDirData, startCount + 20, startCount + 21),
//                                getLEHexData(listOfDirData, startCount + 26, startCount + 27))));
//                        fileEntry.setWrittenTime(getHexLEDec(listOfDirData, startCount + 22, startCount + 23));
//                        fileEntry.setWrittenDate(getHexLEDec(listOfDirData, startCount + 24, startCount + 25));
//                        fileEntry.setSizeOfFile(getHexLEDec(listOfDirData, startCount + 28, startCount + 31));
//                        fileEntry.setListOfClusters(getListOfClusterTraverse(uri, fat, fileEntry.getFirstClusterLoc()));

 //                       System.out.println("INNER INVALID FILE DETECTED: " + fileEntry.getSFname() + " ext " + fileEntry.getNameExt() + " " + getHexLEDec(listOfDirData, startCount + 11, startCount + 11));
                        startCount = startCount + 32;
                    }
                }
            } else if (getHexLEDec(uri, startCount + 11, startCount + 11) == 1 ||
                    getHexLEDec(uri, startCount + 11, startCount + 11) == 2 ||
                    getHexLEDec(uri, startCount + 11, startCount + 11) == 4 ||
                    getHexLEDec(uri, startCount + 11, startCount + 11) == 32) {
                // READ ONLY || HIDDEN FILE || SYSTEM FILE || ARCHIVE
                // Currently unsure what will happen for the following files;
 //               System.out.println("UNSURE FILE DETECTED");
                startCount = startCount + 32;
            } else if (
                    getHexLEDec(uri, startCount + 11, startCount + 11) == 8) {
                // VOLUME_ID
//                System.out.println("VOLUME LABEL DETECTED");
                startCount = startCount + 32;
            } else {
                // INCORRECT FILE FORMAT
//                String fullLFname = "";
//                for (int i = 0; i < numOfLFNentries; i++) {
//                    String tempLFname = "";
//                    if (!getBEHexData(listOfDirData, startCount + 1, startCount + 1).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 1, startCount + 1));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 3, startCount + 3).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 3, startCount + 3));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 5, startCount + 5).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 5, startCount + 5));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 7, startCount + 7).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 7, startCount + 7));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 9, startCount + 9).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 9, startCount + 9));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 14, startCount + 14).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 14, startCount + 14));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 16, startCount + 16).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 16, startCount + 16));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 18, startCount + 18).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 18, startCount + 18));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 20, startCount + 20).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 20, startCount + 20));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 22, startCount + 22).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 22, startCount + 22));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 24, startCount + 24).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 24, startCount + 24));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 28, startCount + 28).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 28, startCount + 28));
//                    }
//                    if (!getBEHexData(listOfDirData, startCount + 30, startCount + 30).equals("FF")) {
//                        tempLFname = tempLFname + getHexToASCII(getBEHexData(listOfDirData, startCount + 30, startCount + 30));
//                    }
//                    fullLFname = tempLFname + fullLFname;
//                    startCount = startCount + 32;
//                }
//                FileEntry fileEntry = new FileEntry();
//                fileEntry.setLFname(fullLFname);
//                fileEntry.setSFname(getHexToASCII(getBEHexData(listOfDirData, startCount, startCount + 7)));
//                fileEntry.setNameExt(getHexToASCII(getBEHexData(listOfDirData, startCount + 8, startCount + 10)));
//                fileEntry.setFileAttribute(getHexLEDec(listOfDirData, startCount + 11, startCount + 11));
//                // 13th is time in tenths of seconds.
//                fileEntry.setCreatedTime(getHexLEDec(listOfDirData, startCount + 14, startCount + 15));
//                fileEntry.setCreatedDate(getHexLEDec(listOfDirData, startCount + 16, startCount + 17));
//                fileEntry.setAccessedDate(getHexLEDec(listOfDirData, startCount + 18, startCount + 19));
//                fileEntry.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(listOfDirData, startCount + 20, startCount + 21),
//                        getLEHexData(listOfDirData, startCount + 26, startCount + 27))));
//                fileEntry.setWrittenTime(getHexLEDec(listOfDirData, startCount + 22, startCount + 23));
//                fileEntry.setWrittenDate(getHexLEDec(listOfDirData, startCount + 24, startCount + 25));
//                fileEntry.setSizeOfFile(getHexLEDec(listOfDirData, startCount + 28, startCount + 31));
//                fileEntry.setListOfClusters(getListOfClusterTraverse(uri, fat, fileEntry.getFirstClusterLoc()));

                startCount = startCount + 32;
//                System.out.println("INVALID FILE DETECTED: " + fileEntry.getSFname() + " ext " + fileEntry.getNameExt() + " " + getHexLEDec(listOfDirData, startCount + 11, startCount + 11));
            }


        }
        return listOfFileAndDir;
    }
}


