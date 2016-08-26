package com.yonguk.test.activity.mapiary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.yonguk.test.activity.mapiary.fragment.FollowFragment;
import com.yonguk.test.activity.mapiary.fragment.MainFragment;
import com.yonguk.test.activity.mapiary.fragment.NewsFragment;
import com.yonguk.test.activity.mapiary.fragment.ProfileFragment;
import com.yonguk.test.activity.mapiary.fragment.RecordFragment;
import com.yonguk.test.activity.mapiary.subactivity.RecordActivity;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    protected long mExitModeTime = 0L;
    protected CoordinatorLayout rootView = null;
    protected BottomBar mBottomBar;
    protected AppBarLayout appBarLayout = null;
    protected Toolbar toolbar = null;
    protected CollapsingToolbarLayout collapsingToolbarLayout = null;
    protected String userID = "";
    protected Context mContext = null;
    private final int REQUEST_CODE_UPLOAD_CARD = 1;
    private final int PICK_IMAGE_REQUEST = 2;

    private Menu menu =  null;
    /*Fragments*/
    protected MainFragment mMainFragment = null;
    protected FollowFragment mFollowFragment = null;
    protected NewsFragment mNewsFragment = null;
    protected ProfileFragment mProfileFragment = null;
    protected RecordFragment mRecordFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = (CoordinatorLayout)findViewById(R.id.root_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        //ivToolbar = (ImageView) findViewById(R.id.iv_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_rv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main");
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        //collapsingToolbarLayout.setTitle("Main");
        mContext = getApplicationContext();

        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");


        setFragment();
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        setBottomBar();

        //Service
    /*    try {
            Intent serviceIntent = new Intent(this,BluetoothService.class);
            startService(serviceIntent);
        }catch(Exception e){
            Log.d("uks",e.getMessage());
        }
*/
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SampleChildActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivityForResult(intent,REQUEST_CODE_UPLOAD_CARD);

            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_UPLOAD_CARD){
            mBottomBar.selectTabAtPosition(0,false);
            if(resultCode == RESULT_OK){
                Snackbar.make(rootView,"업로드 완료",Snackbar.LENGTH_LONG).show();
            }
        }else if(requestCode == PICK_IMAGE_REQUEST){
            mProfileFragment.handleActivityResult(requestCode,resultCode,data);
        }

    }

    private void setFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userID);
        mMainFragment = MainFragment.newInstance();
        mMainFragment.setArguments(bundle);
        mFollowFragment = FollowFragment.newInstance();
        mFollowFragment.setArguments(bundle);
        mRecordFragment = RecordFragment.newInstance();
        mNewsFragment = NewsFragment.newInstance();
        mProfileFragment = ProfileFragment.newInstance();
        mProfileFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mMainFragment)
                .add(R.id.fragment_container, mFollowFragment)
                .add(R.id.fragment_container, mNewsFragment)
                .add(R.id.fragment_container, mProfileFragment)
                .add(R.id.fragment_container, mRecordFragment)
                .hide(mFollowFragment)
                .hide(mNewsFragment)
                .hide(mProfileFragment)
                .hide(mRecordFragment)
                .show(mMainFragment)
                .commit();
    }

    private void setBottomBar(){
        //hiding on scroll
        // Instead of attach(), use attachShy():
        //mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.root_layout),
        //findViewById(R.id.rv), savedInstanceState);
        mBottomBar.useFixedMode();
        mBottomBar.setItems(R.menu.bottombar_menu);
        mBottomBar.setActiveTabColor("#FF4081");
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(int menuItemId) {
                switch (menuItemId) {
                    case R.id.bottombar_main:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mFollowFragment)
                                .hide(mNewsFragment)
                                .hide(mProfileFragment)
                                .hide(mRecordFragment)
                                .show(mMainFragment)
                                .commit();
                        getSupportActionBar().setTitle("Mapiary");
                        //appBarLayout.setExpanded(false, true);
                        //collapsingToolbarLayout.setTitle("Main");
                        //ivToolbar.setVisibility(View.GONE);
                        //lockAppBar(true, "Main");
                        break;

                    case R.id.bottombar_follow:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mMainFragment)
                                .hide(mNewsFragment)
                                .hide(mProfileFragment)
                                .hide(mRecordFragment)
                                .show(mFollowFragment)
                                .commit();
                        getSupportActionBar().setTitle("Follower");

                        //menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.connecting3_v1));
                        //appBarLayout.setExpanded(false, true);
                        //collapsingToolbarLayout.setTitle("Follow");
                        //ivToolbar.setVisibility(View.GONE);
                        //lockAppBar(true, "Follow");
                        break;

                    case R.id.bottombar_record:
                        //Intent intent = new Intent(mContext, SampleChildActivity.class);
                        //intent.putExtra("USER_ID", userID);
                        //startActivityForResult(intent, REQUEST_CODE_UPLOAD_CARD);

                        Intent intent = new Intent(mContext, RecordActivity.class);
                        startActivity(intent);


/*                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mMainFragment)
                                .hide(mFollowFragment)
                                .hide(mNewsFragment)
                                .hide(mProfileFragment)
                                .show(mRecordFragment)
                                .commit();
                        getSupportActionBar().setTitle("Record");*/

                        //appBarLayout.setExpanded(false, true);
                        //collapsingToolbarLayout.setTitle("Record");
                        //ivToolbar.setVisibility(View.GONE);
                        //lockAppBar(true, "Record");
                        break;

                    case R.id.bottombar_news:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mMainFragment)
                                .hide(mFollowFragment)
                                .hide(mProfileFragment)
                                .hide(mRecordFragment)
                                .show(mNewsFragment)
                                .commit();
                        getSupportActionBar().setTitle("소식");
                        //appBarLayout.setExpanded(false, true);
                        //collapsingToolbarLayout.setTitle("News");
                        //ivToolbar.setVisibility(View.GONE);
                        //lockAppBar(true, "News");
                        break;

                    case R.id.bottombar_profile:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mMainFragment)
                                .hide(mFollowFragment)
                                .hide(mNewsFragment)
                                .hide(mRecordFragment)
                                .show(mProfileFragment)
                                .commit();
                        getSupportActionBar().setTitle("내 프로필");
                        //appBarLayout.setExpanded(false, true);
                        //collapsingToolbarLayout.setTitle("Profile");
                        //ivToolbar.setVisibility(View.GONE);
                        //lockAppBar(true, "Profile");
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(int menuItemId) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        hideAllOption();
        showOption(R.id.action_bluetooth_nosignal);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideOption(int id){
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void hideAllOption(){
        MenuItem item1 = menu.findItem(R.id.action_bluetooth_nosignal);
        MenuItem item2 = menu.findItem(R.id.action_bluetooth_connecting);
        MenuItem item3 = menu.findItem(R.id.action_bluetooth_connected);
        item1.setVisible(false);
        item2.setVisible(false);
        item3.setVisible(false);
    }

    private void showOption(int id){
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    private void setOptionTitle(int id, String title){
        MenuItem item = menu.findItem(id);
        item.setTitle(title);
    }

    private void setOptionIcon(int id, int iconRes){
        MenuItem item = menu.findItem(id);
        item.setIcon(iconRes);
    }
/*
    public void lockAppBar(boolean locked,String title) {
        if(locked){
            appBarLayout.setExpanded(false, true);
            int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
            lp.height = px;
            appBarLayout.setLayoutParams(lp);
            collapsingToolbarLayout.setTitleEnabled(false);
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
            toolbar.setTitle(title);
        }else{
            appBarLayout.setExpanded(true, false);
            appBarLayout.setActivated(true);
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            lp.height = (int) getResources().getDimension(R.dimen.toolbarExpandHeight);
            collapsingToolbarLayout.setTitleEnabled(true);
            collapsingToolbarLayout.setTitle(title);
        }
    }*/

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

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //appBarLayout.setExpanded(false, true);
    }
}
