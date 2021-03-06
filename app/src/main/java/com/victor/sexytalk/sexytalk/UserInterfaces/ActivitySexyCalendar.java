package com.victor.sexytalk.sexytalk.UserInterfaces;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.squareup.timessquare.CalendarPickerView;
import com.victor.sexytalk.sexytalk.Helper.CycleStage;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ActivitySexyCalendar extends ActionBarActivity {
protected Toolbar toolbar;
protected Date mFirstDayOfCycle;
protected int mAverageLengthOfCycle;
protected TextView mCycleTitle;
protected TextView mPhaseDescription;
protected BackendlessUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_sexy_calendar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        mCycleTitle = (TextView) findViewById(R.id.cycleTitle);
        mPhaseDescription = (TextView) findViewById(R.id.phase_description);

        //vzimame stoinostite, za zarezdane na kalendara

        /*Intent intent = getIntent();
        mFirstDayOfCycle = (Date) intent.getSerializableExtra(Statics.FIRST_DAY_OF_CYCLE);
        mAverageLengthOfCycle = intent.getIntExtra(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE,0);
        */
        mCurrentUser = Backendless.UserService.CurrentUser();
        mFirstDayOfCycle = (Date) mCurrentUser.getProperty(Statics.FIRST_DAY_OF_CYCLE);
        mAverageLengthOfCycle = (int) mCurrentUser.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
        Calendar firstDayOfCycle = Calendar.getInstance();
        firstDayOfCycle.setTime(mFirstDayOfCycle);

        ArrayList<Date> datesToBeSelected = returnDurationOfCurrentCycle(firstDayOfCycle,mAverageLengthOfCycle);
        Calendar firstDate = Calendar.getInstance();
        firstDate.setTime(datesToBeSelected.get(0));
        firstDate.add(Calendar.DAY_OF_MONTH,-1);

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(datesToBeSelected.get(datesToBeSelected.size() - 1)); //poslednata data
        lastDate.add(Calendar.DAY_OF_MONTH,1);

        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        calendar.init(firstDate.getTime(), lastDate.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .displayOnly()
                .withHighlightedDates(datesToBeSelected);
    }




    /*
    HELPER METODI
     */

    protected ArrayList<Date> returnDurationOfCurrentCycle(Calendar mFirstDayOfCycle, int averageCycleLength){
        ArrayList<Date> datesToBeSelected = new ArrayList<Date>();


        Calendar now = Calendar.getInstance();




        long difference = now.getTimeInMillis() - mFirstDayOfCycle.getTimeInMillis();

        final int days = (int) (difference /(24 * 60 * 60 * 1000));
        final int firstDayOfOvulation = averageCycleLength - 14;
        final int lastDayOfOvulation = averageCycleLength -10;

        datesToBeSelected.add(now.getTime()); //dobaviame dnes
        Calendar dateToBeAdded = Calendar.getInstance();

        String cycleStage = CycleStage.determineCyclePhase(mCurrentUser,getApplicationContext());

        if(cycleStage.equals(getResources().getString(R.string.period_bleeding) )) {
            //bleeding
            //bleeding trae 5 dena! broi se i day 0 do 4
            mCycleTitle.setText(R.string.period_bleeding);
            mPhaseDescription.setText(R.string.description_bleeding_phase);
            int lastDay = 5 - days ;

            //dateToBeAdded.add(Calendar.DAY_OF_MONTH,lastDay);
            //datesToBeSelected.add(dateToBeAdded.getTime());

            //dobaviame vsichki dati m/u
            for(int i = 1; i < lastDay; i++) {
                dateToBeAdded.clear();
                dateToBeAdded.setTime(new Date());
                dateToBeAdded.add(Calendar.DATE,i);
                datesToBeSelected.add( dateToBeAdded.getTime() );
            }


        } else if (cycleStage.equals(getResources().getString(R.string.period_follicular_phase)) ) {
            //folicurar phase
            //active energetic
            mCycleTitle.setText(R.string.period_follicular_phase);
            mPhaseDescription.setText(R.string.description_follicular_phase);
            int lastDay = firstDayOfOvulation - days;

            //dobaviame vsichki dati m/u
            for(int i = 1; i < lastDay ; i++) {
                dateToBeAdded.clear();
                dateToBeAdded.setTime(new Date());
                dateToBeAdded.add(Calendar.DATE,i);
                datesToBeSelected.add(dateToBeAdded.getTime());
            }
        } else if (cycleStage.equals(getResources().getString(R.string.period_ovulation))) {
            //ovulation
            //sexy
            //dobaviame vsichki dati m/u
            mCycleTitle.setText(R.string.period_ovulation);
            mPhaseDescription.setText(R.string.description_ovulation);
            int lastDay = lastDayOfOvulation - days;

            for(int i = 1; i < lastDay; i++) {
                dateToBeAdded.clear();
                dateToBeAdded.setTime(new Date());
                dateToBeAdded.add(Calendar.DATE,i);
                datesToBeSelected.add(dateToBeAdded.getTime());
            }

        } else if (cycleStage.equals(getResources().getString(R.string.period_luteal)) ) {
            //luteal
            //dobaviame vsichki dati m/u
            mCycleTitle.setText(R.string.period_luteal);
            mPhaseDescription.setText(R.string.description_luteal);
            int lastDay = averageCycleLength - days;

            for(int i = 1; i <= lastDay; i++) {
                dateToBeAdded.clear();
                dateToBeAdded.setTime(new Date());
                dateToBeAdded.add(Calendar.DATE,i);
                datesToBeSelected.add(dateToBeAdded.getTime());
            }
        } else {
            //tr da se updatene kalendara
            mCycleTitle.setText(R.string.sexyCalendar_needs_updating_message);
            mPhaseDescription.setText(" ");
            TextView remainingDaysMessage = (TextView) findViewById(R.id.remainingDaysMessage);
            remainingDaysMessage.setVisibility(View.INVISIBLE);
        }


        return datesToBeSelected;
    }
}
