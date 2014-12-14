package com.victor.sexytalk.sexytalk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



/**
 * Created by Victor on 19/10/2014.
 */
public class MaleOrFemaleDialog extends DialogFragment {
    /*
    TextView mainMessage;
    //View fertilityCalandarIcon;
    ParseUser parseUser;

    //butonite za kalendarite za mache i zheni
    private Button showSexyCalendarButton;
    private Button showPrivateDaysCalendarButton;
    private Button sexyCalendarForGuysButton;

    ViewPager pager; //izpolzvat se za updatvane na fragmenta sled kato izbera maz ili zhena
    PagerAdapter adapter;

    Context context;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //vrazvam osnovnoto sabshtenie i butonite za kalendarite
        parseUser = ParseUser.getCurrentUser();
        mainMessage = (TextView) getActivity().findViewById(R.id.mainMessage);
        showSexyCalendarButton = (Button) getActivity().findViewById(R.id.showSexyCalendarButton);
        showPrivateDaysCalendarButton = (Button) getActivity().findViewById(R.id.showPrivateDaysDialog);
        sexyCalendarForGuysButton = (Button) getActivity().findViewById(R.id.sexyCalendarGuys);

        pager = (ViewPager) getActivity().findViewById(R.id.pager);
        adapter = (PagerAdapter) pager.getAdapter();

        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_menu_title)
                .setItems(R.array.guy_or_girl_option, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                switch(which) {
                    case 0:
                        mainMessage.setText(R.string.main_message_male);

                          parseUser.put(ParseConstants.KEY_MALEORFEMALE, ParseConstants.SEX_MALE);
                            parseUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                    //sucess
                                    //
                                    Toast.makeText(context, R.string.messaged_successfully_saved_to_server, Toast.LENGTH_LONG).show();
                                    } else {
                                    //error
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle(R.string.login_error_title)
                                                .setMessage(R.string.messaged_unsuccessfully_saved_to_server)
                                                .setPositiveButton(R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                        //pokazvame mazhkia kalendar i skrivame zhenskite kalendari

                        showSexyCalendarButton.setVisibility(View.INVISIBLE);
                        showPrivateDaysCalendarButton.setVisibility(View.INVISIBLE);
                        sexyCalendarForGuysButton.setVisibility(View.VISIBLE);

                        adapter.notifyDataSetChanged(); //tova updatva fragmenta.
                        //preprashta kam PagerAdapter getItemPosition();
                        // return POSITION_NONE; oznachava da updatene fragmentite




                        break;
                    case 1:
                        mainMessage.setText(R.string.main_message_female);
                        parseUser.put(ParseConstants.KEY_MALEORFEMALE, ParseConstants.SEX_FEMALE);
                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //sucess
                                    //
                                    Toast.makeText(context, R.string.messaged_successfully_saved_to_server, Toast.LENGTH_LONG).show();
                                } else {
                                    //error
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(R.string.login_error_title)
                                            .setMessage(R.string.messaged_unsuccessfully_saved_to_server)
                                            .setPositiveButton(R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                }
                            }
                        });
                        //pokazvame zhenskite kalendari i skrivame mazhkia
                        showSexyCalendarButton.setVisibility(View.VISIBLE);
                        showPrivateDaysCalendarButton.setVisibility(View.VISIBLE);
                        sexyCalendarForGuysButton.setVisibility(View.INVISIBLE);

                        adapter.notifyDataSetChanged();//tova updatva fragmenta.
                        //preprashta kam PagerAdapter getItemPosition();
                        // return POSITION_NONE; oznachava da updatene fragmentite//tova updatva fragmenta.

                        break;
                }
            }
        });
        return builder.create();

    }
    */
}
