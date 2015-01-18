package com.victor.sexytalk.sexytalk;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class ManagePartnersMain extends ActionBarActivity implements MaterialTabListener {
    protected ViewPager pager;
    protected Toolbar toolbar;
    protected MaterialTabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_partners_main);

        //set up the action bar and the tabs
        pager = (ViewPager) findViewById(R.id.manage_partners_main);
        PagerAdapterManagePartners pAdapter =
                new PagerAdapterManagePartners(getSupportFragmentManager(), this);
        pager.setAdapter(pAdapter);
        //pager.setOffscreenPageLimit(1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        //toolbar.setLogo(R.drawable.launch_icon);
        setSupportActionBar(toolbar);
        tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);

        //actionbar = getActionBar();
        //actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionbar.addTab(actionbar.newTab().setText(R.string.search_partners_tab).setTabListener(this));
        //actionbar.addTab(actionbar.newTab().setText(R.string.requests_partners_tab).setTabListener(this));
        //actionbar.addTab(actionbar.newTab().setText(R.string.existing_partners_tab).setTabListener(this));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //actionbar.setSelectedNavigationItem(position);
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
                            .setText(pAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }
        //chek dali ekranat e otvoren ot main activity s cel da ni izprati kam pending partner requests
        //t.e. potrebiteliat e vidial v main activity che ima pending request i e caknal na iconata
        Intent intent = getIntent();
        if(intent.getStringExtra(Statics.KEY_PARTNERS_SELECT_TAB) != null) {
                pager.setCurrentItem(1);
        }
    }


    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }
}
