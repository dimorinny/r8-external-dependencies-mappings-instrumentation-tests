package com.dimorinny.proguard;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        new KotlinClassWithMethodsInsideTestModule().method1();
        new KotlinClassWithMethodsInsideTestModule().method2();

        rule.launchActivity(new Intent());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
