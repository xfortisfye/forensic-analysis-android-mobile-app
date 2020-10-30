package com.example.icarus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class PartInfo extends AppCompatActivity {
    String text;
    TextView TV;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partinfo);
        text = MainActivity.gettext();
        TV= findViewById(R.id.infoview);
        TV.setText(text);

    }
}
