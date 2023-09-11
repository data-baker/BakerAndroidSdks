package com.baker.sdk.demo.gramophone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatTextView;

import com.baker.sdk.demo.R;
import com.baker.sdk.demo.gramophone.activity.EngraveActivity;

public class TipDialogFragment extends AppCompatDialogFragment {
    private AppCompatTextView tvTitle;
    private AppCompatTextView tvContent;

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
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvConfirm = view.findViewById(R.id.tv_confirm);
        tvConfirm.setVisibility(View.GONE);
        tvTitle.setText("提示");
        tvContent.setText("检测到您当前会话已失效，需要重新录制");
        tvCancel.setText("确定");
        tvCancel.setOnClickListener(v -> dismissAllowingStateLoss());

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
