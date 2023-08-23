package com.baker.sdk.demo.gramophone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baker.sdk.demo.R;
import com.baker.sdk.demo.gramophone.activity.DbDetectionActivity;

public class HomeFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);

        view.findViewById(R.id.experience_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(getActivity(), DbDetectionActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
