package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;



public class ViewImageActivity extends ActionBarActivity {
    protected TextView loveMessage;
    protected ImageView imageViewToDisplay;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        loveMessage = (TextView) findViewById(R.id.loveMessage);
        imageViewToDisplay = (ImageView) findViewById(R.id.imageView_to_display_picture);
        String loveMessageToDisplay = getIntent().getStringExtra(Statics.KEY_LOVE_MESSAGE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        loveMessage.setText(loveMessageToDisplay);
            //Uri imageUri = getIntent().getData(); //vzima Uri deto go podadohme ot drugata strana
            String imageUrl = getIntent().getStringExtra(Statics.KEY_URL);
            //Picasso e vanshta bibilioteka, koito ni pozvoliava da otvariame snimki ot internet
            Picasso.with(this).load(imageUrl).into(imageViewToDisplay);


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_image_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int rotationAngle;
        switch(id){
            case R.id.action_rotate_left:
                rotationAngle = (int) imageViewToDisplay.getRotation();
                imageViewToDisplay.setRotation(rotationAngle -90);

                break;
            case R.id.action_rotate_right:
                rotationAngle = (int) imageViewToDisplay.getRotation();
                imageViewToDisplay.setRotation(rotationAngle + 90);
                break;
        }


        return super.onOptionsItemSelected(item);

    }
}



