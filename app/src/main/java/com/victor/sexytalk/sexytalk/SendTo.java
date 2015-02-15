package com.victor.sexytalk.sexytalk;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.Adaptors.AdapterSendTo;


import java.util.ArrayList;


public class SendTo extends ActionBarActivity {

    protected Toolbar toolbar;
    protected FragmentSendTo fragment;
    protected ListView mListView;
    protected BackendlessUser mCurrentUser;
    protected Context mContext;
    //izpolzva se za check dali e message, a ne kiss. Ako e message, mozhe da se izbere samo 1 poluchatel
    protected static boolean isTextMessage;

    protected static MenuItem sendOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment = new FragmentSendTo();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

        //Promenliva dali e izvikano ot text message.
        //Ako izprashtame text message tr da ima samo 1 poluchatel, inache ne raboti otbroiavaneto za 24h ot otvariane
        isTextMessage = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isTextMessage = extras.getBoolean(Statics.TYPE_TEXTMESSAGE);
        }

        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }
        mContext = SendTo.this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_to, menu);
        sendOk = menu.findItem(R.id.action_ok);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_ok:

                Intent intent = new Intent(SendTo.this, SendMessage.class);

                intent.putStringArrayListExtra(Statics.KEY_USERNAME, fragment.mRecepientUserNames);
                intent.putStringArrayListExtra(Statics.KEY_RECEPIENT_EMAILS, fragment.mRecepientEmails);
                intent.putStringArrayListExtra(Statics.KEY_DEVICE_ID, fragment.mDeviceIds);
                setResult(RESULT_OK, intent);
                finish();

                return true;
            case R.id.action_refresh:
                //tarsim dali ima dobaveni partniori, koito ne izlizat v spisaka
                mListView = fragment.getListView();
                final TextView emptyMessage = fragment.mEmptyMessage;
                String whereClause = "email='" + mCurrentUser.getEmail() + "'";
                BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                dataQuery.setWhereClause(whereClause);
                //
                String message = mContext.getResources().getString(R.string.searching);
                Backendless.Data.of(BackendlessUser.class).find(dataQuery,
                        new DefaultCallback<BackendlessCollection<BackendlessUser>>(mContext,message) {
                    @Override
                    public void handleResponse(BackendlessCollection<BackendlessUser> partners) {
                        super.handleResponse(partners);
                      if  (partners.getCurrentPage().get(0).getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                          //updatevame adaptora s partniorite
                            BackendlessUser[] newPartners = (BackendlessUser[]) partners.getCurrentPage().get(0).getProperty(Statics.KEY_PARTNERS);
                          AdapterSendTo adapter = new AdapterSendTo(mContext, newPartners, mCurrentUser);
                          mListView.setEmptyView(emptyMessage);
                          fragment.mPartners = newPartners;
                          mListView.setOnItemClickListener(fragment.onItemClickList);
                          mListView.setAdapter(adapter);
                      } else {
                        //niama namereni partniori
                          Toast.makeText(mContext,R.string.no_partners_found,Toast.LENGTH_LONG).show();
                      }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        super.handleFault(backendlessFault);
                        Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                    }
                });
                return true;

            case R.id.action_settings:
                Intent intentSendMessage = new Intent(this, ManagePartnersMain.class);
                startActivity(intentSendMessage);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

        /*
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        NACHALO NA FRAGMENTA S LIST
        !!!!!!!!!!!!!!!!!!!!!!!!!!!
         */
    public static class FragmentSendTo extends ListFragment {
        protected BackendlessUser[] mPartners;
        protected BackendlessUser mCurrentUser;
        protected ArrayList<Integer> mSendTo;
        protected ArrayList<String> mRecepientUserNames;
        protected ArrayList<String> mRecepientEmails;
        protected ArrayList<String> mDeviceIds;
        protected TextView mEmptyMessage;
        protected Toolbar toolbar;

        protected ListView mSendToList;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View inflatedView = inflater.inflate(R.layout.activity_send_to, container, false);
            toolbar = (Toolbar) inflatedView.findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
            mEmptyMessage = (TextView) inflatedView.findViewById(R.id.emptyMessageSendTo);

            ((SendTo) getActivity()).setSupportActionBar(toolbar);

            return inflatedView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mSendToList = getListView();

            //vrazvam mCurrentUser i list
            if (Backendless.UserService.CurrentUser() != null) {
                mCurrentUser = Backendless.UserService.CurrentUser();


                // specify an adapter (see also next example)
                //samo ako ima partniori
                if (mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                    mEmptyMessage.setVisibility(View.INVISIBLE);
                    mPartners = (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
                    AdapterSendTo adapter = new AdapterSendTo(getActivity(), mPartners, mCurrentUser);
                    mSendToList.setEmptyView(mEmptyMessage);

                    mSendToList.setOnItemClickListener(onItemClickList);
                    mSendToList.setAdapter(adapter);
                }
            }

            //inicializirame arraylists, za da mozem da dobaviame info kam tiah
            mRecepientEmails = new ArrayList<String>();
            mSendTo = new ArrayList<Integer>();
            mRecepientUserNames = new ArrayList<String>();
            mDeviceIds = new ArrayList<String>();
        }

        //onItem click listener za list
        protected AdapterView.OnItemClickListener onItemClickList = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox sendYesNoCheckBox = (CheckBox) view.findViewById(R.id.sendYesNo);

                if (sendYesNoCheckBox.isChecked()) {
                    //cakane v/u ceche izbran partior
                    sendYesNoCheckBox.setChecked(false);
                    int positionToRemove = mSendTo.indexOf(position);
                    mSendTo.remove(positionToRemove);
                    mRecepientEmails.remove(positionToRemove);
                    mRecepientUserNames.remove(positionToRemove);
                    mDeviceIds.remove(positionToRemove);
                    //pokazvame ili skrivame ok check, v zavisimost dali ima izbrani partniori
                    if (mSendTo.size() == 0) {
                        sendOk.setVisible(false);
                    } else {
                        sendOk.setVisible(true);
                    }
                } else {
                    //cakame v/u partior, koito ne e izbran oshte

                    //check kolko partionri sme izbrali. Ako e izbran poveche ot 1 partnior, otbroiavaneto za 24chasa ne raboti
                    //parviat, koito otvori sabshtenieto startira broiacha
                    if (isTextMessage == true) {
                        if (mSendTo.size() > 0) {
                            Toast.makeText(getActivity(), R.string.love_message_more_than_1_recipient, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    sendYesNoCheckBox.setChecked(true);
                    mSendTo.add(position);
                    mRecepientEmails.add(mPartners[position].getEmail());
                    mRecepientUserNames.add((String) mPartners[position].getProperty(Statics.KEY_USERNAME));
                    mDeviceIds.add((String) mPartners[position].getProperty(Statics.KEY_DEVICE_ID));

                    //pokazvame ili skrivame ok check, v zavisimost dali ima izbrani partniori
                    if (mSendTo.size() == 0) {
                        sendOk.setVisible(false);
                    } else {
                        sendOk.setVisible(true);
                    }
                }

            }
        };//krai on onItemClickListener

    } //Krai na fragment

}//krai na actionba activity
