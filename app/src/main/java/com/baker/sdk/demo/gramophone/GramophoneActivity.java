package com.baker.sdk.demo.gramophone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.gramophone.fragment.ExperienceFragment;
import com.baker.sdk.demo.gramophone.fragment.HomeFragment;
import com.baker.sdk.demo.gramophone.util.SharedPreferencesUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.UUID;

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
                sharedPreferencesGet(Constants.GRAMOPHONE_CLIENT_SECRET), SharedPreferencesUtil.getQueryId(GramophoneActivity.this),null);
    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        experienceFragment = new ExperienceFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    private String sharedPreferencesGet(String mKey) {
        if (mSharedPreferences != null && !TextUtils.isEmpty(mKey)) {
            return mSharedPreferences.getString(mKey, "");
        }
        return null;
    }
}