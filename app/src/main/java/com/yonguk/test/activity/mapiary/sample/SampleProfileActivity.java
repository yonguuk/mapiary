package com.yonguk.test.activity.mapiary.sample;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;

import com.yonguk.test.activity.mapiary.R;

public class SampleProfileActivity extends AppCompatActivity {

    protected AppBarLayout appBarLayout = null;
    protected Toolbar toolbar = null;
    protected CollapsingToolbarLayout collapsingToolbarLayout = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_profile);
        appBarLayout = (AppBarLayout) findViewById(R.id.sample_appbar);
        toolbar = (Toolbar) findViewById(R.id.sample_toolbar);
        //collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.sample_collapsing);
        setSupportActionBar(toolbar);
        appBarLayout.setExpanded(false,true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void lockAppBar(boolean locked,String title) {
        if(locked){
            appBarLayout.setExpanded(false, true);
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
            lp.height = px;
            appBarLayout.setLayoutParams(lp);
            collapsingToolbarLayout.setTitleEnabled(false);
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
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
