package com.baker.vpr.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baker.sdk.http.CallbackListener;
import com.baker.sdk.vpr.BakerVpr;
import com.baker.sdk.vpr.bean.response.GetTokenResponse;
import com.baker.vpr.demo.base.BaseActivity;
import com.baker.vpr.demo.databinding.ActivityVprJavaBinding;

public class VprJavaActivity extends BaseActivity<ActivityVprJavaBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpr_java);
        initView();
    }

    private void initView() {
        getMBinding().btnVprGetToken.setOnClickListener(v -> {

            String[] key = getResources().getStringArray(R.array.vpr_key);

            BakerVpr.INSTANCE.initSdk(VprJavaActivity.this,key[0], key[1], new CallbackListener<GetTokenResponse>() {
                @Override
                public void onSuccess(GetTokenResponse response) {
                    System.out.println(response);
                    Toast.makeText(VprJavaActivity.this, "token 获取成功", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(VprJavaActivity.this, "token 获取失败", Toast.LENGTH_LONG).show();
                }
            },false);
        });
    }
}