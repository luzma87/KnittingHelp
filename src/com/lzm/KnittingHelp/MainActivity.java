package com.lzm.KnittingHelp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.lzm.KnittingHelp.activities.SettingsActivity;
import com.lzm.KnittingHelp.adapters.MainFragmentAdapter;
import com.lzm.KnittingHelp.db.DbHelper;
import com.lzm.KnittingHelp.fragments.CountersFragment;
import com.lzm.KnittingHelp.fragments.CreatePatternFragment;
import com.lzm.KnittingHelp.fragments.MasterFragment;
import com.lzm.KnittingHelp.fragments.PatternsListFragment;
import com.lzm.KnittingHelp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public ViewPager viewPager;
    ActionBar actionBar;

    public static int screenHeight;
    public static int screenWidth;

    public final int LIST_POS = 0;
    public final int CREATE_POS = 1;
    public final int TAB_COUNT = 2;
    public final int COUNTERS_COUNT = 3;
    public List<MasterFragment> fragments;
    List<String> titles;
    Activity context;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = this;

        actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.main_title));

        DbHelper helper = new DbHelper(this);
        helper.getWritableDatabase();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;

        fragments = new ArrayList<MasterFragment>(TAB_COUNT);
        fragments.add(new PatternsListFragment());
        fragments.add(new CreatePatternFragment());
        fragments.add(new CountersFragment());

        titles = new ArrayList<String>();
        titles.add(getString(R.string.patterns_list_title));
        titles.add(getString(R.string.create_pattern_title));
        titles.add(getString(R.string.counters_title));

        viewPager = (ViewPager) findViewById(R.id.main_pager);
        viewPager.setAdapter(new MainFragmentAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
//                Log.d("LZM", "onPageScrolled: position=" + i + " from=" + v + " with number of pixels=" + i2);
            }

            @Override
            public void onPageSelected(int i) {
                Utils.hideSoftKeyboard(context);
                actionBar.setSelectedNavigationItem(i);
//                Log.d("LZM", "onPageSelected: position=" + i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
//                if (i == ViewPager.SCROLL_STATE_IDLE) {
//                    Log.d("LZM", "onPageScrollStateChanged: state idle (" + i + ")");
//                }
//                if (i == ViewPager.SCROLL_STATE_DRAGGING) {
//                    Log.d("LZM", "onPageScrollStateChanged: state dragging (" + i + ")");
//                }
//                if (i == ViewPager.SCROLL_STATE_SETTLING) {
//                    Log.d("LZM", "onPageScrollStateChanged: state settling (" + i + ")");
//                }
            }
        });
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            initTabs();
        }
    }

    public void initTabs() {
        for (String title : titles) {
            makeTab(title);
        }
    }

    public void makeTab(String title) {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(title);
        tab.setTabListener(this);
        actionBar.addTab(tab);
    }

    public void selectTab(int pos) {
        Utils.hideSoftKeyboard(context);
        viewPager.setCurrentItem(pos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.main_menu_settings_btn:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//        Log.d("LZM", "onTabSelected at position: " + tab.getPosition() + " name: " + tab.getText());
//        viewPager.setCurrentItem(tab.getPosition());
        selectTab(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//        Log.d("LZM", "onTabUnselected at position: " + tab.getPosition() + " name: " + tab.getText());

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//        Log.d("LZM", "onTabReselected at position: " + tab.getPosition() + " name: " + tab.getText());
    }

}
