package com.example.purpulse.record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.purpulse.HomepageActivity;
import com.example.purpulse.Note;
import com.example.purpulse.NotifyActivity;
import com.example.purpulse.PulseActivity;
import com.example.purpulse.R;
import com.example.purpulse.SqlDataBaseHelper;
import com.example.purpulse.profile.ProfileActivity;
import com.example.purpulse.result.ResultPagerAdapter;
import com.example.purpulse.result.ScatterFragment;
import com.example.purpulse.result.TaijiFragment;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    ImageButton record_img;
    private DrawerLayout record_drawerlayout;
    private NavigationView record_navigation;
    private String activity;
    private String Account = Note.account;
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 9;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;
    private SegmentTabLayout tl_record;
    private ViewPager vp_record;
    private String[] tl_title = {"日","週"};
    private TextView txtHead,txtMail;
    List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        activity = "record";
        record_img = findViewById(R.id.record_img);
        record_drawerlayout = findViewById(R.id.record_drawerlayout);
        record_navigation = findViewById(R.id.record_navigation);
        tl_record = findViewById(R.id.tl_record);
        vp_record = findViewById(R.id.vp_record);
        record_navigation.setNavigationItemSelectedListener(NavigationLis);
        record_img.setOnClickListener(lis);
        tabLayout();
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.record_img:{      //側邊攔
                    record_drawerlayout.openDrawer(Gravity.RIGHT);

                    txtHead = findViewById(R.id.txtHeader);
                    txtMail = findViewById(R.id.txtHeader2);
                    // 建立SQLiteOpenHelper物件
                    sqlDataBaseHelper = new SqlDataBaseHelper(RecordActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
                    DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                    Cursor D = DB.rawQuery("SELECT * FROM Users WHERE account LIKE '"+ Account +"'",null);
                    D.moveToFirst();
                    //側邊欄的個人資訊
                    txtHead.setText(D.getString(0));
                    txtMail.setText(D.getString(3));
                    break;
                }
            }
        }
    };

    /** 日、週選擇 **/
    public void tabLayout(){
        fragmentList.add(new RecordDay());
        fragmentList.add(new RecordWeek());
        vp_record.setAdapter(new RecordAdapter(getSupportFragmentManager()));
        tl_record.setTabData(tl_title);
        tl_record.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vp_record.setCurrentItem(position);
            }
            @Override
            public void onTabReselect(int position) {

            }
        });
        vp_record.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tl_record.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp_record.setCurrentItem(1);
    }

    private class RecordAdapter extends FragmentPagerAdapter {
        public RecordAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tl_title[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }

    NavigationView.OnNavigationItemSelectedListener NavigationLis = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // 點選時收起選單
            record_drawerlayout.closeDrawer(GravityCompat.END);

            int id = item.getItemId();

            // 取得選項id
            if (id == R.id.action_profile) {
                if (activity.equals("profile")){
                    return true;
                }else {
                    startActivity(new Intent(RecordActivity.this, ProfileActivity.class));
                    return true;
                }
            }else if (id == R.id.action_record) {
                if (activity.equals("record")){
                    return true;
                }else {
                    startActivity(new Intent(RecordActivity.this,RecordActivity.class));
                    return true;
                }
            }else if (id == R.id.action_device){
                if (activity.equals("device")){
                    return true;
                }else {
                    startActivity(new Intent(RecordActivity.this, HomepageActivity.class));
                    return true;
                }
            }else if (id == R.id.action_pulse){
                if (activity.equals("pulse")){
                    return true;
                }else {
                    startActivity(new Intent(RecordActivity.this, PulseActivity.class));
                    return true;
                }
            }else {
                if (activity.equals("notify")){
                    return true;
                }else {
                    startActivity(new Intent(RecordActivity.this, NotifyActivity.class));
                    return true;
                }
            }
        }
    };
}