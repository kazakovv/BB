package com.victor.sexytalk.bisou.UserInterfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.victor.sexytalk.bisou.Adaptors.AdapterViewTextMessage;
import com.victor.sexytalk.bisou.Helper.BackendlessMessage;
import com.victor.sexytalk.bisou.R;
import com.victor.sexytalk.bisou.Statics;


public class ViewTextMessageActivity extends ActionBarActivity {
    //protected TextView loveMessageToDisplay;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_text_message);
        //loveMessageToDisplay = (TextView) findViewById(R.id.loveMessageTextMessage);
        String loveMessage = getIntent().getStringExtra(Statics.KEY_LOVE_MESSAGE);
        String senderUsername = getIntent().getStringExtra(Statics.KEY_USERNAME_SENDER);

        //messageId se izpolzva, za da se zadade koga e otvoreno saobsthenieto,
        // ako sme go otvorile kato sme caknali na push notification
        String messageId = getIntent().getStringExtra(Statics.KEY_MESSAGE_ID);
        if(messageId !=null){
            BackendlessMessage.findMessageAndSetDateOpened(messageId);

        }

        //loveMessageToDisplay.setText(loveMessage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        //SET UP THE RECYCLER VIEW
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        AdapterViewTextMessage adapter = new AdapterViewTextMessage(loveMessage, senderUsername,this);
        recList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_text_image_message,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_send_message) {
            Intent intent = new Intent(ViewTextMessageActivity.this,SendMessage.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
