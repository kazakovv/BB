package com.victor.sexytalk.sexytalk.UserInterfaces;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.backendless.persistence.QueryOptions;
import com.victor.sexytalk.sexytalk.Adaptors.AdapterPartnerRequests;
import com.victor.sexytalk.sexytalk.BackendlessClasses.PartnersAddRequest;
import com.victor.sexytalk.sexytalk.R;

import java.util.List;


/**
 *
 */
public class FragmentPartnerRequests extends ListFragment {
    protected BackendlessUser mCurrentUser;
    protected List<PartnersAddRequest> mPendingPartnerRequests;
    protected ListView mPendingPartnersRequestList;
    protected TextView emptyMessage;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected SwipeRefreshLayout mSwipeRefreshEmptyMessage;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_fragment_partner_requests,container,false);
        emptyMessage = (TextView) inflatedView.findViewById(android.R.id.empty);

        mSwipeRefreshLayout = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeRefreshLayoutPartnerRequests);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mSwipeRefreshEmptyMessage = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeRefreshLayout_emptyView);
        mSwipeRefreshEmptyMessage.setOnRefreshListener(mOnRefreshListener);
        return inflatedView;
    }
    //refresh listener za swipe refresh layout
    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(mCurrentUser != null) {
                checkForPendingPartnerRequests();
            }
        }
    };


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPendingPartnersRequestList = getListView();
        mPendingPartnersRequestList.setOnScrollListener(mOnScrollListener);
        //mPendingPartnersRequestList.setEmptyView(emptyMessage);

        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
            checkForPendingPartnerRequests();
        }

    }
    //on scroll listener za list view
    protected ListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {


        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            boolean enable = false;

            if(mPendingPartnersRequestList != null && mPendingPartnersRequestList.getChildCount() > 0){
                // check if the first item of the list is visible
                boolean firstItemVisible = mPendingPartnersRequestList.getFirstVisiblePosition() == 0;
                // check if the top of the first item is visible
                boolean topOfFirstItemVisible = mPendingPartnersRequestList.getChildAt(0).getTop() == 0;
                // enabling or disabling the refresh layout
                enable = firstItemVisible && topOfFirstItemVisible;
            }
            mSwipeRefreshLayout.setEnabled(enable);



        }


    };
    protected void checkForPendingPartnerRequests() {
        //proveriavame dali ima pending partner requests
        //v zavisimost ot tova dali sme drapnali empty message ili veche e imalo neshto v spisaka
        //puskame saotvetnia swipe to refresh

        String whereClause="email_partnerToConfirm='" + mCurrentUser.getEmail() + "'";
        BackendlessDataQuery query = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addRelated( "userRequesting" );
        queryOptions.addRelated( "userRequesting.RELATION-OF-RELATION" );
        query.setWhereClause(whereClause);
        Backendless.Data.of(PartnersAddRequest.class).find(query, new AsyncCallback<BackendlessCollection<PartnersAddRequest>>() {
            @Override
            public void handleResponse(BackendlessCollection<PartnersAddRequest> pendingPartnerRequests) {
                //spirame vratkata ako se refreshva swipe refresh
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if(mSwipeRefreshEmptyMessage.isRefreshing()){
                    mSwipeRefreshEmptyMessage.setRefreshing(false);
                }

                if(pendingPartnerRequests.getData().size()>0) {
                    mPendingPartnerRequests = pendingPartnerRequests.getData();
                    AdapterPartnerRequests adapter =
                            new AdapterPartnerRequests(mPendingPartnersRequestList.getContext(),mPendingPartnerRequests, mCurrentUser);
                    mPendingPartnersRequestList.setAdapter(adapter);
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //spirame vratkata ako se refreshva swipe refresh
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if(mSwipeRefreshEmptyMessage.isRefreshing()){
                    mSwipeRefreshEmptyMessage.setRefreshing(false);
                }
                Log.d("Vic","error" + backendlessFault.getMessage() );
                Toast.makeText(getActivity(), R.string.general_server_error,Toast.LENGTH_LONG).show();
            }
        });

    }
}
