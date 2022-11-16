package com.example.purpulse.result;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.purpulse.Note;
import com.example.purpulse.R;
import com.example.purpulse.SqlDataBaseHelper;
import com.example.purpulse.connection.ConnectionPagerAdapter;
import com.example.purpulse.scatter.FastFragment;
import com.example.purpulse.scatter.IrregularFragment;
import com.example.purpulse.scatter.NormalFragment;
import com.example.purpulse.scatter.SlowFragment;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator;

public class ScatterActivity extends AppCompatActivity{

    private CombinedChart scatter;
    private Random random;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private ConnectionPagerAdapter adapter;
    private CircleIndicator circleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scatter);
        random = new Random();
        scatter = findViewById(R.id.scatter);
        initCombine();
        initView();
        initFragment();
    }

    private void initFragment(){
        fragmentList.add(new NormalFragment());
        fragmentList.add(new SlowFragment());
        fragmentList.add(new FastFragment());
        fragmentList.add(new IrregularFragment());
        adapter = new ConnectionPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        circleIndicator.setViewPager(viewPager);
    }

    public void initView(){
        viewPager = findViewById(R.id.viewPager_scatter);
        viewPager.setOnPageChangeListener(lis);
        circleIndicator = findViewById(R.id.indicator_2);
    }

    ViewPager.OnPageChangeListener lis = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void initCombine(){
        setTitle("CombinedChartTest");
        scatter.getDescription().setEnabled(false);
        scatter.setDrawGridBackground(false);
        scatter.setTouchEnabled(true);
        scatter.setMaxHighlightDistance(10f);
        scatter.setBackgroundColor(Color.WHITE);
        scatter.setDrawBarShadow(false);
        scatter.setHighlightFullBarEnabled(false);

        // 支持缩放和拖动
        scatter.setDragEnabled(true);
        scatter.setScaleEnabled(true);

        scatter.setMaxVisibleValueCount(10);
        scatter.setPinchZoom(true);

        scatter.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });


        Legend l = scatter.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(false);

        YAxis yl = scatter.getAxisLeft();
        yl.setDrawGridLines(false);
        yl.setAxisMinimum(0f);// this replaces setStartAtZero(true)
        yl.setAxisMaximum(2000f);
        yl.setLabelCount(4);

        scatter.getAxisRight().setEnabled(false);

        XAxis xl = scatter.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawGridLines(false);
        xl.setAxisMinimum(0f);
        xl.setAxisMaximum(200f);
        xl.setGranularity(1f);
        xl.setDrawGridLines(false);
        xl.setLabelCount(4);

        CombinedData data = new CombinedData();
        data.setData(generateLineData());
        data.setData(generateScatterData());
        xl.setAxisMaximum(data.getXMax() + 0.25f);

        scatter.setData(data);
        scatter.invalidate();
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries1 = new ArrayList<>();

        entries1.add(new Entry(0,500));
        entries1.add(new Entry(500,0));

        LineDataSet set1 = new LineDataSet(entries1, "1");
        set1.setColor(Color.LTGRAY);
        set1.setLineWidth(2.5f);
        set1.setDrawCircles(false);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setDrawValues(false);



        ArrayList<Entry> entries2 = new ArrayList<>();

        entries2.add(new Entry(0,1000));
        entries2.add(new Entry(1000,0));

        LineDataSet set2 = new LineDataSet(entries2, "2");
        set2.setColor(Color.LTGRAY);
        set2.setLineWidth(2.5f);
        set2.setDrawCircles(false);
        set2.setMode(LineDataSet.Mode.LINEAR);
        set2.setDrawValues(false);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<Entry> entries3 = new ArrayList<>();

        entries3.add(new Entry(0,1500));
        entries3.add(new Entry(1500,0));

        LineDataSet set3 = new LineDataSet(entries3, "3");
        set3.setColor(Color.LTGRAY);
        set3.setLineWidth(2.5f);
        set3.setDrawCircles(false);
        set3.setMode(LineDataSet.Mode.LINEAR);
        set3.setDrawValues(false);
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<Entry> entries4 = new ArrayList<>();

        entries4.add(new Entry(0,0));
        entries4.add(new Entry(2000,2000));

        LineDataSet set4 = new LineDataSet(entries4, "0");
        set4.setColor(Color.LTGRAY);
        set4.setLineWidth(2.5f);
        set4.setDrawCircles(false);
        set4.setMode(LineDataSet.Mode.LINEAR);
        set4.setDrawValues(false);
        set4.setAxisDependency(YAxis.AxisDependency.LEFT);


        d.addDataSet(set1);
        d.addDataSet(set2);
        d.addDataSet(set3);
        d.addDataSet(set4);

        return d;
    }

    private ScatterData generateScatterData() {

        ScatterData d = new ScatterData();


//        ArrayList<Float> rr_intervals = new ArrayList<>();
        ArrayList<Entry> values = new ArrayList<>();
        float a,b;
        Log.e("Note.RRi",""+Note.RRi);
        for (int i = 1; i <= Note.RRi.size(); i++) {
            a = Note.RRi.get(i);
            if (i + 2 > Note.RRi.size()) {  //防止抓超過陣列
                break;
            }
            b = Note.RRi.get(i+1);
            values.add(new Entry(a,b));
        }

        ScatterDataSet set = new ScatterDataSet(values, "Scatter DataSet");
        set.setColors(Color.BLUE);
        set.setScatterShapeSize(7.5f);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        d.addDataSet(set);

        return d;
    }

}