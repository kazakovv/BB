package com.victor.sexytalk.sexytalk.UserInterfaces;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.Adaptors.AdapterLoveDays;
import com.victor.sexytalk.sexytalk.Helper.BackendlessHelper;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentLoveDays extends Fragment {
    protected RecyclerView loveDaysCards;
    protected Context mContext;
    protected BackendlessUser mCurrentUser;

    protected ProgressBar mProgressBar;
    protected RelativeLayout mFragmentLoveDaysLayout;
    protected SwipeRefreshLayout mSwipeToRefreshLayout;

    protected List<BackendlessUser> cardsToDisplay;
    protected MenuItem addPartner;
    protected MenuItem mRefreshButton;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_love_days, container, false);
        mContext = inflatedView.getContext();

        loveDaysCards = (RecyclerView) inflatedView.findViewById(R.id.cardList);
        loveDaysCards.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        loveDaysCards.setLayoutManager(llm);

        mProgressBar = (ProgressBar) inflatedView.findViewById(R.id.progressBar);
        mFragmentLoveDaysLayout = (RelativeLayout) inflatedView.findViewById(R.id.layoutFragmentLoveDays);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeRefreshLayout);

        mSwipeToRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
            loadCardList(mCurrentUser);
        }

        return inflatedView;
    }
    //refresh listener za updatevane na tova dali ima novi saobstehnia
    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //samo tuk davame false kato argument, zashtoto ne iskam da skrivam spinnera, koito si varvi sas swipe to refresh
            refreshPartnersList(false);
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Statics.UPDATE_STATUS) {
            BackendlessUser currentUser = Backendless.UserService.CurrentUser();
            loadCardList(currentUser);
        }

        if(requestCode ==  Statics.MENSTRUAL_CALENDAR_DIALOG) {


            if (resultCode == Activity.RESULT_OK) {
                BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                loadCardList(currentUser);

                Bundle bundle = data.getExtras();
                Boolean sendSexyCalendarUpdateToPartners =
                        bundle.getBoolean(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS);
                //izchisliavam v koi etap ot cikala e i updatevame statusite


                if(sendSexyCalendarUpdateToPartners == true) {
                    //TODO: izprashtam update na partniorite
                }

            }
        }
    } //krai na onActivity result

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                //vrazvam butona za refresh, za da moga da go enable/disable
                mRefreshButton = item;
                refreshPartnersList(true);

                //proveriavame da delete i za pending partner request
                //proveriavame dali ne sa se updatnali partniorite na usera
                if(mCurrentUser !=null) {
                    BackendlessHelper.checkForPendingParnerRequests(mCurrentUser, addPartner);
                    BackendlessHelper.checkForDeletePartnerRequest(mCurrentUser);
                    BackendlessHelper.checkAndUpdatePartners(mCurrentUser);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        addPartner = menu.findItem(R.id.partner_request);
    }
/*
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!!!!!!!!!!!!!!!!!!1  HELPER METODI !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
 */


    protected void refreshPartnersList(final boolean hideLayouts){
        if(hideLayouts == true) {
            mRefreshButton.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
            mFragmentLoveDaysLayout.setVisibility(View.GONE);
        } else {
            //ako ne skirvame layouts, znachi metodat e izvikan ot swipe to refresh
            mSwipeToRefreshLayout.setRefreshing(true);
        }

        String whereClause = "email='" + mCurrentUser.getEmail() +"'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Data.of(BackendlessUser.class).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {


            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> user) {
                if(hideLayouts == true) {
                    mRefreshButton.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);
                    mFragmentLoveDaysLayout.setVisibility(View.VISIBLE);
                }
                if(mSwipeToRefreshLayout.isRefreshing()){
                    mSwipeToRefreshLayout.setRefreshing(false);
                }
                //tova e updatnat tekusht potrebitel
                BackendlessUser currentUser = user.getCurrentPage().get(0);
                //updatevame go lokano
                Backendless.UserService.setCurrentUser(currentUser);
                loadCardList(currentUser);

                if(currentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                    Toast.makeText(mContext,R.string.toast_update_partners,Toast.LENGTH_LONG).show();
                } else {
                    //niama namereni partniori
                    Toast.makeText(mContext,R.string.toast_update_partners_no_partners_found,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                if(hideLayouts == true) {
                    mRefreshButton.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);
                    mFragmentLoveDaysLayout.setVisibility(View.VISIBLE);
                }
                if(mSwipeToRefreshLayout.isRefreshing()){
                    mSwipeToRefreshLayout.setRefreshing(false);
                }
                //niama kakvo da napravim
                Toast.makeText(mContext,"not refreshed...",Toast.LENGTH_LONG).show();

            }
        });

    }

    protected void loadCardList(BackendlessUser currentUser) {
        cardsToDisplay = new ArrayList<BackendlessUser>();
        cardsToDisplay.add(currentUser);

        if(currentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
           BackendlessUser[] partners = (BackendlessUser[]) currentUser.getProperty(Statics.KEY_PARTNERS);
            //updatevame cardList i prezarezhdame list

            for(BackendlessUser partner : partners) {
                cardsToDisplay.add(partner);
            }
      }
        //zarezdame adaptora
        AdapterLoveDays adapter = new AdapterLoveDays(cardsToDisplay, mContext, FragmentLoveDays.this);
        loveDaysCards.setAdapter(adapter);
    }


}
