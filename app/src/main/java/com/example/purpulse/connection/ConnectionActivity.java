package com.example.purpulse.connection;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.purpulse.LoginActivity;
import com.example.purpulse.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class ConnectionActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private ConnectionPagerAdapter adapter;
    private CircleIndicator circleIndicator;
    private String device;
    Fragment fragment = new RighthandFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        initView();
        initFragment();
    }

    private void initFragment(){
        fragmentList.add(new AbdomenFragment());
        fragmentList.add(new LefthandFragment());
        fragmentList.add(fragment);
        adapter = new ConnectionPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        circleIndicator.setViewPager(viewPager);
    }

    public void initView(){
        viewPager = findViewById(R.id.viewPager_connection);
        viewPager.setOnPageChangeListener(lis);
        circleIndicator = findViewById(R.id.indicator);
    }

    ViewPager.OnPageChangeListener lis = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("提示訊息")
                .setMessage("要返回上個頁面嗎？")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    super.onBackPressed();
                }).create().show();
    }
}