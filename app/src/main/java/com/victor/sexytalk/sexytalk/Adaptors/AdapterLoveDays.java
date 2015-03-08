package com.victor.sexytalk.sexytalk.Adaptors;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.BackendlessUser;
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.CustomDialogs.SetFirstDayOfCycle;
import com.victor.sexytalk.sexytalk.Helper.CycleStage;
import com.victor.sexytalk.sexytalk.Helper.RoundedTransformation;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;
import com.victor.sexytalk.sexytalk.UserInterfaces.ActivityChangeSexyStatus;
import com.victor.sexytalk.sexytalk.UserInterfaces.ActivitySexyCalendar;
import com.victor.sexytalk.sexytalk.UserInterfaces.FragmentLoveDays;

import java.util.Date;
import java.util.List;

public class AdapterLoveDays extends RecyclerView.Adapter<AdapterLoveDays.ContactViewHolder> {

    private List<BackendlessUser> cardsToDisplay;
    private BackendlessUser userToDisplay; //tova e potrebiteliat ot cardsToDisplay, za koito sazdavame tekushtata karta
    private Context mContext;
    private FragmentLoveDays mFragmentLoveDays;
    private static int SEX_FEMALE = 0;
    private static int SEX_MALE = 1;

    public AdapterLoveDays(List<BackendlessUser> cardsToDisplay, Context mContext, FragmentLoveDays mFragmentLoveDays) {
        this.cardsToDisplay = cardsToDisplay;
        this.mContext = mContext;
        this.mFragmentLoveDays = mFragmentLoveDays;

    }

