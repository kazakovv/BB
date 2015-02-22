package com.victor.sexytalk.sexytalk.UserInterfaces;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.squareup.timessquare.CalendarPickerView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_sexy_calendar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        mCycleTitle = (TextView) findViewById(R.id.cycleTitle);
        //vzimame stoinostite, za zarezdane na kalendara
        Intent intent = getIntent();
        mFirstDayOfCycle = (Date) intent.getSerializableExtra(Statics.FIRST_DAY_OF_CYCLE);
        mAverageLengthOfCycle = intent.getIntExtra(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE,0);

        ArrayList<Date> datesToBeSelected = returnDurationOfCurrentCycle(mFirstDayOfCycle,mAverageLengthOfCycle);
        Calendar firstDate = Calendar.getInstance();
        firstDate.setTime(datesToBeSelected.get(0));
        firstDate.add(Calendar.DAY_OF_MONTH,-1);

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(datesToBeSelected.get(datesToBeSelected.size() - 1)); //poslednata data
        lastDate.add(Calendar.DAY_OF_MONTH,1);

        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        calendar.init(firstDate.getTime(), lastDate.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)

                .withHighlightedDates(datesToBeSelected);
    }




    /*
    HELPER METODI
     */

    protected ArrayList<Date> returnDurationOfCurrentCycle(Date mFirstDayOfCycle, int averageCycleLength){
        ArrayList<Date> datesToBeSelected = new ArrayList<Date>();


        Calendar now = Calendar.getInstance();

        long difference = now.getTimeInMillis() - mFirstDayOfCycle.getTime();

        final int days = (int) (difference /(24 * 60 * 60 * 1000));
        final int firstDayOfOvulation = averageCycleLength - 14;
        final int lastDayOfOvulation = averageCycleLength -10;

        datesToBeSelected.add(now.getTime()); //dobaviame dnes
        Calendar dateToBeAdded = Calendar.getInstance();
        if(days >= 0 && days <= 4 ) { //obshto 5 dena of bleeding
            //bleeding
            //bleeding trae 5 dena! broi se i day 0 do 4
            mCycleTitle.setText(R.string.period_no_sex);
            int lastDay = 4 - days ;

            //dateToBeAdded.add(Calendar.DAY_OF_MONTH,lastDay);
            //datesToBeSelected.add(dateToBeAdded.getTime());

            //dobaviame vsichki dati m/u
            for(int i = (days+1); i < lastDay; i++) {
                dateToBeAdded.add(Calendar.DAY_OF_MONTH,1);
                datesToBeSelected.add(dateToBeAdded.getTime());
            }


        } else if (days > 4 && days < firstDayOfOvulation ) {
            //folicurar phase
            // active energetic
            mCycleTitle.setText(R.string.period_sexy_days);

            //dobaviame vsichki dati m/u
            for(int i = (days+1); i < firstDayOfOvulation; i++) {
                dateToBeAdded.add(Calendar.DAY_OF_MONTH,1);
                datesToBeSelected.add(dateToBeAdded.getTime());
            }
        } else if (days >= firstDayOfOvulation && days < lastDayOfOvulation) {
            //ovulation
            //sexy
            //dobaviame vsichki dati m/u
            mCycleTitle.setText(R.string.period_baby_days);

            for(int i = (days+1); i < lastDayOfOvulation; i++) {
                dateToBeAdded.add(Calendar.DAY_OF_MONTH,1);
                datesToBeSelected.add(dateToBeAdded.getTime());
            }

        } else if (days >= lastDayOfOvulation && days <= averageCycleLength) {
            //luteal
            //dobaviame vsichki dati m/u
            mCycleTitle.setText(R.string.period_sexy_days);

            for(int i = (days+1); i <= averageCycleLength; i++) {
                dateToBeAdded.add(Calendar.DAY_OF_MONTH,1);
                datesToBeSelected.add(dateToBeAdded.getTime());
            }
        }


        return datesToBeSelected;
    }
}
