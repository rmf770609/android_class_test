package com.example.raymond.simpleui;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by raymond on 3/21/16.
 */
public class SimpleUIApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /* 0321 move from MainActivity */
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
