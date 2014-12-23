package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentLoveDays extends Fragment {
    protected BackendlessUser currentUser;
    protected Button showPrivateDaysDialog;
    private static final int MENSTRUAL_CALENDAR_DIALOG = 11;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mAverageLengthOfMenstrualCycle;
    private boolean mSendSexyCalendarUpdateToPartners;

    TextView cyclePhase;
    Calendar firstDayOfCycle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = Backendless.UserService.CurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_love_days, container, false);


        return inflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showPrivateDaysDialog = (Button) getActivity().findViewById(R.id.showPrivateDaysDialog);
        cyclePhase = (TextView) getActivity().findViewById(R.id.cyclePhase);

        if(currentUser != null) {
            if (currentUser.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE)) {
                showPrivateDaysDialog.setVisibility(View.INVISIBLE);
            } else {
                showPrivateDaysDialog.setVisibility(View.VISIBLE);
            }
        }

        showPrivateDaysDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetFirstDayOfCycle newDialog = new SetFirstDayOfCycle();
                newDialog.setTargetFragment(FragmentLoveDays.this, MENSTRUAL_CALENDAR_DIALOG);
                newDialog.show(getFragmentManager(),"Welcome");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MENSTRUAL_CALENDAR_DIALOG) {


            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle =data.getExtras();


                mYear   =      bundle.getInt(Statics.CALENDAR_YEAR);
                mMonth  =      bundle.getInt(Statics.CALENDAR_MONTH); //mesec -1, Jan e 0, Dec e 11
                mDay    =      bundle.getInt(Statics.CALENDAR_DAY);
                mAverageLengthOfMenstrualCycle =
                        bundle.getInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
                mSendSexyCalendarUpdateToPartners =
                        bundle.getBoolean(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS);

                //izchisliavam v koi etap ot cikala e

                determineCyclePhase();


                if(mSendSexyCalendarUpdateToPartners == true) {
                //TODO: izprashtam update na partniorite
                }
            }
        }
    }

    protected void determineCyclePhase() {
        //izchisliava v koi etap ot cikala e i promenia saobshtenieto
        if(mYear != 0 && mMonth != 0 && mDay != 0 && mAverageLengthOfMenstrualCycle != 0) {
            //firstDayOfCycle = new GregorianCalendar(mYear, mMonth, mDay);
            firstDayOfCycle = Calendar.getInstance();
            firstDayOfCycle.set(Calendar.YEAR, mYear);
            firstDayOfCycle.set(Calendar.MONTH, mMonth);
            firstDayOfCycle.set(Calendar.DAY_OF_MONTH, mDay);
            Calendar now = Calendar.getInstance();

            long difference = now.getTimeInMillis() - firstDayOfCycle.getTimeInMillis();

            long days = difference /(24 * 60 * 60 * 1000);
            long ovulation = days /2; //ovulaciata e v sredata na cikala

            //Tova sa etapite ot cikala
            /*
            Follicular: right after bleeding stops, for about 7 days
            Ovulation: 3 or 4 days of the most fertile time, midway through the cycle
            Luteal: the 10 days or so after ovulation and before menstruation
            Menstruation: the 2-7 days of bleeding
            */

            //razpredeliame dnite

            if(days > 0 && days <= 5 ) {
            //bleeding
                cyclePhase.setText("Blood " + days);
            } else if (days > 5 && days <= 12 ) {
            //folicurar phase
            // active energetic
                cyclePhase.setText("Active " + days);

            } else if (days > 12 && days <= 16) {
            //ovulation
                //sexy
                cyclePhase.setText("Sexy " + days);

            } else if (days > 16 && days <= mAverageLengthOfMenstrualCycle) {
            //luteal
                cyclePhase.setText("Sexy 2 " + days);

            } else if (days > mAverageLengthOfMenstrualCycle) {
            //tr da se updatene
                cyclePhase.setText("Update " + days);

            }


        }
    }
    /*

    private TextView mainMessage;
    private TextView sexyMessage;
    private Button showSexyCalendarButton;
    private Button showPrivateDaysCalendarButton;
    private Button sexyCalendarForGuysButton;

    private static final int MENSTRUAL_CALENDAR_DIALOG = 11;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mAverageLengthOfMenstrualCycle;
    private boolean mSendSexyCalendarUpdateToPartners;

    Calendar firstDayToHaveSex;
    Calendar lastDayToHaveSex;

    //protected ParseRelation<ParseUser> mPartnersRelation;

    //protected static int RESULT_GET_LIST_OF_PARTNERS = 55;

    //protected ParseUser mCurrentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            //miracle
        }
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.frament_love_days, container, false);
        //vrazvam poletata, koito shte zapametiavam
        mainMessage = (TextView) inflatedView.findViewById(R.id.mainMessage);
        sexyMessage = (TextView) inflatedView.findViewById(R.id.textViewSexyMessage);
        showSexyCalendarButton = (Button) inflatedView.findViewById(R.id.showSexyCalendarButton);
        showPrivateDaysCalendarButton = (Button) inflatedView.findViewById(R.id.showPrivateDaysDialog);
        sexyCalendarForGuysButton = (Button) inflatedView.findViewById(R.id.sexyCalendarGuys);

        mCurrentUser = ParseUser.getCurrentUser();
        if(mCurrentUser != null) {//tr da se proveri. Main preprashta kam login, ako ne sme lognati
            //obache se izplaniava i toia kod tuk predi da otde na login screen i zatova blokira
            //proveriavame dali e maz ili zhena za da pokazhem butonite
            if (mCurrentUser.get(ParseConstants.KEY_MALEORFEMALE).equals(ParseConstants.SEX_MALE)) {
                showSexyCalendarButton.setVisibility(View.INVISIBLE);
                showPrivateDaysCalendarButton.setVisibility(View.INVISIBLE);
                sexyCalendarForGuysButton.setVisibility(View.VISIBLE);
            } else { //ako e zhena pokazvame zhenskite kalendati
                showSexyCalendarButton.setVisibility(View.VISIBLE);
                showPrivateDaysCalendarButton.setVisibility(View.VISIBLE);
                sexyCalendarForGuysButton.setVisibility(View.INVISIBLE);

            }

        }//krai na proverkata dali sme lognati
        //Zarezda mainMessage ot savedSettings. Ako niama nishto zapazeno mu dava prazen text
        SharedPreferences savedSettings = getActivity().getSharedPreferences("MYPREFS",0);
        sexyMessage.setText(savedSettings.getString("FertileMessage", ""));
        mainMessage.setText(savedSettings.getString("MainMessage","Welcome! Enter your settings to start using BabyTalk!"));

        //tova e workaround za vazstanoviavane na stoinostite ot kalendara kato izpolzvam
        //shared preferences.
        restoreCalendarValuesFromSharedPrefs();
        setSexyMessage();

        //vazstanoviava saved instance state variables
        //problemat e che vinagi vrashta null i zatova izpolzvam Shared Preferences,
        // za da gi vaznanovia

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mYear = savedInstanceState.getInt(Statics.CALENDAR_YEAR);
            mMonth = savedInstanceState.getInt(Statics.CALENDAR_MONTH);
            mDay = savedInstanceState.getInt(Statics.CALENDAR_DAY);
            mAverageLengthOfMenstrualCycle =
                    savedInstanceState.getInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
            setSexyMessage();
        }
        showSexyCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sexy message izchisliava firstDayToHaveSex i LastDayToHaveSex
                // i izpisva saobshtenieto na stenata
                setSexyMessage();

                Intent intent = new Intent(getActivity().getApplicationContext(), SexyCalendar.class);
                intent.putExtra(Statics.CALENDAR_FIRST_DAY_AFTER_MENSTRUATION, firstDayToHaveSex);
                intent.putExtra(Statics.CALENDAR_LAST_DAY_BEFORE_NEXT_CYCLE, lastDayToHaveSex);
                startActivity(intent);

            }
        });
        showPrivateDaysCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetFirstDayOfCycle newDialog = new SetFirstDayOfCycle();
                newDialog.setTargetFragment(FragmentDays.this,MENSTRUAL_CALENDAR_DIALOG);
                newDialog.show(getFragmentManager(),"Welcome");
            }
        });

        sexyCalendarForGuysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SexyCalendarForGuys.class);
                startActivity(intent);
            }
        });
        return inflatedView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MENSTRUAL_CALENDAR_DIALOG) {


            if (resultCode == Activity.RESULT_OK) {
                // After Ok code.
                Bundle bundle =data.getExtras();


                mYear   =      bundle.getInt(Statics.CALENDAR_YEAR);
                mMonth  =      bundle.getInt(Statics.CALENDAR_MONTH);
                mDay    =      bundle.getInt(Statics.CALENDAR_DAY);
                mAverageLengthOfMenstrualCycle =
                        bundle.getInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
                mSendSexyCalendarUpdateToPartners =
                        bundle.getBoolean(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS);
                //sexy message izchisliava firstDayToHaveSex i LastDayToHaveSex
                // i izpisva saobshtenieto na stenata
                setSexyMessage();

                //Ako mSendSexyCalendarUpdate to parners, izprashtame saobshtenieto
                if (mSendSexyCalendarUpdateToPartners == true) {
                    //Sazdavame spisak na partniorite, za da im izpratim calendar update
                    mPartnersRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDSRELATION);
                    ParseQuery<ParseUser> query = mPartnersRelation.getQuery();
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> parseUsers, ParseException e) {
                            if (e == null) {
                                //sazdavame ArrayList s partniorite
                                final ArrayList<String> recepientIDs = new ArrayList<String>();
                                final ArrayList<String> userNames = new ArrayList<String>();
                                //izprashtame calendar update na vsichki partniori
                                SendParsePushMessagesAndParseObjects sendParse =
                                        new SendParsePushMessagesAndParseObjects();

                                //sazdavame array s partniorite. Ako imame 0 partniori celiat blok
                                //se propuska i nishto ne se sluchva
                                //izprashtame calendar update na vsichki partniori
                                for (ParseUser partner : parseUsers) {
                                    recepientIDs.add(partner.getObjectId()); //masiv s vsichki partniori
                                    userNames.add(partner.getUsername());


                                    //izprashtame message s sexy calendar
                                    sendParse.sendCalendarUpdate(mCurrentUser, recepientIDs,
                                            firstDayToHaveSex.getTime(), lastDayToHaveSex.getTime(),
                                            getActivity().getApplicationContext());
                                }
                                //izprashtame push message, che e imalo update na kalendata
                                // na partniorite
                                String message = mCurrentUser.getUsername() + " " +
                                        getString(R.string.push_notification_message_update_sexy_calendar);

                                sendParse.sendPush(recepientIDs,userNames,"",
                                        ParseConstants.TYPE_PUSH_CALENDAR,
                                        message,getActivity().getApplicationContext());


                            } else {
                                //error
                                Toast.makeText(getActivity().getApplicationContext(),
                                        R.string.error_sending_calendar_updates, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }//zatvariame proverkata dali da izprashteme sexy calendar update to parners
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // After Cancel code.
            }

        }
    }

    protected void setSexyMessage() {
        //pravia si dva kalendara. I dvata sega sochat kam parvia den ot predishnia menstrualen cikal
        firstDayToHaveSex = new GregorianCalendar(mYear,mMonth,mDay);
        lastDayToHaveSex = new GregorianCalendar(mYear,mMonth,mDay);

        //izchisliavane na dnite
        firstDayToHaveSex.add(Calendar.DAY_OF_MONTH, Statics.LENGHT_OF_MENSTRUATION);
        lastDayToHaveSex.add(Calendar.DAY_OF_MONTH, mAverageLengthOfMenstrualCycle);

        //sastaviane na message
        String first = firstDayToHaveSex.get(Calendar.DAY_OF_MONTH) + " " +
                new DateFormatSymbols().getMonths()[firstDayToHaveSex.get(Calendar.MONTH)] + " " +
                firstDayToHaveSex.get(Calendar.YEAR);

        String last = lastDayToHaveSex.get(Calendar.DAY_OF_MONTH) + " " +
                new DateFormatSymbols().getMonths()[lastDayToHaveSex.get(Calendar.MONTH)] + " " +
                lastDayToHaveSex.get(Calendar.YEAR);

        String messageToDisplay = "You are ready for sex from " + first  +
                " until " + last ;

        sexyMessage.setText(messageToDisplay);
    }




    @Override
    public void onStop() {
        super.onStop();
        //Sahraniavam shared preferences kato izlizam ot fragmenta

        SharedPreferences savedSettings = getActivity().getSharedPreferences("MYPREFS",0);
        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putString("MainMessage", mainMessage.getText().toString());
        editor.putString("FertileMessage", sexyMessage.getText().toString());

        editor.commit();


    }

    private void restoreCalendarValuesFromSharedPrefs() {

        //workaroud, zashtoto savedinstanceState vrashta null i ne moga da vazstanovia dannite za
        //kalendara vav fragmenta
        SharedPreferences savedValues = getActivity()
                .getSharedPreferences(Statics.SHARED_PREFS_CALENDAR_VALUES,0);
        mYear = savedValues.getInt(Statics.CALENDAR_YEAR,0);
        mMonth = savedValues.getInt(Statics.CALENDAR_MONTH,0);
        mDay = savedValues.getInt(Statics.CALENDAR_DAY,0);
        mAverageLengthOfMenstrualCycle =
                savedValues.getInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE,0);



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Tova po princip raboti, obache savedInstanceStave v onCrreateView e vinagi null
        i zatova ne moga da vazstanovia tia stoinosti

        outState.putInt(Statics.CALENDAR_YEAR, mYear);
        outState.putInt(Statics.CALENDAR_MONTH, mMonth);
        outState.putInt(Statics.CALENDAR_DAY, mDay);
        outState.putInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE, mAverageLengthOfMenstrualCycle);

        //workaroud, zashtoto SavedInstanceStave vinagi e null v onCreate i ne moga da vazsnanovia stoinostite
        SharedPreferences savedSettings = getActivity()
                .getSharedPreferences(Statics.SHARED_PREFS_CALENDAR_VALUES,0);

        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putInt(Statics.CALENDAR_YEAR,mYear);
        editor.putInt(Statics.CALENDAR_MONTH,mMonth);
        editor.putInt(Statics.CALENDAR_DAY,mDay);
        editor.putInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE,mAverageLengthOfMenstrualCycle);
        editor.commit();
    }

*/

}
