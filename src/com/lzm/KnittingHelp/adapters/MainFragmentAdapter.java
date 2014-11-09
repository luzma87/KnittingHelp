package com.lzm.KnittingHelp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.lzm.KnittingHelp.fragments.MasterFragment;

import java.util.List;

/**
 * Created by luz on 05/11/14.
 */
public class MainFragmentAdapter extends FragmentStatePagerAdapter {

    List<MasterFragment> fragmentList;

    public MainFragmentAdapter(FragmentManager fm, List<MasterFragment> fragments) {
        super(fm);
        fragmentList = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
