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
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.BackendlessClasses.CycleDays;
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

    final static long MILLIS_PER_DAY = 24 * 3600 * 1000;

    protected TextView cyclePhaseStatus; //vzimame statusa, za da izprashtame calendar updates
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.set_first_day_of_cycle,null);

        //vrazvam promenlivite
        datePicker = (DatePicker) inflatedView.findViewById(R.id.datePicker);
        spinnerCycle = (Spinner) inflatedView.findViewById(R.id.spinnerMenstrualCycleLength);
        sendSexyCalendarUpdateToPartners = (CheckBox) inflatedView.findViewById(R.id.sendSexyCalendarUpdateCheck);
        context = inflatedView.getContext();
        cyclePhaseStatus = (TextView) getActivity().findViewById(R.id.sexyStatus);


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


                        //vrashta infoto kam onActivityResult v FragmentDays

                        Intent i = new Intent();
                        Bundle extras = new Bundle();

                        extras.putInt(Statics.CALENDAR_YEAR, datePicker.getYear());
                        extras.putInt(Statics.CALENDAR_MONTH, datePicker.getMonth());
                        extras.putInt(Statics.CALENDAR_DAY, datePicker.getDayOfMonth());
                        extras.putInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE,
                                Integer.parseInt(averageCycleLength));
                        extras.putBoolean(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS,
                                sendSexyCalendarUpdateToPartners.isChecked());
                        boolean test = sendSexyCalendarUpdateToPartners.isChecked();
                        i.putExtras(extras);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);

                        sendSendSexyCalendarUpdateToPartners(); //helper metod po-dolu
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


    public void sendSendSexyCalendarUpdateToPartners() {

            final Calendar firstDayOfCycle = Calendar.getInstance();
            firstDayOfCycle.set(Calendar.YEAR,datePicker.getYear());
            firstDayOfCycle.set(Calendar.MONTH, datePicker.getMonth());
            firstDayOfCycle.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());


            String whereClause = "senderEmail='" + Backendless.UserService.CurrentUser().getEmail() + "'";
            BackendlessDataQuery query = new BackendlessDataQuery();
            query.setWhereClause(whereClause);


            //parvo namirame predishnata info za choveka, za da updatenem kalendara
            Backendless.Persistence.of(CycleDays.class).find(query, new AsyncCallback<BackendlessCollection<CycleDays>>() {
                @Override
                public void handleResponse(BackendlessCollection<CycleDays> result) {
                    CycleDays cycle;
                    if(result.getData().size() > 0) { //ako veche ima info ot predshen pat samo updatevame

                        cycle = result.getCurrentPage().get(0);
                        cycle.setFirstDayOfCycle(firstDayOfCycle.getTime());
                        //TODO: tr da se porvaboti ot kade da se vzima statusa
                        cycle.setSatusText(cyclePhaseStatus.getText().toString());
                        cycle.setAverageCycleLength(Integer.parseInt(spinnerCycle.getSelectedItem().toString()));
                        if(sendSexyCalendarUpdateToPartners.isChecked()) {
                            cycle.setSendCalendarUpdateToPartners(true);
                        } else {
                            cycle.setSendCalendarUpdateToPartners(false);
                        }
                    } else { //sazdavame nova tablica, ponezhe niama talava

                        cycle = new CycleDays();

                        cycle.setSender(Backendless.UserService.CurrentUser());
                        cycle.setSenderEmail(Backendless.UserService.CurrentUser().getEmail());
                        cycle.setFirstDayOfCycle(firstDayOfCycle.getTime());

                        if(sendSexyCalendarUpdateToPartners.isChecked()) {
                            cycle.setSendCalendarUpdateToPartners(true);
                        } else {
                            cycle.setSendCalendarUpdateToPartners(false);
                        }

                        //TODO: tr da se porvaboti ot kade da se vzima statusa
                        cycle.setSatusText(cyclePhaseStatus.getText().toString());
                        cycle.setAverageCycleLength(Integer.parseInt(spinnerCycle.getSelectedItem().toString()));
                    }
                    //updatvame, ili sazvame nov calendar calendar
                    Backendless.Persistence.save(cycle, new AsyncCallback<CycleDays>() {
                        @Override
                        public void handleResponse(CycleDays cycleDays) {
                            if(sendSexyCalendarUpdateToPartners.isChecked()) {
                                //TODO: tr da se poraboti v/u check kolko partniora imame i da se smeni saobstehnieto na plular ili singular
                                Toast.makeText(context,R.string.calendar_update_sent_plural, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context,R.string.calendar_saved, Toast.LENGTH_LONG).show();
                            }


                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(context,R.string.error_sending_calendar_updates, Toast.LENGTH_LONG).show();

                        }
                    });

                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    //error pri find query
                    Toast.makeText(context,R.string.error_sending_calendar_updates, Toast.LENGTH_LONG).show();

                }
            });





    }

}