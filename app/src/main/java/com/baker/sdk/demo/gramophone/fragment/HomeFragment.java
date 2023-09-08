package com.baker.sdk.demo.gramophone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.engrave.lib.configuration.EngraverType;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.gramophone.activity.DbDetectionActivity;
import com.baker.sdk.demo.gramophone.util.PreferenceUtil;

public class HomeFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);

        view.findViewById(R.id.experience_start).setOnClickListener(view -> {

            if (!TextUtils.isEmpty(PreferenceUtil.getString(PreferenceUtil.getEngraverKey(), ""))){
                getActivity().runOnUiThread(() -> {
                    DialogFragment dialog = new ContinueDialogFragment();
                    dialog.show(getParentFragmentManager(), "");
                });
            }else {
                BakerVoiceEngraver.getInstance().setRecordSessionId("");
                BakerVoiceEngraver.getInstance().requestConfig();
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
