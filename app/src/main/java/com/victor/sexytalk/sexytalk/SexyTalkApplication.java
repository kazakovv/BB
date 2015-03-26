package com.victor.sexytalk.sexytalk;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;

/**
 * Created by Victor on 09/12/2014.
 */
public class SexyTalkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String appVersion = "v1";
        String appID = "8269D96C-7534-B883-FFAB-4A95AFE02600";
        String sectetKey = "D33D1EB0-C901-002B-FF35-C6D2C21AB200";
        Backendless.setUrl("https://api.backendless.com");
        Backendless.initApp(this,appID,sectetKey,appVersion);


        //Backendless.Data.mapTableToClass("Users", BackendlessUser.class);
        //Backendless.Data.mapTableToClass("PartnersAddRequest",BackendlessUser.class);



    }
}
