package com.example.purpulse.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.purpulse.HomepageActivity;
import com.example.purpulse.Note;
import com.example.purpulse.PulseActivity;
import com.example.purpulse.R;
import com.example.purpulse.record.RecordActivity;
import com.example.purpulse.SqlDataBaseHelper;
import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener{

    private ImageButton pf_img;
    private DrawerLayout pf_drawerlayout;
    private NavigationView pf_navigation;
    public String activity;
    private String account = Note.account;
    private TextView txtHead,txtMail;
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 9;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        activity = "profile";
        pf_img = findViewById(R.id.pf_img);
        pf_img.setOnClickListener(lis);
        pf_drawerlayout = findViewById(R.id.pf_drawerlayout);
        pf_navigation = findViewById(R.id.pf_navigation);
        pf_navigation.setNavigationItemSelectedListener(NavigationLis);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        Log.d("Act_account",""+account);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.relativ_profile, new ProfileFragment(), "profile").commit();
        else
            onBackStackChanged();
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.pf_img:{
                    pf_drawerlayout.openDrawer(Gravity.RIGHT);

                    txtHead = findViewById(R.id.txtHeader);
                    txtMail = findViewById(R.id.txtHeader2);

                    // 建立SQLiteOpenHelper物件
                    sqlDataBaseHelper = new SqlDataBaseHelper(ProfileActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
                    DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                    Cursor D = DB.rawQuery("SELECT * FROM Users WHERE account LIKE '"+ account +"'",null);
                    D.moveToFirst();
                    //側邊欄的個人資訊
                    txtHead.setText(D.getString(0));
                    txtMail.setText(D.getString(3));
                    break;
                }
            }
        }
    };

    NavigationView.OnNavigationItemSelectedListener NavigationLis = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // 點選時收起選單
            pf_drawerlayout.closeDrawer(GravityCompat.END);

            int id = item.getItemId();

            // 取得選項id
            if (id == R.id.action_profile) {
                if (activity.equals("profile")){
                    return true;
                }else {
                    startActivity(new Intent(ProfileActivity.this,ProfileActivity.class));
                    return true;
                }
            }else if (id == R.id.action_record) {
                if (activity.equals("record")){
                    return true;
                }else {
                    startActivity(new Intent(ProfileActivity.this, RecordActivity.class));
                    return true;
                }
            }else if (id == R.id.action_device){
                if (activity.equals("device")){
                    return true;
                }else {
                    startActivity(new Intent(ProfileActivity.this, HomepageActivity.class));
                    return true;
                }
            }else if (id == R.id.action_pulse){
                if (activity.equals("pulse")){
                    return true;
                }else {
                    startActivity(new Intent(ProfileActivity.this, PulseActivity.class));
                    return true;
                }
            }else {
                if (activity.equals("notify")){
                    return true;
                }else {
//                    startActivity(new Intent(ProfileActivity.this,ProfileActivity.class));
                    return true;
                }
            }
        }
    };

    @Override
    public void onBackStackChanged() {

    }
}