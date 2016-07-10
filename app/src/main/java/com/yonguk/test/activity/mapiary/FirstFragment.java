package com.yonguk.test.activity.mapiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by dosi on 2016-07-08.
 */
public class FirstFragment extends Fragment {
    public static FirstFragment newInstance(){
        FirstFragment f = new FirstFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_first,container,false);
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
