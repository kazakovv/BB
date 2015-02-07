package com.victor.sexytalk.sexytalk;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.victor.sexytalk.sexytalk.Adaptors.AdapterMessage;
import com.victor.sexytalk.sexytalk.BackendlessClasses.Messages;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentLoveBox extends ListFragment {
   protected List<Messages> messagesToDisplay;
   protected View myView;
   protected SwipeRefreshLayout mSwipeRefreshLayout;
   protected BackendlessUser currentUser;
   protected ListView mListView;
   protected TextView mEmptyMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_love_box, container, false);
        mEmptyMessage = (TextView) rootView.findViewById(R.id.empty_message);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        return rootView;
    }
    //refresh listener za updatevane na tova dali ima novi saobstehnia
    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            searchForMessages();
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myView = getListView();
        mListView = getListView();
        mListView.setEmptyView(mEmptyMessage);//TODO ne pokazva praznoto saobstehnie
        mListView.setOnScrollListener(mOnScrollListener);
    }
    //on scroll listener za list view
    protected ListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            boolean enable = false;
            if(mListView != null && mListView.getChildCount() > 0){
                // check if the first item of the list is visible
                boolean firstItemVisible = mListView.getFirstVisiblePosition() == 0;
                // check if the top of the first item is visible
                boolean topOfFirstItemVisible = mListView.getChildAt(0).getTop() == 0;
                // enabling or disabling the refresh layout
                enable = firstItemVisible && topOfFirstItemVisible;
            }
            mSwipeRefreshLayout.setEnabled(enable);



        }


    };


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set up the toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ( (Main)getActivity()).setSupportActionBar(toolbar);

        if(Backendless.UserService.CurrentUser() != null) {
            currentUser = Backendless.UserService.CurrentUser();
            searchForMessages();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


        Messages message = messagesToDisplay.get(position);

        String messageType = message.getMessageType();
        String loveMessage =  message.getLoveMessage();
        String usernameSender = message.getSenderUsername();

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
            intent.putExtra(Statics.KEY_USERNAME_SENDER, usernameSender);
            //zadavame che sme otvorili saobshtenieto, ako ne e bilo otvariano predi
            if(message.getOpened() == null) {
                Calendar c = Calendar.getInstance();
                message.setOpened(c.getTime());

                Backendless.Data.of(Messages.class).save(message, new AsyncCallback<Messages>() {
                    @Override
                    public void handleResponse(Messages messages) {
                        //niama nuzda da pravim nishto
                        Log.d("Vic","v");
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //niama nuzhda da pravim nishto.
                        String error = backendlessFault.getMessage();
                        Log.d("Vic","v");

                    }
                });
            }
            startActivity(intent);


        } else if (messageType.equals(Statics.TYPE_TEXTMESSAGE)){
            //ako e text go otvariame v sashtotia view kato image
            Intent intent = new Intent(getActivity(), ViewTextMessageActivity.class);
            intent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            intent.putExtra(Statics.KEY_USERNAME_SENDER, usernameSender);
            //zadavame che sme otvorili saobshtenieto, ako ne e bilo otvariano predi
            if(message.getOpened() == null) {
                Calendar c = Calendar.getInstance();
                message.setOpened(c.getTime());

                Backendless.Data.of(Messages.class).save(message, new AsyncCallback<Messages>() {
                    @Override
                    public void handleResponse(Messages messages) {
                        //niama nuzda da pravim nishto
                        Log.d("Vic","v");

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //niama nuzhda da pravim nishto.
                        String error = backendlessFault.getMessage();
                        Log.d("Vic","v");
                    }
                });
            }
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


    }

   //Tozi metod se vrazva kam backendless da vidi dali imame saobsthenia
protected void searchForMessages(){

    String whereClause = "recepientEmails LIKE '%" + currentUser.getEmail() + "%'";

    BackendlessDataQuery query = new BackendlessDataQuery();
    QueryOptions queryOptions = new QueryOptions();
    query.setWhereClause(whereClause);
    queryOptions.setPageSize(50); //max saobshtenia koito shte izlizat. Ako ima poveche se gubiat. Max tuk e 100
    //TODO: Eventualno, ako dobavia relations kato users poluchaeli moga da tarsia i po tozi kriterii
    query.setQueryOptions( queryOptions );

    Backendless.Data.of(Messages.class).find(query, new AsyncCallback<BackendlessCollection<Messages>>() {

        @Override
        public void handleResponse(BackendlessCollection<Messages> messages) {

            //ako sme drapnali swipe to refresh prekratiavame refreshvaneto
            if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
            }
            messagesToDisplay = new ArrayList<Messages>();
            if(messages.getData().size() > 0) {
                messagesToDisplay = messages.getData();
                //proveriavame dali ima saobshtenia po-stari ot 24 chasa i gi iztrivame
                checkIfMessagesOlderThan24Hours();
            }

            //proveriavame dali ima saobshtenia po-stari ot 24 chasa i gi iztrivame

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
            //ako sme drapnali swipe to refresh prekratiavame refreshvaneto
            if(mSwipeRefreshLayout.isRefreshing()){
                mSwipeRefreshLayout.setRefreshing(false);
            }

            Toast.makeText(getListView().getContext(),R.string.general_server_error,Toast.LENGTH_LONG).show();
        }
    });

}

    private void checkIfMessagesOlderThan24Hours() {
        int timeToDisplayMessage = Statics.MESSAGE_TIME_TO_DISPLAY;

        Calendar c = Calendar.getInstance();
        Date now = c.getTime();


        for(int i = 0; i< messagesToDisplay.size();i++) {
           Messages message = messagesToDisplay.get(i);
            if(message.getOpened() != null) { //ako ne e null, znachi veche e otvariano
                Date firstOpened = message.getOpened();
                long diff = (now.getTime() - firstOpened.getTime());
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;

                if( (timeToDisplayMessage - hours) <= 0 ) {
                    //iztrivame go
                    messagesToDisplay.remove(i);
                    Backendless.Data.of(Messages.class).remove(message, new AsyncCallback<Long>() {
                        @Override
                        public void handleResponse(Long aLong) {
                            //niama nuzda da pravim nishto, nai-mnogo da go vodim pak
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            //niama nuzda da pravim nishto, nai-mnogo da go vodim pak
                        }
                    });
                }
            }//krai na if statement

        }//krai na for statement

    }//krai na checkIfMessagesOlderThan24Hours

}
