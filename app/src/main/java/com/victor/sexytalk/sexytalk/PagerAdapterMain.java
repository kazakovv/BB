package com.victor.sexytalk.sexytalk;

import android.content.Context;
import android.opengl.Visibility;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MenuItem;

import com.victor.sexytalk.sexytalk.FragmentLoveBox;
import com.victor.sexytalk.sexytalk.FragmentLoveDays;
import com.victor.sexytalk.sexytalk.R;


/**
 * Tova e pager adapter za Main activity
 */
public class PagerAdapterMain extends FragmentPagerAdapter {
    Context mContext;
    public PagerAdapterMain(FragmentManager fm, Context context) {

        super(fm);
        this.mContext = context;

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
                return mContext.getResources().getString(R.string.tab_love_box_title);
            case 1:
                return mContext.getResources().getString(R.string.tab_love_days_title);
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
