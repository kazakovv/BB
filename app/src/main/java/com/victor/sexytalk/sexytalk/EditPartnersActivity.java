package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.List;


public class EditPartnersActivity extends Activity {
    protected EditText searchField;
    protected Button searchButton;
    protected ListView listWithFoundUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_partners);
        searchField = (EditText) findViewById(R.id.searchField);
        searchButton = (Button) findViewById(R.id.searchButton);
        listWithFoundUsers = (ListView) findViewById(R.id.listFoundUsers);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_partners, menu);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToSearch = searchField.getText().toString();
                if(!textToSearch.equals("")) {
                    String whereClause = "email='" + textToSearch + "'";
                    BackendlessDataQuery query = new BackendlessDataQuery();
                    query.setWhereClause(whereClause);

                    Backendless.Data.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<BackendlessUser> users) {
                         //Sazdavame spisak s namerenite potrebiteli

                            List<BackendlessUser> foundUsers = users.getData();
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
                            //TODO:tr da se napishe neshto, ako niama nishto namereno
                            //TODO: da se izchisti spisaka, ako v nego veche ima neshot, etc.

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

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
