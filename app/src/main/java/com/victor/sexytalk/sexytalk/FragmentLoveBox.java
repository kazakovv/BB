package com.victor.sexytalk.sexytalk;



import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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
            queryOptions.addRelated( "recepients" );
            queryOptions.addRelated( "recepients.RELATION-OF-RELATION" );
            query.setQueryOptions( queryOptions );

            Backendless.Data.of(Messages.class).find(query, new AsyncCallback<BackendlessCollection<Messages>>() {
                @Override
                public void handleResponse(BackendlessCollection<Messages> messages) {
                  Log.d("Vic", "Found something");

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


                    MessageAdapter adapter = new MessageAdapter(myView.getContext(),
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

        } else  {

            //view video
            Intent intent = new Intent(getActivity(),ViewMovieActivity.class);
            intent.putExtra(Statics.KEY_URL,fileUrl);
            intent.putExtra(Statics.KEY_LOVE_MESSAGE,loveMessage);
            startActivity(intent);


        }
    }


}
