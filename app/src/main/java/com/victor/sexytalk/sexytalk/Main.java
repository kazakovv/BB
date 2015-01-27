package com.victor.sexytalk.sexytalk;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.DeviceRegistration;
import com.backendless.Messaging;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.Message;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushBroadcastMask;
import com.backendless.messaging.PushPolicyEnum;
import com.backendless.persistence.BackendlessDataQuery;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class Main extends ActionBarActivity implements MaterialTabListener {
    protected ViewPager pager;
    static Context context;
    protected BackendlessUser mCurrentUser;
    protected static Boolean pendingPartnerRequest;
    protected Toolbar toolbar;

    protected String MaleOrFemale;
    TextView mainMessage;

    public static final int ACTIVITY_SEND_TO = 11;

    protected MenuItem addPartner;
    protected MaterialTabHost tabHost;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.drawable.launch_icon);
        setSupportActionBar(toolbar);
        
        //vrazvame osnovnotosaobshtenie
        mCurrentUser = Backendless.UserService.CurrentUser();
        //ako niama lognat potrebitel preprashta kam log-in ekrana

        if (mCurrentUser == null) {
            //prashta ni kam login screen
            navigateToLogin();
        } else {


            //check za pending parner request
            checkForPendingParnerRequests();
            //check za pending delete requests
            checkForDeletePartnerRequest();

            //proveriavame dali e maz ili zhena
            MaleOrFemale = (String) mCurrentUser.getProperty(Statics.KEY_MALE_OR_FEMALE);
            //register device for push notifications
            final String channel = mCurrentUser.getEmail();



            //subscribe to the channel, za da poluchvam saobshtenia
            Backendless.Messaging.subscribe(channel,
                    new AsyncCallback<List<Message>>() {
                        public void handleResponse(List<Message> response) {
                            for (Message message : response) {
                                /*
                                !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                !!!!tuk se obrabotvat pristignalite saobshtenia!!!!
                                !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                 */
                                Log.d("Vic","I received sth");
                                String publisherId = message.getPublisherId();
                                if (message.getData().equals(Statics.KEY_PARTNER_REQUEST)) {
                                    //pokazvame butona za dobaviane na nov partnior
                                    addPartner.setVisible(true);
                                    pendingPartnerRequest = true;
                                } else if(message.getData().equals(Statics.KEY_PARTNER_DELETE)) {
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



            //Load all relations for users (partners, etc)
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



        }

        pager = (ViewPager) findViewById(R.id.pager);
        PagerAdapterMain pAdapter = new PagerAdapterMain(getSupportFragmentManager(), this);
        pager.setAdapter(pAdapter);

        tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);



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
                //TODO nulirame static promenlivite s poluchatelite.
                //TODO adski tapo!!!
                if( AdapterSendTo.mRecepientEmails != null) {
                    AdapterSendTo.mRecepientEmails.clear();
                    AdapterSendTo.mRecepientUserNames.clear();
                }
                String message = mCurrentUser.getProperty(Statics.KEY_USERNAME) + " " +
                        getString(R.string.send_a_kiss_message); //niakoi ti izprati celuvka
                Intent intentSendTo = new Intent(Main.this, SendTo.class);
                startActivityForResult(intentSendTo, ACTIVITY_SEND_TO);
                return true;
            case R.id.menu_send_message:
                Intent intent = new Intent(this, SendMessage.class);
                startActivity(intent);
                return true;
            case R.id.partner_request:
                Intent partnerRequest = new Intent(this,ManagePartnersMain.class);
                //slagame toya KEY, za da prevkluchim na pravilia tab ot drugata strana kato otvorim ekrana
                partnerRequest.putExtra(Statics.KEY_PARTNERS_SELECT_TAB,Statics.KEY_PARTNERS_SELECT_PENDING_REQUESTS);
                startActivity(partnerRequest);
                return true;
            case R.id.menu_sex:
                DialogFragment sexDialog = new MaleOrFemaleDialog();
                sexDialog.show(getFragmentManager(), "Welcome");

                return true;
            case R.id.menu_logout:
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void aVoid) {
                        //prashta kam login screen
                        navigateToLogin();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(Main.this,R.string.logout_error,Toast.LENGTH_LONG).show();
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

        String user = (String) mCurrentUser.getProperty(Statics.KEY_USERNAME);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_SEND_TO) {

                //tezi dva arraylist se vrashtat ot SendTo, sled kato izberem na kogo da pratim celuvka
                ArrayList<String> recepientUserNames =
                        data.getStringArrayListExtra(Statics.KEY_USERNAME);
                ArrayList<String> recepientEmails =
                        data.getStringArrayListExtra(Statics.KEY_RECEPIENT_EMAILS);



                //sazvavame string s emailite na poluchatelite
                String emailsOfRecepients="";
                int numberOfRecepients  = recepientEmails.size();
                for (int i = 0; i < numberOfRecepients ; i++) {

                    emailsOfRecepients += recepientEmails.get(i);
                    if(i < numberOfRecepients -1) {
                        emailsOfRecepients += ","; //dobaviame zapetaia ako ima oshte recepients
                    }
                }
                //message
                String someoneSendsYouAKiss = user + " " + getString(R.string.send_a_kiss_message);

                //sazdavame saobshtenieto
                final Messages kissMessage =  new Messages();
                kissMessage.setMessageType(Statics.TYPE_KISS);
                kissMessage.setLoveMessage(someoneSendsYouAKiss);
                kissMessage.setRecepientEmails(emailsOfRecepients);
                kissMessage.setSederUsername((String) Backendless.UserService.CurrentUser().getProperty(Statics.KEY_USERNAME));
                kissMessage.setSender(Backendless.UserService.CurrentUser());


                //i go izprashtame
                Backendless.Persistence.of(Messages.class).save(kissMessage, new AsyncCallback<Messages>() {
                    @Override
                    public void handleResponse(Messages messages) {
                        //TODO:!!!!!
                        //TODO: push test!!!!!
                        //TODO !!!!!!!!!!!!!

                        PublishOptions publishOptions = new PublishOptions();
                        publishOptions.putHeader( PublishOptions.ANDROID_TICKER_TEXT_TAG, "Backendless" );
                        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TITLE_TAG, getResources().getString(R.string.app_name));
                        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TEXT_TAG, "Hi");
                        DeliveryOptions deliveryOptions = new DeliveryOptions();
                        deliveryOptions.setPushPolicy(PushPolicyEnum.ONLY);
                        //publishOptions.setSubtopic("Vic");
                        deliveryOptions.addPushSinglecast("cad3a932");
                        String message_subtopic = "Vic";
                            Backendless.Messaging.publish(mCurrentUser.getEmail(),"Accept me", publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                                @Override
                                public void handleResponse(MessageStatus messageStatus) {

                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    String error = backendlessFault.getMessage();
                                }
                            });

                        Toast.makeText(Main.this,getString(R.string.send_a_kiss_toast_successful),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(Main.this,getString(R.string.send_a_kiss_toast_unsuccessful),Toast.LENGTH_LONG).show();

                    }
                });
                //send push message


            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.main_menu, menu);

        //vrazvame butona za dobaviane na novi partniori
        addPartner = menu.findItem(R.id.partner_request);
        //po podrazbirane partner request butona e nevidim,
        // no go pokazvame, ako ima pending partner request
        if(pendingPartnerRequest != null && pendingPartnerRequest == true) {
            addPartner.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected void checkForPendingParnerRequests(){

        String whereClause="email_partnerToConfirm='" + mCurrentUser.getEmail() +"'";
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereClause);
        Backendless.Data.of(PartnersAddRequest.class).find(query, new AsyncCallback<BackendlessCollection<PartnersAddRequest>>() {
            @Override
            public void handleResponse(BackendlessCollection<PartnersAddRequest> partners) {
                if(partners.getData().size()>0) {
                    //ako query vrashta rezultat, znachi ima pending request
                    pendingPartnerRequest = true;
                    //pokazvame butona za dobaviane na partniori, ako reference kam nego ne e null
                    if(addPartner != null) {
                        addPartner.setVisible(true);
                    }
                } else {
                    //ako ne varne nishto mahame butona
                    pendingPartnerRequest = false;
                    if(addPartner != null) {
                        addPartner.setVisible(false);
                    }
                }

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                pendingPartnerRequest = false;
                if(addPartner != null) {
                    addPartner.setVisible(false);
                }
            }
        });

    }

    protected void checkForDeletePartnerRequest() {
        String whereClause = "email_userDeleted='" + mCurrentUser.getEmail() + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        Backendless.Data.of(PartnerDeleteRequest.class).find(dataQuery, new AsyncCallback<BackendlessCollection<PartnerDeleteRequest>>() {
            @Override
            public void handleResponse(final BackendlessCollection<PartnerDeleteRequest> partnerDeleteRequest) {
                final List<PartnerDeleteRequest> pendingDeleteRequests = partnerDeleteRequest.getData();

                //svaliame masiv s tekushtite partniori
                BackendlessUser[] currentListWithPartners;
                if(mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                    currentListWithPartners = (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
                } else {
                    currentListWithPartners = new BackendlessUser[0];
                }

                //kopirame currentlistWithPartners v array, za da moze po-lesno da triem ot nego
                List<BackendlessUser> currentListWithPartnersArray = new ArrayList<BackendlessUser>();
                for(BackendlessUser user : currentListWithPartners) {
                    currentListWithPartnersArray.add(user);
                }
                //iztrivame partniorite koito sa pratili delete request ot currentListWithPartnersArray
                for( PartnerDeleteRequest deleteRequest : pendingDeleteRequests ) {
                    BackendlessUser userToRemove = deleteRequest.getUserDeleting();
                    //po emaila tarsim dali ima takav user v sastesvuvashtite partniori
                    //i go iztrivame, ako go namerim
                        for(int i = 0; i < currentListWithPartnersArray.size(); i++ ) {
                                String emailOfExisingPartner = currentListWithPartnersArray.get(i).getEmail();
                                if(userToRemove.getEmail().equals(emailOfExisingPartner)) {
                                //iztrivame toya partnior ot spisaka
                                currentListWithPartnersArray.remove(i);
                            }
                        }


                }
                //kopirame vsichki ostavashti partiori v novia spisak s partniori
                BackendlessUser[] newListWithPartners = new BackendlessUser[currentListWithPartnersArray.size()];
                int i = 0;
                for(BackendlessUser user : currentListWithPartnersArray) {
                    newListWithPartners[i] = user;
                    i++;
                }
                //updatevame novia spisak s partniori za tekushtia potrebitel
                mCurrentUser.setProperty(Statics.KEY_PARTNERS, newListWithPartners);
                //updatevame i na servera
                Backendless.UserService.update(mCurrentUser, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                        //iztrivame pending delete request
                        for(PartnerDeleteRequest deleteRequest:pendingDeleteRequests) {
                            Backendless.Data.of(PartnerDeleteRequest.class).remove(deleteRequest, new AsyncCallback<Long>() {
                                @Override
                                public void handleResponse(Long aLong) {
                                    /*
                                    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                    !!!TOVA E KRAIAT NA USPESHNOTO IZTRIVAME NA PARTNER!!!
                                    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                     */
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    //TODO: tr da se pomisli kakvo da se napravi v sluchai na greshka
                                }
                            });
                        }

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //TODO: ne e zle da napravim neshto, ako ima greshka s updatevamento na partionri
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //TODO: NESHTO TR DA SE NAPRAVI
            }
        });
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
