package com.example.purpulse;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

public class LoginActivity extends AppCompatActivity {

    Button btn_login,btn_gosignup,btn_forgetpw;
    EditText edittext_account,edittext_password;
    CheckBox checkBox;

    private int back = 0;
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 6;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;
    private boolean login = false;

    /**記住我**/
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.btn_login);
        btn_gosignup = findViewById(R.id.btn_gosignup);
        btn_forgetpw = findViewById(R.id.btn_forget);
        edittext_account = findViewById(R.id.edit_account);
        edittext_password = findViewById(R.id.edit_password);
        checkBox = findViewById(R.id.checkbox_1);
        btn_login.setOnClickListener(lis);
        btn_gosignup.setOnClickListener(lis);
        btn_forgetpw.setOnClickListener(lis);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = preferences.getBoolean("remember",false);
        if(isRemember){
            String acc = preferences.getString("acc","");
            String pas = preferences.getString("pas","");
            edittext_account.setText(acc);
            edittext_password.setText(pas);
            checkBox.setChecked(true);
        }

        Stetho.initializeWithDefaults(this);
        // 建立SQLiteOpenHelper物件
        sqlDataBaseHelper = new SqlDataBaseHelper(LoginActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_login: {
                    if (edittext_account.getText().toString().length() < 1 | edittext_password.getText().toString().length() < 1) {
                        Toast.makeText(LoginActivity.this, "欄位不得空白", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        //利用Curse去一筆一筆查看資料庫資料
                        Cursor D = DB.rawQuery("SELECT * FROM " + DataBaseTable, null);
                        D.moveToFirst();
                        for (int i = 0; i < D.getCount(); i++) {   //登入檢查
                            if (edittext_account.getText().toString().equals(D.getString(1)) && edittext_password.getText().toString().equals(D.getString(2))) {
                                editor = preferences.edit();    //記住我
                                if (checkBox.isChecked()) {    //記住我
                                    editor.putBoolean("remember", true);
                                    editor.putString("acc", edittext_account.getText().toString());
                                    editor.putString("pas", edittext_password.getText().toString());
                                } else {
                                    editor.clear();             //記住我
                                }
                                editor.apply();                 //記住我
                                //傳帳號過去當識別資料
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this,HomepageActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("account",""+edittext_account.getText().toString());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                login = true;
                                Note.account = edittext_account.getText().toString();
                                Log.d("note",""+Note.account);
                                break;
                            }
                            D.moveToNext();     //指標往下
                        }

                    }
                    if (login == false){
                        Toast.makeText(LoginActivity.this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case R.id.btn_gosignup:{
                    startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                    break;
                }
                case R.id.btn_forget:{
                    startActivity(new Intent(LoginActivity.this,PasswordActivity.class));
                    break;
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        back+=1;
        if (back == 1){
            Toast.makeText(LoginActivity.this, "再點擊一次則退出程式",Toast.LENGTH_SHORT).show();
        }else if (back>1){
            super.onBackPressed();
        }
    }
}