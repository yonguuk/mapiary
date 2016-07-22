package com.yonguk.test.activity.mapiary;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    protected long mExitModeTime = 0L;
    protected View rootView = null;
    protected BottomBar mBottomBar;
    protected AppBarLayout appBarLayout = null;
    protected Toolbar toolbar = null;
    protected CollapsingToolbarLayout collapsingToolbarLayout = null;
    protected ImageView ivToolbar = null;

    protected MainFragment mMainFragment = null;
    protected FollowFragment mFollowFragment = null;
    protected NewsFragment mNewsFragment = null;
    protected ProfileFragment mProfileFragment = null;
    protected RecordFragment mRecordFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.root_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        ivToolbar = (ImageView) findViewById(R.id.iv_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_rv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle("delaroy");
        setFragment();

        //Service
    /*    try {
            Intent serviceIntent = new Intent(this,BluetoothService.class);
            startService(serviceIntent);
        }catch(Exception e){
            Log.d("uks",e.getMessage());
        }
*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*Fragment*/
        //MainFragment mainFragment = MainFragment.newInstance();
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment).commit();
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
                .commit();
        /*Bottom Navigation bar*/
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        /*hiding on scroll
          Instead of attach(), use attachShy():
          mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.myCoordinator),
          findViewById(R.id.myScrollingContent), savedInstanceState);
        */
        mBottomBar.setItems(R.menu.bottombar_menu);

        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(int menuItemId) {
                switch(menuItemId){
                    case R.id.bottombar_main:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mFollowFragment)
                                .hide(mNewsFragment)
                                .hide(mProfileFragment)
                                .hide(mRecordFragment)
                                .show(mMainFragment)
                                .commit();
                        ivToolbar.setVisibility(View.GONE);
                        lockAppBar(true, "Main");
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
                        ivToolbar.setVisibility(View.GONE);
                        lockAppBar(true, "Follow");
                        break;

                    case R.id.bottombar_record:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mMainFragment)
                                .hide(mFollowFragment)
                                .hide(mNewsFragment)
                                .hide(mProfileFragment)
                                .show(mRecordFragment)
                                .commit();
                        ivToolbar.setVisibility(View.GONE);
                        lockAppBar(true, "Record");
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
                        ivToolbar.setVisibility(View.GONE);
                        lockAppBar(true, "News");
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
                        ivToolbar.setVisibility(View.GONE);
                        lockAppBar(true, "Profile");
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(int menuItemId) {

            }
        });
        /*mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(int menuItemId) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                switch(menuItemId){
                    case R.id.bottombar_main:
                        fragment = MainFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        ivToolbar.setVisibility(View.GONE);
                        lockAppBar(true, "Main");
                        //getSupportActionBar().setTitle("First Fragment");
                        break;

                    case R.id.bottombar_follow:
                        fragment = FollowFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        lockAppBar(true, "Follow");
                        //ivToolbar.setImageResource(R.drawable.image3);
                        //ivToolbar.setVisibility(View.VISIBLE);
                        ivToolbar.setVisibility(View.GONE);
                        //getSupportActionBar().setTitle("Second Fragment");
                        break;

                    case R.id.bottombar_record:
                        fragment = RecordFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        ivToolbar.setVisibility(View.GONE);
                        lockAppBar(true, "Record");
                        //getSupportActionBar().setTitle("Third Fragment");
                        break;

                    case R.id.bottombar_news:
                        fragment = NewsFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        ivToolbar.setVisibility(View.GONE);
                        lockAppBar(true, "News");
                        //getSupportActionBar().setTitle("Third Fragment");
                        break;

                    case R.id.bottombar_profile:
                        fragment = RecordFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        ivToolbar.setVisibility(View.GONE);
                        lockAppBar(true, "Profile");
                        //getSupportActionBar().setTitle("Third Fragment");
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(int menuItemId) {
                if (menuItemId == R.id.bottombar_main) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });
*/
        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        //이 메소드의 기능이 뭔지 잘 모르겠음
        //mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        //mBottomBar.mapColorForTab(1, 0xFF5D4037);
        mBottomBar.mapColorForTab(0, "#7B1FA2");
        mBottomBar.mapColorForTab(1, "#FF5252");
        mBottomBar.mapColorForTab(2, "#FF9800");
    }
    private void setFragment(){
        mMainFragment = MainFragment.newInstance();
        mFollowFragment = FollowFragment.newInstance();
        mRecordFragment = RecordFragment.newInstance();
        mNewsFragment = NewsFragment.newInstance();
        mProfileFragment = ProfileFragment.newInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }
}
