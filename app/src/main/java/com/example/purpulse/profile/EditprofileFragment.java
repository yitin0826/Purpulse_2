package com.example.purpulse.profile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.purpulse.Note;
import com.example.purpulse.R;
import com.example.purpulse.SqlDataBaseHelper;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;

public class EditprofileFragment extends Fragment {

    EditText editText_nickname, edit_mail, edit_birthday, edit_height, edit_weight;
    Spinner spin_sex;
    Button btn_signup;
    String[] sex = new String[]{"男", "女"};
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 6;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;
    private String nickname, mail, birthday, height, weight, gender;
    private String account = Note.account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        editText_nickname = view.findViewById(R.id.edit_nickname);
        edit_mail = view.findViewById(R.id.edit_mail);
        edit_birthday = view.findViewById(R.id.edit_birthday);
        spin_sex = view.findViewById(R.id.spin_sex);
        edit_height = view.findViewById(R.id.edit_height);
        edit_weight = view.findViewById(R.id.edit_weight);
        btn_signup = view.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(btn);

        //下拉選單選項
        ArrayAdapter sp_sex = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, sex);
        sp_sex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_sex.setAdapter(sp_sex);

//        設定預設值
        editText_nickname.setText("" + Note.name);
        edit_mail.setText("" + Note.mail);
        edit_birthday.setText("" + Note.birthday);
        if (Note.gender.equals("男")) {
            spin_sex.setSelection(0, true);
        }
        if (Note.gender.equals("女")) {
            spin_sex.setSelection(1, true);
        }
        edit_height.setText("" + Note.height);
        edit_weight.setText("" + Note.weight);

        //查看資料庫的套件
        Stetho.initializeWithDefaults(getActivity());
        // 建立SQLiteOpenHelper物件
        sqlDataBaseHelper = new SqlDataBaseHelper(getActivity(), DataBaseName, null, DataBaseVersion, DataBaseTable);
        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
        Cursor D = DB.rawQuery("SELECT * FROM Users WHERE account LIKE '" + Note.account + "'", null);
        D.moveToFirst();
        return view;
    }

    View.OnClickListener btn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_signup: {
                    //設定變數給要寫到資料庫的資料
                    nickname = editText_nickname.getText().toString();
                    birthday = edit_birthday.getText().toString();
                    height = edit_height.getText().toString();
                    weight = edit_weight.getText().toString();
                    gender = spin_sex.getSelectedItem().toString();
                    mail = edit_mail.getText().toString().trim();
                    //判斷信箱格式
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    if (mail.matches(emailPattern)) {    //符合格式
                        //判斷格子是否為空
                        if (nickname.length() != 0 && mail.length() != 0 && birthday.length() != 0 && height.length() != 0 && weight.length() != 0) {
                            try {
                                //更新資料
                                DB.execSQL("UPDATE Users SET name='"+nickname+"',email='"+mail+"',birthday='"+birthday+"'," +
                                        "gender='"+gender+"',height='"+height+"',weight='"+weight+"' WHERE account LIKE '"+Note.account+"'");
                                //切換畫面
                                Fragment fragment = new ProfileFragment();
                                getFragmentManager().beginTransaction().replace(R.id.relativ_profile, fragment, "edit_end").addToBackStack(null).commit();
                            }catch (Exception e){
                                Toast.makeText(getActivity(), "失敗", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getActivity(), "請勿留空", Toast.LENGTH_SHORT).show();
                        }
                    } else {     //不符合
                        Toast.makeText(getActivity(), "無效信箱格式", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
}