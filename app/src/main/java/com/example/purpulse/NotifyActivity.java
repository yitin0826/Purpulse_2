package com.example.purpulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.example.purpulse.connection.ConnectionActivity;
import com.example.purpulse.profile.ProfileActivity;
import com.example.purpulse.record.RecordActivity;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotifyActivity extends AppCompatActivity {

    private ImageButton btn_alarmadd,notify_img;
    private TextView txtHead,txtMail;
    private PopupWindow alarmset;
    private Context context;
    private Button alarm_ok;
    private NumberPicker np_hr,np_min;
    private EditText edit_alarm;
    private DrawerLayout notify_drawerlayout;
    private NavigationView notify_navigation;
    private String activity;
    private ListView list_alarm;
    private ArrayList<String> alarmList = new ArrayList();
    private AlarmAdapter alarm_adapter;
    private Switch switchbtn;
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 9;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;
    private String Account = Note.account;
    String s ;
    Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        context = this;
        activity = "notify";
        btn_alarmadd = findViewById(R.id.btn_alarmadd);
        notify_img = findViewById(R.id.notify_img);
        notify_drawerlayout = findViewById(R.id.notify_drawerlayout);
        notify_navigation = findViewById(R.id.notify_navigation);
        notify_navigation.setNavigationItemSelectedListener(NavigationLis);
        btn_alarmadd.setOnClickListener(lis);
        notify_img.setOnClickListener(lis);
        list_alarm = findViewById(R.id.list_alarm);
        //list_alarm.setOnItemClickListener(list_lis);
        alarm_adapter = new AlarmAdapter(NotifyActivity.this, alarmList);
        list_alarm.setAdapter(alarm_adapter);
        alarmWindow();
    }

    NavigationView.OnNavigationItemSelectedListener NavigationLis = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // 點選時收起選單
            notify_drawerlayout.closeDrawer(GravityCompat.END);

            int id = item.getItemId();

            // 取得選項id
            if (id == R.id.action_profile) {
                if (activity.equals("profile")){
                    return true;
                }else {
                    startActivity(new Intent(NotifyActivity.this, ProfileActivity.class));
                    return true;
                }
            }else if (id == R.id.action_record) {
                if (activity.equals("record")){
                    return true;
                }else {
                    startActivity(new Intent(NotifyActivity.this, RecordActivity.class));
                    return true;
                }
            }else if (id == R.id.action_device){
                if (activity.equals("device")){
                    return true;
                }else {
                    startActivity(new Intent(NotifyActivity.this,ProfileActivity.class));
                    return true;
                }
            }else if (id == R.id.action_pulse){
                if (activity.equals("pulse")){
                    return true;
                }else {
                    startActivity(new Intent(NotifyActivity.this, ConnectionActivity.class));
                    return true;
                }
            }else {
                if (activity.equals("notify")){
                    return true;
                }else {
                    startActivity(new Intent(NotifyActivity.this,NotifyActivity.class));
                    return true;
                }
            }
        }
    };

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.notify_img:{
                    notify_drawerlayout.openDrawer(Gravity.RIGHT);
                    txtHead = findViewById(R.id.txtHeader);
                    txtMail = findViewById(R.id.txtHeader2);
                    // 建立SQLiteOpenHelper物件
                    sqlDataBaseHelper = new SqlDataBaseHelper(NotifyActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
                    DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                    Cursor D = DB.rawQuery("SELECT * FROM Users WHERE account LIKE '"+ Account +"'",null);
                    D.moveToFirst();
                    //側邊欄的個人資訊
                    txtHead.setText(D.getString(0));
                    txtMail.setText(D.getString(3));
                    break;
                }
                case R.id.btn_alarmadd:{
                    alarmset.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                }
                case R.id.alarm_ok:{
                    alarmset.dismiss();
                    s = String.format("%02d:%02d",np_hr.getValue(),np_min.getValue());
                    alarmList.add(s);
                    alarm_adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    };

    public void alarmWindow(){
        View view = LayoutInflater.from(context).inflate(R.layout.popwindow_alarm,null);
        alarmset = new PopupWindow(view);
        alarmset.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        alarmset.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        alarmset.setFocusable(false);
        alarm_ok = view.findViewById(R.id.alarm_ok);
        np_hr = view.findViewById(R.id.np_hr);
        np_min = view.findViewById(R.id.np_min);
        numpickerSet();
        alarm_ok.setOnClickListener(lis);
    }

    public void numpickerSet(){
        np_hr.setMaxValue(23);
        np_hr.setMinValue(0);
        np_hr.setValue(Integer.parseInt(new SimpleDateFormat("HH").format(date)));
        np_min.setMaxValue(59);
        np_min.setMinValue(0);
        np_min.setValue(Integer.parseInt(new SimpleDateFormat("mm").format(date)));
    }
}