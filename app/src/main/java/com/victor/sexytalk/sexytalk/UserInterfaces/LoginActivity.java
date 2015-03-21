package com.victor.sexytalk.sexytalk.UserInterfaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;
import com.victor.sexytalk.sexytalk.CustomDialogs.CustomAlertDialog;
import com.victor.sexytalk.sexytalk.CustomDialogs.ForgotPassword;
import com.victor.sexytalk.sexytalk.Helper.BackendlessMessage;
import com.victor.sexytalk.sexytalk.Helper.SharedPrefsHelper;
import com.victor.sexytalk.sexytalk.Main;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;


public class LoginActivity extends Activity {
    protected TextView signUpClickButton;

    protected EditText mEmail;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected static int FORGOT_PASSWORD = 111;

    //onClick Listener za recover password
    protected DialogInterface.OnClickListener recoverPasswordOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            ForgotPassword forgotPassword = new ForgotPassword();
            forgotPassword.show(getFragmentManager(),"Welcome");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //vrazvame ostanalite TextFields i butona
        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.logInButton);
        //zarezdame emaila na poslednia lognal se potrebitel
        String emailOfLastLoggedInUser = SharedPrefsHelper.loadEmailOfLastLoggedInUser(LoginActivity.this);
        if (emailOfLastLoggedInUser !=null) {
            mEmail.setText(emailOfLastLoggedInUser);
        }
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //razkarvam ako ima intervali v username, passpword i email
                final String userEmail = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(userEmail.isEmpty() || password.isEmpty() ) {
                    String title = getResources().getString(R.string.login_error_title);
                    String message = getResources().getString(R.string.login_error_message);
                    CustomAlertDialog kissDialog = new CustomAlertDialog();
                    Bundle dialogContent = new Bundle();
                    dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                    dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                    kissDialog.setArguments(dialogContent);
                    kissDialog.show(getFragmentManager(),"tag_alert_dialog");
                    /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle(R.string.login_error_title)
                            .setMessage(R.string.login_error_message)
                            .setPositiveButton(R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();*/
                }

                else {
                    //ako username i password ne sa prazni se logvame Backendless

                    BackendlessUser user = new BackendlessUser();
                    user.setEmail(userEmail);
                    user.setPassword(password);

                    String progressSignInMessage = getResources().getString(R.string.progress_sign_in_message);
                    Backendless.UserService.login(userEmail, password,  new DefaultCallback<BackendlessUser>(LoginActivity.this, progressSignInMessage) {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            BackendlessUser test = backendlessUser;
                            //Register device for push notifications
                            BackendlessMessage.registerDeviceForPush(backendlessUser);
                            // zapisvame username v shared prefs, za da moze da se zaredi po-lesno sledvashtiat pat
                            SharedPrefsHelper.saveEmailForLogin(LoginActivity.this, backendlessUser);
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
                            //ako ne se izvka dolniat red dialog box varti nerestanno
                            super.handleFault(backendlessFault);
                            String error = backendlessFault.getCode();
                            if(error.equals(Statics.BACKENDLESS_INVALID_LOGIN_OR_PASS_MESSAGE)) {
                                //greshno portrebitelsko ime ili parola
                                String title = getResources().getString(R.string.login_error_title);
                                String message = getResources().getString(R.string.invalid_login_details_dialog);
                                CustomAlertDialog dialogError = new CustomAlertDialog();
                                Bundle dialogContent = new Bundle();
                                dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                                dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                                dialogError.setArguments(dialogContent);
                                dialogError.show(getFragmentManager(),"tag_alert_dialog");
                                /*

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle(R.string.login_error_title)
                                        .setMessage(R.string.invalid_login_details_dialog)
                                        .setNeutralButton(R.string.forgot_your_password,recoverPasswordOnClickListener)
                                        .setPositiveButton(R.string.try_again_enter_correct_password, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                */
                            } else {
                                //niakakva greshka sas servera
                                String title = getResources().getString(R.string.login_error_title);
                                String message = getResources().getString(R.string.general_login_error_message);
                                CustomAlertDialog dialogError = new CustomAlertDialog();
                                Bundle dialogContent = new Bundle();
                                dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                                dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                                dialogError.setArguments(dialogContent);
                                dialogError.show(getFragmentManager(),"tag_alert_dialog");

                                /*
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle(R.string.login_error_title)
                                        .setMessage(R.string.general_login_error_message)
                                        .setPositiveButton(R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                */
                            }
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


}
