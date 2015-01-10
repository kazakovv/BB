package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushPolicyEnum;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.List;

/*

Tuk tarsim partniorite

 */

public class EditPartnersActivity extends Activity {
    protected EditText searchField;
    protected Button searchButton;
    protected ListView listWithFoundUsers;
    protected TextView emptyMessage;
    protected List<BackendlessUser> foundUsers;
    protected ArrayList<Integer> selectedUsers;
    protected BackendlessUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_partners);
        searchField = (EditText) findViewById(R.id.searchField);
        searchButton = (Button) findViewById(R.id.searchButton);
        listWithFoundUsers = (ListView) findViewById(R.id.listFoundUsers);
        emptyMessage = (TextView) findViewById(R.id.emptyMessage);
        emptyMessage.setText(""); //za da ne izkarva saobshtenie ot nachalo
        listWithFoundUsers.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listWithFoundUsers.setEmptyView(emptyMessage);


        if(Backendless.UserService.CurrentUser() != null) {
            currentUser = Backendless.UserService.CurrentUser();
        }
        //onClick Listener za search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //inicializirame array i po tozi nachin iztrivame predhodnite soinosti,
                // ot predishni tarsenia ako ima takiva
                selectedUsers = new ArrayList<Integer>();

                String textToSearch = searchField.getText().toString();
                if(!textToSearch.equals("")) {
                    //TODO: izkarva rezultati po niakolko potati. Tr da se opravi kriteriat
                    String whereClause = "email LIKE'%" + textToSearch + "%'";
                    BackendlessDataQuery query = new BackendlessDataQuery();
                    query.setWhereClause(whereClause);

                    Backendless.Data.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<BackendlessUser> users) {
                            //Sazdavame spisak s namerenite potrebiteli

                            foundUsers = users.getData();
                            int numberOfUsersFound  = foundUsers.size();

                            if(numberOfUsersFound > 0) {
                                //BackendlessUser foundUser = users.getCurrentPage().get(0);
                                String[] userName = new String[numberOfUsersFound];
                                for (int i = 0; i < numberOfUsersFound; i++) {
                                    userName[i] = (String) foundUsers.get(0).getProperty(Statics.KEY_USERNAME);
                                }

                                /*
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        EditPartnersActivity.this,
                                        android.R.layout.simple_list_item_checked,
                                        userName
                                );*/
                                PartnersAdapter adapter = new PartnersAdapter(listWithFoundUsers.getContext(),foundUsers);
                                listWithFoundUsers.setAdapter(adapter);

                            } else { //zatvariame check dali sme namerili neshto
                                //izchistvame spisaka, ako ne e namereno nishto
                                emptyMessage.setText(R.string.no_partners_found);//gore go zadadohme da e prazno
                                listWithFoundUsers.setAdapter(null);
                                listWithFoundUsers.setEmptyView(emptyMessage);
                            }
                        }
                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(EditPartnersActivity.this, R.string.general_server_error,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else { //zatrvariame check dali search field e prazno
                    //display message che search e prazen
                    Toast.makeText(EditPartnersActivity.this, R.string.empty_search_field_message,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        //OnClick listener za cakane varhu rezultatite v spisaka
        listWithFoundUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*
                CheckedTextView item = (CheckedTextView) view;
                if(item.isChecked()) {
                    selectedUsers.add(position);
                     checkButtonSendPartnerRequest.setVisible(true);
                } else {
                    int positionToRemove = selectedUsers.indexOf(position);
                    selectedUsers.remove(positionToRemove);

                    //pokazvame ili skrivame butona ot menuto za izprashtane na partner request
                    if(selectedUsers.size()>0) {
                        checkButtonSendPartnerRequest.setVisible(true);
                    } else {
                        checkButtonSendPartnerRequest.setVisible(false);
                    }
                }
                */
            }
        });
    }

    protected void sendPartnerRequest(int selectedPartnerPosition) {
        //Tozi metod se izvikva ot PartnersAdapter i  izprashta partner request


        //Ako caknem na add ot list se sluchvat 2 neshta chrez 2 async tasks edna v druga
        //1. Kazchavame data table s user request
        //2. Izprashtame push message, che ima pending partner request na saotvetnia user

            //zatvariame prozoreca i se vrashtame kam main activity
            finish();

            final BackendlessUser selectedPartner = foundUsers.get(selectedPartnerPosition);

            //izprashtame request da si stanem partniori
            PartnersAddRequest partnerToAdd = new PartnersAddRequest();
            partnerToAdd.setEmail_partnerToConfirm(selectedPartner.getEmail());
            partnerToAdd.setEmail_userRequesting(currentUser.getEmail());
            partnerToAdd.setPartnerAddRequestConfirmed(false);
            partnerToAdd.setPartnerToConfirm(selectedPartner);
            partnerToAdd.setUserRequesting(currentUser);

            //Kachvame zaiavkata v Backendless

            Backendless.Data.of(PartnersAddRequest.class).save(partnerToAdd, new AsyncCallback<PartnersAddRequest>() {
                @Override
                public void handleResponse(PartnersAddRequest partnersAddRequest) {
                    //sled kato kachim data v backendless izprashtame i push

                    //tova e za kanala, po koito da izpratim push message
                    String receiverID = selectedPartner.getObjectId();

                    Backendless.Messaging.publish(receiverID,Statics.KEY_PARTNER_REQUEST,new AsyncCallback<MessageStatus>() {
                        @Override
                        public void handleResponse(MessageStatus messageStatus) {
                            Toast.makeText(EditPartnersActivity.this,
                                    R.string.partner_request_sent_toast,Toast.LENGTH_LONG).show();                            }
                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            //TODO:tr da se promeni saobshtenieto. Izpratili sme tablicata, no ne push message
                            Toast.makeText(EditPartnersActivity.this,
                                    R.string.partner_request_not_sent_toast,Toast.LENGTH_LONG).show();                            }
                    });
                }
                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Toast.makeText(EditPartnersActivity.this,
                            R.string.partner_request_not_sent_toast,Toast.LENGTH_LONG).show();
                }
            });

        }


}
