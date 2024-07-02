package com.example.seedingsystemandroidapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.seedingsystemandroidapp.RTKSettingFragment.MotorControlFragment;
import com.example.seedingsystemandroidapp.RTKSettingFragment.PlanFragment;
import com.example.seedingsystemandroidapp.RTKSettingFragment.RTKDataDisplayFragment;
import com.example.seedingsystemandroidapp.RTKSettingFragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RTKSettingActivity extends AppCompatActivity {

    private BottomNavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private Fragment[] fragments;
    private int lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtksetting);

        mNavigationView = findViewById(R.id.main_navigation_bar);
        initFragment();
        initListener();

        // Delay loading other fragments to avoid crash
        new Handler().postDelayed(this::loadOtherFragments, 500);
    }

    private void initFragment() {
        RTKDataDisplayFragment mRTKDataDisplayFragment = new RTKDataDisplayFragment();
        PlanFragment mPlanFragment = new PlanFragment();
        MotorControlFragment mMotorControlFragment = new MotorControlFragment();
        SettingFragment mSettingFragment = new SettingFragment();
        fragments = new Fragment[]{mRTKDataDisplayFragment, mPlanFragment, mMotorControlFragment, mSettingFragment};
        mFragmentManager = getSupportFragmentManager();
        // 默认显示HomeFragment
        lastFragment = 0;
        mFragmentManager.beginTransaction()
                .replace(R.id.main_page_controller, mRTKDataDisplayFragment)
                .show(mRTKDataDisplayFragment)
                .commit();
    }

    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.home) {
                    if (lastFragment != 0) {
                        switchFragment(lastFragment, 0);
                        lastFragment = 0;
                    }
                    return true;
                } else if (i == R.id.plan) {
                    if (lastFragment != 1) {
                        switchFragment(lastFragment, 1);
                        lastFragment = 1;
                    }
                    return true;
                } else if (i == R.id.game) {
                    if (lastFragment != 2) {
                        switchFragment(lastFragment, 2);
                        lastFragment = 2;
                    }
                    return true;
                } else if (i == R.id.setting) {
                    if (lastFragment != 3) {
                        switchFragment(lastFragment, 3);
                        lastFragment = 3;
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void switchFragment(int lastFragment, int index) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.hide(fragments[lastFragment]);
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.main_page_controller, fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }

    private void loadOtherFragments() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (int i = 1; i < fragments.length; i++) {
            if (!fragments[i].isAdded()) {
                transaction.add(R.id.main_page_controller, fragments[i]);
                transaction.hide(fragments[i]);
            }
        }
        transaction.commitAllowingStateLoss();
    }
}
