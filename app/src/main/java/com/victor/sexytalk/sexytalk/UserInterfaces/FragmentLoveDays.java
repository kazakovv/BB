package com.victor.sexytalk.sexytalk.UserInterfaces;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.Adaptors.AdapterLoveDays;
import com.victor.sexytalk.sexytalk.Helper.BackendlessHelper;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor on 13/10/2014.
 */
public class FragmentLoveDays extends Fragment {
    protected RecyclerView loveDaysCards;
    protected Context mContext;
    protected BackendlessUser mCurrentUser;

    protected ProgressBar mProgressBar;
    protected RelativeLayout mFragmentLoveDaysLayout;

    protected List<BackendlessUser> cardsToDisplay;
    protected MenuItem addPartner;
    protected MenuItem mRefreshButton;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_love_days, container, false);
        mContext = inflatedView.getContext();

        loveDaysCards = (RecyclerView) inflatedView.findViewById(R.id.cardList);
        loveDaysCards.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        loveDaysCards.setLayoutManager(llm);

        mProgressBar = (ProgressBar) inflatedView.findViewById(R.id.progressBar);
        mFragmentLoveDaysLayout = (RelativeLayout) inflatedView.findViewById(R.id.layoutFragmentLoveDays);

        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
            loadCardList(mCurrentUser);
        }

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Statics.UPDATE_STATUS) {
            BackendlessUser currentUser = Backendless.UserService.CurrentUser();
            loadCardList(currentUser);
        }

        if(requestCode ==  Statics.MENSTRUAL_CALENDAR_DIALOG) {


            if (resultCode == Activity.RESULT_OK) {
                BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                loadCardList(currentUser);

                Bundle bundle = data.getExtras();
                Boolean sendSexyCalendarUpdateToPartners =
                        bundle.getBoolean(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS);
                //izchisliavam v koi etap ot cikala e i updatevame statusite


                if(sendSexyCalendarUpdateToPartners == true) {
                    //TODO: izprashtam update na partniorite
                }

            }
        }
    } //krai na onActivity result

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                //vrazvam butona za refresh, za da moga da go enable/disable
                mRefreshButton = item;
                refreshPartnersList();

                //proveriavame da delete i za pending partner request
                //proveriavame dali ne sa se updatnali partniorite na usera
                if(mCurrentUser !=null) {
                    BackendlessHelper.checkForPendingParnerRequests(mCurrentUser, addPartner);
                    BackendlessHelper.checkForDeletePartnerRequest(mCurrentUser);
                    BackendlessHelper.checkAndUpdatePartners(mCurrentUser);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        addPartner = menu.findItem(R.id.partner_request);
    }
