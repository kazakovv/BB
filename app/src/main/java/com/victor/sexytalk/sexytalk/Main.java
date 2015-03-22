package com.victor.sexytalk.sexytalk;


import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;


import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.Adaptors.AdapterNavigationDrawer;
import com.victor.sexytalk.sexytalk.CustomDialogs.ChangePassword;
import com.victor.sexytalk.sexytalk.CustomDialogs.ChangeProfilePic;
import com.victor.sexytalk.sexytalk.CustomDialogs.ChangeUsername;
import com.victor.sexytalk.sexytalk.CustomDialogs.GuyOrGirlDialog;
import com.victor.sexytalk.sexytalk.CustomDialogs.SetBirthday;
import com.victor.sexytalk.sexytalk.Helper.BackendlessHelper;
import com.victor.sexytalk.sexytalk.Helper.BackendlessMessage;
import com.victor.sexytalk.sexytalk.Helper.RoundedTransformation;
import com.victor.sexytalk.sexytalk.UserInterfaces.DefaultCallback;
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
    protected Toolbar toolbar;

    protected String MaleOrFemale;

    public static final int ACTIVITY_SEND_TO = 11;

    protected MenuItem addPartner;

    protected MaterialTabHost tabHost;

    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected ListView mDrawerList;

    private LinearLayout mDrawerLinear;
    private Button logoutButtonNavigationDrawer;

    public static int CHOOSE_PHOTO_REQUEST = 222;
    public static int TAKE_PHOTO_REQUEST = 333;

    protected ChangeProfilePic changeProfilePic;//tova se izpolzva za onactivity result ako smeniame profile pic

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
            //zarezdame navigation drawer
            setUpDrawer();

            //TODO!!!!!
            //TODO TR da se optimizira ot kam backendless api requests
            //todo tr da se vidi neshto na servera

            //check za pending parner request
            BackendlessHelper.checkForPendingParnerRequests(mCurrentUser, addPartner);
            //check za pending delete requests
            BackendlessHelper.checkForDeletePartnerRequest(mCurrentUser);
            //updatevame partnirite
            BackendlessHelper.checkAndUpdatePartners(mCurrentUser);

                //proveriavame dali e maz ili zhena
                MaleOrFemale = (String) mCurrentUser.getProperty(Statics.KEY_MALE_OR_FEMALE);



            pager = (ViewPager) findViewById(R.id.pager);
            PagerAdapterMain pAdapter = new PagerAdapterMain(getSupportFragmentManager(), this);
            pager.setAdapter(pAdapter);


            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    mDrawerToggle.syncState();
                }

                @Override
                public void onPageSelected(int position) {
                    // when user do a swipe the selected tab change
                    tabHost.setSelectedNavigationItem(position);
                    mDrawerToggle.syncState();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    mDrawerToggle.syncState();
                }
            });

            tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
            tabHost.setTextColor(mContext.getResources().getColor(R.color.tab_text_color));

            // insert all tabs from pagerAdapter data

            for (int i = 0; i < pAdapter.getCount(); i++) {
                tabHost.addTab(
                        tabHost.newTab()
                                .setTabListener(this)
                                .setText(pAdapter.getPageTitle(i))

                );
            }



        }

        //proveriavame dali Main activity ne e startirana ot Push receiver i ne e zadaden
        //flag, koito da prenasochva kam niakoi ekran
        checkIntentFlags();
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.

        if(mDrawerToggle !=null) {


            mDrawerToggle.syncState();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }
    protected void checkIntentFlags(){
        //proerivame dali ne sa zadadeni nikakavi flagove ot push receiver
        Integer flag = getIntent().getFlags();
        if(flag != null) {
            if(flag == Statics.FLAG_CALENDAR_UPDATE){
                //prevkluchvame na love days
                pager.setCurrentItem(1);

            }

            if(flag == Statics.FLAG_PARTNER_REQUEST){
                Intent intent = new Intent(mContext,ManagePartnersMain.class);
                intent.putExtra(Statics.KEY_PARTNERS_SELECT_TAB,Statics.KEY_PARTNERS_SELECT_PENDING_REQUESTS);
                startActivity(intent);

            }
        }//krai na check za flagove
    }

    protected void navigateToLogin() {
        //preprashta kam login screen
        Intent intent = new Intent(this, LoginActivity.class);

        //Celta na sledvashtite 2 reda e da ne moze da otidesh ot log-in ekrana
        //kam osnovnia ekran, ako natisnesh back butona

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //sazdavo zadacha
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //iztriva vsichki predishni zadachi.
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //tova e za da se otvaria navigation drawer
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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
                //ogranicheni sa da vrashtat samo edin poluchatel
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
                //izprashtame kiss.
                //Ima ogranichenie da izprashtame kiss samo do 1 poluchatel,
                // zatova prosto vzimame parvia element ot array
                    BackendlessMessage.sendKissMessage(mCurrentUser,
                                                       recepientEmails.get(0),
                                                       deviceIds.get(0),
                                                       mContext, this);
            } //krai na REQUESTCODE == Activity Send to
            if(requestCode == CHOOSE_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST){
                //obrabotva se v OnActivityResult v ChangeProfilePic dialog box
                changeProfilePic.onActivityResult(requestCode,resultCode,data);

            }//on activity result za promiana na profile pic

        } //krai na RESULTCODE OK
    } //krai na onactivity result


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        //vrazvame butona za dobaviane na novi partniori
        addPartner = menu.findItem(R.id.partner_request);
        if (Statics.pendingPartnerRequest == true) {
            addPartner.setVisible(true);
        } else  {
            addPartner.setVisible(false);
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

     /*
        HELPER METODI ZA NAVIGATION DRAWER
     */

    //setup the drawer
    private void setUpDrawer(){
        //vrazvame navigation drawer
        mDrawerLinear = (LinearLayout) findViewById(R.id.left_drawer_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);




        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        )

        {




            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {

                super.onDrawerClosed(view);
                mDrawerToggle.syncState();



            }

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                loadHeaderNavigationDrawer();
                mDrawerToggle.syncState();


            }
            //disables animation from hamburger to back arrow
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); // this disables the animation
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                mDrawerToggle.syncState();

            }


        };


        mDrawerToggle.syncState();

        //zadavame spisaka, koito shte se pokazva
        List<NavigationDrawerItems> items = new ArrayList<NavigationDrawerItems>();
        //partners
        items.add(new NavigationDrawerItems(R.drawable.partner_icon,getString(R.string.menu_edit_partner)));
        String partnerOptions[] = getResources().getStringArray(R.array.navigation_drawer_partners_options);
        for(String option: partnerOptions ){
            items.add(new NavigationDrawerItems(option));
        }

        //account settings
        items.add(new NavigationDrawerItems(R.drawable.ic_action_settings,getString(R.string.account_settings_title)));
        String[] accountOptions =  getResources().getStringArray(R.array.edit_profile_options);
        for(String option: accountOptions) {
            items.add(new NavigationDrawerItems(option));
        }



       // ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(mContext, R.array.edit_profile_options, android.R.layout.simple_spinner_dropdown_item);
       AdapterNavigationDrawer adapter = new AdapterNavigationDrawer(this,items);

        mDrawerList.setAdapter(adapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //vrazvmam logout butona ot navigation drawer
        logoutButtonNavigationDrawer = (Button) findViewById(R.id.logout_button);
        logoutButtonNavigationDrawer.setOnClickListener(logoutButtonOnClick);


        // Set the drawer toggle as the DrawerListener
        //mDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
/*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        */
        mDrawerToggle.syncState();


    }

    //load header
    private void loadHeaderNavigationDrawer(){
        if(mCurrentUser != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
            //zarezdame profile pic, ako ima takava
            if (mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH) != null) {
                //ako ima profile pic ia zarezdame s picaso
                //existingprofilePicPath se izpolzva i v sluchaite, kogato user si smenia profile pic
                // togava kachvame na servera novata kartinka i izpolzvame tazi promenliva,
                // za da iztriem starata profile pic ot servera
                String existingProfilePicPath = (String) mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH);
                ImageView profilePicture = (ImageView) findViewById(R.id.drawer_header_image);
                Picasso.with(Main.this)
                        .load(existingProfilePicPath)
                        .transform(new RoundedTransformation(Statics.PICASSO_ROUNDED_CORNERS, 0))
                        .into(profilePicture);

            }
            //vrazvame username i password
            TextView usernameDrawerHeader = (TextView) findViewById(R.id.username);
            TextView emailDrawerHeader = (TextView) findViewById(R.id.emailUser);

            emailDrawerHeader.setText(mCurrentUser.getEmail());
            usernameDrawerHeader.setText((String)mCurrentUser.getProperty(Statics.KEY_USERNAME));
        }
    }
    /*
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    ON CLICK LISTENER ZA NAVIGATION DRAWER
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    //on lick za logout buttona
    private View.OnClickListener logoutButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //logout
            Backendless.UserService
                    .logout(new DefaultCallback<Void>(Main.this, getResources().getString(R.string.logout_message)) {
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
        }
    };
    //on click za ostanalite opcii
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        protected String mMessageType;



        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    //zaglavna linia Partners

                    return;
                case 1:
                    //search for partners
                    Intent searchPartner = new Intent(Main.this, ManagePartnersMain.class);
                    //slagame toya KEY, za da prevkluchim na pravilia tab ot drugata strana kato otvorim ekrana
                    searchPartner.putExtra(Statics.KEY_PARTNERS_SELECT_TAB, Statics.KEY_PARTNERS_SELECT_SEARCH);
                    startActivity(searchPartner);
                    return;
                case 2:
                    //pending partner requests
                    Intent partnerRequest = new Intent(Main.this, ManagePartnersMain.class);
                    //slagame toya KEY, za da prevkluchim na pravilia tab ot drugata strana kato otvorim ekrana
                    partnerRequest.putExtra(Statics.KEY_PARTNERS_SELECT_TAB, Statics.KEY_PARTNERS_SELECT_PENDING_REQUESTS);
                    startActivity(partnerRequest);
                    return;
                case 3:
                    //existing partners
                    Intent existingPartners = new Intent(Main.this, ManagePartnersMain.class);
                    //slagame toya KEY, za da prevkluchim na pravilia tab ot drugata strana kato otvorim ekrana
                    existingPartners.putExtra(Statics.KEY_PARTNERS_SELECT_TAB, Statics.KEY_PARTNERS_SELECT_EXISTING_PARTNERS);
                    startActivity(existingPartners);
                    return;
                case 4:
                    //zaglavna lina za account settings
                    return;
                case 5:
                    //change sex
                    DialogFragment sexDialog = new GuyOrGirlDialog();
                    sexDialog.show(getFragmentManager(), "Welcome");
                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerLinear);
                    return;
                case 6:
                    //change date of birth

                    SetBirthday setBirthday = new SetBirthday();
                    //setBirthday.setTargetFragment(FragmentEditProfileActivity.this,SET_BIRTHDAY);

                    setBirthday.show(getSupportFragmentManager(),"Welcome");
                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerLinear);
                    return;
                case 7:
                    //change password

                    ChangePassword changePassword = new ChangePassword();
                    //changePassword.setTargetFragment(FragmentEditProfileActivity.this, CHANGE_PASSWORD);
                    changePassword.show(getSupportFragmentManager(),"Welcome");

                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerLinear);
                    return;
                case 8:
                    //change profile picture

                    //rezultatat se obrabotva v OnActivityResult v ChangeProfilePic dialog
                    changeProfilePic = new ChangeProfilePic();
                    changeProfilePic.show(getSupportFragmentManager(),"Welcome");
                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerLinear);
                    return;
                case 9:
                    //change username
                    ChangeUsername changeUsername = new ChangeUsername();
                    //changeUsername.setTargetFragment(FragmentEditProfileActivity.this, CHANGE_USERNAME);
                    changeUsername.show(getSupportFragmentManager(),"Welcome");
                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerLinear);
                    return;

            }
        }




    }

}
