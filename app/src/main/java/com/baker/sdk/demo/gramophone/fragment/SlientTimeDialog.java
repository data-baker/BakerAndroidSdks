package com.baker.sdk.demo.gramophone.fragment;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.baker.sdk.demo.R;

public class SlientTimeDialog extends AppCompatDialogFragment {

    public interface DismissListener {
        void onDismiss();
    }

    DismissListener dismissListener;

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    private TextView tvShow;
    private TextView tvNumber;
    private final Handler mHandler = new Handler(Looper.getMainLooper());


    int count;
    private final CountDownTimer timer = new CountDownTimer(2000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            mHandler.post(() -> {
                tvNumber.setText(String.valueOf(count));
                count--;
            });
        }

        @Override
        public void onFinish() {
            if (dismissListener != null) {
                dismissListener.onDismiss();
            }
            dismissAllowingStateLoss();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialogFragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(requireContext()).inflate(R.layout.dialog_slient_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvShow = (TextView) view.findViewById(R.id.tvShow);
        tvNumber = (TextView) view.findViewById(R.id.tvNumber);
        count = 2;
        timer.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
              //  WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
               // layoutParams.width = dp2px(130);
               // getDialog().getWindow().setAttributes(layoutParams);
            }
        }
    }

    private int dp2px(float dipValue) {
        final float scale = requireContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
