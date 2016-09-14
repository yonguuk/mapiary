package com.yonguk.test.activity.mapiary.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yonguk.test.activity.mapiary.fragment.FollowFragment;
import com.yonguk.test.activity.mapiary.fragment.MainFragment;
import com.yonguk.test.activity.mapiary.fragment.NewsFragment;
import com.yonguk.test.activity.mapiary.fragment.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonguk on 2016-09-14.
 */
public class TabPageAdapter extends FragmentStatePagerAdapter {
    int numOftabs;

    public TabPageAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numOftabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                MainFragment mainFragment = MainFragment.newInstance();
                return mainFragment;
            case 1:
                FollowFragment followFragment = FollowFragment.newInstance();
                return followFragment;
            case 2:
                NewsFragment newsFragment = NewsFragment.newInstance();
                return newsFragment;
            case 3:
                ProfileFragment profileFragment = ProfileFragment.newInstance();
                return profileFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOftabs;
    }

/*    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }*/
}
