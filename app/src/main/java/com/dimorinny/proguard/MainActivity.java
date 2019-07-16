package com.dimorinny.proguard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new KotlinClassWithMethods().method1();
        new KotlinClassWithMethods().method2();
    }
}
