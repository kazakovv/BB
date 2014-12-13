package com.victor.sexytalk.sexytalk;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.List;


public class SendTo extends ListActivity {
    protected BackendlessUser[] mPartners;
    protected ArrayList<Integer> mSendTo;
    protected ArrayList<String> mRecepientUserNames;
    protected ArrayList<String> mRecepientEmails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inicializirame arraylists, za da mozem da dobaviame info kam tiah
        mRecepientEmails = new ArrayList<String>();
        mSendTo = new ArrayList<Integer>();
        mRecepientUserNames = new ArrayList<String>();

        //namirame partniorite i zapalvame spisaka
        findPartners();
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_to, menu);
        return super.onCreateOptionsMenu(menu);

    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_ok:
                Intent intent = new Intent(SendTo.this, SendMessage.class);

                intent.putStringArrayListExtra(Statics.KEY_USERNAME,mRecepientUserNames);
                intent.putStringArrayListExtra(Statics.KEY_RECEPIENT_EMAILS,  mRecepientEmails);
                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.action_settings:
                Intent intentSendMessage = new Intent(this, EditPartnersActivity.class);
                startActivity(intentSendMessage);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void findPartners() {

        BackendlessUser currentUser = Backendless.UserService.CurrentUser();

        String whereClause = "email='" + currentUser.getEmail() + "'";

        BackendlessDataQuery query = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        query.setWhereClause(whereClause);
        queryOptions.addRelated( "partners" );
        queryOptions.addRelated( "partners.RELATION-OF-RELATION" );
        query.setQueryOptions( queryOptions );


         Backendless.Data.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
             @Override
             public void handleResponse(BackendlessCollection<BackendlessUser> collection) {
                 if (collection.getCurrentPage().isEmpty()) {
                     //no partners
                     AlertDialog.Builder builder = new AlertDialog.Builder(SendTo.this);
                     builder.setTitle(R.string.error_title)
                             .setMessage(R.string.general_error_message)
                             .setPositiveButton(R.string.ok, null);
                     AlertDialog dialog = builder.create();
                     dialog.show();
                 } else {
                     //create list of objects
                     BackendlessUser user = collection.getCurrentPage().get(0);


                         mPartners = (BackendlessUser[]) user.getProperty(Statics.KEY_PARTNERS);

                         //Crashes if there are no partners!!!!!
                         // needs A FIX

                         int numberOfPartners = mPartners.length;

                         String[] usernames = new String[numberOfPartners];
                         int i = 0;


                         for (BackendlessUser partner : mPartners) {
                             usernames[i] = (String) partner.getProperty(Statics.KEY_USERNAME);
                             i++;
                         }

                         ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                 SendTo.this,
                                 android.R.layout.simple_list_item_checked,
                                 usernames
                         );
                         setListAdapter(adapter);
                 }


             }

             @Override
             public void handleFault(BackendlessFault backendlessFault) {
                 Log.e("Vic", backendlessFault.getMessage());
                 AlertDialog.Builder builder = new AlertDialog.Builder(SendTo.this);
                 builder.setTitle(R.string.error_title)
                         .setMessage(R.string.general_error_message)
                         .setPositiveButton(R.string.ok, null);
                 AlertDialog dialog = builder.create();
                 dialog.show();
             }
         });

    }


}
