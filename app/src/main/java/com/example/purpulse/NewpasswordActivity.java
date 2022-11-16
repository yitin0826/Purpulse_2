package com.example.purpulse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

public class NewpasswordActivity extends AppCompatActivity {

    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 4;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;
    EditText np_password, np_password2;
    Button np_confirm;
    private String account,newpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpassword);
        np_password = findViewById(R.id.np_password);
        np_password2 = findViewById(R.id.np_password2);
        np_confirm = findViewById(R.id.np_confirm);

        np_confirm.setOnClickListener(btn);

        Stetho.initializeWithDefaults(this);
        // 建立SQLiteOpenHelper物件
        sqlDataBaseHelper = new SqlDataBaseHelper(NewpasswordActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
        //收email
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String email = bundle.getString("email");

        //找到此mail的帳號
        Cursor D = DB.rawQuery("SELECT * FROM Users WHERE email LIKE '"+email+"'",null);
        D.moveToFirst();
        account = D.getString(1);
        Log.d("account",""+account);
    }

    View.OnClickListener btn = view -> {
        if (np_password.getText().toString().equals(np_password2.getText().toString())) {  //兩次輸入一樣
//            DB.rawQuery("UPDATE Users SET password = '"+newpassword+"' WHERE account = '"+account+"'", null);
            newpassword = np_password.getText().toString();
            ContentValues cv = new ContentValues();
            cv.put("password",newpassword);
            Log.d("newpassword",""+newpassword);
            Log.d("cv",""+cv);
            DB.update("Users",cv,"account="+account,null);
            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "更改失敗", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "密碼輸入不一致", Toast.LENGTH_SHORT).show();
        }

    };
}