/*
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!!!!!!!!!!!!!!!!!!1  HELPER METODI !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
 */


    protected void refreshPartnersList(){

        mRefreshButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mFragmentLoveDaysLayout.setVisibility(View.GONE);


        String whereClause = "email='" + mCurrentUser.getEmail() +"'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Data.of(BackendlessUser.class).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {


            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> user) {
                mRefreshButton.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                mFragmentLoveDaysLayout.setVisibility(View.VISIBLE);

                //tova e updatnat tekusht potrebitel
                BackendlessUser currentUser = user.getCurrentPage().get(0);
                //updatevame go lokano
                Backendless.UserService.setCurrentUser(currentUser);

                if(currentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {

                    BackendlessUser[] partners = (BackendlessUser[]) user.getCurrentPage().get(0).getProperty(Statics.KEY_PARTNERS);
                    //updatevame cardList i prezarezhdame list
                    cardsToDisplay.clear();
                    cardsToDisplay.add(currentUser);
                    for(BackendlessUser partner : partners) {
                        cardsToDisplay.add(partner);
                    }

                    //zarezdame adaptora
                    AdapterLoveDays adapter = new AdapterLoveDays(cardsToDisplay, mContext, FragmentLoveDays.this);
                    loveDaysCards.setAdapter(adapter);

                    Toast.makeText(mContext,R.string.toast_update_partners,Toast.LENGTH_LONG).show();

                } else {

                    //niama namereni partniori
                    Toast.makeText(mContext,R.string.toast_update_partners_no_partners_found,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                mRefreshButton.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                mFragmentLoveDaysLayout.setVisibility(View.VISIBLE);
                //niama kakvo da napravim
                Toast.makeText(mContext,"not refreshed...",Toast.LENGTH_LONG).show();

            }
        });

    }

    protected void loadCardList(BackendlessUser currentUser) {
        cardsToDisplay = new ArrayList<BackendlessUser>();
        cardsToDisplay.add(currentUser);

        if(currentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
           BackendlessUser[] partners = (BackendlessUser[]) currentUser.getProperty(Statics.KEY_PARTNERS);
            //updatevame cardList i prezarezhdame list

            for(BackendlessUser partner : partners) {
                cardsToDisplay.add(partner);
            }
      }
        //zarezdame adaptora
        AdapterLoveDays adapter = new AdapterLoveDays(cardsToDisplay, mContext, FragmentLoveDays.this);
        loveDaysCards.setAdapter(adapter);
    }


    /*
    protected BackendlessUser mCurrentUser;
    protected Button showPrivateDaysDialog;
    protected Spinner listOfPartnersSpinner;
    protected BackendlessUser[] mPartners; //array s partnirite
    protected ImageView profilePic;
    protected Button sexyCalendar;
    protected TextView mChoosePartnerLabel;
    protected ProgressBar mProgressBar;
    protected RelativeLayout mFragmentLoveDaysLayout;
    protected MenuItem mRefreshButton;

    private static final int MENSTRUAL_CALENDAR_DIALOG = 11;
    private static final int UPDATE_STATUS = 22;



    protected TextView cyclePhaseTitle;
    protected TextView mSexyStatus;
    protected Calendar firstDayOfCycle;

    protected ActionBar toolbar;
    protected MenuItem addPartner;

    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = Backendless.UserService.CurrentUser();
        mContext = getActivity();
        setHasOptionsMenu(true);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_love_days, container, false);
        profilePic = (ImageView) inflatedView.findViewById(R.id.profilePicture);
        mChoosePartnerLabel = (TextView) inflatedView.findViewById(R.id.chooseYourPartnerLabel);
        mProgressBar = (ProgressBar) inflatedView.findViewById(R.id.progressBar);
        mFragmentLoveDaysLayout = (RelativeLayout) inflatedView.findViewById(R.id.layoutFragmentLoveDays);


        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressBar.setVisibility(View.GONE);

        showPrivateDaysDialog = (Button) getActivity().findViewById(R.id.showPrivateDaysDialog);
        cyclePhaseTitle = (TextView) getActivity().findViewById(R.id.cyclePhase);
        mSexyStatus = (TextView) getActivity().findViewById(R.id.sexyStatus);
        listOfPartnersSpinner = (Spinner) getActivity().findViewById(R.id.listOfPartners);
        sexyCalendar = (Button) getActivity().findViewById(R.id.showSexyCalendar);

        //TODO: triabva da se optimizira, zashtoto taka se vrazva neprekasnato kam servera

        if(mCurrentUser != null) {
            if (mCurrentUser.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE)) {
                showPrivateDaysDialog.setVisibility(View.INVISIBLE);
                listOfPartnersSpinner.setVisibility(View.VISIBLE);
                mChoosePartnerLabel.setVisibility(View.VISIBLE);
                //ako e maz samo zarezhdame partniorite v spinnera

                findPartnersAndPopulateSpinner();

            } else {
                showPrivateDaysDialog.setVisibility(View.VISIBLE);
                listOfPartnersSpinner.setVisibility(View.INVISIBLE);
                mChoosePartnerLabel.setVisibility(View.INVISIBLE);

                //ako e zhena vazstanoviavame kalendara: Year, month,day,cyclelength

                if(mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH) != null) {
                    //zarezda profile pic ako ima takava
                    String existingProfilePicPath = (String) mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH);
                    Picasso.with(getActivity())
                            .load(existingProfilePicPath)
                            .transform(new RoundedTransformation(Statics.PICASSO_ROUNDED_CORNERS, 0))
                            .into(profilePic);
                    //Picasso.with(getActivity()).load(existingProfilePicPath).into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.ic_action_person_black);

                }
                restoreValuesForLoggedInUser();
                mSexyStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ActivityChangeSexyStatus.class);
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
                //skrivame SexyCalendar, ako selected partner e maz
                if(selectedPartner.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE)) {
                    sexyCalendar.setVisibility(View.INVISIBLE);
                } else {
                    //ako e zhena pokazvame sexy calendara
                    sexyCalendar.setVisibility(View.VISIBLE);
                }

                if(selectedPartner.getProperty(Statics.KEY_PROFILE_PIC_PATH) != null) {
                    String existingProfilePicPath = (String) selectedPartner.getProperty(Statics.KEY_PROFILE_PIC_PATH);
                    Picasso.with(getActivity())
                            .load(existingProfilePicPath)
                            .transform(new RoundedTransformation(Statics.PICASSO_ROUNDED_CORNERS,0))
                            .into(profilePic);
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
                Intent intent = new Intent(mContext, ActivitySexyCalendar.class);

                if(mCurrentUser.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE)) {
                    //ako e zhena

                    //proveriavame za greshka predi da startirame kalendara
                    if(mCurrentUser.getProperty(Statics.FIRST_DAY_OF_CYCLE) == null ||
                            mCurrentUser.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE) == null ) {
                        //display error message
                        Toast.makeText(mContext,R.string.general_calendar_error,Toast.LENGTH_LONG).show();
                        return;
                    }
                        Date firstDayOfCycle = (Date) mCurrentUser.getProperty(Statics.FIRST_DAY_OF_CYCLE);
                        int averageCycleLength = (int) mCurrentUser.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
                        intent.putExtra(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE, averageCycleLength);
                        intent.putExtra(Statics.FIRST_DAY_OF_CYCLE, firstDayOfCycle);
                        startActivity(intent);

                }else {
                    int position = listOfPartnersSpinner.getSelectedItemPosition();

                    //proveriavame za greshka predi da startirame kalendara
                    if(mPartners == null) {
                        Toast.makeText(mContext,R.string.no_partners_error,Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(mPartners[position].getProperty(Statics.FIRST_DAY_OF_CYCLE) == null ||
                       mPartners[position].getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE) == null ) {
                        //display error message
                        String error = mPartners[position].getProperty(Statics.KEY_USERNAME) + " " +
                                getResources().getString(R.string.partner_hasnt_updated_calendar);
                        Toast.makeText(mContext,error,Toast.LENGTH_LONG).show();
                        return;
                    }
                        Date firstDayOfCycle = (Date) mPartners[position].getProperty(Statics.FIRST_DAY_OF_CYCLE);
                        int averageCycleLength = (int) mPartners[position].getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
                        intent.putExtra(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE, averageCycleLength);
                        intent.putExtra(Statics.FIRST_DAY_OF_CYCLE, firstDayOfCycle);
                        startActivity(intent);

                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPDATE_STATUS) {

            String status = data.getStringExtra(Statics.KEY_SET_STATUS);
            mSexyStatus.setText(status);
        }

        if(requestCode == MENSTRUAL_CALENDAR_DIALOG) {


            if (resultCode == Activity.RESULT_OK) {

                Bundle bundle = data.getExtras();
                Boolean sendSexyCalendarUpdateToPartners =
                        bundle.getBoolean(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS);
               //izchisliavam v koi etap ot cikala e i updatevame statusite

                String titleCycle = bundle.getString(Statics.TITLE_CYCLE);
                cyclePhaseTitle.setText(titleCycle);

                if(sendSexyCalendarUpdateToPartners == true) {
                //TODO: izprashtam update na partniorite
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                //vrazvam butona za refresh, za da moga da go enable/disable
                mRefreshButton = item;
                refreshPartnersList();

                //proveriavame da delete i za pending partner request
                //proveriavame dali ne sa se updatnali partniorite na usera
                if(mCurrentUser !=null) {
                    BackendlessHelper.checkForPendingParnerRequests(mCurrentUser, addPartner);
                    BackendlessHelper.checkForDeletePartnerRequest(mCurrentUser);
                    BackendlessHelper.checkAndUpdatePartners(mCurrentUser);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        addPartner = menu.findItem(R.id.partner_request);
    }

    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    !!!!!!!!!!!!!!     NACHALO NA HELPER METODITE     !!!!!!!!!!!!!!!!!!!!!!!!!!
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    protected void refreshPartnersList(){

        mRefreshButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mFragmentLoveDaysLayout.setVisibility(View.GONE);


        String whereClause = "email='" + mCurrentUser.getEmail() +"'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Data.of(BackendlessUser.class).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {


            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> users) {
                if(users.getCurrentPage().get(0).getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                    mRefreshButton.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);
                    mFragmentLoveDaysLayout.setVisibility(View.VISIBLE);

                    BackendlessUser[] partners = (BackendlessUser[]) users.getCurrentPage().get(0).getProperty(Statics.KEY_PARTNERS);
                    //updatevame lokalno
                    mCurrentUser.setProperty(Statics.KEY_PARTNERS, partners);
                    Backendless.UserService.setCurrentUser(mCurrentUser);
                    //updatevame spinnera s statusite
                    if(mCurrentUser.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE)) {
                        findPartnersAndPopulateSpinner();
                    }

                    Toast.makeText(mContext,R.string.toast_update_partners,Toast.LENGTH_LONG).show();

                } else {
                    mRefreshButton.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);
                    mFragmentLoveDaysLayout.setVisibility(View.VISIBLE);
                    //niama namereni partniori
                    Toast.makeText(mContext,R.string.toast_update_partners_no_partners_found,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                mRefreshButton.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                mFragmentLoveDaysLayout.setVisibility(View.VISIBLE);
                //niama kakvo da napravim
                Toast.makeText(mContext,"not refreshed...",Toast.LENGTH_LONG).show();

            }
        });

    }

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
            mSexyStatus.setText(R.string.no_partners_message); //Add your partners to start using SexyTalk
        }
    }

    //helper metod, koito updateva kategoriite i saobshteniata na osnovnia ekran
    private void updateMessagesForPartner(final BackendlessUser partner) {
      final String partnerUsername = (String) partner.getProperty(Statics.KEY_USERNAME);

        //ako e maz zapalvame s default saobshtenie za maz

        if(partner.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE)) {
            //ako e maz
            String message = partnerUsername + " " + getString(R.string.sexy_calendar_default_message_guys);

            cyclePhaseTitle.setText(message);
            mSexyStatus.setText(" ");
        } else {
            //ako e zhena
            if(partner.getProperty(Statics.FIRST_DAY_OF_CYCLE) != null) {
                Calendar firstDayOfCycle = Calendar.getInstance();
                firstDayOfCycle.setTime((Date) partner.getProperty(Statics.FIRST_DAY_OF_CYCLE));

                String cycleTitle = CycleStage.determineCyclePhase(partner, mContext);
                cyclePhaseTitle.setText(cycleTitle);
                //determineCyclePhase(partner);
                mSexyStatus.setText((String) partner.getProperty(Statics.KEY_SEXY_STATUS));
            } else {
                //nishto ne e namereno, sledovatelno partnera ne si e updatenal kalendara
                String message = partnerUsername + " " + getString(R.string.partner_hasnt_updated_calendar);

                cyclePhaseTitle.setText(" ");
                mSexyStatus.setText(message);
            }
        }//krai na if statement maz ili zhena
    }

    private void restoreValuesForLoggedInUser() {
        if(mCurrentUser.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE) != null &&
                mCurrentUser.getProperty(Statics.FIRST_DAY_OF_CYCLE) != null) {
            Date firstDayOfCycle = (Date) mCurrentUser.getProperty(Statics.FIRST_DAY_OF_CYCLE);
            Calendar firstDay = new GregorianCalendar();
            firstDay.setTime(firstDayOfCycle);


            if(mCurrentUser.getProperty(Statics.KEY_SEXY_STATUS) !=null) {
            String sexyStatus = (String) mCurrentUser.getProperty(Statics.KEY_SEXY_STATUS);
               mSexyStatus.setText(sexyStatus);
            }
            //na baza na stoinostite opredelia fazata
            String cycleTitle = CycleStage.determineCyclePhase(mCurrentUser, mContext);
            cyclePhaseTitle.setText(cycleTitle);
            //determineCyclePhase(mCurrentUser);
        }

    }

    */
}
