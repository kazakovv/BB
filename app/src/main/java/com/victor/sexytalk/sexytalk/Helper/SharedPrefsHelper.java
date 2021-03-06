package com.victor.sexytalk.sexytalk.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.backendless.BackendlessUser;
import com.victor.sexytalk.sexytalk.Statics;

/**
 * Created by Victor on 07/03/2015.
 */
public class SharedPrefsHelper {

    public static void saveEmailForLogin(Context context, BackendlessUser currentUser){
        String email = currentUser.getEmail();
        SharedPreferences pref = context.getSharedPreferences(Statics.SHARED_PREFS, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Statics.KEY_SAVED_EMAIL_FOR_LOGIN, email); // Storing string
        editor.commit();
    }

    public static String loadEmailOfLastLoggedInUser(Context context) {
        String email;
        SharedPreferences pref = context.getSharedPreferences(Statics.SHARED_PREFS, 0);
        SharedPreferences.Editor editor = pref.edit();

        email = pref.getString(Statics.KEY_SAVED_EMAIL_FOR_LOGIN, null); // getting String
        return email;
    }

    public static boolean displayOneLoveMessagePerDayDialog(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Statics.SHARED_PREFS, 0);
        SharedPreferences.Editor editor = pref.edit();

        Boolean displayOneLoveMessagePerDayDialog = pref.getBoolean(Statics.displayOneMessagePerDayDialogBox, true);

        return displayOneLoveMessagePerDayDialog;

    }

    public static void doNotShowOneMessagePerDay(Context context, boolean doNotShowOneMessagePerDayDialog){
        SharedPreferences pref = context.getSharedPreferences(Statics.SHARED_PREFS, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Statics.displayOneMessagePerDayDialogBox, doNotShowOneMessagePerDayDialog);
        editor.commit();

    }

}
