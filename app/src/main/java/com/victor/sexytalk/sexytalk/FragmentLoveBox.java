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
            /*
            //view image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            //intent.setData(fileUri);
            intent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);
            */
        } else if (messageType.equals(Statics.TYPE_TEXTMESSAGE)){
            //ako e text go otvariame v sashtotia view kato image
            Intent intent = new Intent(getActivity(), ViewTextMessageActivity.class);
            intent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);

        } else  {
            /*
            //view video
            Intent intent = new Intent(getActivity(),ViewMovieActivity.class);
            //intent.setData(fileUrl);
            intent.putExtra(Statics.KEY_LOVE_MESSAGE,loveMessage);
            startActivity(intent);
            */

        }
    }

    /*
    public static final String TAG = FragmentChat.class.getSimpleName();

    protected List<ParseObject> mMessages;

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
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        //tarsim dali imame polucheni saobshtenia
        if(ParseUser.getCurrentUser() !=null) {
            //getActivity().setProgressBarIndeterminateVisibility(true);

            ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.CLASS_MESSAGES);
            query.whereEqualTo(ParseConstants.KEY_RECEPIENT_IDS, ParseUser.getCurrentUser()
                    .getObjectId());
            query.addAscendingOrder(ParseConstants.KEY_CREATEDAT);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> messages, ParseException e) {
                 //   getActivity().setProgressBarIndeterminateVisibility(false);


                    if (e == null) {

                        //sucessful!
                        mMessages = messages;

                        String[] usernames = new String[mMessages.size()];
                        int i = 0;

                        for (ParseObject message : mMessages) {
                          usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                            i++;
                        }
                        //sortirame mMessages, taka che poslednite saobshtenia da izlizat parvi

                        Collections.sort(mMessages,new Comparator<ParseObject>() {
                            @Override
                            public int compare(ParseObject o1, ParseObject o2) {
                                return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                            }
                        });
                        //Tova e po-gotinia ni ArrayAdaptor s kartinka v zavisimost ot tipa na file

                        MessageAdapter adapter = new MessageAdapter(myView.getContext(),
                                mMessages);

                        setListAdapter(adapter);

                        //sazdavame adapter, ako list se niama takav. Naprimer, ako se
                        // sazdava za prav pat


                    } else {

                        Log.e(TAG, e.getMessage());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                        builder.setTitle(R.string.error_title)
                                .setMessage(R.string.general_error_message)
                                .setPositiveButton(R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });


        }//zatvariam check dali parsuser ne e null


    }




    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //otvariame saobshteniata i gi gledame
        /*
        ParseObject message = mMessages.get(position);
        String messageType = (String) message.get(ParseConstants.KEY_FILE_TYPE);
        String loveMessage = (String) message.get(ParseConstants.KEY_LOVE_MESSAGE);

        ParseFile file;
        Uri fileUri = null;

        //ako saobshtenieto ne e text, zapisvame reference kam file
        if(!messageType.equals(ParseConstants.TYPE_TEXTMESSAGE) ) {
            file = message.getParseFile(ParseConstants.KEY_FILE);
            fileUri = Uri.parse(file.getUrl());

        }
        if(messageType.equals(ParseConstants.TYPE_IMAGE)) {
            //view image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            intent.putExtra(ParseConstants.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);
        } else if (messageType.equals(ParseConstants.TYPE_TEXTMESSAGE)){
            //ako e text go otvariame v sashtotia view kato image
            Intent intent = new Intent(getActivity(), ViewTextMessageActivity.class);
            intent.putExtra(ParseConstants.KEY_LOVE_MESSAGE, loveMessage);
            startActivity(intent);

        } else  {
        //view video
            Intent intent = new Intent(getActivity(),ViewMovieActivity.class);
            intent.setData(fileUri);
            intent.putExtra(ParseConstants.KEY_LOVE_MESSAGE,loveMessage);
            startActivity(intent);


        }

    }
    */
}
