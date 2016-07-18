package com.yonguk.test.activity.mapiary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yonguk.test.activity.mapiary.R;
import com.yonguk.test.activity.mapiary.SampleChildActivity;

/**
 * Created by dosi on 2016-07-18.
 */
public class MainFragment extends Fragment {

    public static MainFragment newInstance(){
        MainFragment f = new MainFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout)inflater.inflate(R.layout.fragment_main,container,false);
        Button mButton = (Button) mLinearLayout.findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SampleChildActivity.class);
                startActivity(intent);
            }
        });
        return mLinearLayout;
    }
}
