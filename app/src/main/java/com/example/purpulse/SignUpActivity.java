package com.example.purpulse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;

public class SignUpActivity extends AppCompatActivity {

    Button btn_signup,btn_gologin,birth_ok;
    EditText editText_nickname,signup_account,signup_password,edit_mail,edit_birthday,edit_height,edit_weight;
    Spinner spin_sex;
    String[] sex = new String[] {"男","女"};
    Context context;

    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 6;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;
    private String account,nickname,password,mail,birthday,height,weight,gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = this;
        btn_signup = findViewById(R.id.btn_signup);
        btn_gologin = findViewById(R.id.btn_gologin);
        editText_nickname = findViewById(R.id.edit_nickname);
        signup_account = findViewById(R.id.signup_account);
        signup_password = findViewById(R.id.signup_password);
        edit_mail = findViewById(R.id.edit_mail);
        edit_birthday = findViewById(R.id.edit_birthday);
        edit_birthday.setInputType(InputType.TYPE_NULL);
        edit_birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    showDatePickerDialog();
                }
            }
        });
        edit_birthday.setOnClickListener(lis);
        edit_height = findViewById(R.id.edit_height);
        edit_weight = findViewById(R.id.edit_weight);
        spin_sex = findViewById(R.id.spin_sex);
        btn_signup.setOnClickListener(lis);
        btn_gologin.setOnClickListener(lis);

        ArrayAdapter sp_sex = new ArrayAdapter(this, android.R.layout.simple_spinner_item,sex);
        sp_sex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_sex.setAdapter(sp_sex);

        Stetho.initializeWithDefaults(this);
        // 建立SQLiteOpenHelper物件
        sqlDataBaseHelper = new SqlDataBaseHelper(SignUpActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_signup:{
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    nickname = editText_nickname.getText().toString();
                    account = signup_account.getText().toString();
                    password = signup_password.getText().toString();
                    mail = edit_mail.getText().toString().trim();
                    birthday = edit_birthday.getText().toString();
                    height = edit_height.getText().toString();
                    weight = edit_weight.getText().toString();
                    gender = spin_sex.getSelectedItem().toString();
                    if (mail.matches(emailPattern)){

                    }else {
                        Toast.makeText(SignUpActivity.this, "無效信箱格式", Toast.LENGTH_SHORT).show();
                    };
                    if (nickname.length() != 0 && account.length() != 0 && password.length() != 0 && mail.length() != 0 && birthday.length() != 0 && height.length() != 0 && weight.length() != 0) {
                        Cursor D = DB.rawQuery("SELECT * FROM " + DataBaseTable,null);
                        D.moveToFirst();
                        if (D.getCount()<1){  //當一開始啥都沒有的時候
                            try {
                                DB.execSQL("INSERT INTO Users(name,account,password,email,birthday,gender,height,weight) " +
                                        "VALUES('" + nickname + "','" + account + "','" + password + "','" + mail + "','" + birthday + "','" + gender + "','" + height + "','" + weight + "')");
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                break;
                            }catch (InputMismatchException e){
                                Toast.makeText(SignUpActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                            }
                        }else {   //有了一筆資料之後
                            for (int i=0;i<=D.getCount();i++) {   //註冊檢查帳號密碼是否重複
                                if (account.equals(D.getString(1))) {
                                    Toast.makeText(SignUpActivity.this, "帳號重複", Toast.LENGTH_SHORT).show();
                                    break;
                                }else {
                                    try {
                                        DB.execSQL("INSERT INTO Users(name,account,password,email,birthday,gender,height,weight) " +
                                                "VALUES('" + nickname + "','" + account + "','" + password + "','" + mail + "','" + birthday + "','" + gender + "','" + height + "','" + weight + "')");
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        break;
                                    }catch (InputMismatchException e){
                                        Toast.makeText(SignUpActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }else {
                        Toast.makeText(SignUpActivity.this, "請勿留空", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case R.id.btn_gologin:{
                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                    break;
                }
                case R.id.edit_birthday:{
                    showDatePickerDialog();
                    break;
                }
            }
        }
    };

    private void showDatePickerDialog(){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                edit_birthday.setText(year+"-"+month+"-"+day);
            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }
}