package com.victor.sexytalk.sexytalk;

import android.app.Application;

import com.backendless.Backendless;

/**
 * Created by Victor on 09/12/2014.
 */
public class LoveSpotApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String appVersion = "v1";
        String appID = "8269D96C-7534-B883-FFAB-4A95AFE02600";
        String sectetKey = "4F549B0E-7AD0-80D5-FF7B-64AA08BA8400";
        Backendless.setUrl("https://api.backendless.com");
        Backendless.initApp(this, appID, sectetKey, appVersion);

    }
}
