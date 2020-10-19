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

    TextView txtPath; // CAN REMOVE IF NOT NEEDED
    Button startAnalyseButton;
    TextView testingText;
    Intent myFileIntent;
    private static final int READ_REQUEST_CODE = 42;
    int partitionCounter = 0;
    Boolean extendedPartExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*** FOR HIDING TOP BAR ***/
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        /*** Detect Start Analyse Button ***/
        startAnalyseButton = (Button)findViewById(R.id.startAnalyseButton);
        startAnalyseButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v){
                Intent openFileIntent= new Intent(Intent.ACTION_OPEN_DOCUMENT);
                openFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                openFileIntent.setType("*/*");
                startActivityForResult(openFileIntent, READ_REQUEST_CODE);
            }
        });
    }
    /*** Detect File input ***/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if (data != null){
                Uri uri = data.getData();

                try {
                    /*** Insert all the FAT reading functions here ***/
                    testingText = (TextView)findViewById(R.id.testingText);
                    testingText.setText("");
                    Long startCount = 0L;
                    MBR mbr = getMBR(uri, startCount +0); // Instantiate new MBR object

                    if (mbr.chkMBRValidity(testingText)) {

                        mbr.setPartition1(getMBR_PartitionInfo(uri, startCount + 446));
                        mbr.setPartition2(getMBR_PartitionInfo(uri, startCount + 462));
                        mbr.setPartition3(getMBR_PartitionInfo(uri, startCount + 478));
                        mbr.setPartition4(getMBR_PartitionInfo(uri, startCount + 494));

                        mbr.getPartition1().setEndOfPartition();
                        mbr.getPartition2().setEndOfPartition();
                        mbr.getPartition3().setEndOfPartition();
                        mbr.getPartition4().setEndOfPartition();

                        Partition[] partitionAvailability = {mbr.getPartition1(), mbr.getPartition2(),
                                mbr.getPartition3(), mbr.getPartition4()};

                        for (Partition partition : partitionAvailability) {
                            if (partition.getPartitionType().equals("Extended")) {
                                extendedPartExist = true;
                            }

                            else {
                                if (!partition.getPartitionType().equals("Empty")) {
                                    partition.toString(testingText);
                                    partitionCounter++;
                                    partition.setVBR(getVBRInfo(uri, partition.getStartOfPartition()*partition.getVBR().getBytesPerSector()));
                                    partition.setFSInfo(getFSInfo(uri, (partition.getStartOfPartition() + partition.getVBR().getFSInfoSector())*partition.getVBR().getBytesPerSector()));
                                    FATable fat0 = new FATable();
                                    FATable fat1 = new FATable();
                                    partition.setFAT(getFATInfo(uri, (partition.getStartOfPartition()+partition.getVBR().getReservedAreaSize())*partition.getVBR().getBytesPerSector()));


                                    System.out.println("Partition VBR: ");
                                    System.out.println(partition.getVBR().getVBRInfo());


                                }
                                else {
                                    //Ignore
                                }
                            }
                        }
                        testingText.append("\nMBR Signature Type: " + mbr.getSignatureType() + "Extended found? : " + extendedPartExist);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Unable to read file");
                }
            }
        }

    }

    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/

    /*** Get Hex Data String in Big Endian Mode ***/
    public StringBuilder getBEHexData(Uri uri, Long startCount, Long endCount) throws IOException {
        int decimalValue;
        StringBuilder hexString = new StringBuilder();

        try {
            InputStream file1 = getContentResolver().openInputStream(uri);
            file1.skip(startCount);
            for (Long i = startCount; i<= endCount; i++)
            {
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

    /*** Get Hex Data String in Big Endian Mode ***/
    public StringBuilder getLEHexData(Uri uri, Long startCount, Long endCount) throws IOException {
        int decimalValue;
        StringBuilder hexString = new StringBuilder();

        try {
            InputStream file1 = getContentResolver().openInputStream(uri);
            file1.skip(startCount);
            for (Long i = startCount; i<= endCount; i++)
            {
                decimalValue = file1.read();
                hexString.append(String.format("%02X", decimalValue));
            }

            StringBuilder hexLE = new StringBuilder();
            for (int j = hexString.length(); j != 0; j-=2) {
                hexLE.append(hexString.substring(j-2, j));
            }

            // System.out.println("getLEHexData:" + hexLE);
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
    public Long getHexToDecimal(StringBuilder hexString) {
        Long decValue = Long.parseLong(String.valueOf(hexString),16);
        // System.out.println("Convert Hex: " + hexString + " to Decimal: " + decValue);
        return decValue;
    }

    /*** Convert Hex to ASCII String  ***/
    public String getHexToASCII(StringBuilder hexString) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hexString.length(); i+=2) {
            String str = hexString.substring(i, i+2);
            temp.append((char)Integer.parseInt(str, 16));
        }
        return temp.toString();
    }

    /*** Concat two Strings of Hex ***/
    public StringBuilder concatHex(StringBuilder firstHex, StringBuilder secondHex) {
        StringBuilder concatHex = new StringBuilder();
        concatHex.append(firstHex).append(secondHex);
        System.out.println("ConcatHex: " + concatHex);
        return concatHex;
    }

    /*** Change Hex to LE to Decimal ***/
    public Long getHexLEDec(Uri uri, Long startCount, Long endCount) throws IOException {
        return getHexToDecimal(getLEHexData(uri, startCount, endCount));
    }

    /***** ***** ***** ***** START OF MASTER BOOT RECORD ***** ***** ***** *****/
    /***** ***** ***** ***** START OF MASTER BOOT RECORD ***** ***** ***** *****/
    /***** ***** ***** ***** START OF MASTER BOOT RECORD ***** ***** ***** *****/
    /***** ***** ***** ***** START OF MASTER BOOT RECORD ***** ***** ***** *****/

    /*** Get MBR Status Information ***/
    public MBR getMBR(Uri uri, Long startCount) throws IOException {
        MBR mbr = new MBR(getLEHexData(uri, startCount + 440, startCount + 444).toString());
        mbr.setSignatureType(getLEHexData(uri, startCount + 510, startCount + 511).toString());
        return mbr;
    }

    public Partition getMBR_PartitionInfo (Uri uri, Long startCount) throws IOException {
        Partition partition = new Partition();
        partition.setBootableStatus(getLEHexData(uri, startCount+0, startCount+0));
        partition.setPartitionType(getLEHexData(uri, startCount+4, startCount+4));
        partition.setStartOfPartition(getHexLEDec(uri, startCount+8, startCount+11));
        partition.setLenOfPartition(getHexLEDec(uri, startCount+12, startCount+15));
        return partition;
    }

    public VBR getVBRInfo(Uri uri, Long startCount) throws IOException {
        VBR vbr = new VBR();
        vbr.setOEM(getHexToASCII(getBEHexData(uri, startCount + 3, startCount + 10)));
        vbr.setBytesPerSector(getHexToDecimal(getLEHexData(uri, startCount + 11, startCount + 12)));
        vbr.setSectorsPerCluster(getHexToDecimal(getLEHexData(uri, startCount + 13, startCount + 13)));
        vbr.setReservedAreaSize(getHexToDecimal(getLEHexData(uri, startCount + 14, startCount + 15)));
        vbr.setNumOfFats(getHexToDecimal(getLEHexData(uri, startCount + 16, startCount + 16)));
        vbr.setMaxRootFiles(getHexToDecimal(getLEHexData(uri, startCount + 17, startCount + 18)));
        vbr.setBit16Sectors(getHexToDecimal(getLEHexData(uri, startCount + 19, startCount + 20)));
        vbr.setMediaType(getLEHexData(uri, startCount + 21, startCount + 21));
        vbr.setOffset(getHexToDecimal(getLEHexData(uri, startCount + 28, startCount + 31)));
        vbr.setBit32Sectors(getHexToDecimal(getLEHexData(uri, startCount + 32, startCount + 35)));
        vbr.setBit32SectorsOfFat(getHexToDecimal(getLEHexData(uri, startCount + 36, startCount + 39)));
        vbr.setRootCluster(getHexToDecimal(getLEHexData(uri, startCount + 44, startCount + 47)));
        vbr.setFSInfoSector(getHexToDecimal(getLEHexData(uri, startCount + 48, startCount + 49)));

        return vbr;
    }

    public FSInfo getFSInfo(Uri uri, Long startCount) throws IOException {
        FSInfo fsinfo = new FSInfo();
        fsinfo.setFSInfoSignature(getLEHexData(uri, startCount+0, startCount+3).toString());
        fsinfo.setLastKnownFreeCluster(getLEHexData(uri, startCount+484, startCount+487).toString());
        fsinfo.setLocalSignature(getLEHexData(uri, startCount+488, startCount+491).toString());
        fsinfo.setNextFreeCluster(getLEHexData(uri, startCount+492, startCount+495).toString());
        fsinfo.setTrailingSignature(getLEHexData(uri, startCount+508, startCount+511).toString());
        return fsinfo;
    }

    public FATable getFATInfo (Uri uri, Long startCount) throws IOException {
        FATable fat = new FATable();
        fat.setFatID(getLEHexData(uri, startCount+0, startCount+3).toString());
        fat.setEndClusterMarker(getLEHexData(uri, startCount+4, startCount+7).toString());
        return fat;
    }

    /*** Get VBR Status Information ***/
//    public VBR getVBR(Partition partition, Uri uri, int startCount) throws IOException {
//        VBR vbr = new VBR();
//        vbr.setOEM(getHexToASCII(getBEHexData(uri, (int)( startCount + vbr.getVBRSector()*512 + 3), (int) (startCount + vbr.getVBRSector()*512 + 10))));
//        return vbr;
//    }


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

