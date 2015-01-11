package com.victor.sexytalk.sexytalk;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;


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
