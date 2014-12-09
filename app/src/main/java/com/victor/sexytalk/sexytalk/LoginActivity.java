package com.victor.sexytalk.sexytalk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


public class LoginActivity extends Activity {
    protected TextView signUpClickButton;

    protected EditText mEmail;
    protected EditText mPassword;
    protected Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); //vzmoznost da pokazva spinner dokato misli
        setContentView(R.layout.activity_login);

        ActionBar actionbar = getActionBar();
        actionbar.hide();//skirvame actionabar che e po-krasivo

        //vrazvame ostanalite TextFields i butona
        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.logInButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //razkarvam ako ima intervali v username, passpword i email
                String userEmail = mEmail.getText().toString().trim();
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
                    setProgressBarIndeterminateVisibility(true); //pokazva spiner che se sluchva neshto

                    BackendlessUser user = new BackendlessUser();
                    user.setEmail(userEmail);
                    user.setPassword(password);

                    Backendless.UserService.login(userEmail, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            //tuk triabva da se registrirame za push, kogato go napravia!!!!!!
                            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            //!!!!!!!!!!!!!!!!!!!!!!!!!1


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




}
