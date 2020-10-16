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

                File file = new File(uri.getPath());

                try {
                    /*** Insert all the FAT reading functions here***/
                    //printHexEdit(uri);
                    System.out.println(concatHex(hexToLE(getHexData(uri, 4, 5)), hexToLE(getHexData(uri, 0, 3))));
                    System.out.println(hexToDecimal(concatHex(hexToLE(getHexData(uri, 4, 5)), hexToLE(getHexData(uri, 0, 3)))));
                    testingText = (TextView)findViewById(R.id.testingText);
                    testingText.setText("");
                    testingText.append("Hex: ");
                    testingText.append(concatHex(hexToLE(getHexData(uri, 4, 5)), hexToLE(getHexData(uri, 0, 3))));
                    testingText.append("\n Decimal: ");
                    testingText.append(hexToDecimal(concatHex(hexToLE(getHexData(uri, 4, 5)), hexToLE(getHexData(uri, 0, 3)))).toString());
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

    /*** Get Hex Data String in Big Endian Mode ***/
    public StringBuilder getHexData(Uri uri, int startCount, int endCount) throws IOException {
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

            System.out.println("getHexData:" + hexString);
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

    /*** Convert to Little Endian Mode***/
    public StringBuilder hexToLE(StringBuilder hexString) {
        StringBuilder hexLE = new StringBuilder();
        for (int j = hexString.length(); j != 0; j-=2) {
            hexLE.append(hexString.substring(j-2, j));
        }

        System.out.println("Hex to LE: " + hexLE);
        return hexLE;
    }

    /*** Concat two Strings of Hex ***/
    public StringBuilder concatHex(StringBuilder firstHex, StringBuilder secondHex) {
        StringBuilder concatHex = new StringBuilder();
        concatHex.append(firstHex).append(secondHex);
        System.out.println("ConcatHex: " + concatHex);
        return concatHex;
    }

    /*** Change Hex to Decimal ***/ //Long is used in scenario when number is too huge.
    public Long hexToDecimal(StringBuilder hexString) {
        Long decValue = Long.parseLong(String.valueOf(hexString),16);
        System.out.println("Convert Hex: " + hexString + " to Decimal: " + decValue);
        return decValue;
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