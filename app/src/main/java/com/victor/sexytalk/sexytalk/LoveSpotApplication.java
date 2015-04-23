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

        String appVersion = "";
        String appID = "";
        String sectetKey = "";
        Backendless.setUrl("");
        Backendless.initApp(this,appID,sectetKey,appVersion);


        //Backendless.Data.mapTableToClass("Users", BackendlessUser.class);
        //Backendless.Data.mapTableToClass("PartnersAddRequest",BackendlessUser.class);



    }
}
