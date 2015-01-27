package com.victor.sexytalk.sexytalk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class LoginActivity extends Activity {
    protected TextView signUpClickButton;

    protected EditText mEmail;
    protected EditText mPassword;
    protected Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //vrazvame ostanalite TextFields i butona
        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.logInButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //razkarvam ako ima intervali v username, passpword i email
                final String userEmail = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(userEmail.isEmpty() || password.isEmpty() ) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle(R.string.login_error_title)
                            .setMessage(R.string.login_error_message)
                            .setPositiveButton(R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                else {
                    //ako username i password ne sa prazni se logvame Backendless

                    BackendlessUser user = new BackendlessUser();
                    user.setEmail(userEmail);
                    user.setPassword(password);

                    Backendless.UserService.login(userEmail, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            //Register device for push notifications
                            registerDeviceForPush(backendlessUser);

                            //User successfully loged in!.Switch to main screen.
                            Intent intent = new Intent(LoginActivity.this, Main.class);
                            //dobaviame flagove, za da ne moze usera da se varne pak kam toya ekran
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            //neuspeshen login

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle(R.string.login_error_title)
                                    .setMessage(R.string.general_login_error_message)
                                    .setPositiveButton(R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });






                }
            }
        });

        //onClickListener za signup Activity

        signUpClickButton = (TextView) findViewById(R.id.signUpText);
        signUpClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

        public void registerDeviceForPush(final BackendlessUser currentUser){

            final String channel = currentUser.getEmail();


            Backendless.Messaging.registerDevice(Statics.GOOGLE_PROJECT_ID, channel, new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void aVoid) {
                    //Get registration and re-register....
                    Backendless.Messaging.getRegistrations(new AsyncCallback<DeviceRegistration>() {
                        @Override
                        public void handleResponse(final DeviceRegistration deviceRegistration) {
                            String token = deviceRegistration.getDeviceToken();
                            List<String> channels = new ArrayList<String>();
                            channels.add(channel);
                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.YEAR, 10);
                            Date expiration = c.getTime();
                            Backendless.Messaging.registerDeviceOnServer(token, channels, expiration.getTime(), new AsyncCallback<String>() {
                                @Override
                                public void handleResponse(String s) {

                                    currentUser.setProperty(Statics.KEY_DEVICE_ID,deviceRegistration.getDeviceId());

                                    Backendless.UserService.update(currentUser, new AsyncCallback<BackendlessUser>() {
                                        @Override
                                        public void handleResponse(BackendlessUser backendlessUser) {
                                            Toast.makeText(LoginActivity.this,"updated user" + currentUser.getProperty(Statics.KEY_DEVICE_ID),Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault backendlessFault) {
                                            String error = backendlessFault.getMessage();
                                            Log.d("Vic","error");
                                        }
                                    });
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    String error = backendlessFault.getMessage();
                                    Toast.makeText(LoginActivity.this, "fault", Toast.LENGTH_LONG).show();
                                    Log.d("Vic", "token");

                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            //TODO handle fault
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    //TODO handle fault
                    Log.d("Vic", "device not registered " + backendlessFault.getMessage());
                }
            });

        }


}
