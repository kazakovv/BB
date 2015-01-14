package com.victor.sexytalk.sexytalk;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;


/**
 */
public class FragmentSearchPartners extends ListFragment {
    protected EditText searchField;
    protected Button searchButton;
    protected TextView emptyMessage;
    protected List<BackendlessUser> foundUsers;
    protected ArrayList<Integer> selectedUsers;
    protected BackendlessUser currentUser;
    protected ListView listWithFoundUsers;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View inflatedView = inflater.inflate(R.layout.fragment_fragment_search_partners, container, false);
        //vrazvame promenlivite
        searchField = (EditText) inflatedView.findViewById(R.id.searchField);
        searchButton = (Button) inflatedView.findViewById(R.id.searchButton);
        emptyMessage = (TextView) inflatedView.findViewById(R.id.emptyMessage);
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

                                //prehvarliame current user kam adaptora.
                                //tam se izpalnaiva koda za dobaviane na partners kato caknem na butona
                                //current user e nuzen, za da izpratim info kam Backendless
                                AdapterSearchPartners adapter = new AdapterSearchPartners(listWithFoundUsers.getContext(),
                                        foundUsers,currentUser);

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
}
