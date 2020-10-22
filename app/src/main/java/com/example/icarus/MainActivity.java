package com.example.icarus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

                                                FATable fat = new FATable(startFirstFatSect, endFirstFatSect, endLastFatSect, extmbr.getExtPartition().getVBR().getBytesPerSector());
                                                extmbr.getExtPartition().setFAT(getFATInfo(uri, fat.getStartFirstFatDec(), fat));

                                                DataRegion dataRegion = new DataRegion(startDataRegionSect, endDataRegionSect, extmbr.getExtPartition().getVBR().getBytesPerSector());
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

                                    /*** Generation of Report ***/
                                    partition.toString(testingText);
                                    partition.getVBR().toString(testingText);
                                    partition.getFAT().toString(testingText);
                                    partition.getDataRegion().toString(testingText);
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
        return vbr;
    }

    /*** Create new FAT object with its' information ***/
    public FATable getFATInfo(Uri uri, long startCount, FATable fat) throws IOException {
        fat.setFatID(getLEHexData(uri, startCount + 0, startCount + 3).toString());
        fat.setEndClusterMarker(getLEHexData(uri, startCount + 4, startCount + 7).toString());
        return fat;
    }

    /*** Create new Data object with its' information ***/
    public Data getData(Uri uri, long startCount) throws IOException {
        Data data = new Data();
//        data.setFatID(getLEHexData(uri, startCount+0, startCount+3).toString());
//        data.setEndClusterMarker(getLEHexData(uri, startCount+4, startCount+7).toString());
        return data;
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
}

