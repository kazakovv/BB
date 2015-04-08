package com.victor.sexytalk.bisou;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.victor.sexytalk.bisou.UserInterfaces.LoginActivity;


public class SplashScreen extends Activity {
    private static final int SPLASH_DISPLAY_TIME = 1000; // splash screen delay time
    private BackendlessUser mCurrentUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mCurrentUser = Backendless.UserService.CurrentUser();

        new Handler().postDelayed(new Runnable() {
            public void run() {

                Intent intent = new Intent();
                if(mCurrentUser != null) {
                    intent.setClass(SplashScreen.this, Main.class);
                } else {
                    intent.setClass(SplashScreen.this, LoginActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //sazdavo zadacha
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //iztriva vsichki predishni zadachi.
                SplashScreen.this.startActivity(intent);
                SplashScreen.this.finish();

                // transition from splash to main menu
                overridePendingTransition(R.anim.abc_fade_in,
                        R.anim.abc_fade_out);

            }
        }, SPLASH_DISPLAY_TIME);
    }
}
