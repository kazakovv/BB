package com.victor.sexytalk.sexytalk;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;



import com.backendless.Backendless;
import com.backendless.BackendlessUser;


import java.util.ArrayList;



public class SendTo extends ActionBarActivity   {
    protected BackendlessUser[] mPartners;
    protected BackendlessUser mCurrentUSer;
    protected ArrayList<Integer> mSendTo;
    protected ArrayList<String> mRecepientUserNames;
    protected ArrayList<String> mRecepientEmails;
    protected Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to);
        //vrazvam mCurrentUser i list
        if (Backendless.UserService.CurrentUser() != null) {
            mCurrentUSer = Backendless.UserService.CurrentUser();
            mPartners = (BackendlessUser[]) mCurrentUSer.getProperty(Statics.KEY_PARTNERS);
            //vrazvame recyclerview
            mRecyclerView = (RecyclerView) findViewById(R.id.list_with_partners);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new AdapterSendTo(mPartners);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        }
        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);



        //inicializirame arraylists, za da mozem da dobaviame info kam tiah
        mRecepientEmails = new ArrayList<String>();
        mSendTo = new ArrayList<Integer>();
        mRecepientUserNames = new ArrayList<String>();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_to, menu);
        return super.onCreateOptionsMenu(menu);

    }

/*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(l.isItemChecked(position)) {
            mSendTo.add(position);
            mRecepientEmails.add(mPartners[position].getEmail());
            mRecepientUserNames.add((String) mPartners[position].getProperty(Statics.KEY_USERNAME));
        } else {
            int positionToRemove = mSendTo.indexOf(position);
            mSendTo.remove(positionToRemove);
            mRecepientEmails.remove(positionToRemove);
            mRecepientUserNames.remove(positionToRemove);
        }
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO:sigurno ima po-dobar variant ot tova da se izpolzvat statichni promenlivi v adaptora
        mRecepientEmails = AdapterSendTo.mRecepientEmails;
        mRecepientUserNames = AdapterSendTo.mRecepientUserNames;
        int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_ok:

                Intent intent = new Intent(SendTo.this, SendMessage.class);

                intent.putStringArrayListExtra(Statics.KEY_USERNAME, mRecepientUserNames);
                intent.putStringArrayListExtra(Statics.KEY_RECEPIENT_EMAILS,  mRecepientEmails);
                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.action_settings:
                Intent intentSendMessage = new Intent(this, ManagePartnersMain.class);
                startActivity(intentSendMessage);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }




}