    @Override
    public int getItemCount() {
        return cardsToDisplay.size();
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, int i) {
        userToDisplay = cardsToDisplay.get(i);
        String title = (String) userToDisplay.getProperty(Statics.KEY_USERNAME);
        contactViewHolder.vTitle.setText(title);

        //zadavame sexy status, ako ima takav
        if(userToDisplay.getProperty(Statics.KEY_SEXY_STATUS) !=null) {
            String sexyStatus = (String) userToDisplay.getProperty(Statics.KEY_SEXY_STATUS);
            contactViewHolder.vSexyStatus.setText(sexyStatus);
        }
        //Picasso e vanshta bibilioteka, koito ni pozvoliava da otvariame snimki ot internet
        //zarezdame profile pic
        if(userToDisplay.getProperty(Statics.KEY_PROFILE_PIC_PATH) !=null) {
            String profilePicPath = (String) userToDisplay.getProperty(Statics.KEY_PROFILE_PIC_PATH);
            Picasso.with(mContext)
                    .load(profilePicPath)
                    .transform(new RoundedTransformation(Statics.PICASSO_ROUNDED_CORNERS, 0))
                    .into(contactViewHolder.vProfilePic);
        }

        if(i == 0){
            //tuk sa nastroikite samo za tekushtia potrebitel
            //Tekushtiat potrebitel vinagi izliza kato parva karta
            //Seldvashtite karti sa za partniorite mu

            if(userToDisplay.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE)){
                contactViewHolder.vPrivateDays.setVisibility(View.VISIBLE);

                //pokazva cycle stage, ako si e zadala dnite
                String cyclePhaseTitle = CycleStage.determineCyclePhase(userToDisplay,mContext);
                contactViewHolder.vCyclePhase.setText(cyclePhaseTitle);
                contactViewHolder.vSexyCalendar.setOnClickListener(sexyCalendarOnClick);

            }
        //on click za promeniane na statusa za tekushtia potervitel samo
            contactViewHolder.vSexyStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActivityChangeSexyStatus.class);
                    mFragmentLoveDays.startActivityForResult(intent,Statics.UPDATE_STATUS);
                }
            });
        //on Click listener za private days. Pak samo za tekushtia potrebitel
            if(contactViewHolder.vPrivateDays != null) {
                contactViewHolder.vPrivateDays.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SetFirstDayOfCycle newDialog = new SetFirstDayOfCycle();
                        newDialog.setTargetFragment(mFragmentLoveDays, Statics.MENSTRUAL_CALENDAR_DIALOG);
                        newDialog.show(mFragmentLoveDays.getFragmentManager(),"Welcome");
                    }
                });
            }
        } else {
            //tuk sa nastroikite za partniorite na tekushtia potrebitel
            if(userToDisplay.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE)) {
                //ako e zhena
                Boolean sendSexyCalendarUpdate = (Boolean) userToDisplay.getProperty(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS);
                if(sendSexyCalendarUpdate != null && sendSexyCalendarUpdate==true){
                    String cyclePhaseTitle = CycleStage.determineCyclePhase(userToDisplay,mContext);
                    contactViewHolder.vCyclePhase.setText(cyclePhaseTitle);
                    contactViewHolder.vSexyCalendar.setOnClickListener(sexyCalendarOnClick);
                } else {
                    //pokazva saobshtenie, che ne si spodelia private days
                    String cyclePhaseTitle = mContext.getResources().getString(R.string.message_does_not_share_private_days);
                    contactViewHolder.vCyclePhase.setText(cyclePhaseTitle);
                    //skrivame butona za kalendara
                    contactViewHolder.vSexyCalendar.setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if(cardsToDisplay.get(position).getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE)){
            viewType = SEX_FEMALE;
        } else {
            viewType = SEX_MALE;
        }
        return viewType;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //v zavisimost dali e maz ili zhena pokazvame saotvetnia lyaout
        View itemView;
        if(viewType == SEX_FEMALE) {
            //pokazvame layout za zhena
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_love_days_female, viewGroup, false);
        } else {
            //pokazvame layout za maz
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_love_days_male, viewGroup, false);

        }


        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView vTitle;
        protected TextView vSexyStatus;
        protected ImageView vProfilePic;

        //za zheni
        protected Button vSexyCalendar;
        protected Button vPrivateDays;
        protected TextView vCyclePhase;

        public ContactViewHolder(View v) {
            super(v);
            vTitle = (TextView) v.findViewById(R.id.card_title);
            vSexyStatus = (TextView) v.findViewById(R.id.sexyStatus);
            vProfilePic = (ImageView) v.findViewById(R.id.profilePicture);

            //za zheni
            vSexyCalendar = (Button) v.findViewById(R.id.showSexyCalendar);
            if(vSexyCalendar != null) {
                vSexyCalendar.setTag(this);
            }
            vPrivateDays = (Button) v.findViewById(R.id.showPrivateDaysDialog);
            vCyclePhase = (TextView) v.findViewById(R.id.cyclePhase);
        }
    }

    /*
    Helper
     */



    protected View.OnClickListener sexyCalendarOnClick =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContactViewHolder holder = (ContactViewHolder) v.getTag();
            int position = holder.getPosition();

            Intent intent = new Intent(mContext, ActivitySexyCalendar.class);

            if( cardsToDisplay.get(position).getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE)) {
                //ako e zhena

                //proveriavame za greshka predi da startirame kalendara
                if(cardsToDisplay.get(position).getProperty(Statics.FIRST_DAY_OF_CYCLE) == null ||
                        cardsToDisplay.get(position).getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE) == null ) {
                    //display error message
                    Toast.makeText(mContext,R.string.general_calendar_error,Toast.LENGTH_LONG).show();
                    return;
                }
                Date firstDayOfCycle = (Date) cardsToDisplay.get(position).getProperty(Statics.FIRST_DAY_OF_CYCLE);
                int averageCycleLength = (int) cardsToDisplay.get(position).getProperty(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE);
                intent.putExtra(Statics.AVERAGE_LENGTH_OF_MENSTRUAL_CYCLE, averageCycleLength);
                intent.putExtra(Statics.FIRST_DAY_OF_CYCLE, firstDayOfCycle);
                mContext.startActivity(intent);

            }
        }
    };


}