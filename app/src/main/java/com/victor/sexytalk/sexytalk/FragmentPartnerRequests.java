package com.victor.sexytalk.sexytalk;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;


/**
 *
 */
public class FragmentPartnerRequests extends ListFragment {
    protected BackendlessUser mCurrentUser;
    protected List<PartnersAddRequest> mPendingPartnerRequests;
    protected ListView mPendingPartnersRequestList;
    protected TextView emptyMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_fragment_partner_requests,container,false);
        emptyMessage = (TextView) inflatedView.findViewById(R.id.noPendingPartnerRequestsMessage);
        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPendingPartnersRequestList = getListView();
        mPendingPartnersRequestList.setEmptyView(emptyMessage);

        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }
        //proveriavame dali ima pending partner requests
        String whereClause="email_partnerToConfirm='" + mCurrentUser.getEmail() + "'";
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereClause);
        Backendless.Data.of(PartnersAddRequest.class).find(query, new AsyncCallback<BackendlessCollection<PartnersAddRequest>>() {
            @Override
            public void handleResponse(BackendlessCollection<PartnersAddRequest> pendingPartnerRequests) {
                if(pendingPartnerRequests.getData().size()>0) {
                    mPendingPartnerRequests = pendingPartnerRequests.getData();
                    PartnerRequestsAdapter adapter =
                            new PartnerRequestsAdapter(mPendingPartnersRequestList.getContext(),mPendingPartnerRequests);
                    mPendingPartnersRequestList.setAdapter(adapter);
                }
                Log.d("Vic","found sth" );
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.d("Vic","error" + backendlessFault.getMessage() );
                Toast.makeText(getActivity(), R.string.general_server_error,Toast.LENGTH_LONG).show();
            }
        });
    }
}
