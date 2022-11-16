package com.example.purpulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.purpulse.connection.ConnectionActivity;
import com.example.purpulse.profile.ProfileActivity;
import com.example.purpulse.record.RecordActivity;
import com.google.android.material.navigation.NavigationView;

public class HomepageActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener{

    //全域變數
    private ImageButton hp_img;
    private Toolbar hp_toolbar;
    private DrawerLayout hp_drawerlayout;
    private NavigationView hp_navigation;
    public View view;
    public String activity;
    private TextView txtHead,txtMail;
    private String Account = Note.account;
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 9;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        activity = "device";
        hp_img = findViewById(R.id.hp_img);
        hp_img.setOnClickListener(lis);
        hp_drawerlayout = findViewById(R.id.hp_drawerlayout);
        hp_navigation = findViewById(R.id.hp_navigation);
        hp_navigation.setNavigationItemSelectedListener(NavigationLis);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.device, new DeviceFragment(), "devices").commit();
        else
            onBackStackChanged();
    }
    //點選右上按鈕
    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hp_drawerlayout.openDrawer(Gravity.RIGHT);
            txtHead = findViewById(R.id.txtHeader);
            txtMail = findViewById(R.id.txtHeader2);

            // 建立SQLiteOpenHelper物件
            sqlDataBaseHelper = new SqlDataBaseHelper(HomepageActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
            DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
            Cursor D = DB.rawQuery("SELECT * FROM Users WHERE account LIKE '"+ Account +"'",null);
            D.moveToFirst();
            //側邊欄的個人資訊
            txtHead.setText(D.getString(0));
            txtMail.setText(D.getString(3));
        }
    };
        //側邊攔
    NavigationView.OnNavigationItemSelectedListener NavigationLis = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // 點選時收起選單
            hp_drawerlayout.closeDrawer(GravityCompat.END);


            // 取得選項id
            int id = item.getItemId();

            // 依照id判斷點了哪個項目並做相應事件
            if (id == R.id.action_profile) {
                if (activity.equals("profile")){    //會員資料
                    return true;
                }else {
                    startActivity(new Intent(HomepageActivity.this,ProfileActivity.class));
                    return true;
                }
            }else if (id == R.id.action_record) {
                if (activity.equals("record")){     //量測記錄
                    return true;
                }else {
                    startActivity(new Intent(HomepageActivity.this, RecordActivity.class));
                    return true;
                }
            }else if (id == R.id.action_device){
                if (activity.equals("device")){     //裝置連接
                    return true;
                }else {
                    startActivity(new Intent(HomepageActivity.this,ProfileActivity.class));
                    return true;
                }
            }else if (id == R.id.action_pulse){
                if (activity.equals("pulse")){      //心跳測量
                    return true;
                }else {
                    startActivity(new Intent(HomepageActivity.this, ConnectionActivity.class));
                    return true;
                }
            }else {
                if (activity.equals("notify")){     //定時通知
                    return true;
                }else {
                    startActivity(new Intent(HomepageActivity.this,NotifyActivity.class));
                    return true;
                }
            }
        }
    };

    @Override
    public void onBackStackChanged() {
        //getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount()>0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //手機返回鍵
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("提示訊息")
                .setMessage("確定要離開這個頁面嗎？")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    super.onBackPressed();
                }).create().show();
    }
}