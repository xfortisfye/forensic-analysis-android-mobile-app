package com.example.icarus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
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

                                                /*** Generation of Report ***/
                                                extmbr.getExtPartition().toString(testingText);
                                                extmbr.getExtPartition().getVBR().toString(testingText);
                                                extmbr.getExtPartition().getFAT().toString(testingText);
                                                extmbr.getExtPartition().getDataRegion().toString(testingText);
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
                                    if (extmbr.getExtPartition().getExt2Offset() == 0L){
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

                                    /*** ANDY's NOTE to anyone: A lot of halfway work so it is messy. Please re-edit creation of root directory
                                     and the how traverseDirectory a.k.a the recursive method should take in what attribute etc.***/
                                    ArrayList<String> listOfRootDirData = new ArrayList<String>();
                                    listOfRootDirData = getListOfDataTraverse(uri, fat, dataRegion, getListOfClusterTraverse(uri, fat, partition.getVBR().getRootCluster()), partition.getVBR().getBytesPerCluster());


//                                    FileEntry rootDirectory = new FileEntry(uri, partition.getFAT(), listOfRootDirData);
//                                    traverseDirectory(uri, fat, dataRegion, listOfRootDirData, partition.getVBR().getBytesPerCluster());
                                    //
//                                    ArrayList<FileEntry> fileList = new ArrayList<>();
////                                    rootDirectory.setFileEntryArrayList(fileList);
////                                    partition.setRootDirectory(rootDirectory);
//////                                    traverseDirectory(uri, fat, dataRegion, getFileData(uri, rootDirectory, rootDirectory.getContentCluster(),
////                                            dataRegion, partition.getVBR().getBytesPerCluster()),
////                                            rootDirectory.getFileEntryArrayList());
//                                    for (int i = 0; i < fileList.size(); i++) {
//                                        fileList.get(i).toString(testingText);
//                                        if (fileList.get(i).getFileAttribute() == 16) {
//                                           // fileList.get(i).getFileEntryArrayList().toString();
//                                        }
//                                    }



                                    /*** Carving of file ***/
                                    System.out.println("===============================================================");
                                    System.out.println("Fat table 1 " + partition.getFAT().getStartFirstFatDec());
                                    System.out.println("Fat table 2 " + partition.getFAT().getEndFirstFatDec());
                                    System.out.println("Root Dir " + partition.getDataRegion().getStartDataRegionDec());
                                    System.out.println("===============================================================");
                                    long rootDirDec = partition.getDataRegion().getStartDataRegionDec();
                                    long sectPerCluster = partition.getVBR().getSectorsPerCluster();
                                    long bytesPerSector = partition.getVBR().getBytesPerSector();
                                    long rootDirSect = partition.getDataRegion().getStartDataRegionSect();
                                    long startClusterDec = 0, fileSizeDec = 0;
                                    long startDirBlockDec;
                                    String DirName;

//                                    System.out.println(partition.getVBR().getSectorsPerCluster());
//                                    System.out.println(test);
//                                    System.out.println(test+partition.getVBR().getSectorsPerCluster()*512);
                                    long sectDec = sectPerCluster * 512;
                                    ArrayList<String> fileInfo;
                                    ArrayList<String> fileDateTime;
                                    long test = rootDirDec+sectDec;
                                    while(rootDirDec != test){

                                        startClusterDec = getHexToDecimal(getLEHexData(uri, rootDirDec+26, rootDirDec + 27));
                                        fileSizeDec = getHexToDecimal(getLEHexData(uri, rootDirDec+28, rootDirDec + 31));
                                        System.out.println(getLEHexData(uri, rootDirDec + 11, rootDirDec + 11).toString());
//                                        if(getLEHexData(uri, rootDirDec + 11, rootDirDec + 11).toString().equals("0F")){
//                                            System.out.println("Long file name found");
//                                            System.out.println(getHexToASCII(getBEHexData(uri, rootDirDec, rootDirDec + 10)));
//                                        }
                                        if(getLEHexData(uri, rootDirDec + 11, rootDirDec + 11).toString().equals("10")){
                                            System.out.println("===============================================================");
                                            System.out.println("Directory found");
                                            System.out.println("===============================================================");

                                            DirName = getHexToASCII(getBEHexData(uri, rootDirDec, rootDirDec + 7));

                                            fileDateTime = getFileDateTime(uri, rootDirDec);

                                            System.out.println("===============================================================");
                                            System.out.println("Time Directory created (24hr) " + fileDateTime.get(0));
                                            System.out.println("Date Directory created " + fileDateTime.get(1));
                                            System.out.println("Last Accessed Date " +  fileDateTime.get(2));
                                            System.out.println("Last Write Time " +  fileDateTime.get(3));

                                            startDirBlockDec = Long.parseLong(getStartLocOfBlock(rootDirSect, startClusterDec, sectPerCluster, bytesPerSector));

                                            //System.out.println("Start Location of Directory Block " + startDirBlockDec);
                                            System.out.println("===============================================================");
                                            System.out.println("===============================================================");
                                            System.out.println("Jumping to " + DirName + " Directory ");
                                            System.out.println("===============================================================");



                                            long startDirBlockSect = startDirBlockDec/512;
                                            long test2 = startDirBlockDec+sectDec;


                                            while(startDirBlockDec != test2) {

                                                long startDirClusterDec = getHexToDecimal(getLEHexData(uri, startDirBlockDec+26, startDirBlockDec + 27));
                                                long fileSizeInDirDec = getHexToDecimal(getLEHexData(uri, startDirBlockDec+28, startDirBlockDec + 31));

                                                if (getLEHexData(uri, startDirBlockDec + 11, startDirBlockDec + 11).toString().equals("20")) {
                                                    System.out.println("===============================================================");
                                                    System.out.println("File found in " + DirName);
                                                    System.out.println("===============================================================");

                                                    fileDateTime = getFileDateTime(uri, rootDirDec);

                                                    System.out.println("===============================================================");
                                                    System.out.println("Time File created (24hr) " + fileDateTime.get(0));
                                                    System.out.println("Date File created " + fileDateTime.get(1));
                                                    System.out.println("Last Accessed Date " + fileDateTime.get(2));
                                                    System.out.println("Last Write Time " + fileDateTime.get(3));
                                                    System.out.println("===============================================================");

                                                    fileInfo = getFileInfo(uri, startDirBlockDec, rootDirSect, startDirClusterDec, sectPerCluster, bytesPerSector, fileSizeInDirDec);

                                                    System.out.println("===============================================================");
                                                    System.out.println("File Name " + fileInfo.get(0));
                                                    System.out.println("File extension " + fileInfo.get(1));
                                                    System.out.println("Start Location of File Block " + fileInfo.get(2));
                                                    System.out.println("End Location of File Block " + fileInfo.get(3));
                                                    System.out.println("===============================================================");

                                                }
                                                startDirBlockDec += 16;
                                            }
                                        }

                                        if(getLEHexData(uri, rootDirDec + 11, rootDirDec + 11).toString().equals("20")){
                                            System.out.println("===============================================================");
                                            System.out.println("File found in root dir");
                                            System.out.println("===============================================================");

                                            fileDateTime = getFileDateTime(uri, rootDirDec);

                                            System.out.println("===============================================================");
                                            System.out.println("Time File created (24hr) " + fileDateTime.get(0));
                                            System.out.println("Date File created " + fileDateTime.get(1));
                                            System.out.println("Last Accessed Date " +  fileDateTime.get(2));
                                            System.out.println("Last Write Time " +  fileDateTime.get(3));
                                            System.out.println("===============================================================");

                                            fileInfo = getFileInfo(uri, rootDirDec, rootDirSect, startClusterDec, sectPerCluster, bytesPerSector, fileSizeDec);

                                            System.out.println("===============================================================");
                                            System.out.println("File name " + fileInfo.get(0));
                                            System.out.println("File extension " + fileInfo.get(1));
                                            System.out.println("Start Location of File Block " + fileInfo.get(2));
                                            System.out.println("End Location of File Block " + fileInfo.get(3));
                                            System.out.println("===============================================================");

                                        }

                                        rootDirDec+=16;

                                    }


                                    /*** Generation of Report ***/
                                    partition.toString(testingText);
                                    partition.getVBR().toString(testingText);
                                    partition.getFAT().toString(testingText);
                                    partition.getDataRegion().toString(testingText);
                                    //rootDirectory.rootDirtoString(testingText);
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
    /***** ***** ***** ***** FUNCTIONS TO CARVE DATA ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO CARVE DATA ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO CARVE DATA ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO CARVE DATA ***** ***** ***** *****/

    /*** Getting File Name / extension / block  ***/
    public ArrayList<String> getFileInfo(Uri uri, long rootDirDec, long rootDirSect, long startClusterDec, long sectPerCluster, long bytesPerSector, long fileSizeDec) throws IOException {

        String fileName, fileExt, StartLockOfFileBlock, EndLocOfFileBlock;

        ArrayList<String> fileInfo = new ArrayList<String>();

        fileName = getHexToASCII(getBEHexData(uri, rootDirDec, rootDirDec + 7));
        fileExt = getHexToASCII(getBEHexData(uri, rootDirDec+8, rootDirDec + 10));

        StartLockOfFileBlock = getStartLocOfBlock(rootDirSect,startClusterDec,sectPerCluster,bytesPerSector); // Element 0 = start Loc, Element 1 = end Loc

        EndLocOfFileBlock = getEndLocOfBlock(Long.parseLong(StartLockOfFileBlock),fileSizeDec);

        fileInfo.add(fileName);
        fileInfo.add(fileExt);
        fileInfo.add(StartLockOfFileBlock);
        fileInfo.add(EndLocOfFileBlock);

        return fileInfo;
    }


    /*** Getting Date & Time from either file or directory  ***/ //Long is used in scenario when number is too huge.
    public ArrayList<String> getFileDateTime(Uri uri, long rootDirDec) throws IOException {

        String timeCreated,dateCreated,lastAccDate,lastWriteTime,timeCreatedBin,dateCreatedBin,lastAccDateBin,lastWriteTimeBin;
        ArrayList<String> fileDateTime = new ArrayList<String>();

        timeCreatedBin = getDecToBin(getHexToDecimal(getLEHexData(uri, rootDirDec+14, rootDirDec + 15)));
        dateCreatedBin = getDecToBin(getHexToDecimal(getLEHexData(uri, rootDirDec+16, rootDirDec + 17)));
        lastAccDateBin = getDecToBin(getHexToDecimal(getLEHexData(uri, rootDirDec+18, rootDirDec + 19)));
        lastWriteTimeBin = getDecToBin(getHexToDecimal(getLEHexData(uri, rootDirDec+20, rootDirDec + 21)));

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

    public String getBinToDate(String binDate){

        String Date,Year,Month,Day,yearBin,monthBin,dayBin;

        yearBin = binDate.substring(0,7);
        monthBin = binDate.substring(7,11);
        dayBin = binDate.substring(11,16);

        Year = String.valueOf(Integer.parseInt(yearBin,2) + 1980);
        Month = String.valueOf(Integer.parseInt(monthBin,2));
        Day = String.valueOf(Integer.parseInt(dayBin,2));

        Date = String.join("-", Day, Month, Year);

        return Date;
    }

    /*** Change Binary to Date ***/ //Long is used in scenario when number is too huge.

    public String getBinToTime(String binTime){

        String Time,Hour,Min,Sec,hourBin,minBin,secBin;
        if(binTime.equals("0000000000000000")){
            return "Nil";
        }

        hourBin = binTime.substring(0,5);
        minBin = binTime.substring(5,11);
        secBin = binTime.substring(11,16);

        Hour = String.valueOf(Integer.parseInt(hourBin,2));
        Min = String.valueOf(Integer.parseInt(minBin,2));
        Sec = String.valueOf(Integer.parseInt(secBin,2) * 2);

        Time = String.join(":", Hour, Min, Sec);

        return Time;
    }

    /*** Get the Starting location of block ***/ //Long is used in scenario when number is too huge.

    public String getStartLocOfBlock(long rootDirSec, long startCluster, long clusterSize, long bytesPerSec){
        String startLocDec;

        startLocDec = Long.toString(((rootDirSec + ((startCluster - 2) * clusterSize)) * bytesPerSec));
//        System.out.println(rootDirSec);
//        System.out.println(startCluster);
//        System.out.println(clusterSize);
//        System.out.println(bytesPerSec);
        return startLocDec;
    }

    /*** Get the Ending location of block ***/ //Long is used in scenario when number is too huge.

    public String getEndLocOfBlock(long startLocDec, long fileSizeDec){
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

    public StringBuilder getLEHexData(String hexString) throws IOException {
        StringBuilder hexLE = new StringBuilder();
        for (int j = hexString.length(); j != 0; j -= 2) {
            hexLE.append(hexString.substring(j - 2, j));
        }

        return hexLE;
    }

    /*** Change Hex to Decimal ***/ //Long is used in scenario when number is too huge.
    public long getHexToDecimal(StringBuilder hexString) {
        long decValue = Long.parseLong(String.valueOf(hexString), 16);
        return decValue;
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

    /*** Change Hex to LE to Decimal ***/
    public long getHexLEDec(Uri uri, long startCount, long endCount) throws IOException {
        return getHexToDecimal(getLEHexData(uri, startCount, endCount));
    }

    /*** Change Hex to LE to Decimal ***/
    public long getHexLEDec(String hexString) throws IOException {
        return getHexToDecimal(getLEHexData(hexString));
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

    //Get List of Clusters link to the specific cluster for Root Directory
    public ArrayList<Long> getListOfClusterTraverse(Uri uri, FATable fat, Long clusterNoNum) throws IOException {
        ArrayList<Long> clusterNumlist = new ArrayList<Long>();
        clusterNumlist.add(clusterNoNum);
        boolean endOfFileReach = false;
        do {
            long clusterHex = getHexLEDec(uri, (fat.getStartFirstFatDec() + (4*(clusterNoNum))), (fat.getStartFirstFatDec() + ((4*(clusterNoNum))+3)));

            if ((!fat.chkDmgCluster(clusterHex)) && !fat.chkEOFCluster(clusterHex)) {
                clusterNumlist.add(clusterHex);
                clusterNoNum = clusterNumlist.get(clusterNumlist.size() - 1);
            }

            else {
                endOfFileReach = true;
            }
        } while (endOfFileReach == false);

        return clusterNumlist;
    }
    // Collecting all the DATA for Root Directory
    public ArrayList<String> getListOfDataTraverse(Uri uri, FATable fat, DataRegion dataRegion, ArrayList<Long> clusterNumList,
                                                    long bytesPerCluster) throws IOException {
        ArrayList<String> clusterContent = new ArrayList<String>();
        long totalFileSize = clusterNumList.size() * bytesPerCluster;

        System.out.println("clusterNumList.size: " + clusterNumList.size());
        System.out.println("bytes Per Clust: " + bytesPerCluster);
        System.out.println("Total File Size: " + totalFileSize);
        for (int i=0; i<clusterNumList.size(); i++) {
            if (totalFileSize >= bytesPerCluster) {
                totalFileSize = totalFileSize - bytesPerCluster;
                clusterContent.add(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster),
                        (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 1) * bytesPerCluster - 1)).toString());
                System.out.println(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster),
                        (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 1) * bytesPerCluster - 1)).toString());
            }
            else {
                clusterContent.add(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster),
                        (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster + totalFileSize)).toString());
            }
        }

        return clusterContent;
    }

    // Collecting all the DATA for Normal Data
    public ArrayList<String> getListOfDataTraverse(Uri uri, FATable fat, DataRegion dataRegion,
                                                    ArrayList<Long> clusterNumList, long bytesPerCluster, long totalFileSize) throws IOException {
        ArrayList<String> clusterContent = new ArrayList<String>();
        for (int i=0; i<clusterNumList.size(); i++) {
            totalFileSize = totalFileSize - (bytesPerCluster);
            if (totalFileSize >= bytesPerCluster) {
                clusterContent.add(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster),
                        (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 1) * bytesPerCluster - 1)).toString());
            }
            else {
                clusterContent.add(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster),
                        (dataRegion.getStartDataRegionDec() + (clusterNumList.get(i) - 2) * bytesPerCluster + totalFileSize)).toString());
            }
        }

        return clusterContent;
    }

    //Get List of Clusters link to the specific cluster
    public ArrayList<Long> getListOfClusterTraverse(Uri uri, FATable fat, Long clusterNoNum, DataRegion dataRegion) throws IOException {
        ArrayList<Long> clusterNumlist = new ArrayList<Long>();
        clusterNumlist.add(clusterNoNum);
        boolean endOfFileReach = false;
        do {
            long clusterHex = getHexLEDec(uri, (fat.getStartFirstFatDec() + (4*(clusterNoNum))), (fat.getStartFirstFatDec() + ((4*(clusterNoNum))+3)));

            if ((!fat.chkDmgCluster(clusterHex)) && !fat.chkEOFCluster(clusterHex)) {
                clusterNumlist.add(clusterHex);
                clusterNoNum = clusterNumlist.get(clusterNumlist.size() - 1);
            }
            else {
                endOfFileReach = true;
            }

        } while (endOfFileReach == false);

        return clusterNumlist;
    }

    public void traverseDirectory(Uri uri, FATable fat, DataRegion dataRegion,
                                       String content, ArrayList<FileEntry> fileEntryList) throws IOException {
        int numOfLFNentries;
        long startCount = 0;
        long endCount = content.length();

        while (startCount < endCount - 32) {
            numOfLFNentries = 0;

            if (getHexLEDec(uri, startCount + 11, startCount + 11) == 0) {
                // EMPTY
                startCount = startCount + 32;
            }

            if (getHexLEDec(uri, startCount + 11, startCount + 11) == 1) {
                // READ ONLY
                startCount = startCount + 32;
            }

            if (getHexLEDec(uri, startCount + 11, startCount + 11) == 2) {
                // HIDDEN
                startCount = startCount + 32;
            }

            if (getHexLEDec(uri, startCount + 11, startCount + 11) == 4) {
                // SYSTEM
                startCount = startCount + 32;
            }

            if (getHexLEDec(uri, startCount + 11, startCount + 11) == 8) {
                System.out.println("VOLUME LABEL DETECTED");
                // VOLUME_ID
                startCount = startCount + 32;
            }

            if (getHexLEDec(uri, startCount + 11, startCount + 11) == 15) {
                // FILE

                if (getHexLEDec(uri, startCount, startCount) == 0 ||
                        getHexLEDec(uri, startCount, startCount) == 46) {
                    // Skip as entry is 0 OR a '.' Directory;
                    startCount = startCount + 32;
                }

                else if (getHexLEDec(uri, startCount, startCount) == 229) {
                    // File is deleted.
                    startCount = startCount + 32;
                    FileEntry fileEntry = new FileEntry();

                    fileEntry.setName(getHexToASCII(getBEHexData(uri, startCount, startCount+10)));
                    fileEntry.setNameExt(getHexToASCII(getBEHexData(uri, startCount+8, startCount+10)));
                    fileEntry.setFileAttribute(getHexLEDec(uri, startCount+11, startCount+11));
                    // 13th is time in tenths of seconds.
                    fileEntry.setCreatedTime(getHexLEDec(uri, startCount+14, startCount+15));
                    fileEntry.setCreatedDate(getHexLEDec(uri, startCount+16, startCount+17));
                    fileEntry.setAccessedDate(getHexLEDec(uri, startCount+18, startCount+19));
                    fileEntry.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(uri, startCount+20, startCount+21),
                            getLEHexData(uri, startCount+26, startCount+27))));
                    fileEntry.setWrittenTime(getHexLEDec(uri, startCount+22, startCount+23));
                    fileEntry.setWrittenDate(getHexLEDec(uri, startCount+24, startCount+25));
                    fileEntry.setSizeOfFile(getHexLEDec(uri, startCount+28, startCount+31));
                    fileEntry.setListOfClusters(getListOfClusterTraverse(uri, fat, fileEntry.getFirstClusterLoc(), dataRegion));
                    System.out.println("Del File is found");
                    if (fileEntry.getFileAttribute() != 22) {
                        //System.out.println("Content" + fileEntry.getListOfClusters());
                    }
                    fileEntryList.add(fileEntry);

                    startCount = startCount + 32;
                }

                else {
                    // File is present
                    numOfLFNentries = Integer.parseInt(getLEHexData(uri, startCount, startCount).toString());
                    numOfLFNentries = numOfLFNentries - 40;
                    startCount = startCount + (numOfLFNentries) * 32;
                    FileEntry fileEntry = new FileEntry();

                    fileEntry.setName(getHexToASCII(getBEHexData(uri, startCount, startCount+10)));
                    fileEntry.setNameExt(getHexToASCII(getBEHexData(uri, startCount+8, startCount+10)));
                    fileEntry.setFileAttribute(getHexLEDec(uri, startCount+11, startCount+11));
                    // 13th is time in tenths of seconds.
                    fileEntry.setCreatedTime(getHexLEDec(uri, startCount+14, startCount+15));
                    fileEntry.setCreatedDate(getHexLEDec(uri, startCount+16, startCount+17));
                    fileEntry.setAccessedDate(getHexLEDec(uri, startCount+18, startCount+19));
                    fileEntry.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(uri, startCount+20, startCount+21),
                            getLEHexData(uri, startCount+26, startCount+27))));
                    fileEntry.setWrittenTime(getHexLEDec(uri, startCount+22, startCount+23));
                    fileEntry.setWrittenDate(getHexLEDec(uri, startCount+24, startCount+25));
                    fileEntry.setSizeOfFile(getHexLEDec(uri, startCount+28, startCount+31));
                    System.out.println("File is found");
                    if (fileEntry.getFileAttribute() != 22) {
                        fileEntry.setListOfClusters(getListOfClusterTraverse(uri, fat, fileEntry.getFirstClusterLoc(), dataRegion));
                    }

                    fileEntryList.add(fileEntry);

                    startCount = startCount + 32;
                }
            }

            if (getHexLEDec(uri, startCount + 11, startCount + 11) == 16) {
                // DIRECTORY
                if (getHexLEDec(uri, startCount, startCount) == 229) {
                    // DIRECTORY IS DELETED.
                    startCount = startCount + 32;
                    FileEntry fileEntry = new FileEntry();
                    ArrayList<FileEntry> dirFileEntryArrayList = new ArrayList<>();
                    fileEntry.setName(getHexToASCII(getBEHexData(uri, startCount, startCount+10)));
                    fileEntry.setNameExt(getHexToASCII(getBEHexData(uri, startCount+8, startCount+10)));
                    fileEntry.setFileAttribute(getHexLEDec(uri, startCount+11, startCount+11));
                    // 13th is time in tenths of seconds.
                    fileEntry.setCreatedTime(getHexLEDec(uri, startCount+14, startCount+15));
                    fileEntry.setCreatedDate(getHexLEDec(uri, startCount+16, startCount+17));
                    fileEntry.setAccessedDate(getHexLEDec(uri, startCount+18, startCount+19));
                    fileEntry.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(uri, startCount+20, startCount+21),
                            getLEHexData(uri, startCount+26, startCount+27))));
                    fileEntry.setWrittenTime(getHexLEDec(uri, startCount+22, startCount+23));
                    fileEntry.setWrittenDate(getHexLEDec(uri, startCount+24, startCount+25));
                    fileEntry.setSizeOfFile(getHexLEDec(uri, startCount+28, startCount+31));
                    fileEntry.setListOfClusters(getListOfClusterTraverse(uri, fat, fileEntry.getFirstClusterLoc(), dataRegion));
                    //traverseDirectory(uri, fat, dirFileEntryArrayList, fileEntryList, dataRegion);
                    //fileEntry.setListOfFileAndDir(dirFileEntryArrayList);
                    fileEntryList.add(fileEntry);
                    System.out.println("Del Dir is found");
                    startCount = startCount + 32;
                }

                else {
                    // DIRECTORY IS PRESENT
                    numOfLFNentries = Integer.parseInt(getLEHexData(uri, startCount, startCount).toString());
                    numOfLFNentries = numOfLFNentries - 40;
                    startCount = startCount + (numOfLFNentries) * 32;
                    FileEntry fileEntry = new FileEntry();
                    ArrayList<FileEntry> dirFileEntryArrayList = new ArrayList<>();
                    fileEntry.setName(getHexToASCII(getBEHexData(uri, startCount, startCount+10)));
                    fileEntry.setNameExt(getHexToASCII(getBEHexData(uri, startCount+8, startCount+10)));
                    fileEntry.setFileAttribute(getHexLEDec(uri, startCount+11, startCount+11));
                    // 13th is time in tenths of seconds.
                    fileEntry.setCreatedTime(getHexLEDec(uri, startCount+14, startCount+15));
                    fileEntry.setCreatedDate(getHexLEDec(uri, startCount+16, startCount+17));
                    fileEntry.setAccessedDate(getHexLEDec(uri, startCount+18, startCount+19));
                    fileEntry.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(uri, startCount+20, startCount+21),
                            getLEHexData(uri, startCount+26, startCount+27))));
                    fileEntry.setWrittenTime(getHexLEDec(uri, startCount+22, startCount+23));
                    fileEntry.setWrittenDate(getHexLEDec(uri, startCount+24, startCount+25));
                    fileEntry.setSizeOfFile(getHexLEDec(uri, startCount+28, startCount+31));
                    fileEntry.setListOfFileAndDir(dirFileEntryArrayList);
                    fileEntryList.add(fileEntry);
                    System.out.println("Dir is found");
                    startCount = startCount + 32;
                }
            }

            if (getHexLEDec(uri, startCount + 11, startCount + 11) == 32) {
                // ARCHIVE
                startCount = startCount + 32;
            }

        }
    }


    /***** OBSOLETE: NOT USED AS IT IS INCOMPATIBLE AND SLOW *****/
    /***** OBSOLETE: NOT USED AS IT IS INCOMPATIBLE AND SLOW *****/
    /***** OBSOLETE: NOT USED AS IT IS INCOMPATIBLE AND SLOW *****/
    /**********Print hex with ASCII version**********/
    public void printHexEdit(Uri uri) throws IOException {
        int bytesCount = 0;
        int valueCount = 0;
        StringBuilder SBHex = new StringBuilder();
        StringBuilder SBText = new StringBuilder();
        StringBuilder SBResult = new StringBuilder();

        try {
            InputStream file1 = getContentResolver().openInputStream(uri);

            while (((valueCount = file1.read()) != -1)) {
                SBHex.append(String.format("%02X ", valueCount));
                if (!Character.isISOControl(valueCount)) {
                    SBText.append((char) valueCount);
                } else {
                    SBText.append(".");
                }

                if (bytesCount == 15) {
                    SBResult.append(SBHex).append("      ").append(SBText).append("\n");
                    System.out.println(SBResult);
                    SBResult.setLength(0);
                    SBHex.setLength(0);
                    SBText.setLength(0);
                    bytesCount = 0;
                } else {
                    bytesCount++;
                }
            }

            if (bytesCount != 0) {
                for (; bytesCount < 16; bytesCount++) {
                    SBHex.append("   ");
                }
                SBResult.append(SBHex).append("      ").append("\n");
                System.out.println(SBResult);
                SBResult.setLength(0);
            }

            // out.println(SBResult);
            file1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //                if (getHexLEDec(uri, startCount + 11, startCount + 11) == 1) {
//                    // READ ONLY
//                    startCount = startCount + 32;
//                }
//
//                if (getHexLEDec(uri, startCount + 11, startCount + 11) == 2) {
//                    // HIDDEN
//                    startCount = startCount + 32;
//                }
//
//                if (getHexLEDec(uri, startCount + 11, startCount + 11) == 4) {
//                    // SYSTEM
//                    startCount = startCount + 32;
//                }
//
//                if (getHexLEDec(uri, startCount + 11, startCount + 11) == 8) {
//                    // VOLUME_ID
//                    startCount = startCount + 32;
//                }
//
//                if (getHexLEDec(uri, startCount + 11, startCount + 11) == 15) {
//                    // FILE
//
//                    if (getHexLEDec(uri, startCount, startCount) == 0 ||
//                            getHexLEDec(uri, startCount, startCount) == 46) {
//                        // Skip as entry is 0 OR a '.' Directory;
//                        startCount = startCount + 32;
//                    }
//
//                    else if (getHexLEDec(uri, startCount, startCount) == 229) {
//                        // File is deleted.
//                        startCount = startCount + 32;
//                        ShortFile shortFile = new ShortFile();
//
//                        shortFile.setName(getHexToASCII(getBEHexData(uri, startCount, startCount+10)));
//                        shortFile.setNameExt(getHexToASCII(getBEHexData(uri, startCount+8, startCount+10)));
//                        shortFile.setFileAttribute(getHexLEDec(uri, startCount+11, startCount+11));
//                        // 13th is time in tenths of seconds.
//                        shortFile.setCreatedTime(getHexLEDec(uri, startCount+14, startCount+15));
//                        shortFile.setCreatedDate(getHexLEDec(uri, startCount+16, startCount+17));
//                        shortFile.setAccessedDate(getHexLEDec(uri, startCount+18, startCount+19));
//                        shortFile.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(uri, startCount+20, startCount+21),
//                                getLEHexData(uri, startCount+26, startCount+27))));
//                        shortFile.setWrittenTime(getHexLEDec(uri, startCount+22, startCount+23));
//                        shortFile.setWrittenDate(getHexLEDec(uri, startCount+24, startCount+25));
//                        shortFile.setSizeOfFile(getHexLEDec(uri, startCount+28, startCount+31));
//                        shortFileList.add(shortFile);
//
//                        startCount = startCount + 32;
//                    }
//
//                    else {
//                        numOfLFNentries = (int) getHexLEDec(uri, startCount, startCount);
//                        numOfLFNentries = numOfLFNentries | 40;
//                        startCount = startCount + (numOfLFNentries) * 32;
//                        ShortFile shortFile = new ShortFile();
//
//                        shortFile.setName(getHexToASCII(getBEHexData(uri, startCount, startCount+10)));
//                        shortFile.setNameExt(getHexToASCII(getBEHexData(uri, startCount+8, startCount+10)));
//                        shortFile.setFileAttribute(getHexLEDec(uri, startCount+11, startCount+11));
//                        // 13th is time in tenths of seconds.
//                        shortFile.setCreatedTime(getHexLEDec(uri, startCount+14, startCount+15));
//                        shortFile.setCreatedDate(getHexLEDec(uri, startCount+16, startCount+17));
//                        shortFile.setAccessedDate(getHexLEDec(uri, startCount+18, startCount+19));
//                        shortFile.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(uri, startCount+20, startCount+21),
//                                getLEHexData(uri, startCount+26, startCount+27))));
//                        shortFile.setWrittenTime(getHexLEDec(uri, startCount+22, startCount+23));
//                        shortFile.setWrittenDate(getHexLEDec(uri, startCount+24, startCount+25));
//                        shortFile.setSizeOfFile(getHexLEDec(uri, startCount+28, startCount+31));
//                        shortFileList.add(shortFile);
//
//                        startCount = startCount + 32;
//                    }
//                }
//
//                if (getHexLEDec(uri, startCount + 11, startCount + 11) == 16) {
//                    // DIRECTORY
//                    if (getHexLEDec(uri, startCount, startCount) == 229) {
//                        // Directory is deleted.
//                        startCount = startCount + 32;
//                        Directory directory = new Directory();
//
//                        directory.setName(getHexToASCII(getBEHexData(uri, startCount, startCount+10)));
//                        directory.setNameExt(getHexToASCII(getBEHexData(uri, startCount+8, startCount+10)));
//                        directory.setFileAttribute(getHexLEDec(uri, startCount+11, startCount+11));
//                        // 13th is time in tenths of seconds.
//                        directory.setCreatedTime(getHexLEDec(uri, startCount+14, startCount+15));
//                        directory.setCreatedDate(getHexLEDec(uri, startCount+16, startCount+17));
//                        directory.setAccessedDate(getHexLEDec(uri, startCount+18, startCount+19));
//                        directory.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(uri, startCount+20, startCount+21),
//                                getLEHexData(uri, startCount+26, startCount+27))));
//                        directory.setWrittenTime(getHexLEDec(uri, startCount+22, startCount+23));
//                        directory.setWrittenDate(getHexLEDec(uri, startCount+24, startCount+25));
//                        directory.setSizeOfFile(getHexLEDec(uri, startCount+28, startCount+31));
//                        directoryList.add(directory);
//
//                        startCount = startCount + 32;
//                    }
//
//                    else {
//                        numOfLFNentries = (int) getHexLEDec(uri, startCount, startCount);
//                        numOfLFNentries = numOfLFNentries | 40;
//                        startCount = startCount + (numOfLFNentries) * 32;
//                        Directory directory = new Directory();
//
//                        directory.setName(getHexToASCII(getBEHexData(uri, startCount, startCount+10)));
//                        directory.setNameExt(getHexToASCII(getBEHexData(uri, startCount+8, startCount+10)));
//                        directory.setFileAttribute(getHexLEDec(uri, startCount+11, startCount+11));
//                        // 13th is time in tenths of seconds.
//                        directory.setCreatedTime(getHexLEDec(uri, startCount+14, startCount+15));
//                        directory.setCreatedDate(getHexLEDec(uri, startCount+16, startCount+17));
//                        directory.setAccessedDate(getHexLEDec(uri, startCount+18, startCount+19));
//                        directory.setFirstClusterLoc(getHexToDecimal(concatHex(getLEHexData(uri, startCount+20, startCount+21),
//                                getLEHexData(uri, startCount+26, startCount+27))));
//                        directory.setWrittenTime(getHexLEDec(uri, startCount+22, startCount+23));
//                        directory.setWrittenDate(getHexLEDec(uri, startCount+24, startCount+25));
//                        directory.setSizeOfFile(getHexLEDec(uri, startCount+28, startCount+31));
//                        directoryList.add(directory);
//
//                        startCount = startCount + 32;
//                    }
//                }
//
//                if (getHexLEDec(uri, startCount + 11, startCount + 11) == 32) {
//                    // ARCHIVE
//                    startCount = startCount + 32;
//                }


//    public ArrayList dataClusterTraverse(Uri uri, FATable fat, Long clusterNoNum, DataRegion dataRegion) throws IOException {
//        ArrayList<Long> clusterNumlist = new ArrayList<Long>();
//        ArrayList<String> clusterContent = new ArrayList<String>();
//        clusterNumlist.add(clusterNoNum);
//        boolean endOfFileReach = false;
//        do {
//            clusterContent.add(getBEHexData(uri, (dataRegion.getStartDataRegionDec() + (clusterNoNum-2)*dataRegion.getClusterSizeInDec()),
//                    (dataRegion.getStartDataRegionDec() + (clusterNoNum-1)*dataRegion.getClusterSizeInDec()-1)).toString());
//            System.out.println(clusterContent.get(clusterContent.size()-1));
//            long clusterHex = getHexLEDec(uri, (fat.getStartFirstFatDec() + (4*(clusterNoNum))), (fat.getStartFirstFatDec() + ((4*(clusterNoNum))+3)));
//
//            if ((!fat.chkDmgCluster(clusterHex)) && !fat.chkEOFCluster(clusterHex)) {
//                clusterNumlist.add(clusterHex);
//                clusterNoNum = clusterNumlist.get(clusterNumlist.size() - 1);
//            }
//            else {
//                endOfFileReach = true;
//            }
//
//        } while (endOfFileReach == false);
//
//        return clusterContent;
//    }
}

