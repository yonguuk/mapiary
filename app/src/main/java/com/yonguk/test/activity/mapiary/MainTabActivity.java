package com.yonguk.test.activity.mapiary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.yonguk.test.activity.mapiary.adapter.TabPageAdapter;
import com.yonguk.test.activity.mapiary.fragment.FollowFragment;
import com.yonguk.test.activity.mapiary.fragment.MainFragment;
import com.yonguk.test.activity.mapiary.fragment.NewsFragment;
import com.yonguk.test.activity.mapiary.fragment.ProfileFragment;
import com.yonguk.test.activity.mapiary.fragment.RecordFragment;

public class MainTabActivity extends AppCompatActivity implements View.OnKeyListener{

    protected long mExitModeTime = 0L;
    protected CoordinatorLayout rootView = null;
    protected TabPageAdapter tabPageAdapter = null;
    protected Toolbar toolbar;
    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    protected Context mContext = null;

    protected String userID = "";

    /*Fragments*/
    protected MainFragment mMainFragment = null;
    protected FollowFragment mFollowFragment = null;
    protected NewsFragment mNewsFragment = null;
    protected ProfileFragment mProfileFragment = null;

    private static final String TAG = "MainActivity";
    private final int REQUEST_CODE_UPLOAD_CARD = 1;
    private final int PICK_IMAGE_REQUEST = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");

        setView();
        //setFragment();
        setAdapter();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_UPLOAD_CARD){
            if(resultCode == RESULT_OK){
                Snackbar.make(rootView,"업로드 완료",Snackbar.LENGTH_LONG).show();
            }
        }else if(requestCode == PICK_IMAGE_REQUEST){
            mProfileFragment.handleActivityResult(requestCode,resultCode,data);
        }

    }

    private void setView(){
        rootView = (CoordinatorLayout) findViewById(R.id.root);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mContext = getApplicationContext();
        tabLayout.addTab(tabLayout.newTab().setText("Main"));
        tabLayout.addTab(tabLayout.newTab().setText("Followers"));
        tabLayout.addTab(tabLayout.newTab().setText("News"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    }

    private void setFragment(){
        try {
            Bundle bundle = new Bundle();
            bundle.putString("USER_ID", userID);
            Log.i(TAG, userID);
            mMainFragment = MainFragment.newInstance();
            mMainFragment.setArguments(bundle);
            mFollowFragment = FollowFragment.newInstance();
            mFollowFragment.setArguments(bundle);
            mNewsFragment = NewsFragment.newInstance();
            mNewsFragment.setArguments(bundle);
            mProfileFragment = ProfileFragment.newInstance();
            mProfileFragment.setArguments(bundle);
        }catch (Exception e){
            Log.i(TAG, e.toString());
        }
    }

    private void setAdapter(){
        try {
            tabPageAdapter = new TabPageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
            viewPager.setAdapter(tabPageAdapter);
            tabLayout.setTabsFromPagerAdapter(tabPageAdapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }catch(Exception e){
            Log.i(TAG, e.toString());
        }
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if(mExitModeTime !=0 && SystemClock.uptimeMillis() - mExitModeTime<1500){
            finish();
        }else{
            //Toast.makeText(this, "이전키를 한번 더 누르면 종료됩니다", Toast.LENGTH_LONG).show();
            Snackbar.make(rootView,"이전키를 한번 더 누르면 종료됩니다",Snackbar.LENGTH_LONG).show();
            mExitModeTime = SystemClock.uptimeMillis();
        }
    }
}
