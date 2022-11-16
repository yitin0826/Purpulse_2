package com.example.purpulse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PasswordActivity extends AppCompatActivity {

    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 4;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;
    Button pw_confirm;
    TextView pw_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        pw_confirm = findViewById(R.id.pw_confirm);
        pw_email = findViewById(R.id.pw_email);
        pw_confirm.setOnClickListener(lis);
        // 建立SQLiteOpenHelper物件
        sqlDataBaseHelper = new SqlDataBaseHelper(PasswordActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.pw_confirm:{
                    Cursor D = DB.rawQuery("SELECT * FROM " + DataBaseTable,null);
                    D.moveToFirst();
                    for (int i=0;i<D.getCount();i++) {   //登入檢查
                        Log.d("email",""+D.getString(3));
                        if (pw_email.getText().toString().equals(D.getString(3))) {
                            //將email傳到下個頁面使用
                            Intent intent = new Intent();
                            intent.setClass(PasswordActivity.this,NewpasswordActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("email",pw_email.getText().toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {

                        }
                        D.moveToNext();
                    }
                    break;
                }
            }
        }
    };
}