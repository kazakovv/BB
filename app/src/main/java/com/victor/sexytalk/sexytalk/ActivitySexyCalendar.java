package com.victor.sexytalk.sexytalk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Range;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;


public class ActivitySexyCalendar extends ActionBarActivity {
protected Toolbar toolbar;
protected Date mFirstDayOfCycle;
protected int mAverageLengthOfCycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_sexy_calendar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_sexy_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    HELPER METODI
     */

    protected ArrayList<Date> returnDurationOfCurrentCycle(Date mFirstDayOfCycle, int averaceCycleLength){
        ArrayList<Date> datesToBeSelected = new ArrayList<Date>();


        Calendar now = Calendar.getInstance();

        long difference = now.getTimeInMillis() - mFirstDayOfCycle.getTime();

        final int days = (int) (difference /(24 * 60 * 60 * 1000));
        final int firstDayOfOvulation = averaceCycleLength - 14;
        final int lastDayOfOvulation = averaceCycleLength -10;

        datesToBeSelected.add(now.getTime()); //dobaviame dnes
        Calendar dateToBeAdded = Calendar.getInstance(); //poslednata data ot cikala
        if(days >= 0 && days <= 5 ) {
            //bleeding

            int lastDay = 5 - days ;

            //dateToBeAdded.add(Calendar.DAY_OF_MONTH,lastDay);
            //datesToBeSelected.add(dateToBeAdded.getTime());

            //dobaviame vsichki dati m/u
            for(int i = (days+1); i <= lastDay; i++) {
                dateToBeAdded.add(Calendar.DAY_OF_MONTH,1);
                datesToBeSelected.add(dateToBeAdded.getTime());
            }


        } else if (days > 5 && days < firstDayOfOvulation ) {
            //folicurar phase
            // active energetic
            dateToBeAdded.add(Calendar.DAY_OF_MONTH, (firstDayOfOvulation -1));
            datesToBeSelected.add(dateToBeAdded.getTime());
            //dobaviame vsichki dati m/u
            for(int i = (days+1); i < firstDayOfOvulation; i++) {
                dateToBeAdded.add(Calendar.DAY_OF_MONTH,1);
                datesToBeSelected.add(dateToBeAdded.getTime());
            }
        } else if (days >= firstDayOfOvulation && days <= lastDayOfOvulation) {
            //ovulation
            //sexy
            dateToBeAdded.add(Calendar.DAY_OF_MONTH, (lastDayOfOvulation -1));
            datesToBeSelected.add(dateToBeAdded.getTime());

        } else if (days > lastDayOfOvulation && days <= averaceCycleLength) {
            //luteal
            dateToBeAdded.add(Calendar.DAY_OF_MONTH, averaceCycleLength );
            datesToBeSelected.add(dateToBeAdded.getTime());
        }


        return datesToBeSelected;
    }
}
