package com.victor.sexytalk.sexytalk;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Tova e adapter za manage partners
 */
public class PagerAdapterManagePartners extends FragmentPagerAdapter {
    public PagerAdapterManagePartners(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentSearchPartners();
            case 1:
                return new FragmentPartnerRequests();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
