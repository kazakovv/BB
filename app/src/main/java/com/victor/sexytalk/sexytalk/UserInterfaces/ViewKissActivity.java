package com.victor.sexytalk.sexytalk.UserInterfaces;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;


public class ViewKissActivity extends ActionBarActivity {
    protected TextView kissMessageToDisplay;
    protected TextView kissNumber;
    protected Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_kiss);
        kissNumber = (TextView) findViewById(R.id.kissNumber);
        kissMessageToDisplay = (TextView) findViewById(R.id.kissMessage);
        String loveMessage = getIntent().getStringExtra(Statics.KEY_LOVE_MESSAGE);
        int kissesSent = getIntent().getIntExtra(Statics.KEY_NUMBER_OF_KISSES,0);
        kissMessageToDisplay.setText(loveMessage);

        //vkarvame broia na celuvkite kato promenliva v string
        String numberOfKissesMessage;
        if(kissesSent > 1) {
            numberOfKissesMessage = getResources().getString(R.string.number_of_kisses_received_plural, kissesSent);
        } else {
            numberOfKissesMessage = getResources().getString(R.string.number_of_kisses_received_singular, kissesSent);
        }
        String message = getIntent().getStringExtra(Statics.KEY_USERNAME_SENDER) + " " + numberOfKissesMessage ;

        kissNumber.setText(message);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);

        setSupportActionBar(toolbar);
    }


}
