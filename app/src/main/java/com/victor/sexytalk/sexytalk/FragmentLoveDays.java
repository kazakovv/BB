package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.Calendar;
import java.util.List;

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

    protected TextView cyclePhaseTitle;
    protected TextView cyclePhaseStatus;
    protected TextView cycleExplainationText;
    List<CycleTitles> cycleTitles; //statusite, koito se svaliat ot backendless
    protected Calendar firstDayOfCycle;

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
        cyclePhaseTitle = (TextView) getActivity().findViewById(R.id.cyclePhase);
        cyclePhaseStatus = (TextView) getActivity().findViewById(R.id.sexyStatus);
        cycleExplainationText = (TextView) getActivity().findViewById(R.id.explainationText);

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

            final int days = (int) (difference /(24 * 60 * 60 * 1000));
            int ovulation = mAverageLengthOfMenstrualCycle /2; //ovulaciata e v sredata na cikala
            final int firstDayOfOvulation = mAverageLengthOfMenstrualCycle - 14;
            final int lastDayOfOvulation = mAverageLengthOfMenstrualCycle -10;

            //Tova sa etapite ot cikala
            /*
            Follicular: right after bleeding stops, for about 7 days
            Ovulation: 3 or 4 days of the most fertile time, midway through the cycle
            Luteal: the 10 days or so after ovulation and before menstruation
            Menstruation: the 2-7 days of bleeding
            */


            //svaliame statusite ot Backendless i zadavame statusite

            Backendless.Data.of(CycleTitles.class).find(new AsyncCallback<BackendlessCollection<CycleTitles>>() {
                @Override
                public void handleResponse(BackendlessCollection<CycleTitles> cycleTitlesBackendlessCollection) {
                    cycleTitles = cycleTitlesBackendlessCollection.getData();

                    //razpredeliame dnite


                    if(days >= 0 && days <= 5 ) {
                        //bleeding

                        //pretarsvame spisaka za saotvetnia cycle stage i zadavame poletata
                        for (CycleTitles cycle : cycleTitles) {
                            String cyclePhase = cycle.getCyclePhase();
                            if (cyclePhase.equals(Statics.KEY_MENSTRUATION)) {
                                cyclePhaseTitle.setText(cycle.getCyclePhaseTitle());
                                cyclePhaseStatus.setText(cycle.getCyclePhaseStatus());
                                cycleExplainationText.setText(cycle.getCyclePhaseExplaination());
                            }

                        }


                    } else if (days > 5 && days < firstDayOfOvulation ) {
                        //folicurar phase
                        // active energetic

                        //pretarsvame spisaka za saotvetnia cycle stage i zadavame poletata
                        for (CycleTitles cycle : cycleTitles) {
                            String cyclePhase = cycle.getCyclePhase();
                            if (cyclePhase.equals(Statics.KEY_FOLLICULAR)) {
                                cyclePhaseTitle.setText(cycle.getCyclePhaseTitle());
                                cyclePhaseStatus.setText(cycle.getCyclePhaseStatus());
                                cycleExplainationText.setText(cycle.getCyclePhaseExplaination());
                            }
                        }


                    } else if (days >= firstDayOfOvulation && days <= lastDayOfOvulation) {
                        //ovulation
                        //sexy
                        //pretarsvame spisaka za saotvetnia cycle stage i zadavame poletata
                        for (CycleTitles cycle : cycleTitles) {
                            String cyclePhase = cycle.getCyclePhase();
                            if (cyclePhase.equals(Statics.KEY_OVULATION)) {
                                cyclePhaseTitle.setText(cycle.getCyclePhaseTitle());
                                cyclePhaseStatus.setText(cycle.getCyclePhaseStatus());
                                cycleExplainationText.setText(cycle.getCyclePhaseExplaination());
                            }
                        }

                    } else if (days > lastDayOfOvulation && days <= mAverageLengthOfMenstrualCycle) {
                        //luteal

                        //pretarsvame spisaka za saotvetnia cycle stage i zadavame poletata
                        for (CycleTitles cycle : cycleTitles) {
                            String cyclePhase = cycle.getCyclePhase();
                            if (cyclePhase.equals(Statics.KEY_LUTEAL)) {
                                cyclePhaseTitle.setText(cycle.getCyclePhaseTitle());
                                cyclePhaseStatus.setText(cycle.getCyclePhaseStatus());
                                cycleExplainationText.setText(cycle.getCyclePhaseExplaination());
                            }
                        }


                        //handles errors
                    } else if (days > mAverageLengthOfMenstrualCycle) {
                        //tr da se updatene
                        cyclePhaseTitle.setText("Update " + days);
                        cyclePhaseStatus.setText("Update me");

                    } else if (days < 0) {
                        cyclePhaseTitle.setText("Error baby " + days);
                        cyclePhaseStatus.setText("Error");
                    }
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    //TODO: kakvo pravim, ako ima greshka
                }
            });



        }
    }


}
