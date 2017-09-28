package com.seeger.record.app;

import android.app.Application;

import com.seeger.record.utils.FileUtils;

/**
 * @author Suagr
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.init();
    }
}
