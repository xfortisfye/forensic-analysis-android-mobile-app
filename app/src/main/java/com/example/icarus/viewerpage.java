package com.example.icarus;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class viewerpage extends AppCompatActivity {
    TextView text;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer);
        ImageView image = (ImageView) findViewById(R.id.imagehold);

        int imageResource = getResources().getIdentifier("@drawable/icarus",null, this.getPackageName());
        image.setImageResource(imageResource);

        text = findViewById(R.id.texthold);
        text.setText("this is a test statement for viewing purpose");
    }
}
