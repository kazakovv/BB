package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.UserInterfaces.DefaultCallback;
import com.victor.sexytalk.sexytalk.Helper.CycleStage;
import com.victor.sexytalk.sexytalk.Helper.BackendlessMessage;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Victor on 17/10/2014.
 */
public class SetFirstDayOfCycle extends DialogFragment implements AdapterView.OnItemSelectedListener {
    DatePicker datePicker;
    Spinner spinnerCycle;
    int averageLengthOfMenstrualCycle;
    CheckBox sendSexyCalendarUpdateToPartners;
    Context context;

    protected BackendlessUser mCurrentUser;

    final static long MILLIS_PER_DAY = 24 * 3600 * 1000;

    protected TextView cyclePhaseStatus; //vzimame statusa, za da izprashtame calendar updates
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_set_first_day_of_cycle,null);

        //vrazvam promenlivite
        datePicker = (DatePicker) inflatedView.findViewById(R.id.datePicker);
        spinnerCycle = (Spinner) inflatedView.findViewById(R.id.spinnerMenstrualCycleLength);
        sendSexyCalendarUpdateToPartners = (CheckBox) inflatedView.findViewById(R.id.sendSexyCalendarUpdateCheck);
        context = inflatedView.getContext();
        cyclePhaseStatus = (TextView) getActivity().findViewById(R.id.sexyStatus);
        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }

        //sazdavame masiv s vazmoznostite za prodalzhitelnostta na cikala
        //stoinostite sa ot 21 do 35 dena
        Integer[] lengthOfCycle = new Integer[15];
        for(int i = 0; i<15;i++) {
        lengthOfCycle[i] = i + 21;
        }

        //zadavame stoinostite na spinnera
        ArrayAdapter <Integer> adapter = new ArrayAdapter<Integer>( context,android.R.layout.simple_spinner_item,lengthOfCycle );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCycle.setAdapter(adapter);
        spinnerCycle.setSelection(7);
        spinnerCycle.setOnItemSelectedListener(this);

        //zadavame ogranichenia za datata na parvia cikal
        datePicker.setMaxDate(new Date().getTime());//da ne moze da zaavash badeshti dni
        spinnerCycle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String averageCycleLength = spinnerCycle.getSelectedItem().toString();
                int averageLength = Integer.parseInt(averageCycleLength);

                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_YEAR, -averageLength);
                datePicker.setMinDate(0);
                datePicker.setMinDate(c.getTimeInMillis());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // Set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String averageCycleLength = spinnerCycle.getSelectedItem().toString();
                        updateCurrentUser();
                        String titleCycle =
                                CycleStage.determineCyclePhase(mCurrentUser, context);

                        //vrashta infoto kam onActivityResult v FragmentDays
                        Intent i = new Intent();
                        Bundle extras = new Bundle();
                        extras.putString(Statics.TITLE_CYCLE,titleCycle);
                        extras.putBoolean(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS,sendSexyCalendarUpdateToPartners.isChecked());
                        i.putExtras(extras);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);

                        dismiss();
                    }//krai na else statment

                })//end ok button
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SetFirstDayOfCycle.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        averageLengthOfMenstrualCycle = (Integer) parent.getItemAtPosition(position);
        //averageLengthOfMenstrualCycle = Integer.parseInt((String) parent.getItemAtPosition(position));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    /*
    HELPER METODI
     */


    public void updateCurrentUser() {

            final Calendar firstDayOfCycle = Calendar.getInstance();
            firstDayOfCycle.set(Calendar.YEAR,datePicker.getYear());
            firstDayOfCycle.set(Calendar.MONTH, datePicker.getMonth());
            firstDayOfCycle.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

            Date test  = firstDayOfCycle.getTime();

            String whereClause = "senderEmail='" + Backendless.UserService.CurrentUser().getEmail() + "'";
            BackendlessDataQuery query = new BackendlessDataQuery();
            query.setWhereClause(whereClause);


            mCurrentUser.setProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE,
                    Integer.parseInt(spinnerCycle.getSelectedItem().toString()) );
            mCurrentUser.setProperty(Statics.FIRST_DAY_OF_CYCLE, firstDayOfCycle.getTime());
            //updatevame current user lokalno
            Backendless.UserService.setCurrentUser(mCurrentUser);

        if(sendSexyCalendarUpdateToPartners.isChecked()) {
            mCurrentUser.setProperty(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS, true);
        } else {
            mCurrentUser.setProperty(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS, false);
        }

        Backendless.UserService.update(mCurrentUser, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                if(sendSexyCalendarUpdateToPartners.isChecked()) {

                    sendPushUpdateToAllPartners(context,mCurrentUser);
                    //Toast.makeText(context,R.string.calendar_update_sent, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context,R.string.calendar_saved, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                String error = backendlessFault.getMessage();
                Toast.makeText(context,R.string.error_sending_calendar_updates, Toast.LENGTH_LONG).show();

            }
        });

    }

    /*
    HELPER METODI
     */

    protected void sendPushUpdateToAllPartners(final Context context, BackendlessUser currentUser){
        //namirame partniorite
        //moze da se napravi i directno
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = "email='" + currentUser.getEmail() + "'";
        dataQuery.setWhereClause(whereClause);
        String message = context.getResources().getString(R.string.sending_sexy_calendar_update_to_partners_dialog_message);
        Backendless.Data.of(BackendlessUser.class).find(dataQuery,
                new DefaultCallback<BackendlessCollection<BackendlessUser>>(context, message) {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> user) {
                super.handleResponse(user);
                //Sazdavame masiv s partniorite
               if( user.getCurrentPage().get(0).getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[] ) {
                    BackendlessUser[] parters = (BackendlessUser[]) user.getCurrentPage().get(0).getProperty(Statics.KEY_PARTNERS);
                    //send push
                   for (BackendlessUser partner : parters) {
                       //ako ne sa prazni izprashtame push message
                       BackendlessMessage.sendPush(mCurrentUser, partner, null, context, Statics.TYPE_CALENDAR_UPDATE);

                   }//krai na send push
               }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                super.handleFault(backendlessFault);
                Toast.makeText(context,R.string.general_server_error,Toast.LENGTH_LONG).show();
            }
        });

    }

}