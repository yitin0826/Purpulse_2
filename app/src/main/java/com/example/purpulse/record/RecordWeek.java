package com.example.purpulse.record;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.purpulse.Note;
import com.example.purpulse.R;
import com.example.purpulse.SqlDataBaseHelper;
import com.example.purpulse.result.ResultPagerAdapter;
import com.example.purpulse.result.ScatterFragment;
import com.example.purpulse.result.TaijiFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordWeek extends Fragment {

    /** UI **/
    View view;
    private Button btn_week;
    private TextView txt_getweek;
    private LineChart lineChart;
    private RecyclerView rv_week;
    /** 資料庫 **/
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 9;
    private static String DataBaseTable = "Users";
    private static SQLiteDatabase DB;
    private SqlDataBaseHelper sqlDataBaseHelper;
    /** RecyclerView **/
    MyListAdapter myListAdapter;
    ArrayList<String> Heart = new ArrayList<>();
    ArrayList<String> Date = new ArrayList<>();
    ArrayList<String> state = new ArrayList<>();
    /** itemClick **/
    private PopupWindow record;
    private TextView txt_lf,txt_sdnn,txt_hf;
    private Button record_ok;
    public static int lastPosition = 0;
    private ViewPager vp_individual;
    private TabLayout tl_individual;
    private ResultPagerAdapter adapter;
    /** popupWindow **/
    private PopupWindow weekset;
    private NumberPicker np_year,np_month,np_week;
    private Button week_ok;
    private Date date = new Date();
    private String s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record_week, container, false);
        btn_week = view.findViewById(R.id.btn_week);
        txt_getweek = view.findViewById(R.id.txt_getweek);
        lineChart = view.findViewById(R.id.lineChart);
        btn_week.setOnClickListener(lis);
        setWeek();

        //無數據時顯示
        lineChart.setNoDataText("無獲取數據");
        lineChart.setNoDataTextColor(Color.parseColor("#1b75bb"));


        return view;
    }

//    /** 折線圖設置 **/
//    public void setLineChart(){
//        //無數據時顯示
//        lineChart.setNoDataText("無獲取數據");
//        lineChart.setNoDataTextColor(Color.parseColor("#1b75bb"));
//        //初始化顯示
//        List<Float> floats = new ArrayList<>();
//        floats.add(0.5f);
//        floats.add(3f);
//        floats.add(1f);
//        floats.add(0.1f);
//        List<Entry> entries = new ArrayList<>();
//        for (int i = 0;i<floats.size();i++){
//            entries.add(new Entry(i,floats.get(i)));
//        }
//        //将数据赋给数据集,一个数据集表示一条线
//        LineDataSet lineDataSet = new LineDataSet(entries,"");
//        LineData lineData = new LineData(lineDataSet);
//        //不显示曲线点的具体数值
//        lineData.setDrawValues(false);
//        lineChart.setData(lineData);
//    }

    public void initRecycler() throws ParseException {
        int year = np_year.getValue();
        int month = np_month.getValue();
        int week = np_week.getValue();
        Heart.clear();
        Date.clear();
        state.clear();
        // 建立SQLiteOpenHelper物件
        sqlDataBaseHelper = new SqlDataBaseHelper(getActivity(),DataBaseName,null,DataBaseVersion,DataBaseTable);
        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
        Cursor D = DB.rawQuery("SELECT * FROM Data",null);
        D.moveToFirst();

        rv_week = view.findViewById(R.id.rv_week);
        rv_week.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_week.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        myListAdapter = new MyListAdapter();
        rv_week.setAdapter(myListAdapter);
        String dateformat = "yyyy/MM/dd HH:mm:ss"; //日期的格式
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        for (int i = 0;i<D.getCount();i++){     //按照順序顯示資料
            Date date = df.parse(D.getString(1));
            SimpleDateFormat mon = new SimpleDateFormat("M");
            int Mon = Integer.valueOf(mon.format(date));      //月
            SimpleDateFormat yea = new SimpleDateFormat("yyyy");
            int Yea = Integer.valueOf(yea.format(date));      //年
            if (Mon == month && week == D.getInt(2 ) && year == Yea){
                Heart.add(D.getString(9));
                Date.add(D.getString(1));
                state.add(D.getString(3));
            }
            D.moveToNext();     //下一筆資料
        }
        //折線圖
        //初始化顯示
        List<Integer> heartList = new ArrayList<>();
        for(int i=Heart.size()-1; i>(-1); i--){
            heartList.add(Integer.parseInt(Heart.get(i)));
        }
        Log.d("heartlist",""+heartList);
        List<Entry> entries = new ArrayList<>();
        for (int i = 0;i<heartList.size();i++){
            entries.add(new Entry(i,heartList.get(i)));
        }
        Log.d("entirs",""+entries);
        //將數據放入DataSet,一個數據列表示一條線
        LineDataSet lineDataSet = new LineDataSet(entries,"");
        LineData lineData = new LineData(lineDataSet);
        //不顯示點的具體數值
        lineData.setDrawValues(false);
        lineChart.setData(lineData);
    }

    /** 週數選擇的PopupWindow **/
    public void setWeek(){
        View weekView = LayoutInflater.from(getActivity()).inflate(R.layout.popwindow_week,null);
        weekset = new PopupWindow(weekView);
        weekset.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        weekset.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        weekset.setFocusable(false);
        week_ok = weekView.findViewById(R.id.week_ok);
        np_year = weekView.findViewById(R.id.np_year);
        np_month = weekView.findViewById(R.id.np_month);
        np_week = weekView.findViewById(R.id.np_week);
        npSet();
        week_ok.setOnClickListener(lis);
    }

    /** NumberPicker值設定 **/
    public void npSet(){
        Calendar cal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        np_year.setMinValue(year-10);
        np_year.setMaxValue(year+10);
        np_year.setValue(year);
        np_month.setMinValue(1);
        np_month.setMaxValue(12);
        np_month.setValue(cal.get(Calendar.MONTH)+1);
        int month = np_month.getValue();
        calendar.set(Calendar.MONTH,month);
        int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DATE,day);
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        np_week.setMinValue(1);
        np_week.setMaxValue(week);
        np_week.setValue(cal.get(Calendar.WEEK_OF_MONTH));
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.record_ok:{
                    record.dismiss();
                    break;
                }
                case R.id.btn_week:{
                    //Toast.makeText(getActivity(),"0.0.0",Toast.LENGTH_SHORT).show();
                    weekset.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                }
                case R.id.week_ok:{
                    try {
                        initRecycler();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    weekset.dismiss();
                    s = String.format("%04d/%02d 第%d週",np_year.getValue(),np_month.getValue(),np_week.getValue());
                    txt_getweek.setText(s);
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
                    public void onClick(View view) {        //點擊歷史紀錄

                        Note.Date = item_date.getText().toString();
                        Log.d("date",""+Note.Date);

                        // 建立SQLiteOpenHelper物件
                        sqlDataBaseHelper = new SqlDataBaseHelper(getActivity(),DataBaseName,null,DataBaseVersion,DataBaseTable);
                        DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                        Cursor D = DB.rawQuery("SELECT * FROM Data WHERE time LIKE '"+Note.Date+"'",null);
                        D.moveToFirst();
                        //點擊歷史紀錄顯示的值，取到小數後4位
                        txt_lf.setText(D.getString(7).substring(0,6));
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