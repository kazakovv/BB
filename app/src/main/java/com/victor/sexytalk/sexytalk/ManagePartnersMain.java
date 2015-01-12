package com.victor.sexytalk.sexytalk;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;


public class ManagePartnersMain extends FragmentActivity implements ActionBar.TabListener {
    ViewPager pager;
    ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_partners_main);

        pager = (ViewPager) findViewById(R.id.manage_partners_main);
        PagerAdapterManagePartners pAdapter = new PagerAdapterManagePartners(getSupportFragmentManager());
        pager.setAdapter(pAdapter);
        pager.setOffscreenPageLimit(1);
        //TODO: actionbar is depreciated in API 21
        actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.addTab(actionbar.newTab().setText(R.string.search_partners_tab).setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText(R.string.requests_partners_tab).setTabListener(this));
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

        //chek dali ekranat e otvoren ot main activity s cel da ni izprati kam pending partner requests
        Intent intent = getIntent();
        if(intent.getStringExtra(Statics.KEY_PARTNERS_SELECT_TAB) != null) {
                pager.setCurrentItem(1);
        }
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
