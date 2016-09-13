package com.example.smr.instaplan;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;


/**
 * Created by SMR on 9/11/2016.
 */
public class MyAplication extends Application {

    private static MyAplication mInstance;
    public static ArrayList<ImageSelectItem> imageSelected;

    public static final String SELECTED = "selected";

    public static MyAplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mInstance = this;
    }
}
