package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;


public class Main extends Activity {
    BackendlessUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //vrazvame osnovnotosaobshtenie
        currentUser = Backendless.UserService.CurrentUser();
        //ako niama lognat potrebitel preprashta kam log-in ekrana

        if (currentUser == null) {
            //prashta ni kam login screen
            navigateToLogin();
        } else {
            // ako ima lognat potrebitel prodalzhava natatak
            Log.i("Vic", "imame lognat potrebitel");

            //proveriavame dali e maz ili zhena
           // MaleOrFemale = currentUser.getString(ParseConstants.KEY_MALEORFEMALE);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void navigateToLogin() {
        //preprashta kam login screen
        Intent intent = new Intent(this, LoginActivity.class);

        //Celta na sledvashtite 2 reda e da ne moze da otidesh ot log-in ekrana
        //kam osnovnia ekran, ako natisnesh back butona

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //sazdavo zadacha
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //iztriva vsichki predishni zadachi.
        startActivity(intent);
    }

}
