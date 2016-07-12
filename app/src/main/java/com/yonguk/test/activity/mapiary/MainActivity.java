package com.yonguk.test.activity.mapiary;

import android.os.Bundle;
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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends AppCompatActivity {

    private BottomBar mBottomBar;
    private AppBarLayout appBarLayout = null;
    Toolbar toolbar = null;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_rv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle("delaroy");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*Fragment*/
        FirstFragment firstFragment = FirstFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();

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
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                switch(menuItemId){
                    case R.id.bottombar_first:
                        fragment = FirstFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        lockAppBar(true, "First Fragment");
                        //getSupportActionBar().setTitle("First Fragment");
                        break;

                    case R.id.bottombar_sec:
                        fragment = SecondFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        lockAppBar(false, "Second Fragment");
                        //getSupportActionBar().setTitle("Second Fragment");
                        break;

                    case R.id.bottombar_thd:
                        fragment = ThirdFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        lockAppBar(true, "Third Fragment");
                        //getSupportActionBar().setTitle("Third Fragment");
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(int menuItemId) {
                if (menuItemId == R.id.bottombar_first) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });

        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        //이 메소드의 기능이 뭔지 잘 모르겠음
        //mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        //mBottomBar.mapColorForTab(1, 0xFF5D4037);
        mBottomBar.mapColorForTab(0, "#7B1FA2");
        mBottomBar.mapColorForTab(1, "#FF5252");
        mBottomBar.mapColorForTab(2, "#FF9800");
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
}
