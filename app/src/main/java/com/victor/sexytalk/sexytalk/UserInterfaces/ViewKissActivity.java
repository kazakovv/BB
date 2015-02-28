package com.victor.sexytalk.sexytalk.UserInterfaces;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;


public class ViewKissActivity extends ActionBarActivity {
    protected TextView kissMessageToDisplay;
    protected Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_kiss);

        kissMessageToDisplay = (TextView) findViewById(R.id.kissMessage);
        String loveMessage = getIntent().getStringExtra(Statics.KEY_LOVE_MESSAGE);
        kissMessageToDisplay.setText(loveMessage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);

        setSupportActionBar(toolbar);
    }


}
