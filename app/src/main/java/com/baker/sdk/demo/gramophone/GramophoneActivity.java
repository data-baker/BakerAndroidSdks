package com.baker.sdk.demo.gramophone;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.engrave.lib.callback.InitListener;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.gramophone.fragment.ExperienceFragment;
import com.baker.sdk.demo.gramophone.fragment.HomeFragment;
import com.baker.sdk.demo.gramophone.util.SharedPreferencesUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class GramophoneActivity extends BakerBaseActivity {
    private SharedPreferences mSharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private ExperienceFragment experienceFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gramophone);

        setTitle("声音复刻");

        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);
        initView();

        //初始化复刻SDK
        BakerVoiceEngraver.getInstance().initSDK(GramophoneActivity.this, sharedPreferencesGet(Constants.GRAMOPHONE_CLIENT_ID),
                sharedPreferencesGet(Constants.GRAMOPHONE_CLIENT_SECRET), SharedPreferencesUtil.getQueryId(GramophoneActivity.this), new InitListener() {
                    @Override
                    public void onInitSuccess() {
                         //BakerVoiceEngraver.getInstance().setRecordSessionId("v0002d9eff36714fb05625de902ec985b011a99vkb1692949276039");
                        BakerVoiceEngraver.getInstance().requestConfig();
                    }

                    @Override
                    public void onInitError(Exception e) {

                    }
                });
    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        experienceFragment = new ExperienceFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            Fragment fragment = null;
            switch (menuItem.getItemId()) {
                case R.id.action_home:
                    fragment = homeFragment;
                    break;
                case R.id.action_experience:
                    fragment = experienceFragment;
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    //"
    private String sharedPreferencesGet(String mKey) {
        if (mSharedPreferences != null && !TextUtils.isEmpty(mKey)) {
            return mSharedPreferences.getString(mKey, "");
        }
        return null;
    }
}