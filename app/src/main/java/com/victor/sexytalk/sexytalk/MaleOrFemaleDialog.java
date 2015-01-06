package com.victor.sexytalk.sexytalk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;



/**
 * Created by Victor on 19/10/2014.
 */
public class MaleOrFemaleDialog extends DialogFragment {

    TextView mainMessage;
    BackendlessUser currentUser;

    //butonite za kalendarite za mache i zheni
    //private Button showSexyCalendarButton;
    private Button showPrivateDaysCalendarButton;
    private Spinner showPartnersList;
    //private Button sexyCalendarForGuysButton;

    ViewPager pager; //izpolzvat se za updatvane na fragmenta sled kato izbera maz ili zhena
    PagerAdapter adapter;

    Context context;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //vrazvam osnovnoto sabshtenie i butonite za kalendarite
        currentUser = Backendless.UserService.CurrentUser();
        //TODO: butonite za mazki i zhenski kalendari
        //mainMessage = (TextView) getActivity().findViewById(R.id.mainMessage);
        //showSexyCalendarButton = (Button) getActivity().findViewById(R.id.showSexyCalendarButton);
        showPrivateDaysCalendarButton = (Button) getActivity().findViewById(R.id.showPrivateDaysDialog);
        showPartnersList = (Spinner) getActivity().findViewById(R.id.listOfPartners);
        //sexyCalendarForGuysButton = (Button) getActivity().findViewById(R.id.sexyCalendarGuys);

        pager = (ViewPager) getActivity().findViewById(R.id.pager);
        adapter = (PagerAdapter) pager.getAdapter();

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_menu_title)
                .setItems(R.array.sex_options, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                switch(which) {
                    case 0:
                        //mainMessage.setText(R.string.main_message_male);

                        //update v backendless che e male
                        currentUser.setProperty(Statics.KEY_MALE_OR_FEMALE, Statics.SEX_MALE);
                        Backendless.Data.of(BackendlessUser.class).save(currentUser, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser backendlessUser) {
                            Toast.makeText(context,
                                    R.string.selection_saved_successfully,Toast.LENGTH_LONG).show();
                                showPrivateDaysCalendarButton.setVisibility(View.INVISIBLE);
                                showPartnersList.setVisibility(View.VISIBLE);
                                refreshFragments();

                                }



                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(context,
                                        R.string.selection_not_saved,Toast.LENGTH_LONG).show();
                            }
                        });


                        break;
                    case 1:
                       // update v backendless che e female

                        currentUser.setProperty(Statics.KEY_MALE_OR_FEMALE, Statics.SEX_FEMALE);
                        Backendless.Data.of(BackendlessUser.class).save(currentUser, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser backendlessUser) {
                                Toast.makeText(context,
                                        R.string.selection_saved_successfully,Toast.LENGTH_LONG).show();
                                showPrivateDaysCalendarButton.setVisibility(View.VISIBLE);
                                showPartnersList.setVisibility(View.INVISIBLE);
                                refreshFragments();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(context,
                                        R.string.selection_not_saved,Toast.LENGTH_LONG).show();
                            }
                        });
                        //TODO: pokazvame zhenskite kalendari i skrivame mazhkia
                        //showSexyCalendarButton.setVisibility(View.VISIBLE);
                        //showPrivateDaysCalendarButton.setVisibility(View.VISIBLE);
                        //sexyCalendarForGuysButton.setVisibility(View.INVISIBLE);

                        //adapter.notifyDataSetChanged();//tova updatva fragmenta.
                        //preprashta kam PagerAdapter getItemPosition();
                        //return POSITION_NONE; oznachava da updatene fragmentite//tova updatva fragmenta.

                        break;
                }
            }
        });
        return builder.create();

    }
protected void refreshFragments(){
  PagerAdapter adapter = (PagerAdapter) pager.getAdapter();
    pager.setAdapter(adapter);
   pager.setCurrentItem(1);

}
    @Override
    public void onDetach() {
        super.onDetach();
        //refresh FragmentLoveDays
    }
}
