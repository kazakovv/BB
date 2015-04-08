package com.victor.sexytalk.bisou.UserInterfaces;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.bisou.Adaptors.AdapterExistingPartners;
import com.victor.sexytalk.bisou.R;
import com.victor.sexytalk.bisou.Statics;

import java.util.ArrayList;
import java.util.List;


/**
 * Tova e fragment sas spisak na sashtestvuvashte partniori
 */
public class FragmentExistingPartners extends ListFragment {
    BackendlessUser mCurrentUser;
    List<BackendlessUser> mExistingPartners;
    TextView emptyMessage;
    Context mContext;
    ListView mListview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_fragment_existing_partners,container,false);
        emptyMessage = (TextView) inflatedView.findViewById(R.id.noExistingPartnersMessage);
        mContext = inflatedView.getContext();
        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setEmptyView(emptyMessage);
        if (Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }
        mListview = getListView();
        if(mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
            //imama partniori
            BackendlessUser[] existingPartners = (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);

            mExistingPartners = new ArrayList<BackendlessUser>();
            for(BackendlessUser partner : existingPartners) {
                mExistingPartners.add(partner);
            }

            AdapterExistingPartners adapter =
                    new AdapterExistingPartners(mContext,mExistingPartners, mCurrentUser, getActivity());
            mListview.setAdapter(adapter);

            //updatevame list s partniori za vzeki sluchai. Tova se izkarva sled kato veche sme zaredili parvonachalnia spisak
            String whereClause = "email='" + mCurrentUser.getEmail() + "'";
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause);
            Backendless.Data.of(BackendlessUser.class).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                @Override
                public void handleResponse(BackendlessCollection<BackendlessUser> user) {
                    //namira niakakvi partniori
                    if (user.getCurrentPage().get(0).getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                        BackendlessUser[] newListWithPartners =
                                (BackendlessUser[]) user.getCurrentPage().get(0).getProperty(Statics.KEY_PARTNERS);
                        List<BackendlessUser> newPartners = new ArrayList<BackendlessUser>();
                        for(BackendlessUser partner : newListWithPartners) {
                            newPartners.add(partner);
                        }
                        //dobaviame i lokalno
                        mCurrentUser.setProperty(Statics.KEY_PARTNERS,newListWithPartners);
                        //zarezdame spisakat nanovo
                        AdapterExistingPartners adapter =
                                new AdapterExistingPartners(mContext,newPartners, mCurrentUser, getActivity());
                        mListview.setAdapter(adapter);

                    }
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    //niama kakvo da napravim
                }
            });
        }
    }
}
