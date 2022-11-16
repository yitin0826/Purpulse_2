package com.example.purpulse.result;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import com.example.purpulse.NameMapping;
import com.example.purpulse.Note;
import com.example.purpulse.R;
import com.example.purpulse.SqlDataBaseHelper;

import java.util.ArrayList;

public class TaijiActivity extends AppCompatActivity {

    private String Account = Note.account;

    ArrayList<NameMapping> taiji = new ArrayList<NameMapping>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taiji);
        ListItem();
    }

    public void ListItem(){
        taiji.add(new NameMapping("平常有鍛鍊，生活作息正常，睡眠正常。",R.mipmap.yinyang_1));
        taiji.add(new NameMapping("長壽體質，胃腸與免疫功能較強，易入睡。",R.mipmap.yinyang_2));
        taiji.add(new NameMapping("活動力強，較易緊張，睡眠較有問題，可能會不易入睡、早醒、睡眠不安穩。",R.mipmap.yinyang_3));
        taiji.add(new NameMapping("睡眠品質差、淺眠、早醒、不易入睡，生活極度緊張。",R.mipmap.yinyang_4));
        taiji.add(new NameMapping("易入睡，情緒非常平穩，生活步調較慢的人。",R.mipmap.yinyang_5));
        taiji.add(new NameMapping("臟器的神經活性較差，覺得可能全身狀況沒很好，睡眠與精神都處於不足的狀態。",R.mipmap.yinyang_6));
        taiji.add(new NameMapping("神經活性較差，全身狀況都有小毛病，如疼痛之類，情緒處於不安穩的狀態，睡眠品質差。",R.mipmap.yinyang_7));
        taiji.add(new NameMapping("感覺渾身不對勁，可入眠但覺得睡眠品質很差，有睡像沒睡一樣。",R.mipmap.yinyang_8));

        TaijiAdapter adapter = new TaijiAdapter(this, taiji);
        ListView listView = findViewById(R.id.list_taiji);
        listView.setAdapter(adapter);
    }
}