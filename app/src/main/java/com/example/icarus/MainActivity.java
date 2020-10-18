package com.example.icarus;

/*import org.apache.commons.codec.binary.*;*/

import java.io.File;
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

//import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;
/*import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;*/



public class MainActivity extends AppCompatActivity {

    TextView txtPath;
    Button startAnalyseButton;
    TextView testingText;
    Intent myFileIntent;
    private static final int READ_REQUEST_CODE = 42;

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
                    MBR mbr = getMBR(uri, 0);

                    if (mbr.chkMBRValidity()) {
                        mbr.getPartition1().setEndOfPartition();
                        mbr.getPartition2().setEndOfPartition();
                        mbr.getPartition3().setEndOfPartition();
                        mbr.getPartition4().setEndOfPartition();
                        testingText.setText("");
                        testingText.append("MBR detected.");
                        testingText.append("\n");
                        testingText.append("MBR Disk Identifier: " + mbr.getDiskIdentifer() + "\n");
                        testingText.append("Partition 1: " + "\n");
                        testingText.append("    Partition 1 BS: " + mbr.getPartition1().getBootableStatus() + "\n");
                        testingText.append("    Partition 1 PT: " + mbr.getPartition1().getPartitionType() + "\n");
                        testingText.append("    Partition 1 Start of Part: " + mbr.getPartition1().getStartOfPartition() + "\n");
                        testingText.append("    Partition 1 End of Part: " + mbr.getPartition1().getEndOfPartition() + "\n");
                        testingText.append("    Partition 1 Len of Part: " + mbr.getPartition1().getLenOfPartition() + "\n");
                        testingText.append("Partition 2: " + "\n");
                        testingText.append("    Partition 2 BS: " + mbr.getPartition2().getBootableStatus() + "\n");
                        testingText.append("    Partition 2 PT: " + mbr.getPartition2().getPartitionType() + "\n");
                        testingText.append("    Partition 2 Start of Part: " + mbr.getPartition2().getStartOfPartition() + "\n");
                        testingText.append("    Partition 2 End of Part: " + mbr.getPartition2().getEndOfPartition() + "\n");
                        testingText.append("    Partition 2 Len of Part: " + mbr.getPartition2().getLenOfPartition() + "\n\n\n\n\n\n\n\n\n");
                        testingText.append("Partition 3: " + "\n");
                        testingText.append("    Partition 3 BS: " + mbr.getPartition3().getBootableStatus() + "\n");
                        testingText.append("    Partition 3 PT: " + mbr.getPartition3().getPartitionType() + "\n");
                        testingText.append("    Partition 3 Start of Part: " + mbr.getPartition3().getStartOfPartition() + "\n");
                        testingText.append("    Partition 3 End of Part: " + mbr.getPartition3().getEndOfPartition() + "\n");
                        testingText.append("    Partition 3 Len of Part: " + mbr.getPartition3().getLenOfPartition() + "\n");
                        testingText.append("Partition 4: " + "\n");
                        testingText.append("    Partition 4 BS: " + mbr.getPartition4().getBootableStatus() + "\n");
                        testingText.append("    Partition 4 PT: " + mbr.getPartition4().getPartitionType() + "\n");
                        testingText.append("    Partition 4 Start of Part: " + mbr.getPartition4().getStartOfPartition() + "\n");
                        testingText.append("    Partition 4 End of Part: " + mbr.getPartition4().getEndOfPartition() + "\n");
                        testingText.append("    Partition 4 Len of Part: " + mbr.getPartition4().getLenOfPartition() + "\n");
                        testingText.append("MBR Signature Type: " + mbr.getSignatureType());
                    }
                    else {
                        testingText.setText("");
                        testingText.append("Invalid MBR. Cannot detect.");
                    }


//                    testingText.setText("");
//                    testingText.append("Hex: ");
//                    testingText.append(concatHex(getLEHexData(uri, 4, 5), getLEHexData(uri, 0, 3)));
//                    testingText.append("\nDecimal: ");
//                    testingText.append(hexToDecimal(concatHex(getLEHexData(uri, 4, 5), getLEHexData(uri, 0, 3))).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Unable to read file");
                }
            }
        }
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    String path = data.getData().getPath();
                    txtPath.setText(path);
                }
                break;
        }
    }

    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/
    /***** ***** ***** ***** FUNCTIONS TO GRAB HEX ***** ***** ***** *****/

    /*** Get Hex Data String in Big Endian Mode ***/
    public StringBuilder getBEHexData(Uri uri, int startCount, int endCount) throws IOException {
        int decimalValue;
        StringBuilder hexString = new StringBuilder();

        try {
            InputStream file1 = getContentResolver().openInputStream(uri);
            file1.skip(startCount);
            for (int i = startCount; i<= endCount; i++)
            {
                decimalValue = file1.read();
                hexString.append(String.format("%02X", decimalValue));
            }

            System.out.println("getBEHexData:" + hexString);
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
    public StringBuilder getLEHexData(Uri uri, int startCount, int endCount) throws IOException {
        int decimalValue;
        StringBuilder hexString = new StringBuilder();

        try {
            InputStream file1 = getContentResolver().openInputStream(uri);
            file1.skip(startCount);
            for (int i = startCount; i<= endCount; i++)
            {
                decimalValue = file1.read();
                hexString.append(String.format("%02X", decimalValue));
            }

            StringBuilder hexLE = new StringBuilder();
            for (int j = hexString.length(); j != 0; j-=2) {
                hexLE.append(hexString.substring(j-2, j));
            }

            System.out.println("getLEHexData:" + hexLE);
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
    public Long hexToDecimal(StringBuilder hexString) {
        Long decValue = Long.parseLong(String.valueOf(hexString),16);
        System.out.println("Convert Hex: " + hexString + " to Decimal: " + decValue);
        return decValue;
    }

    /*** Concat two Strings of Hex ***/
    public StringBuilder concatHex(StringBuilder firstHex, StringBuilder secondHex) {
        StringBuilder concatHex = new StringBuilder();
        concatHex.append(firstHex).append(secondHex);
        System.out.println("ConcatHex: " + concatHex);
        return concatHex;
    }

    /*** Change Hex to LE to Decimal ***/
    public Long hex_LE_Dec(Uri uri, int startCount, int endCount) throws IOException {
        return hexToDecimal(getLEHexData(uri, startCount, endCount));
    }

    /***** ***** ***** ***** START OF MASTER BOOT RECORD ***** ***** ***** *****/
    /***** ***** ***** ***** START OF MASTER BOOT RECORD ***** ***** ***** *****/
    /***** ***** ***** ***** START OF MASTER BOOT RECORD ***** ***** ***** *****/
    /***** ***** ***** ***** START OF MASTER BOOT RECORD ***** ***** ***** *****/

    public Partition getMBR_PartitionInfo (Uri uri, int startCount) throws IOException {
        Partition partition = new Partition();
        partition.setBootableStatus(getLEHexData(uri, startCount+0, startCount+0));
        partition.setPartitionType(getLEHexData(uri, startCount+4, startCount+4));
        partition.setStartOfPartition(hex_LE_Dec(uri, startCount+8, startCount+11));
        partition.setLenOfPartition(hex_LE_Dec(uri, startCount+12, startCount+15));

        return partition;
    }

    public MBR getMBR(Uri uri, int startCount) throws IOException {
        MBR mbr = new MBR(getLEHexData(uri, startCount + 440, startCount + 444).toString());
        mbr.setSignatureType(getLEHexData(uri, startCount + 510, startCount + 511).toString());

        mbr.setPartition1(getMBR_PartitionInfo(uri, 446));
        mbr.setPartition2(getMBR_PartitionInfo(uri, 462));
        mbr.setPartition3(getMBR_PartitionInfo(uri, 478));
        mbr.setPartition4(getMBR_PartitionInfo(uri, 494));

        return mbr;
    }


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