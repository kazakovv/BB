package com.victor.sexytalk.sexytalk.UserInterfaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;


public class ActivityChangeSexyStatus extends ActionBarActivity {
protected Toolbar toolbar;
protected EditText mSexyStatus;
protected BackendlessUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sexy_status);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        mSexyStatus = (EditText) findViewById(R.id.changeSexyStatus);

        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_sexy_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ok) {
            //proveriavame dali e vavedeno sabshtenie
            if(mSexyStatus.getText().toString().trim().length() == 0) {
                //tr da se vavede status
                Toast.makeText(this,R.string.empty_sexy_status,Toast.LENGTH_LONG).show();
            } else {
                //ako statusa ne e prazen, go uploadvame v backendless i posle se vrashtame kam osnovnata activity


                mCurrentUser.setProperty(Statics.KEY_SEXY_STATUS,mSexyStatus.getText().toString());
                String message = this.getResources().getString(R.string.saving_message);
                Backendless.UserService.update(mCurrentUser, new DefaultCallback<BackendlessUser>(this,message) {
                   @Override
                   public void handleResponse(BackendlessUser backendlessUser) {
                       super.handleResponse(backendlessUser);

                       Intent data = new Intent();
                       data.putExtra(Statics.KEY_SET_STATUS, mSexyStatus.getText().toString().trim());
                       setResult(Activity.RESULT_OK, data);
                       finish();
                       Toast.makeText(ActivityChangeSexyStatus.this,R.string.sexy_status_saved_message,Toast.LENGTH_LONG).show();
                   }

                   @Override
                   public void handleFault(BackendlessFault backendlessFault) {
                        super.handleFault(backendlessFault);
                       AlertDialog.Builder builder = new AlertDialog.Builder(ActivityChangeSexyStatus.this);
                       builder.setTitle(R.string.general_error_title)
                               .setMessage(R.string.error_updating_status)
                               .setPositiveButton(R.string.ok, null);
                       AlertDialog dialog = builder.create();
                       dialog.show();
                   }
               });
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
