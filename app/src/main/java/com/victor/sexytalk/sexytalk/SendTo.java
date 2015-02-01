package com.victor.sexytalk.sexytalk;


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
import com.backendless.BackendlessUser;
import com.victor.sexytalk.sexytalk.Adaptors.AdapterSendTo;


import java.util.ArrayList;



public class SendTo extends ActionBarActivity  {

    protected Toolbar toolbar;
    protected FragmentSendTo fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment = new FragmentSendTo();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_to, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_ok:

                Intent intent = new Intent(SendTo.this, SendMessage.class);

                intent.putStringArrayListExtra(Statics.KEY_USERNAME, fragment.mRecepientUserNames);
                intent.putStringArrayListExtra(Statics.KEY_RECEPIENT_EMAILS,fragment.mRecepientEmails);
                intent.putStringArrayListExtra(Statics.KEY_DEVICE_ID, fragment.mDeviceIds);
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
            if( mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                mEmptyMessage.setVisibility(View.INVISIBLE);
                mPartners = (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
                AdapterSendTo adapter = new AdapterSendTo(getActivity(),mPartners, mCurrentUser);
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

        if(sendYesNoCheckBox.isChecked()) {
            sendYesNoCheckBox.setChecked(false);
            int positionToRemove = mSendTo.indexOf(position);
            mSendTo.remove(positionToRemove);
            mRecepientEmails.remove(positionToRemove);
            mRecepientUserNames.remove(positionToRemove);
            mDeviceIds.remove(positionToRemove);
        } else {
            sendYesNoCheckBox.setChecked(true);
            mSendTo.add(position);
            mRecepientEmails.add(mPartners[position].getEmail());
            mRecepientUserNames.add((String) mPartners[position].getProperty(Statics.KEY_USERNAME));
            mDeviceIds.add((String) mPartners[position].getProperty(Statics.KEY_DEVICE_ID));
        }

    }
    };//krai on onItemClickListener

} //Krai na fragment

}//krai na actionba activity
