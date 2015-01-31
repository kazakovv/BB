package com.victor.sexytalk.sexytalk;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.victor.sexytalk.sexytalk.Adaptors.AdapterExistingPartners;

import java.util.ArrayList;
import java.util.List;


/**
 * Tova e fragment sas spisak na sashtestvuvashte partniori
 */
public class FragmentExistingPartners extends ListFragment {
    BackendlessUser mCurrentUser;
    List<BackendlessUser> mExistingPartners;
    TextView emptyMessage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_fragment_existing_partners,container,false);
        emptyMessage = (TextView) inflatedView.findViewById(R.id.noExistingPartnersMessage);
        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setEmptyView(emptyMessage);
        if (Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }
        if(mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
            //imama partniori
            BackendlessUser[] existingPartners = (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);

            mExistingPartners = new ArrayList<BackendlessUser>();
            for(BackendlessUser partner : existingPartners) {
                mExistingPartners.add(partner);
            }

            AdapterExistingPartners adapter =
                    new AdapterExistingPartners(getListView().getContext(),mExistingPartners, mCurrentUser);
            getListView().setAdapter(adapter);
        }
    }
}
