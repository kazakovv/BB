package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentLoveDays extends Fragment {
    protected BackendlessUser currentUser;
    protected Button showPrivateDaysDialog;
    protected Spinner listOfPartnersSpinner;
    BackendlessUser[] mPartners; //array s partnirite

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
        listOfPartnersSpinner = (Spinner) getActivity().findViewById(R.id.listOfPartners);


        //TODO: triabva da se optimizira, zashtoto taka se vrazva neprekasnato kam servera
        //TODO: triabva da razkaram shared prefs i da se vrazva kam servera otnachalo vseki pat

        if(currentUser != null) {
            if (currentUser.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE)) {
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
            }
        }

        //zapalvame spinnera s imenata na partnirite


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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

        //sashtoto kato SendTo metoda
        //tarsim spisak s partniori

        BackendlessUser currentUser = Backendless.UserService.CurrentUser();

        String whereClause = "email='" + currentUser.getEmail() + "'";

        BackendlessDataQuery query = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        query.setWhereClause(whereClause);
        queryOptions.addRelated( "partners" );
        queryOptions.addRelated( "partners.RELATION-OF-RELATION" );
        query.setQueryOptions( queryOptions );


        Backendless.Data.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> partners) {

                if(partners.getData().size() > 0) {
                    //spisakat sadarza samo 1 potrebitel - tekushtiat
                    List<BackendlessUser> listOfPartners = partners.getData();

                    //Vzimame spisakat s partniorite kato izposlvame .getProperty("partners") na tekushtiat potrebitel
                    //s instanceof proveriavame dali ima zadadeni partniori
                    if(listOfPartners.get(0).getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                        mPartners =
                                (BackendlessUser[]) listOfPartners.get(0).getProperty(Statics.KEY_PARTNERS);
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
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(getActivity(), R.string.general_server_error,Toast.LENGTH_LONG).show();
            }
        });
    }

    //helper metod, koito updateva kategoriite i saobshteniata na osnovnia ekran
    private void updateMessagesForPartner(final BackendlessUser partner) {
      final String partnerUsername = (String) partner.getProperty(Statics.KEY_USERNAME);
      String partnerEmail = partner.getEmail();
      String whereClause = "senderEmail='" + partnerEmail +"'";
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereClause);
        Backendless.Data.of(CycleDays.class).find(query, new AsyncCallback<BackendlessCollection<CycleDays>>() {
            @Override
            public void handleResponse(BackendlessCollection<CycleDays> cycleStatuses) {
                if(cycleStatuses.getData().size() > 0) {
                    List<CycleDays> statuses = cycleStatuses.getData();
                    //triabva da ima samo edno savpadenie poneze tarsim po email
                    //zatova list triabva da e ot samo edin element

                    Calendar firstDayOfCycle = Calendar.getInstance();
                    firstDayOfCycle.setTime(statuses.get(0).getFirstDayOfCycle());
                    int year = firstDayOfCycle.get(Calendar.YEAR);
                    int month = firstDayOfCycle.get(Calendar.MONTH);
                    int day = firstDayOfCycle.get(Calendar.DAY_OF_MONTH);
                    int averageCyclelength = statuses.get(0).getAverageCycleLength();

                    //vikame helper metod, za da updatenem statusite
                    determineCyclePhase(year,month,day,averageCyclelength);
                    //zadavame personaliziranoto saobshtenie
                    cyclePhaseStatus.setText(statuses.get(0).getSatusText());

                } else {
                    //nishto ne e namereno, sledovatelno partnera ne si e updatenal kalendara
                    String message = partnerUsername + " " + getString(R.string.partner_hasnt_updated_calendar);

                    cyclePhaseTitle.setText(" ");
                    cyclePhaseStatus.setText(message);
                    cycleExplainationText.setText(" ");

                    //po-dolu e drug variant za error message, no gornoto e po-personalizirano
                    //determineCyclePhase(0,0,0,0);

                }
            }
            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(getActivity(),R.string.general_server_error,Toast.LENGTH_LONG).show();
            }
        });

    }

    private void restoreValuesForLoggedInUser() {
        BackendlessUser currentUser = Backendless.UserService.CurrentUser();
        String currentUserEmail = currentUser.getEmail();
        String whereClause="senderEmail='" + currentUserEmail + "'";
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereClause);

        Backendless.Data.of(CycleDays.class).find(query, new AsyncCallback<BackendlessCollection<CycleDays>>() {
            @Override
            public void handleResponse(BackendlessCollection<CycleDays> cycleDays) {
                if(cycleDays.getData().size() > 0) { //proveriava dali ima data
                    mAverageLengthOfMenstrualCycle = cycleDays.getCurrentPage().get(0).getAverageCycleLength();
                    Date firstDayOfCycle = cycleDays.getCurrentPage().get(0).getFirstDayOfCycle();
                    Calendar firstDay = new GregorianCalendar();
                    firstDay.setTime(firstDayOfCycle);

                    mYear = firstDay.get(Calendar.YEAR);
                    mMonth = firstDay.get(Calendar.MONTH);
                    mDay = firstDay.get(Calendar.DAY_OF_MONTH);
                    //na baza na stoinostite opredelia fazata
                    determineCyclePhase(mYear, mMonth, mDay, mAverageLengthOfMenstrualCycle);
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.general_error_title)
                        .setMessage(R.string.load_settings_error)
                        .setPositiveButton(R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
