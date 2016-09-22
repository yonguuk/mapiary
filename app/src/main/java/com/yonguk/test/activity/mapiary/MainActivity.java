package com.yonguk.test.activity.mapiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yonguk.test.activity.mapiary.adapter.TabPageAdapter;
import com.yonguk.test.activity.mapiary.fragment.FollowFragment;
import com.yonguk.test.activity.mapiary.fragment.MainFragment;
import com.yonguk.test.activity.mapiary.fragment.NewsFragment;
import com.yonguk.test.activity.mapiary.fragment.ProfileFragment;
import com.yonguk.test.activity.mapiary.network.VolleySingleton;
import com.yonguk.test.activity.mapiary.sample.SampleTrackingAtcitivy;
import com.yonguk.test.activity.mapiary.subactivity.TrackingActivity;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    protected long mExitModeTime = 0L;
    protected CoordinatorLayout rootView = null;
    //protected BottomBar mBottomBar;
    protected AppBarLayout appBarLayout = null;
    protected Toolbar toolbar = null;
    protected CollapsingToolbarLayout collapsingToolbarLayout = null;
    protected String userID = "";
    protected Context mContext = null;
    private final int REQUEST_CODE_RECORD = 1;
    private final int PICK_IMAGE_REQUEST = 2;

    private VolleySingleton volleySingleton = null;
    private RequestQueue requestQueue = null;

    private Menu menu =  null;
    private static final String CLEAR_DB_URL = "http://kktt0202.dothome.co.kr/master/upload/delete/reset.php";
    private static final String TAG = "MainActivity";
    private final String KEY_ID = "user_id";
    /*Fragments*/
    protected MainFragment mMainFragment = null;
    protected FollowFragment mFollowFragment = null;
    protected NewsFragment mNewsFragment = null;
    protected ProfileFragment mProfileFragment = null;


    ViewPager viewPager;
    TabLayout tabLayout;
    TabPageAdapter tabPageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        volleySingleton = VolleySingleton.getInstance(this);
        requestQueue = volleySingleton.getRequestQueue();
        rootView = (CoordinatorLayout)findViewById(R.id.root_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        //ivToolbar = (ImageView) findViewById(R.id.iv_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_rv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main");
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        //collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        //collapsingToolbarLayout.setTitle("Main");
        mContext = getApplicationContext();

        Intent intent = getIntent();
        userID = intent.getStringExtra(KEY_ID);


        setFragment();

        tabPageAdapter = new TabPageAdapter(getSupportFragmentManager());
        tabPageAdapter.addFragment(mMainFragment,"Main");
        tabPageAdapter.addFragment(mFollowFragment,"Followers");
        tabPageAdapter.addFragment(mNewsFragment,"News");
        tabPageAdapter.addFragment(mProfileFragment,"Profile");


        viewPager.setAdapter(tabPageAdapter);
        tabLayout.setTabsFromPagerAdapter(tabPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getSupportActionBar().setTitle(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TrackingActivity.class);
                intent.putExtra(KEY_ID, userID);
                startActivityForResult(intent,REQUEST_CODE_RECORD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_RECORD){
            //mBottomBar.selectTabAtPosition(0,false);
            if(resultCode == RESULT_OK){
                Snackbar.make(rootView,"업로드 완료",Snackbar.LENGTH_LONG).show();
            }
        }else if(requestCode == PICK_IMAGE_REQUEST){
            mProfileFragment.handleActivityResult(requestCode,resultCode,data);
        }

    }

    private void setFragment(){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID, userID);
        mMainFragment = MainFragment.newInstance();
        mMainFragment.setArguments(bundle);
        mFollowFragment = FollowFragment.newInstance();
        mFollowFragment.setArguments(bundle);
        mNewsFragment = NewsFragment.newInstance();
        mNewsFragment.setArguments(bundle);
        mProfileFragment = ProfileFragment.newInstance();
        mProfileFragment.setArguments(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        //hideAllOption();
        //showOption(R.id.action_bluetooth_nosignal);
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

        if(id == R.id.action_upload){
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, CLEAR_DB_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(),"삭제완료" , Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    Log.i(TAG, error.toString());
                }
            });

            requestQueue.add(stringRequest);
        }

        return super.onOptionsItemSelected(item);
    }

/*
    private void hideOption(int id){
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }
*/
/*

    private void hideAllOption(){
        MenuItem item1 = menu.findItem(R.id.action_bluetooth_nosignal);
        MenuItem item2 = menu.findItem(R.id.action_bluetooth_connecting);
        MenuItem item3 = menu.findItem(R.id.action_bluetooth_connected);
        item1.setVisible(false);
        item2.setVisible(false);
        item3.setVisible(false);
    }
*/

/*    private void showOption(int id){
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
    }
}
