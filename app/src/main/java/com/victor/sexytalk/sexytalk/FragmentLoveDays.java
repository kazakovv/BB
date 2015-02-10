package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.BackendlessClasses.CycleTitles;
import com.victor.sexytalk.sexytalk.CustomDialogs.SetFirstDayOfCycle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentLoveDays extends Fragment {
    protected BackendlessUser mCurrentUser;
    protected Button showPrivateDaysDialog;
    protected Spinner listOfPartnersSpinner;
    protected BackendlessUser[] mPartners; //array s partnirite
    protected ImageView profilePic;
    protected Button sexyCalendar;

    private static final int MENSTRUAL_CALENDAR_DIALOG = 11;
    private static final int UPDATE_STATUS = 22;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mAverageLengthOfMenstrualCycle;
    private boolean mSendSexyCalendarUpdateToPartners;

    protected TextView cyclePhaseTitle;
    protected TextView cyclePhaseStatus;
    protected TextView cycleExplainationText;
    protected List<CycleTitles> cycleTitles; //statusite, koito se svaliat ot backendless
    protected Calendar firstDayOfCycle;

    protected Toolbar toolbar;

    protected Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = Backendless.UserService.CurrentUser();
        context = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_love_days, container, false);
        profilePic = (ImageView) inflatedView.findViewById(R.id.profilePicture);



        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showPrivateDaysDialog = (Button) getActivity().findViewById(R.id.showPrivateDaysDialog);
        cyclePhaseTitle = (TextView) getActivity().findViewById(R.id.cyclePhase);
        cyclePhaseStatus = (TextView) getActivity().findViewById(R.id.sexyStatus);
        cycleExplainationText = (TextView) getActivity().findViewById(R.id.explanationText);
        listOfPartnersSpinner = (Spinner) getActivity().findViewById(R.id.listOfPartners);
        sexyCalendar = (Button) getActivity().findViewById(R.id.showSexyCalendar);

        //TODO: triabva da se optimizira, zashtoto taka se vrazva neprekasnato kam servera

        if(mCurrentUser != null) {
            if (mCurrentUser.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE)) {
                showPrivateDaysDialog.setVisibility(View.INVISIBLE);
                listOfPartnersSpinner.setVisibility(View.VISIBLE);

                //ako e maz samo zarezhdame partniorite v spinnera
                mYear = 0;
                mMonth = 0;
                mDay = 0;
                mAverageLengthOfMenstrualCycle=0;
                findPartnersAndPopulateSpinner();

            } else {
                showPrivateDaysDialog.setVisibility(View.VISIBLE);
                listOfPartnersSpinner.setVisibility(View.INVISIBLE);
                //ako e zhena vazstanoviavame kalendara: Year, month,day,cyclelength
                restoreValuesForLoggedInUser();
                cyclePhaseStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,ActivityChangeSexyStatus.class);
                        startActivityForResult(intent, UPDATE_STATUS);
                    }
                });
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

        listOfPartnersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                BackendlessUser selectedPartner = mPartners[position];
                //Kato se izpere partner
                // vikame helper metod, za da updatenem statusite, messages, etc.
                updateMessagesForPartner(selectedPartner);

                if(selectedPartner.getProperty(Statics.KEY_PROFILE_PIC_PATH) != null) {
                    String existingProfilePicPath = (String) mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH);
                    Picasso.with(getActivity()).load(existingProfilePicPath).into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.ic_action_person_black);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        sexyCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivitySexyCalendar.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPDATE_STATUS) {

            String status = data.getStringExtra(Statics.KEY_SET_STATUS);
            cyclePhaseStatus.setText(status);
        }

        if(requestCode == MENSTRUAL_CALENDAR_DIALOG) {


            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();

                mYear   =      bundle.getInt(Statics.CALENDAR_YEAR);
                mMonth  =      bundle.getInt(Statics.CALENDAR_MONTH); //mesec -1, Jan e 0, Dec e 11
                mDay    =      bundle.getInt(Statics.CALENDAR_DAY);
                mAverageLengthOfMenstrualCycle =
                        bundle.getInt(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
                mSendSexyCalendarUpdateToPartners =
                        bundle.getBoolean(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS);

                //izchisliavam v koi etap ot cikala e i updatevame statusite
                determineCyclePhase(mYear, mMonth, mDay, mAverageLengthOfMenstrualCycle);


                if(mSendSexyCalendarUpdateToPartners == true) {
                //TODO: izprashtam update na partniorite
                }
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Vic", "selected" + item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    /*
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    !!!!!!!!!!!!!!     NACHALO NA HELPER METODITE     !!!!!!!!!!!!!!!!!!!!!!!!!!
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    */

    //Helper metod
    protected void determineCyclePhase(int year, int month, int day, int averageLengthOfMenstruation) {
        //izchisliava v koi etap ot cikala e i promenia saobshtenieto
        if(year != 0 && day != 0 && averageLengthOfMenstruation != 0) {
            //firstDayOfCycle = new GregorianCalendar(mYear, mMonth, mDay);
            firstDayOfCycle = Calendar.getInstance();
            firstDayOfCycle.set(Calendar.YEAR, year);
            firstDayOfCycle.set(Calendar.MONTH, month);
            firstDayOfCycle.set(Calendar.DAY_OF_MONTH, day);
            Calendar now = Calendar.getInstance();

            long difference = now.getTimeInMillis() - firstDayOfCycle.getTimeInMillis();

            final int days = (int) (difference /(24 * 60 * 60 * 1000));
            final int firstDayOfOvulation = averageLengthOfMenstruation - 14;
            final int lastDayOfOvulation = averageLengthOfMenstruation -10;

            //Tova sa etapite ot cikala
            /*
            Follicular: right after bleeding stops, for about 7 days
            Ovulation: 3 or 4 days of the most fertile time, midway through the cycle
            Luteal: the 10 days or so after ovulation and before menstruation
            Menstruation: the 2-7 days of bleeding
            */



            //svaliame statusite ot Backendless i updatevame statusite na ekrana

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
                                //cyclePhaseStatus.setText(cycle.getCyclePhaseStatus());
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
                                //cyclePhaseStatus.setText(cycle.getCyclePhaseStatus());
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
                               // cyclePhaseStatus.setText(cycle.getCyclePhaseStatus());
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
                               // cyclePhaseStatus.setText(cycle.getCyclePhaseStatus());
                                cycleExplainationText.setText(cycle.getCyclePhaseExplaination());
                            }
                        }

                        //handles errors
                        //TODO:tr da se opravi
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
        } else {
        //ako sa nuli znachi partniorat ne si e updatenal kalendara
            cyclePhaseTitle.setText(" ");
            cyclePhaseStatus.setText(R.string.general_calendar_error);
            cycleExplainationText.setText(" ");
        }
    } //krai na determine cycle phase helper method




    //Helper metod namira spisak s partniorite i dobavia imenata im v spinnera

    protected void findPartnersAndPopulateSpinner() {
        //TODO: tr da go opravia da ne tarsi v backendless
        if(mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
            mPartners =
                    (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
            //sazdavame spisak s usernames
            List<String> usernamesSpinnerArray = new ArrayList<String>();
            for (BackendlessUser partner : mPartners) {
                usernamesSpinnerArray.add(partner.getProperty(Statics.KEY_USERNAME).toString());
            }
            //zapalvame list
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item, usernamesSpinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listOfPartnersSpinner.setAdapter(adapter);

        } else {
            //niamame dobaveni partniori, izkarvame niakakvo saobshtenie
            cyclePhaseTitle.setText(" ");
            cyclePhaseStatus.setText(R.string.no_partners_message); //Add your partners to start using SexyTalk
            cycleExplainationText.setText(" ");
        }
    }

    //helper metod, koito updateva kategoriite i saobshteniata na osnovnia ekran
    private void updateMessagesForPartner(final BackendlessUser partner) {
      final String partnerUsername = (String) partner.getProperty(Statics.KEY_USERNAME);

        //ako e maz zapalvame s default saobshtenie za maz

        if(partner.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE)) {
            //ako e maz
            String message = partnerUsername + " " + getString(R.string.sexy_calendar_default_message_guys);

            cyclePhaseTitle.setText(" ");
            cyclePhaseStatus.setText(message);
            cycleExplainationText.setText(" ");
        } else {
            //ako e zhena
            if(partner.getProperty(Statics.FIRST_DAY_OF_CYCLE) != null) {
                Calendar firstDayOfCycle = Calendar.getInstance();
                firstDayOfCycle.setTime((Date) partner.getProperty(Statics.FIRST_DAY_OF_CYCLE));
                int year = firstDayOfCycle.get(Calendar.YEAR);
                int month = firstDayOfCycle.get(Calendar.MONTH);
                int day = firstDayOfCycle.get(Calendar.DAY_OF_MONTH);
                int averageCyclelength = (int) partner.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
                determineCyclePhase(year, month, day, averageCyclelength);
                cyclePhaseStatus.setText((String) partner.getProperty(Statics.KEY_SEXY_STATUS));
            } else {
                //nishto ne e namereno, sledovatelno partnera ne si e updatenal kalendara
                String message = partnerUsername + " " + getString(R.string.partner_hasnt_updated_calendar);

                cyclePhaseTitle.setText(" ");
                cyclePhaseStatus.setText(message);
                cycleExplainationText.setText(" ");
            }


        }//krai na if statement maz ili zhena


    }

    private void restoreValuesForLoggedInUser() {
        if(mCurrentUser.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE) != null) {
            mAverageLengthOfMenstrualCycle = (int) mCurrentUser.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
            Date firstDayOfCycle = (Date) mCurrentUser.getProperty(Statics.FIRST_DAY_OF_CYCLE);
            Calendar firstDay = new GregorianCalendar();
            firstDay.setTime(firstDayOfCycle);
            mYear = firstDay.get(Calendar.YEAR);
            mMonth = firstDay.get(Calendar.MONTH);
            mDay = firstDay.get(Calendar.DAY_OF_MONTH);

            if(mCurrentUser.getProperty(Statics.KEY_SEXY_STATUS) !=null) {
            String sexyStatus = (String) mCurrentUser.getProperty(Statics.KEY_SEXY_STATUS);
               cyclePhaseStatus.setText(sexyStatus);
            }
            //na baza na stoinostite opredelia fazata
            determineCyclePhase(mYear, mMonth, mDay, mAverageLengthOfMenstrualCycle);
        }

    }
}
