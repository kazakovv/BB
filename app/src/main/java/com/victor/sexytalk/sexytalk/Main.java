package com.victor.sexytalk.sexytalk;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.Message;
import com.backendless.persistence.BackendlessDataQuery;


public class Main extends FragmentActivity implements ActionBar.TabListener {
    ViewPager pager;
    ActionBar actionbar;
    static Context context;
    protected BackendlessUser currentUser;
    protected static Boolean pendingPartnerRequest;

    protected String MaleOrFemale;
    TextView mainMessage;

    public static final int ACTIVITY_SEND_TO = 11;

    public static final String TAG = Main.class.getSimpleName();

    protected MenuItem addPartner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        //vrazvame osnovnotosaobshtenie
        currentUser = Backendless.UserService.CurrentUser();
        //ako niama lognat potrebitel preprashta kam log-in ekrana

        if (currentUser == null) {
            //prashta ni kam login screen
            navigateToLogin();
        } else {
            // ako ima lognat potrebitel prodalzhava natatak

            //check za pending parner request
            checkForPendingParnerRequests();

            //proveriavame dali e maz ili zhena
            MaleOrFemale = (String) currentUser.getProperty(Statics.KEY_MALE_OR_FEMALE);
            //register device for push notifications
            String GCMSenderID = "473995671207";
            String channel = currentUser.getObjectId();


            Backendless.Messaging.registerDevice(GCMSenderID,channel, new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void aVoid) {
                    Log.d("Vic","device registered for messaging");
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Log.d("Vic","device not registered " +backendlessFault.getMessage());
                }
            });

            //subscribe to the channel, za da poluchvam saobshtenia
            Backendless.Messaging.subscribe(channel,
                    new AsyncCallback<List<Message>>() {
                        public void handleResponse(List<Message> response) {
                            for (Message message : response) {
                                //tuk se obrabotvat pristignalite saobshtenia
                                String publisherId = message.getPublisherId();
                                if (message.getData().equals(Statics.KEY_PARTNER_REQUEST)) {
                                    //pokazvame butona za dobaviane na nov partnior
                                    addPartner.setVisible(true);
                                    pendingPartnerRequest = true;
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

        }

        pager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter pAdapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pAdapter);
        pager.setOffscreenPageLimit(1);
        //TODO: actionbar is depreciated in API 21
        actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.addTab(actionbar.newTab().setText(R.string.tab_chat_title).setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText(R.string.tab_days_title).setTabListener(this));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionbar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



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
                String message = currentUser.getProperty(Statics.KEY_USERNAME) + " " +
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
                /*
                Intent intentSendMessage = new Intent(this, EditPartnersActivity.class);
                startActivity(intentSendMessage);
                */
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //tuk se izprashta push message sled cakane za izprashtane na celuvka

        String user = (String) currentUser.getProperty(Statics.KEY_USERNAME);
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
                Messages kissMessage =  new Messages();
                kissMessage.setMessageType(Statics.TYPE_KISS);
                kissMessage.setLoveMessage(someoneSendsYouAKiss);
                kissMessage.setRecepientEmails(emailsOfRecepients);
                kissMessage.setSederUsername((String) Backendless.UserService.CurrentUser().getProperty(Statics.KEY_USERNAME));
                kissMessage.setSender(Backendless.UserService.CurrentUser());

                //i go izprashtame
                Backendless.Persistence.of(Messages.class).save(kissMessage, new AsyncCallback<Messages>() {
                    @Override
                    public void handleResponse(Messages messages) {
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

        String whereClause="email_partnerToConfirm='" + currentUser.getEmail() +"'";
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


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


}
