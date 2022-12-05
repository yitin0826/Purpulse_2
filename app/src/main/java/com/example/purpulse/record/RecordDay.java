package com.example.purpulse.record;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.purpulse.Note;
import com.example.purpulse.R;
import com.example.purpulse.SqlDataBaseHelper;
import com.example.purpulse.result.ResultPagerAdapter;
import com.example.purpulse.result.ScatterFragment;
import com.example.purpulse.result.TaijiFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecordDay extends Fragment {

    View view;
    RecyclerView recyclerView;
    MyListAdapter myListAdapter;
    ArrayList<String> Heart = new ArrayList<>();
    ArrayList<String> Date = new ArrayList<>();
    ArrayList<String> state = new ArrayList<>();
    /** 資料庫 **/
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 9;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;
    /** itemClick **/
    private PopupWindow record;
    private TextView txt_lf,txt_sdnn,txt_hf;
    private Button record_ok;
    public static int lastPosition = 0;
    private ViewPager vp_individual;
    private TabLayout tl_individual;
    private ResultPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record_day, container, false);
        initRecycler();
        return view;
    }

    public void initRecycler(){
        // 建立SQLiteOpenHelper物件
        sqlDataBaseHelper = new SqlDataBaseHelper(getActivity(),DataBaseName,null,DataBaseVersion,DataBaseTable);
        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
        Cursor D = DB.rawQuery("SELECT * FROM Data",null);
        D.moveToFirst();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        myListAdapter = new MyListAdapter();
        recyclerView.setAdapter(myListAdapter);
        HashMap<String,String> hashMap = new HashMap<>();
        for (int i = 0;i<D.getCount();i++){     //按照順序顯示資料
            int j = Integer.parseInt(D.getString(9));
            if (j<80){
                Heart.add("80");
            }else if (j > 100){
                Heart.add("100");
            }else {
                Heart.add(D.getString(9));
            }
            Date.add(D.getString(1));
            state.add(D.getString(3));
            D.moveToNext();     //下一筆資料
        }
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.record_ok:{
                    record.dismiss();
                    break;
                }
            }
        }
    };

    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView item_heartrate,item_status,item_date;
            private ImageView img_circle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                item_heartrate = itemView.findViewById(R.id.item_heartrate);
                item_status = itemView.findViewById(R.id.item_status);
                item_date = itemView.findViewById(R.id.item_date);
                img_circle = itemView.findViewById(R.id.img_circle);
                popRecord();

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Note.Date = item_date.getText().toString();
                        Log.d("date",""+Note.Date);

                        // 建立SQLiteOpenHelper物件
                        sqlDataBaseHelper = new SqlDataBaseHelper(getActivity(),DataBaseName,null,DataBaseVersion,DataBaseTable);
                        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                        Cursor D = DB.rawQuery("SELECT * FROM Data WHERE time LIKE '"+Note.Date+"'",null);
                        D.moveToFirst();
                        //點擊歷史紀錄顯示的值
                        txt_lf.setText(D.getString(7).substring(0,6));;
                        txt_hf.setText(D.getString(8).substring(0,6));
                        txt_sdnn.setText(D.getString(5));
                        //太極圖數據
                        Note.HFn = D.getDouble(8);
                        Note.LFn = D.getDouble(7);
                        Note.sdNN = D.getDouble(5);
                        //散點圖數據
                        Note.RRi.clear();   //先清空怕資料重疊
                        String test = D.getString(10).replaceAll(" ","");    //取得數據以及清除當中的空格
                        Log.d("test",""+test);
                        String[] sqlrri = test.split("\\[|,|\\]",0);    //去除括弧和逗號
                        Log.d("sqlrri",""+ Arrays.toString(sqlrri));
                        for (int i=1;i<sqlrri.length;i++){
                            Note.RRi.add(Integer.parseInt(sqlrri[i]));      //轉成int並寫到Note.RRi當變數用
                        }
                        Log.d("Note.RRi",""+Note.RRi);

                        record.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 0);
                    }
                });
            }
        }

        @NonNull
        @Override
        public MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record,parent,false);
            return new MyListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyListAdapter.ViewHolder holder, int position) {
            holder.item_heartrate.setText(Heart.get(position));
            holder.item_status.setText(state.get(position));
            holder.item_date.setText(Date.get(position));
            String heart = Heart.get(position);
            if(Integer.parseInt(heart)<80){
                holder.img_circle.setImageResource(R.drawable.circle_green);
            }else if (Integer.parseInt(heart)>=80 && Integer.parseInt(heart)<90){
                holder.img_circle.setImageResource(R.drawable.circle_yellow);
            }else if (Integer.parseInt(heart)>=90 && Integer.parseInt(heart)<100){
                holder.img_circle.setImageResource(R.drawable.circle_red);
            }else if (Integer.parseInt(heart)>=100){
                holder.img_circle.setImageResource(R.drawable.circle_purple);
            }
        }

        @Override
        public int getItemCount() {
            return Date.size();
        }

        public void popRecord(){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popwindow_record,null);
            record = new PopupWindow(view);
            int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            record.setWidth(width*5/7);
            record.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            record.setFocusable(false);
            txt_lf = view.findViewById(R.id.txt_lf);
            txt_sdnn = view.findViewById(R.id.txt_sdnn);
            txt_hf = view.findViewById(R.id.txt_hf);
            record_ok = view.findViewById(R.id.record_ok);
            vp_individual = view.findViewById(R.id.vp_individual);
            tl_individual = view.findViewById(R.id.tl_individual);
            record_ok.setOnClickListener(lis);
            //initView();
        }

        public void initView(){
            vp_individual.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    lastPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            setupViewPager(vp_individual);
            tl_individual.setupWithViewPager(vp_individual);
            tl_individual.getTabAt(0).setIcon(R.drawable.scatter_graph);
            tl_individual.getTabAt(1).setIcon(R.drawable.yin_yang);
        }

        private void setupViewPager(ViewPager viewPager){
            List<Fragment> fragmentList = new ArrayList<>();
            fragmentList.add(new ScatterFragment());
            fragmentList.add(new TaijiFragment());
            adapter = new ResultPagerAdapter(getActivity().getSupportFragmentManager(),getActivity(),fragmentList);
            viewPager.setAdapter(adapter);
        }
    }
}