package com.victor.sexytalk.sexytalk.UserInterfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.victor.sexytalk.sexytalk.Adaptors.AdapterViewImage;
import com.victor.sexytalk.sexytalk.Helper.BackendlessMessage;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;


public class ViewImageActivity extends ActionBarActivity {
    protected TextView loveMessage;
    protected ImageView imageViewToDisplay;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        //loveMessage = (TextView) findViewById(R.id.loveMessage);
        //imageViewToDisplay = (ImageView) findViewById(R.id.imageView_to_display_picture);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        //GET THE LOVE MESSAGE AND THE PIC
        String loveMessageToDisplay = getIntent().getStringExtra(Statics.KEY_LOVE_MESSAGE);
        String imageUrl = getIntent().getStringExtra(Statics.KEY_URL);
        String senderUsername = getIntent().getStringExtra(Statics.KEY_USERNAME_SENDER);

        //messageId se izpolzva, za da se zadade koga e otvoreno saobsthenieto,
        // ako sme go otvorile kato sme caknali na push notification
        String messageId = getIntent().getStringExtra(Statics.KEY_MESSAGE_ID);
        if(messageId !=null){
            BackendlessMessage.findMessageAndSetDateOpened(messageId);

        }
        //SET UP THE RECYCLER VIEW
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        AdapterViewImage adapter = new AdapterViewImage(loveMessageToDisplay,senderUsername,imageUrl,this);
        recList.setAdapter(adapter);

        //loveMessage.setText(loveMessageToDisplay);
            //String imageUrl = getIntent().getStringExtra(Statics.KEY_URL);
            //Picasso e vanshta bibilioteka, koito ni pozvoliava da otvariame snimki ot internet
            //Picasso.with(this).load(imageUrl).into(imageViewToDisplay);


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_text_image_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_send_message) {
            Intent intent = new Intent(ViewImageActivity.this,SendMessage.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}



