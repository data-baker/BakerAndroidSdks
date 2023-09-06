package com.baker.sdk.demo.gramophone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatTextView;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.gramophone.activity.DbDetectionActivity;
import com.baker.sdk.demo.gramophone.util.PreferenceUtil;

public class ContinueDialogFragment extends AppCompatDialogFragment {
    private AppCompatTextView tvTitle;
    private AppCompatTextView tvContent;
    private LinearLayout llBottom;
    private AppCompatTextView tvCancel;
    private AppCompatTextView tvConfirm;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(requireContext()).inflate(R.layout.dialog_continue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        llBottom = view.findViewById(R.id.ll_bottom);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvConfirm = view.findViewById(R.id.tv_confirm);
        tvTitle.setText("提示");
        tvContent.setText("是否继续录制");
        tvCancel.setOnClickListener(v -> {
            PreferenceUtil.putString("sessionId", "");
            BakerVoiceEngraver.getInstance().setRecordSessionId("");
            BakerVoiceEngraver.getInstance().requestConfig();
            startActivity(new Intent(getActivity(), DbDetectionActivity.class));
            dismissAllowingStateLoss();
        });
        tvConfirm.setOnClickListener(v -> {
            BakerVoiceEngraver.getInstance().setRecordSessionId(PreferenceUtil.getString("sessionId", ""));
            BakerVoiceEngraver.getInstance().requestConfig();
            startActivity(new Intent(getActivity(), DbDetectionActivity.class));
            dismissAllowingStateLoss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
    }
}
