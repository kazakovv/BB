package com.victor.sexytalk.bisou;

import android.app.Application;

import com.backendless.Backendless;

/**
 * Created by Victor on 09/12/2014.
 */
public class BisouApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String appVersion = "v1";
        String appID = "8269D96C-7534-B883-FFAB-4A95AFE02600";
        String sectetKey = "6E90BE90-B3A9-C20F-FFA7-3BEBB6D94600";
        Backendless.setUrl("https://api.backendless.com");
        Backendless.initApp(this,appID,sectetKey,appVersion);


        //Backendless.Data.mapTableToClass("Users", BackendlessUser.class);
        //Backendless.Data.mapTableToClass("PartnersAddRequest",BackendlessUser.class);



    }
}
