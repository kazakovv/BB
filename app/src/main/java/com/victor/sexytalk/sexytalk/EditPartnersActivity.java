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

    MenuItem checkButtonSendPartnerRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_partners);
        searchField = (EditText) findViewById(R.id.searchField);
        searchButton = (Button) findViewById(R.id.searchButton);
        listWithFoundUsers = (ListView) findViewById(R.id.listFoundUsers);
        emptyMessage = (TextView) findViewById(R.id.emptyMessage);
        emptyMessage.setText(""); //za da ne izkarva saobshtenie ot nachalo

        listWithFoundUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listWithFoundUsers.setEmptyView(emptyMessage);


        if(Backendless.UserService.CurrentUser() != null) {
            currentUser = Backendless.UserService.CurrentUser();
        }
        //onClick Listener za search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mahame otmetkata ot menuto za izprashtane na partner request
                checkButtonSendPartnerRequest.setVisible(false);
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
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        EditPartnersActivity.this,
                                        android.R.layout.simple_list_item_checked,
                                        userName
                                );
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
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_partners, menu);

        //tova e checkButton ot menuto, koito izprashta partner request
        //Predi da izberem pone edin chovek ot spisaka toi ne se vizda
        checkButtonSendPartnerRequest = (MenuItem) menu.findItem(R.id.action_send_partner_request);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_partner_request) {


            if(selectedUsers.size()>0) {

                int receiverNumber = selectedUsers.get(0);
                BackendlessUser receiverBackendless = foundUsers.get(receiverNumber);
                String receiverID = receiverBackendless.getObjectId();

                Backendless.Messaging.publish(receiverID,"partnerRequest",new AsyncCallback<MessageStatus>() {
                    @Override
                    public void handleResponse(MessageStatus messageStatus) {
                        Log.d("Vic","sent");

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.d("Vic","not sent");

                    }
                });


                /*
                //zatvariame prozoreca i se vrashtame kam main activity
                finish();

                //izprashtame request da si stanem partniori
                PartnersAddRequest partnersToAdd = new PartnersAddRequest();
                for(int i = 0; i < selectedUsers.size(); i++) {
                    //selectedUsers sadarza poziciite na otbeliazanite partniori,
                    // koito iskame da dobavim ot vsichki foundUsers

                    int selectedUser = selectedUsers.get(i); //dava poziciata ot foundUsers
                    BackendlessUser selectedPartner = foundUsers.get(selectedUser);
                    partnersToAdd.setEmail_partnerToConfirm(selectedPartner.getEmail());
                    partnersToAdd.setEmail_userRequesting(currentUser.getEmail());
                    partnersToAdd.setPartnerAddRequestConfirmed(false);
                    partnersToAdd.setPartnerToConfirm(selectedPartner);
                    partnersToAdd.setUserRequesting(currentUser);
                }

                //Kachvame zaiavkata v Backendless

                Backendless.Data.of(PartnersAddRequest.class).save(partnersToAdd, new AsyncCallback<PartnersAddRequest>() {
                    @Override
                    public void handleResponse(PartnersAddRequest partnersAddRequest) {
                       Toast.makeText(EditPartnersActivity.this,
                               R.string.partner_request_sent_toast,Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(EditPartnersActivity.this,
                                R.string.partner_request_not_sent_toast,Toast.LENGTH_LONG).show();
                    }
                });

            } else {
            //ako niama izbrani potrebiteli samo zatvariame prozoreca
                finish();
                */
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
