package com.victor.sexytalk.sexytalk.UserInterfaces;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.Adaptors.AdapterSearchPartners;
import com.victor.sexytalk.sexytalk.CustomDialogs.SendEmailInvitation;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.ArrayList;
import java.util.List;


/**
 */
public class FragmentSearchPartners extends ListFragment {
    protected EditText emailSearchField;
    protected ImageButton searchButton;
    protected TextView emptyMessage;
    protected List<BackendlessUser> foundUsers;
    protected ArrayList<Integer> selectedUsers;
    protected BackendlessUser currentUser;
    protected ListView listWithFoundUsers;
    protected ProgressBar progressBar;
    protected Context mContext;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View inflatedView = inflater.inflate(R.layout.fragment_fragment_search_partners, container, false);
        //vrazvame promenlivite
        mContext = inflater.getContext();
        emailSearchField = (EditText) inflatedView.findViewById(R.id.searchField);
        searchButton = (ImageButton) inflatedView.findViewById(R.id.searchButton);
        emptyMessage = (TextView) inflatedView.findViewById(R.id.emptyMessage);
        progressBar = (ProgressBar) inflatedView.findViewById(R.id.progressBar);
        emptyMessage.setText(""); //za da ne izkarva saobshtenie ot nachalo

        return inflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //vrazvame listview i zadavame niakoi stoinosti
        listWithFoundUsers = getListView();
        listWithFoundUsers.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listWithFoundUsers.setEmptyView(emptyMessage);
        //vrazvame current user
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

                String emailToSearch = emailSearchField.getText().toString();
                if(!emailToSearch.equals("")) { //check dali search field a prazno
                    //pokazvame spinner
                    searchButton.setEnabled(false);
                    listWithFoundUsers.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    String whereClause = "email='" + emailToSearch + "'";
                    BackendlessDataQuery query = new BackendlessDataQuery();
                    query.setWhereClause(whereClause);

                    Backendless.Data.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<BackendlessUser> users) {
                            //skrivame spinner
                            searchButton.setEnabled(true);
                            listWithFoundUsers.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);

                            //Sazdavame spisak s namerenite potrebiteli

                            foundUsers = users.getData();
                            int numberOfUsersFound  = foundUsers.size();

                            if(numberOfUsersFound > 0) {
                                //BackendlessUser foundUser = users.getCurrentPage().get(0);
                                String[] userName = new String[numberOfUsersFound];
                                for (int i = 0; i < numberOfUsersFound; i++) {
                                    userName[i] = (String) foundUsers.get(0).getProperty(Statics.KEY_USERNAME);
                                }

                                //prehvarliame current user kam adaptora.
                                //tam se izpalnaiva koda za dobaviane na partners kato caknem na butona
                                //current user e nuzen, za da izpratim info kam Backendless
                                AdapterSearchPartners adapter = new AdapterSearchPartners(listWithFoundUsers.getContext(),
                                        foundUsers,currentUser, getActivity());

                                listWithFoundUsers.setAdapter(adapter);


                            } else { //zatvariame check dali sme namerili neshto
                                //izchistvame spisaka, ako ne e namereno nishto

                                emptyMessage.setText(R.string.no_partners_found);//gore go zadadohme da e prazno
                                listWithFoundUsers.setAdapter(null);
                                listWithFoundUsers.setEmptyView(emptyMessage);

                                //izkarvame dialog box dali ne izkame da izpratim email s pokana
                                SendEmailInvitation sendEmailInvitation = new SendEmailInvitation();
                                Bundle dialogContent = new Bundle();
                                String emailToSend = emailSearchField.getText().toString();
                                dialogContent.putString(Statics.KEY_RECEPIENT_EMAILS,emailToSend);
                                sendEmailInvitation.setArguments(dialogContent);
                                sendEmailInvitation.show(getFragmentManager(),"tag_alert_dialog");


                                /*
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(R.string.dialog_invite_partner_join_sexytalk_title)
                                        .setMessage(R.string.dialog_message_partner_not_joined_sexytalk)
                                        .setNegativeButton(R.string.cancel, null)
                                        .setPositiveButton(R.string.ok, sendEmail);

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                */
                            }
                        }
                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            //skrivame spinner
                            searchButton.setEnabled(true);
                            listWithFoundUsers.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(getActivity(), R.string.general_server_error,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else { //zatrvariame check dali search field e prazno
                    //display message che search e prazen
                    Toast.makeText(getActivity(), R.string.empty_search_field_message,
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d("Vic","We are hare");

    }
/*
    DialogInterface.OnClickListener sendEmail = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String emailToSearch = emailSearchField.getText().toString();
            String subject = mContext.getResources().getString(R.string.email_invite_to_sexytalk_subject);
            String body = mContext.getResources().getString(R.string.email_invite_to_sexytalk_body);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailToSearch});
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT   , body);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, R.string.toast_no_email_clients_installed, Toast.LENGTH_SHORT).show();
            }
        }
    };
    */
}
