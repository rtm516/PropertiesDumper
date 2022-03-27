package com.rtm516.propertiesdumper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import java.io.IOException;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String propertiesData;
        try {
            StringWriter writer = new StringWriter();
            new NativeDeviceInfoProvider(this).getNativeDeviceProperties().store(writer, "PropertiesDumper by rtm516");
            propertiesData = writer.toString();
        } catch (IOException e) {
            propertiesData = "Failed to get properties!\n" + e.getMessage();
        }

        ((EditText)findViewById(R.id.txtProperties)).setText(propertiesData);
    }
}