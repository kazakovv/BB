package com.victor.sexytalk.sexytalk;

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

/**
 * Created by Victor on 17/10/2014.
 */
public class SetFirstDayOfCycle extends DialogFragment implements AdapterView.OnItemSelectedListener {
    DatePicker datePicker;
    Spinner spinnerCycle;
    int averageLengthOfMenstrualCycle;
    CheckBox sendSexyCalendarUpdateToPartners;
    Context context;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.set_first_day_of_cycle,null);

        //vrazvam promenlivite
        datePicker = (DatePicker) inflatedView.findViewById(R.id.datePicker);
        spinnerCycle = (Spinner) inflatedView.findViewById(R.id.spinnerMenstrualCycleLength);
        sendSexyCalendarUpdateToPartners = (CheckBox) inflatedView.findViewById(R.id.sendSexyCalendarUpdateCheck);
        context = inflatedView.getContext();

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

        // Set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                        //vrashta infoto kam onActivityResult v FragmentDays
                        String averageCycleLength = spinnerCycle.getSelectedItem().toString();

                        Intent i = new Intent();
                        Bundle extras = new Bundle();

                        extras.putInt(Statics.CALENDAR_YEAR,datePicker.getYear());
                        extras.putInt(Statics.CALENDAR_MONTH,datePicker.getMonth());
                        extras.putInt(Statics.CALENDAR_DAY,datePicker.getDayOfMonth());
                        extras.putInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE,
                                Integer.parseInt(averageCycleLength));
                        extras.putBoolean(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS,
                                sendSexyCalendarUpdateToPartners.isChecked());
                        boolean test = sendSexyCalendarUpdateToPartners.isChecked();
                        i.putExtras(extras);
                        getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK,i);
                        dismiss() ;


                    }
                })
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

}