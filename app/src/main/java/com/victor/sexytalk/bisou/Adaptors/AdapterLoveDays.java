package com.victor.sexytalk.bisou.Adaptors;

import android.content.Context;
import android.content.Intent;
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
import com.victor.sexytalk.bisou.CustomDialogs.SetFirstDayOfCycle;
import com.victor.sexytalk.bisou.Helper.CycleStage;
import com.victor.sexytalk.bisou.Helper.RoundedTransformation;
import com.victor.sexytalk.bisou.R;
import com.victor.sexytalk.bisou.Statics;
import com.victor.sexytalk.bisou.UserInterfaces.ActivityChangeSexyStatus;
import com.victor.sexytalk.bisou.UserInterfaces.ActivitySexyCalendar;
import com.victor.sexytalk.bisou.UserInterfaces.FragmentLoveDays;

import java.util.Date;
import java.util.List;

public class AdapterLoveDays extends RecyclerView.Adapter<AdapterLoveDays.ContactViewHolder> {

    private List<BackendlessUser> cardsToDisplay;
    private BackendlessUser userToDisplay; //tova e potrebiteliat ot cardsToDisplay, za koito sazdavame tekushtata karta
    private Context mContext;
    private FragmentLoveDays mFragmentLoveDays;
    private static int SEX_FEMALE = 0;
    private static int SEX_MALE = 1;
    private static int CURRENT_USER_MALE = 2;
    private static int CURRENT_USER_FEMALE = 3;

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
        } else {
            //ako ne si e updatenal statusa zadavame message v savisimost ot tova dali partner e maz ili zhena

            //parvo obache proveriavame dali partner e razlichen ot tekushtia user.
            //ako e razlichen proveniame saobstehnieto, ako ne go ostaviame
            if (i > 0) {
            //tekushtiat user e parviat v spisaka. T.e. ako i >0 znachi stava vapros za partior, a ne za current user
                String message = (String) userToDisplay.getProperty(Statics.KEY_USERNAME) + " ";
                if (userToDisplay.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE)) {
                    message = message + mContext.getResources().getString(R.string.sexy_status_not_set_female);
                } else {
                    message = message + mContext.getResources().getString(R.string.sexy_status_not_set_male);
                }
                contactViewHolder.vSexyStatus.setText(message);
            } //krai na else dali sexy status e null
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

        int viewType = contactViewHolder.getItemViewType();
        if(viewType == CURRENT_USER_FEMALE || viewType == CURRENT_USER_MALE){
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
                Boolean sendSexyCalendarUpdate = (Boolean) userToDisplay
                        .getProperty(Statics.SEND_SEXY_CALENDAR_UPDATE_TO_PARTNERS);
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
    } //krai na on bindviewholder

    @Override
    public int getItemViewType(int position) {
        int viewType=0;
        //proveriavame dali ne e tekushtia potrebitel
        if(position == 0){
            //tekushtia potrebitel e tova
            if(cardsToDisplay.get(position).getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE))
            viewType = CURRENT_USER_FEMALE;

            if(cardsToDisplay.get(position).getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE))
                viewType = CURRENT_USER_MALE;

        } else {

            if (cardsToDisplay.get(position).getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE)) {
                viewType = SEX_FEMALE;
            } else if (cardsToDisplay.get(position).getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_MALE)) {
                viewType = SEX_MALE;
            }
        }
        return viewType;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //pokazvame saotvetnia lyaout v zavisimost dali e maz ili zhena
        // i dali e tek potrebitel ili partnior na tek potrebitel

        View itemView=null;
        if(viewType == SEX_FEMALE) {
            //pokazvame layout za zhena
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_love_days_female, viewGroup, false);
        } else if( viewType ==SEX_MALE) {
            //pokazvame layout za maz
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_love_days_male, viewGroup, false);

        } else if(viewType == CURRENT_USER_FEMALE){
            //tekusht potrebitel zhena
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_love_days_current_user_female, viewGroup, false);

        } else if(viewType == CURRENT_USER_MALE) {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.item_love_days_current_user_male, viewGroup, false);
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