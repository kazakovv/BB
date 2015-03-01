package com.victor.sexytalk.sexytalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.BackendlessClasses.KissesCount;
import com.victor.sexytalk.sexytalk.BackendlessClasses.Messages;
import com.victor.sexytalk.sexytalk.BackendlessClasses.PartnerDeleteRequest;
import com.victor.sexytalk.sexytalk.BackendlessClasses.PartnersAddRequest;
import com.victor.sexytalk.sexytalk.Helper.BackendlessHelper;
import com.victor.sexytalk.sexytalk.Helper.SendPushMessage;
import com.victor.sexytalk.sexytalk.UserInterfaces.DefaultCallback;
import com.victor.sexytalk.sexytalk.UserInterfaces.EditProfileActivity;
import com.victor.sexytalk.sexytalk.UserInterfaces.LoginActivity;
import com.victor.sexytalk.sexytalk.UserInterfaces.SendMessage;
import com.victor.sexytalk.sexytalk.UserInterfaces.SendTo;
import com.victor.sexytalk.sexytalk.UserInterfacesSupport.PagerAdapterMain;


import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class Main extends ActionBarActivity implements MaterialTabListener {
    protected ViewPager pager;
    static Context mContext;
    protected BackendlessUser mCurrentUser;
    protected static Boolean pendingPartnerRequest;
    protected Toolbar toolbar;

    protected String MaleOrFemale;

    public static final int ACTIVITY_SEND_TO = 11;

    protected MenuItem addPartner;
    protected MaterialTabHost tabHost;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        //vrazvame osnovnotosaobshtenie
        mCurrentUser = Backendless.UserService.CurrentUser();
        //ako niama lognat potrebitel preprashta kam log-in ekrana


        if (mCurrentUser == null) {
            //prashta ni kam login screen
            navigateToLogin();
        } else {

            //TODO!!!!!

            //TODO TR da se optimizira ot kam backendless api requests
            //todo tr da se vidi neshto na servera

            //check za pending parner request
            pendingPartnerRequest = BackendlessHelper.checkForPendingParnerRequests(mCurrentUser, addPartner);
            //pokazvame ili skrivame butona za dobaviane na partniori
            if (pendingPartnerRequest == true) {
                if (addPartner != null) {
                    addPartner.setVisible(true);
                } else { //niama chakashti zaiavki za partniori
                    if (addPartner != null) {
                        addPartner.setVisible(false);
                    }
                }
                //check za pending delete requests
                BackendlessHelper.checkForDeletePartnerRequest(mCurrentUser);

                //proveriavame dali e maz ili zhena
                MaleOrFemale = (String) mCurrentUser.getProperty(Statics.KEY_MALE_OR_FEMALE);
                //register device for push notifications
                final String channel = mCurrentUser.getEmail();

                //TODO!!!!!!! causing a lot of polling requests
            /*
            //subscribe to the channel, za da poluchvam saobshtenia
            Backendless.Messaging.subscribe(channel,
                    new AsyncCallback<List<Message>>() {
                        public void handleResponse(List<Message> response) {
                            for (Message message : response) {
                                /*
                                !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                !!!!tuk se obrabotvat pristignalite saobshtenia!!!!
                                !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                 TODO tuk tr da se zatvori gornia komment

                                String publisherId = message.getPublisherId();
                                if (message.getData().equals(Statics.KEY_PARTNER_REQUEST)) {
                                    //pokazvame butona za dobaviane na nov partnior
                                    addPartner.setVisible(true);
                                    pendingPartnerRequest = true;
                                } else if (message.getData().equals(Statics.KEY_PARTNER_DELETE)) {
                                    //niakoi iska da iztrie tekushtia potrebitel kato partnior
                                    checkForDeletePartnerRequest();
                                }
                            }
                        }

                        public void handleFault(BackendlessFault fault) {
                        }
                    },
                    new AsyncCallback<Subscription>() {
                        public void handleResponse(Subscription response) {
                            Log.d("Vic", "subscribed" + response.getChannelName());
                        }

                        public void handleFault(BackendlessFault fault) {
                            Log.d("Vic", "subscription error" + fault.getMessage());
                        }
                    }
            );

*/
                //TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //Load all relations for users (partners, etc)
/*
            List<String> rels = new ArrayList<String>();
            rels.add("*");

                Backendless.Data.of(BackendlessUser.class).loadRelations(mCurrentUser,rels, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                        Log.d("Vic", "relation loaded");

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.d("Vic", "relation not loaded loaded" + backendlessFault.getMessage());

                    }
                });
*/
            }

            pager = (ViewPager) findViewById(R.id.pager);
            PagerAdapterMain pAdapter = new PagerAdapterMain(getSupportFragmentManager(), this);
            pager.setAdapter(pAdapter);

            tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
            tabHost.setTextColor(mContext.getResources().getColor(R.color.tab_text_color));

            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    // when user do a swipe the selected tab change
                    tabHost.setSelectedNavigationItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            // insert all tabs from pagerAdapter data
            for (int i = 0; i < pAdapter.getCount(); i++) {
                tabHost.addTab(
                        tabHost.newTab()
                                .setTabListener(this)
                                .setText(pAdapter.getPageTitle(i))

                );
            }
        }
    }


    protected void navigateToLogin() {
        //preprashta kam login screen
        Intent intent = new Intent(this, LoginActivity.class);

        //Celta na sledvashtite 2 reda e da ne moze da otidesh ot log-in ekrana
        //kam osnovnia ekran, ako natisnesh back butona

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //sazdavo zadacha
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //iztriva vsichki predishni zadachi.
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.menu_send_kiss:
                //SendPushMessages sadarza metoda za izprashtane na push

                String message = mCurrentUser.getProperty(Statics.KEY_USERNAME) + " " +
                        getString(R.string.send_a_kiss_message); //niakoi ti izprati celuvka
                Intent intentSendTo = new Intent(Main.this, SendTo.class);
                startActivityForResult(intentSendTo, ACTIVITY_SEND_TO);
                return true;
            case R.id.menu_send_message:
                Intent intent = new Intent(this, SendMessage.class);
                startActivity(intent);
                return true;

            case R.id.refresh:
                //refresh se sluchva v saotvetnia fragment
                return false;

            case R.id.partner_request:
                Intent partnerRequest = new Intent(this, ManagePartnersMain.class);
                //slagame toya KEY, za da prevkluchim na pravilia tab ot drugata strana kato otvorim ekrana
                partnerRequest.putExtra(Statics.KEY_PARTNERS_SELECT_TAB, Statics.KEY_PARTNERS_SELECT_PENDING_REQUESTS);
                startActivity(partnerRequest);
                return true;
            case R.id.menu_edit_profile:
                Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
                startActivity(editProfileIntent);
                return true;
            case R.id.menu_logout:
                Backendless.UserService
                        .logout(new DefaultCallback<Void>(this, getResources().getString(R.string.logout_message)) {
                            @Override
                            public void handleResponse(Void aVoid) {

                                //prashta kam login screen
                                navigateToLogin();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                super.handleFault(backendlessFault);
                                Toast.makeText(Main.this, R.string.logout_error, Toast.LENGTH_LONG).show();
                            }
                        });


                return true;

            case R.id.menu_edit_friends:
                Intent intentManagePartners = new Intent(this, ManagePartnersMain.class);
                startActivity(intentManagePartners);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //tuk se izprashta push message sled cakane za izprashtane na celuvka

        final String user = (String) mCurrentUser.getProperty(Statics.KEY_USERNAME);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_SEND_TO) {

                //tezi dva arraylist se vrashtat ot SendTo, sled kato izberem na kogo da pratim celuvka
                ArrayList<String> recepientUserNames =
                        data.getStringArrayListExtra(Statics.KEY_USERNAME);
                final ArrayList<String> recepientEmails =
                        data.getStringArrayListExtra(Statics.KEY_RECEPIENT_EMAILS);
                final ArrayList<String> deviceIds =
                        data.getStringArrayListExtra(Statics.KEY_DEVICE_ID);


                //sazvavame string s emailite na poluchatelite
                String emailsOfRecepients = "";
                int numberOfRecepients = recepientEmails.size();
                for (int i = 0; i < numberOfRecepients; i++) {

                    emailsOfRecepients += recepientEmails.get(i);
                    if (i < numberOfRecepients - 1) {
                        emailsOfRecepients += ","; //dobaviame zapetaia ako ima oshte recepients
                    }
                }


                //message
                //1. parvo tarsim kolko celuvki sa izprateni veche
                //2. sastaviame kiss i go izprashtame
                //3. uvelichavame broia na izpratenite celuvki

                //1. TARSIM BROI CELUVKI, KOITO SA IZPRATENI DOSEGA
                BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                String whereClause = "senderEmail='" + mCurrentUser.getEmail() + "'" + " AND "
                                    + "receiverEmail='" + recepientEmails.get(0) +"'";
                dataQuery.setWhereClause(whereClause);
                final String finalEmailsOfRecepients = emailsOfRecepients;
                Backendless.Data.of(KissesCount.class).find(dataQuery, new AsyncCallback<BackendlessCollection<KissesCount>>() {
                    @Override
                    public void handleResponse(final BackendlessCollection<KissesCount> kissesSent) {

                        int kissesSentAlready = 0;
                        //check dali veche sme prashtali celuvki
                        if(kissesSent.getCurrentPage().size() > 0) {
                            kissesSentAlready  = kissesSent.getCurrentPage().get(0).getNumberOfKisses();
                        }
                        final int kissNumber;
                        if (kissesSentAlready > 0) {
                           kissNumber = kissesSentAlready + 1;
                        } else {
                            kissNumber = 1;
                        }

                        //2. IZPRASHTAME KISS SAOBSHTENIETO

                        String someoneSendsYouAKiss = user + " " + getString(R.string.send_a_kiss_message);

                        //sazdavame saobshtenieto
                        final Messages kissMessage = new Messages();
                        kissMessage.setMessageType(Statics.TYPE_KISS);
                        kissMessage.setLoveMessage(someoneSendsYouAKiss);
                        kissMessage.setRecepientEmails(finalEmailsOfRecepients);
                        kissMessage.setSederUsername((String) Backendless.UserService.CurrentUser().getProperty(Statics.KEY_USERNAME));
                        kissMessage.setSender(Backendless.UserService.CurrentUser());
                        kissMessage.setKissNumber(kissNumber);


                        //i go izprashtame
                        Backendless.Persistence.of(Messages.class).save(kissMessage, new AsyncCallback<Messages>() {
                            @Override
                            public void handleResponse(Messages messages) {
                                //send push message
                                int i = 0;
                                for (String device : deviceIds) {
                                    String channel = recepientEmails.get(i); //kanalat e email na poluchatelia
                                    SendPushMessage.sendPush(device, channel, mContext, Statics.TYPE_KISS);
                                    i++;
                                }
                                Toast.makeText(Main.this, getString(R.string.send_a_kiss_toast_successful), Toast.LENGTH_LONG).show();

                                //3. UPDATEVAME TABLICATA, CHE SME IZPRATILI OSHTE EDNA CELUVKA
                                KissesCount kissToUpdate;
                                if(kissesSent.getCurrentPage().size() == 0) {
                                    //ako niama entry go sazdavame
                                    kissToUpdate = new KissesCount();
                                    kissToUpdate.setSender(mCurrentUser);
                                    kissToUpdate.setSenderEmail(mCurrentUser.getEmail());
                                    kissToUpdate.setReceiverEmail(finalEmailsOfRecepients);
                                    //TODO tr da dobavim i receiver kato Backendless user
                                } else {
                                    kissToUpdate = kissesSent.getCurrentPage().get(0);
                                }
                                kissToUpdate.setNumberOfKisses(kissNumber);

                                Backendless.Data.of(KissesCount.class).save(kissToUpdate, new AsyncCallback<KissesCount>() {
                                    @Override
                                    public void handleResponse(KissesCount kissesCount) {
                                     //uspeshno sme updatenali celukvata
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                    //ne e uspeshno updatnata, shte izleze s 1 po-malko, no kakvo da se pravi
                                    }
                                });//krai na updatevane na broi na veche izprateni celuvki

                            }//krai na uspeshtanata send a kiss

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                String error = backendlessFault.getMessage();
                                Toast.makeText(Main.this, getString(R.string.send_a_kiss_toast_unsuccessful), Toast.LENGTH_LONG).show();

                            }//krai na nesupeshnata send a kiss
                        });//krai na send a kiss


                    } //krai na upseshno data quaery za tarsene na broi izprateni celuvki

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //error finind number of kiss messages already sent
                        String error = backendlessFault.getMessage();
                        //tova e v sluchai, che niama sazdadena KissTable
                        if(backendlessFault.getCode().equals(Statics.BACKENDLESS_TABLE_NOT_FOUND_CODE)) {
                            BackendlessHelper.createTables(mCurrentUser, mCurrentUser);
                        }
                        Toast.makeText(Main.this, getString(R.string.send_a_kiss_toast_unsuccessful), Toast.LENGTH_LONG).show();
                    } //krai na neuspeshnata data query za tarsene na broi celuvki
                }); //krai na cialata data query za tarsene na broi celuvki



            } //krai na REQUESTCODE == Activity Send to
        } //krai na RESULTCODE OK
    } //krai na onactivity result


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        //vrazvame butona za dobaviane na novi partniori
        addPartner = menu.findItem(R.id.partner_request);
        //po podrazbirane partner request butona e nevidim,
        // no go pokazvame, ako ima pending partner request
        if (pendingPartnerRequest != null && pendingPartnerRequest == true) {
            addPartner.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onTabSelected(MaterialTab tab) {
        // when the tab is clicked the pager swipe content to the tab position
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

}
