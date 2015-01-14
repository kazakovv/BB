package com.victor.sexytalk.sexytalk;



import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentLoveBox extends ListFragment {
    List<Messages> messagesToDisplay;
    View myView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_love_box, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myView = getListView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final BackendlessUser currentUser = Backendless.UserService.CurrentUser();
        if(currentUser != null) {
            String whereClause = "recepientEmails LIKE '%" + currentUser.getEmail() + "%'";

            BackendlessDataQuery query = new BackendlessDataQuery();
            QueryOptions queryOptions = new QueryOptions();
            query.setWhereClause(whereClause);
            //TODO: Eventualno, ako dobavia relations kato users poluchaeli moga da tarsia i po tozi kriterii
            query.setQueryOptions( queryOptions );

            Backendless.Data.of(Messages.class).find(query, new AsyncCallback<BackendlessCollection<Messages>>() {
                @Override
                public void handleResponse(BackendlessCollection<Messages> messages) {

                   messagesToDisplay = new ArrayList<Messages>();
                    int numberOfMesages = messages.getCurrentPage().size();

                    for (int i = 0; i <numberOfMesages; i++) {
                    messagesToDisplay.add(messages.getCurrentPage().get(i));
                        Log.d("Vic","one more added");
                    }


                    Collections.sort(messagesToDisplay,new Comparator<Messages>() {
                        @Override
                        public int compare(Messages o1, Messages o2) {

                            return o2.getCreated().compareTo(o1.getCreated());
                        }
                    });


                    AdapterMessage adapter = new AdapterMessage(myView.getContext(),
                            messagesToDisplay);

                    setListAdapter(adapter);
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setTitle(R.string.error_title)
                            .setMessage(R.string.general_error_message)
                            .setPositiveButton(R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


        Messages message = messagesToDisplay.get(position);

        String messageType = message.getMessageType();
        String loveMessage =  message.getLoveMessage();

        String fileUrl = "";

        //ako saobshtenieto ne e text, zapisvame link kam file
        if(!messageType.equals(Statics.TYPE_TEXTMESSAGE) ) {
           fileUrl = message.getMediaUrl();
        }

        if(messageType.equals(Statics.TYPE_IMAGE)) {

            //view image

            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.putExtra(Statics.KEY_URL, fileUrl);
            intent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);

        } else if (messageType.equals(Statics.TYPE_TEXTMESSAGE)){
            //ako e text go otvariame v sashtotia view kato image
            Intent intent = new Intent(getActivity(), ViewTextMessageActivity.class);
            intent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);

        } else if (messageType.equals(Statics.TYPE_KISS)) {
            //otvariame kiss message
            Intent intent = new Intent(getActivity(),ViewKissActivity.class);
            intent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);

            //delete the kiss
           String stringOfRecepients = message.getRecepientEmails();
           String[] emails = stringOfRecepients.split(",");
           int numberOfRecepients = emails.length;

            if(numberOfRecepients == 1) {
                Backendless.Persistence.of(Messages.class).remove(message, new AsyncCallback<Long>() {
                    @Override
                    public void handleResponse(Long aLong) {
                        //TODO: moze da dobavaia neshto tuk
                        Log.d("Vic","deletion suucess");
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //TODO: moze da dobavaia neshto tuk
                        Log.d("Vic", "deleted error" + backendlessFault.toString());
                    }
                });
            } else {
                String newStringOfRecepients="";
                String emailOfCurrentUser = Backendless.UserService.CurrentUser().getEmail();

                //ne iztrivame saobshtenieto, a samo mahame emaila na poluchatelia i zapazvame saobshtenieto
                //za celta sazdavame nov string s emails bez emaila na satovetnia poluchatel
                for(int i = 0; i < numberOfRecepients; i++) {
                    if(! emails[i].equals(emailOfCurrentUser) ) {
                        newStringOfRecepients += emails[i];
                        if(i<numberOfRecepients - 1) {
                        newStringOfRecepients +=","; //dobaviame zapetaia, ako ima oshte emaili
                        }
                    }
                }
                //updatevame message i go zapisvame v Backenless

                message.setRecepientEmails(newStringOfRecepients);
                Backendless.Persistence.save(message, new AsyncCallback<Messages>() {
                    @Override
                    public void handleResponse(Messages messages) {
                        //TODO: moze da dobavaia neshto tuk
                        Log.d("Vic","saved");
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //TODO: moze da dobavaia neshto tuk
                        Log.d("Vic","saved error" + backendlessFault.toString());


                    }
                });

            }
        }

        else  {

            //view video
            Intent intent = new Intent(getActivity(),ViewMovieActivity.class);
            intent.putExtra(Statics.KEY_URL,fileUrl);
            intent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);

        }
    }


}
