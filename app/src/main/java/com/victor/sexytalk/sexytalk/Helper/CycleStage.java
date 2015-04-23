package com.victor.sexytalk.sexytalk.Helper;

import android.content.Context;

import com.backendless.BackendlessUser;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Victor on 14/02/2015.
 */
public class CycleStage {
    public static String determineCyclePhase(BackendlessUser user, Context context) {
        String cyclePhaseMassage = "";
        //izchisliava v koi etap ot cikala e i promenia saobshtenieto
        if (user.getProperty(Statics.FIRST_DAY_OF_CYCLE) != null &&
                user.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE) != null) {
            int averageLengthOfCycle = (int) user.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
            Calendar firstDayOfCycle = Calendar.getInstance();
            firstDayOfCycle.setTime((Date) user.getProperty(Statics.FIRST_DAY_OF_CYCLE));
            Calendar now = Calendar.getInstance();



            long difference = now.getTimeInMillis() - firstDayOfCycle.getTimeInMillis();

            final int days = (int) (difference / (24 * 60 * 60 * 1000));
            final int firstDayOfOvulation = averageLengthOfCycle - 14;
            final int lastDayOfOvulation = averageLengthOfCycle - 10;

            //Tova sa etapite ot cikala
            /*
            Follicular: right after bleeding stops, for about 7 days
            Ovulation: 3 or 4 days of the most fertile time, midway through the cycle
            Luteal: the 10 days or so after ovulation and before menstruation
            Menstruation: the 2-7 days of bleeding
            */

            if (days >= 0 && days <= 4) {
                //bleeding
                cyclePhaseMassage = context.getResources().getString(R.string.period_bleeding);
            } else if (days > 5 && days < firstDayOfOvulation) {
                //folicurar phase
                // active energetic
                cyclePhaseMassage = context.getResources().getString(R.string.period_follicular_phase);
            } else if (days >= firstDayOfOvulation && days < lastDayOfOvulation) {
                //ovulation
                //sexy
                cyclePhaseMassage = context.getResources().getString(R.string.period_ovulation);
            } else if (days >= lastDayOfOvulation && days <= averageLengthOfCycle) {
                //luteal
                cyclePhaseMassage = context.getResources().getString(R.string.period_luteal);

                //TODO:tr da se opravi
            } else if (days > averageLengthOfCycle) {
                //tr da se updatene

                cyclePhaseMassage = context.getResources().getString(R.string.sexyCalendar_needs_updating_message);


            } else if (days < 0) {
                cyclePhaseMassage = context.getResources().getString(R.string.sexyCalendar_error_message);

            }

        } else {
            //ako sa nuli znachi partniorat ne si e updatenal kalendara
            cyclePhaseMassage = context.getResources().getString(R.string.sexyCalendar_needs_updating_message);
        }
        return cyclePhaseMassage;
    }//krai na determine cycle phase
}
