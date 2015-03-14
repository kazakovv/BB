package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.victor.sexytalk.sexytalk.UserInterfaces.LoginActivity;

/**
 * Created by Victor on 14/03/2015.
 */
public class SplashScreen extends Activity {
    private static final int SPLASH_DISPLAY_TIME = 4000; // splash screen delay time
    private BackendlessUser mCurrentUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mCurrentUser = Backendless.UserService.CurrentUser();

        new Handler().postDelayed(new Runnable() {
            public void run() {

                Intent intent = new Intent();
                if(mCurrentUser != null) {
                    intent.setClass(SplashScreen.this, Main.class);
                } else {
                    intent.setClass(SplashScreen.this, LoginActivity.class);
                }
                SplashScreen.this.startActivity(intent);
                SplashScreen.this.finish();

                // transition from splash to main menu
                //overridePendingTransition(R.animate.activityfadein,
                //        R.animate.splashfadeout);

            }
        }, SPLASH_DISPLAY_TIME);
    }
}
