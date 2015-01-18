package com.victor.sexytalk.sexytalk;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Tova e pager adapter za Main activity
 */
public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentLoveBox();
            case 1:

                return new FragmentLoveDays();
            default:
                break;
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "LoveBox";
            case 1:
                return "LoveDays";
        }
        return super.getPageTitle(position);

    }

    @Override
    public int getItemPosition(Object object) {


        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
