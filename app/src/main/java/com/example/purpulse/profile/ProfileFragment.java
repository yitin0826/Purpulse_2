package com.example.purpulse.profile;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.purpulse.Note;
import com.example.purpulse.R;
import com.example.purpulse.SqlDataBaseHelper;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    //全域變數
    private Button btn_edit;
    private TextView name,mail,birthday,gender,height,weight;
    private String Account = Note.account;
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 4;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d("Account_ProfileFragment",""+Account);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name = view.findViewById(R.id.TV_name);
        mail = view.findViewById(R.id.TV_mail);
        birthday = view.findViewById(R.id.TV_bir);
        gender = view.findViewById(R.id.TV_gender);
        height = view.findViewById(R.id.TV_height);
        weight = view.findViewById(R.id.TV_weight);
        btn_edit = view.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(lis);

        //查看SQL的套件
        Stetho.initializeWithDefaults(getActivity());
        // 建立SQLiteOpenHelper物件
        sqlDataBaseHelper = new SqlDataBaseHelper(getActivity(),DataBaseName,null,DataBaseVersion,DataBaseTable);
        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
        Cursor D = DB.rawQuery("SELECT * FROM Users WHERE account LIKE '"+ Account +"'",null);
        D.moveToFirst();
        //從資料庫抓要顯示的值
        name.setText(D.getString(0));
        mail.setText(D.getString(3));
        birthday.setText(D.getString(4));
        gender.setText(D.getString(5));
        height.setText(D.getString(6));
        weight.setText(D.getString(7));
        return view;
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //取得資料
            Note.name = name.getText().toString();
            Note.mail = mail.getText().toString();
            Note.birthday = birthday.getText().toString();
            Note.gender = gender.getText().toString();
            Note.height = height.getText().toString();
            Note.weight = weight.getText().toString();

            //切換頁面
            EditprofileFragment fragment = new EditprofileFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.relativ_profile,fragment);
            fragmentTransaction.commit();
        }
    };


}