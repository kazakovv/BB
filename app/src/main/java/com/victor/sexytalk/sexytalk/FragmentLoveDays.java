package com.victor.sexytalk.sexytalk;

import android.app.Activity;
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
import com.backendless.BackendlessUser;
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.CustomDialogs.SetFirstDayOfCycle;
import com.victor.sexytalk.sexytalk.Helper.CycleStage;

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
    protected TextView mChoosePartnerLabel;

    private static final int MENSTRUAL_CALENDAR_DIALOG = 11;
    private static final int UPDATE_STATUS = 22;



    protected TextView cyclePhaseTitle;
    protected TextView cyclePhaseStatus;
    protected Calendar firstDayOfCycle;

    protected Toolbar toolbar;

    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = Backendless.UserService.CurrentUser();
        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_love_days, container, false);
        profilePic = (ImageView) inflatedView.findViewById(R.id.profilePicture);
        mChoosePartnerLabel = (TextView) inflatedView.findViewById(R.id.chooseYourPartnerLabel);


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
                    Picasso.with(getActivity()).load(existingProfilePicPath).into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.ic_action_person_black);

                }
                restoreValuesForLoggedInUser();
                cyclePhaseStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,ActivityChangeSexyStatus.class);
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
                    String existingProfilePicPath = (String) selectedPartner.getProperty(Statics.KEY_PROFILE_PIC_PATH);
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
                Intent intent = new Intent(mContext, ActivitySexyCalendar.class);

                if(mCurrentUser.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE)) {
                    //ako e zhena
                    Date firstDayOfCycle = (Date) mCurrentUser.getProperty(Statics.FIRST_DAY_OF_CYCLE);
                    int averageCycleLength = (int) mCurrentUser.getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
                    intent.putExtra(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE, averageCycleLength);
                    intent.putExtra(Statics.FIRST_DAY_OF_CYCLE, firstDayOfCycle);
                    //proveriavame za greshka predi da startirame kalendara
                    if(firstDayOfCycle == null || averageCycleLength == 0 ) {
                        //display error message

                    } else {
                        startActivity(intent);
                    }
                }else {
                    int position = listOfPartnersSpinner.getSelectedItemPosition();
                    Date firstDayOfCycle = (Date) mPartners[position].getProperty(Statics.FIRST_DAY_OF_CYCLE);
                    int averageCycleLength = (int) mPartners[position].getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
                    intent.putExtra(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE, averageCycleLength);
                    intent.putExtra(Statics.FIRST_DAY_OF_CYCLE, firstDayOfCycle);
                    //proveriavame za greshka predi da startirame kalendara
                    if(firstDayOfCycle == null || averageCycleLength == 0 ) {
                        //display error message
                    } else {
                        startActivity(intent);
                    }
                }

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
        Log.d("Vic", "selected" + item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    /*
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    !!!!!!!!!!!!!!     NACHALO NA HELPER METODITE     !!!!!!!!!!!!!!!!!!!!!!!!!!
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    */



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
        } else {
            //ako e zhena
            if(partner.getProperty(Statics.FIRST_DAY_OF_CYCLE) != null) {
                Calendar firstDayOfCycle = Calendar.getInstance();
                firstDayOfCycle.setTime((Date) partner.getProperty(Statics.FIRST_DAY_OF_CYCLE));

                String cycleTitle = CycleStage.determineCyclePhase(partner, mContext);
                cyclePhaseTitle.setText(cycleTitle);
                //determineCyclePhase(partner);
                cyclePhaseStatus.setText((String) partner.getProperty(Statics.KEY_SEXY_STATUS));
            } else {
                //nishto ne e namereno, sledovatelno partnera ne si e updatenal kalendara
                String message = partnerUsername + " " + getString(R.string.partner_hasnt_updated_calendar);

                cyclePhaseTitle.setText(" ");
                cyclePhaseStatus.setText(message);
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
               cyclePhaseStatus.setText(sexyStatus);
            }
            //na baza na stoinostite opredelia fazata
            String cycleTitle = CycleStage.determineCyclePhase(mCurrentUser, mContext);
            cyclePhaseTitle.setText(cycleTitle);
            //determineCyclePhase(mCurrentUser);
        }

    }
}
