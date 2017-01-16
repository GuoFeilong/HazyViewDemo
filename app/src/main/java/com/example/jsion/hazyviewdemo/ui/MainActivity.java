package com.example.jsion.hazyviewdemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jsion.hazyviewdemo.R;
import com.example.jsion.hazyviewdemo.customview.HazyView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HazyView hazyView = (HazyView) findViewById(R.id.hz_view);
        hazyView.setHzValueMax(550);
        hazyView.setCurrentPercent(68);

    }
}
