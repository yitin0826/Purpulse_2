package com.example.purpulse.result;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.purpulse.Note;
import com.example.purpulse.NotifyActivity;
import com.example.purpulse.SqlDataBaseHelper;
import com.example.purpulse.profile.ProfileActivity;
import com.example.purpulse.PulseActivity;
import com.example.purpulse.R;
import com.example.purpulse.record.RecordActivity;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.me.berndporr.iirj.Butterworth;

public class ResultActivity extends AppCompatActivity {

    private ImageButton result_img;
    private DrawerLayout result_drawerlayout;
    private NavigationView result_navigation;
    private TextView txtHead,txtMail;
    public String activity;
    public Bundle instance;
    private Context context;

    /** Output **/
    @SuppressLint("HandlerLeak")
    private double[] butterFilter;
    private TextView tv_result;
    public static Handler mHandler,mHandler2;
    private String json;
    private Double RMSSD,sdNN,LFHF,LFn,HFn,Heart;
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 6;
    private static String DataBaseTable = "Users";
    private static String DataBaseTable2 = "Data";
    private static SQLiteDatabase DB;
    private static SQLiteDatabase DB2;
    private SqlDataBaseHelper sqlDataBaseHelper;
    private String account = Note.account;
    private ArrayList<Integer> RRi = new ArrayList<>();
    private String success;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        activity = "result";
        context = this;
        result_img = findViewById(R.id.result_img);
        result_img.setOnClickListener(lis);
        result_drawerlayout = findViewById(R.id.result_drawerlayout);
        result_navigation = findViewById(R.id.result_navigation);
        result_navigation.setNavigationItemSelectedListener(NavigationLis);
        mHandler = new MHandler();
        mHandler2 = new MHandler2();
        instance = savedInstanceState;
        makeCSV();
        uploadDialog();
    }

    public void uploadDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("結果上傳");
        progressDialog.setMessage("結果上傳中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
//        if (success.equals("true")){
//            progressDialog.dismiss();
//        }
    }

    /** 巴特沃斯濾波(只濾一次) **/
    public double[] butter_bandpass_filter(List<Double> data, int lowCut, int highCut, int fs, int order) {
        Butterworth butterworth = new Butterworth();
        double widthFrequency = highCut - lowCut;
        double centerFrequency = (highCut + lowCut) / 2;
        butterworth.bandPass(order, fs, centerFrequency, widthFrequency);
        double[] list = new double[data.size()];
        int in = 0;
        for (double v : data) {
            double f = butterworth.filter(v);
            list[in] = f;
            in++;
        }
        return list;
    }

    private void makeCSV() {
        List<String> list = getIntent().getStringArrayListExtra("saveList");
        List<Double> listDouble = new ArrayList<>();
        for (String s : list) {
            try {
                listDouble.add(Double.valueOf(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            butterFilter = butter_bandpass_filter(listDouble, 3, 60, 125, 1);
            Thread.sleep(200);
        } catch (Exception e) {

        }
        new Thread(() -> {
            /** 檔名 */
            String date = new SimpleDateFormat("yyyy-MM-dd-hhmmss",
                    Locale.getDefault()).format(System.currentTimeMillis());
            String fileName = "[" + date + "]revlis.csv";
            String[] title = {"Lead2"};
            StringBuffer csvText = new StringBuffer();
            for (int i = 0; i < title.length; i++) {
                csvText.append(title[i] + ",");
            }
            /** 內容 */
            for (int i = 0; i < butterFilter.length; i++) {
                csvText.append("\n" + butterFilter[i]);
            }

            Log.d("CSV", "makeCSV: " + csvText);
            ResultActivity.this.runOnUiThread(() -> {
                try {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    builder.detectFileUriExposure();
                    FileOutputStream out = ResultActivity.this.openFileOutput(fileName, Context.MODE_PRIVATE);
                    out.write((csvText.toString().getBytes()));
                    out.close();
                    File fileLocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
                    FileOutputStream fos = new FileOutputStream(fileLocation);
                    fos.write(csvText.toString().getBytes());
                    Uri path = Uri.fromFile(fileLocation);
                    Intent fileIntent = new Intent(Intent.ACTION_SEND);
                    fileIntent.setType("text/csv");
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT, fileName);
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                    FileUpload.run(fileLocation);
                    Log.d("location", "makeCSV: " + fileLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    /** 檔案上傳*/
    public static class FileUpload {
        private static final MediaType MEDIA_TYPE_CSV = MediaType.parse("text/csv");
        private static final OkHttpClient client = new OkHttpClient();

        public static void run(File f) throws Exception {
            String date = new SimpleDateFormat("yyyyMMddHHmmss_888888",
                    Locale.getDefault()).format(System.currentTimeMillis());
            String fileName = "[" + date + "]";
            final File file = f;
            new Thread() {
                @Override
                public void run() {
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("title", "Square Logo")
                            .addFormDataPart("file", fileName + ".csv",
                                    RequestBody.create(MEDIA_TYPE_CSV, file))
                            .build();
                    Request request = new Request.Builder()
//                            .url("http://192.168.2.210:8090")
//                            .url("http://59.127.211.178:8090")
//                            .url("http://192.168.2.94:8090")
                            .url("http://59.126.42.176:8090")
                            .post(requestBody)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()){
                            /** 量測失敗*/
                            throw new IOException("Unexpected code " + response);

                        }
//                        Log.d("returnResult", "run: " + response.body().string());
                        Message msg = mHandler.obtainMessage();
                        Message msg2 = mHandler2.obtainMessage();
                        String res = response.body().string();
                        Boolean success = response.isSuccessful();
                        msg.obj = res;
                        msg2.obj = success;
                        Log.d("resResult", "run: " + res);
                        mHandler.sendMessage(msg);
                        mHandler2.sendMessage(msg2);
//                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /** 接收線程裡的檔案*/
    class MHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            json = msg.obj.toString();
            //tv_result.setText("" + msg.obj.toString());
            catchData();
        }
    }
    class MHandler2 extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            success = msg.obj.toString();
            if (success == "true") {
                progressDialog.dismiss();
                if (instance == null)
                    getSupportFragmentManager().beginTransaction().add(R.id.relativ_result, new ResultFragment(), "result").commit();
                else
                    onBackStackChanged();
            }
            Log.d("kkk",success);
        }
    }

    /** 計算完後得到的資料*/
    private void catchData() {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject(json);
                Log.d("JsonTT", "" + jsonObject.getString("f_name"));

                RMSSD = jsonObject.getDouble("RMSSD");
                Log.d("JsonTT", "" + jsonObject.getDouble("RMSSD"));
                sdNN = jsonObject.getDouble("sdNN");
                Note.sdNN = Math.floor(jsonObject.getDouble("sdNN"));
                Log.d("JsonTT", "" + jsonObject.getDouble("sdNN"));
                LFHF = jsonObject.getDouble("LF/HF");
                Log.d("JsonTT", "" + jsonObject.getDouble("LF/HF"));
                LFn = jsonObject.getDouble("LFn");
                Note.LFn = jsonObject.getDouble("LFn");
                Log.d("JsonTT", "" + jsonObject.getDouble("LFn"));
                HFn = jsonObject.getDouble("HFn");
                Note.HFn = jsonObject.getDouble("HFn");
                Log.d("JsonTT", "" + jsonObject.getDouble("HFn"));
                Heart = jsonObject.getDouble("ecg_hr_mean");
                Log.d("JsonTT", "" + jsonObject.getDouble("ecg_hr_mean"));

                Note.RRi.clear();   //清空Note.RRi
                JSONArray RRArray = jsonObject.getJSONArray("ecg_R_intervals");
                for (int i = 0; i < RRArray.length(); i++) {
                    Double RR = (double) RRArray.get(i);
                    Math.floor(RR); //無條件捨去
                    int IntRR = Integer.valueOf(RR.intValue());     //轉成Int
//                    Log.d("JsonTT", "catchData: "+RR);
                    Log.d("IntRR",""+IntRR);
                    RRi.add(IntRR);
                    Note.RRi.add(IntRR);
                }
                Log.d("RRi",""+RRi);
                Log.v("Note.RRi",""+Note.RRi);

                // 建立SQLiteOpenHelper物件
                sqlDataBaseHelper = new SqlDataBaseHelper(ResultActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
                DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                Cursor U = DB.rawQuery("SELECT * FROM Users WHERE account LIKE '" + Note.account + "'", null);
                U.moveToFirst();
                //更新資料(當下量測)
                DB.execSQL("UPDATE Users SET RMSSD = '"+jsonObject.getDouble("RMSSD")+"'," +
                        "sdNN = '"+jsonObject.getDouble("sdNN")+"'," +
                        "LFHF = '"+jsonObject.getDouble("LF/HF")+"'," +
                        "LFn = '"+jsonObject.getDouble("LFn")+"'," +
                        "HFn = '"+jsonObject.getDouble("HFn")+"'," +
                        "Heart = '"+jsonObject.getDouble("ecg_hr_mean")+"'," +
                        "RRi = '"+RRi+"' WHERE account LIKE '"+Note.account+"'");

                // 建立SQLiteOpenHelper物件
                sqlDataBaseHelper = new SqlDataBaseHelper(ResultActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable2);
                DB2 = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                //取得今天日期、週數
                String dateformat = "yyyy/MM/dd HH:mm:ss"; //日期的格式
                Calendar mCal = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat(dateformat);
                int week = mCal.get(Calendar.WEEK_OF_MONTH);        //取得當下時間在本月第幾周
                String today = df.format(mCal.getTime());
                //更新資料(存歷史紀錄)
                DB2.execSQL( "INSERT INTO Data (account,time,week,state,RMSSD,sdNN,LFHF,LFn,HFn,Heart,RRi) " +
                        "VALUES('"+Note.account+"'," +
                        "'"+today+"'," +
                        "'"+week+"'," +
                        "'"+Note.state+"'," +
                        "'"+jsonObject.getDouble("RMSSD")+"'," +
                        "'"+jsonObject.getDouble("sdNN")+"'," +
                        "'"+jsonObject.getDouble("LF/HF")+"'," +
                        "'"+jsonObject.getDouble("LFn")+"'," +
                        "'"+jsonObject.getDouble("HFn")+"'," +
                        "'"+jsonObject.getDouble("ecg_hr_mean")+"'," +
                        "'"+RRi+"')");
                //清空陣列
                RRi.clear();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();

    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.result_img:{
                    result_drawerlayout.openDrawer(Gravity.RIGHT);

                    txtHead = findViewById(R.id.txtHeader);
                    txtMail = findViewById(R.id.txtHeader2);
                    // 建立SQLiteOpenHelper物件
                    sqlDataBaseHelper = new SqlDataBaseHelper(ResultActivity.this,DataBaseName,null,DataBaseVersion,DataBaseTable);
                    DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                    Cursor D = DB.rawQuery("SELECT * FROM Users WHERE account LIKE '"+ account +"'",null);
                    D.moveToFirst();
                    //側邊欄的個人資訊
                    txtHead.setText(D.getString(0));
                    txtMail.setText(D.getString(3));
                    break;
                }
            }
        }
    };

    NavigationView.OnNavigationItemSelectedListener NavigationLis = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // 點選時收起選單
            result_drawerlayout.closeDrawer(GravityCompat.END);

            int id = item.getItemId();

            // 取得選項id
            if (id == R.id.action_profile) {
                if (activity.equals("profile")){
                    return true;
                }else {
                    startActivity(new Intent(ResultActivity.this, ProfileActivity.class));
                    return true;
                }
            }else if (id == R.id.action_record) {
                if (activity.equals("record")){
                    return true;
                }else {
                    startActivity(new Intent(ResultActivity.this, RecordActivity.class));
                    return true;
                }
            }else if (id == R.id.action_device){
                if (activity.equals("device")){
                    return true;
                }else {
                    startActivity(new Intent(ResultActivity.this,ProfileActivity.class));
                    return true;
                }
            }else if (id == R.id.action_pulse){
                if (activity.equals("pulse")){
                    return true;
                }else {
                    startActivity(new Intent(ResultActivity.this, PulseActivity.class));
                    return true;
                }
            }else {
                if (activity.equals("notify")){
                    return true;
                }else {
                    startActivity(new Intent(ResultActivity.this, NotifyActivity.class));
                    return true;
                }
            }
        }
    };

    private void onBackStackChanged() {
    }

    public boolean onKeyDown(int keycode, KeyEvent event){
        if (keycode == KeyEvent.KEYCODE_BACK){
            if (getSupportFragmentManager().getBackStackEntryCount()>0){
                getSupportFragmentManager().popBackStack();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keycode,event);
    }
}