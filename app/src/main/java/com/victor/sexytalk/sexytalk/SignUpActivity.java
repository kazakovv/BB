package com.victor.sexytalk.sexytalk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


public class SignUpActivity extends Activity {
    protected EditText mUserName;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //Vrazvame opciite za spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerMaleOrFemale);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sex_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //vrazvame ostanalite TextFields i butona
        mUserName = (EditText) findViewById(R.id.sign_up_username);
        mPassword = (EditText) findViewById(R.id.sign_up_password);
        mEmail = (EditText) findViewById(R.id.sign_up_email);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //razkarvam ako ima intervali v username, passpword i email
                String userName = mUserName.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();

                if(userName.isEmpty() || password.isEmpty() || email.isEmpty() ) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle(R.string.sign_up_error_title)
                            .setMessage(R.string.sign_up_error_message)
                            .setPositiveButton(R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                else {
                    //ako username, password, email ne sa prazni gi zapisvame v parse

                    BackendlessUser newUser = new BackendlessUser();
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setProperty(Statics.KEY_USERNAME, userName);
                    newUser.setProperty(Statics.KEY_MALE_OR_FEMALE,spinner.getSelectedItem().toString());


                    //TODO: Check dali email ne eveche zaet

                    final String message = getResources().getString(R.string.signing_up_message);
                    Backendless.UserService.register(newUser,
                            new DefaultCallback<BackendlessUser>(SignUpActivity.this, message) {

                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {

                            //tuk varzvame push!!!!!!!!!!!!!!!!!
                            //!!!!!!!!!!!!!!!!!!
                            //User successfully created!
                            //log in!
                            Backendless.UserService.login(email, password,
                                    new DefaultCallback<BackendlessUser>(SignUpActivity.this, message) {
                                @Override
                                public void handleResponse(BackendlessUser backendlessUser) {
                                    // Switch to main screen.
                                    Intent intent = new Intent(SignUpActivity.this,Main.class);
                                    //dobaviame flagove, za da ne moze usera da se varne pak kam toya ekran
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    setProgressBarIndeterminateVisibility(false);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                    builder.setTitle(R.string.sign_up_error_title)
                                            .setMessage(R.string.error_user_registered_but_unable_to_log_in)
                                            .setPositiveButton(R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });


                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            setProgressBarIndeterminateVisibility(false);
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setTitle(R.string.sign_up_error_title)
                                    .setMessage(R.string.error_unable_to_sign_in)
                                    .setPositiveButton(R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });




                }
            }
        });
    }
}
