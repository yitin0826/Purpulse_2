package com.example.purpulse;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class OutPutCSV extends AppCompatActivity {
    @SuppressLint("HandlerLeak")
    private double[] butterFilter;
    private TextView tv_result;
    public static Handler mHandler;
    private String json;
    private Double RMSSD, sdNN, LFHF, LFn, HFn, Heart;
    private static final String DataBaseName = "db";
    private static final int DataBaseVersion = 6;
    private static String DataBaseTable = "Users";
    private static String DataBaseTable2 = "Data";
    private static SQLiteDatabase DB;
    private static SQLiteDatabase DB2;
    private SqlDataBaseHelper sqlDataBaseHelper;
    private String account = Note.account;
    private ArrayList<Float> RRi = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.customer_result);
        //tv_result = findViewById(R.id.tv_result);
        mHandler = new MHandler();
        makeCSV();
    }

    /**
     * 巴特沃斯濾波(只濾一次)
     */
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
            runOnUiThread(() -> {
                try {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    builder.detectFileUriExposure();
                    FileOutputStream out = openFileOutput(fileName, Context.MODE_PRIVATE);
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
    }//makeCSV

    /**
     * 檔案上傳
     */
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
                        if (!response.isSuccessful())
                        /** 量測失敗*/
                            throw new IOException("Unexpected code " + response);
//                        Log.d("returnResult", "run: " + response.body().string());
                        Message msg = mHandler.obtainMessage();
                        String res = response.body().string();
                        msg.obj = res;
                        Log.d("resResult", "run: " + res);
                        mHandler.sendMessage(msg);
//                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }

    /**
     * 接收線程裡的檔案
     */
    class MHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            json = msg.obj.toString();
            //tv_result.setText("" + msg.obj.toString());
            catchData();
        }
    }

    /**
     * 計算完後得到的資料
     */
    private void catchData() {
        new Thread(() -> {
                            //取值
            try {
                JSONObject jsonObject = new JSONObject(json);
                Log.d("JsonTT", "" + jsonObject.getString("f_name"));

                RMSSD = jsonObject.getDouble("RMSSD");
                Log.d("JsonTT", "" + jsonObject.getDouble("RMSSD"));
                sdNN = jsonObject.getDouble("sdNN");
                Note.sdNN = jsonObject.getDouble("sdNN");
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

                JSONArray RRArray = jsonObject.getJSONArray("ecg_R_intervals");
                for (int i = 0; i < RRArray.length(); i++) {
                    float RR = (float) RRArray.get(i);
                    Log.d("JsonTT", "catchData: " + RR);
                    RRi.add(RR);
//                    Note.RRi.add(RR);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("value", "No Data");
            }

            try {           //寫到資料庫
                // 建立SQLiteOpenHelper物件
                sqlDataBaseHelper = new SqlDataBaseHelper(OutPutCSV.this, DataBaseName, null, DataBaseVersion, DataBaseTable);
                DB = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                Cursor U = DB.rawQuery("SELECT * FROM Users WHERE account LIKE '" + Note.account + "'", null);
                U.moveToFirst();
                //更新資料(當下量測)
                DB.execSQL("UPDATE Users SET RMSSD = '" + RMSSD + "'," +
                        "sdNN = '" + sdNN + "'," +
                        "LFHF = '" + LFHF + "'," +
                        "LFn = '" + LFn + "'," +
                        "HFn = '" + HFn + "'," +
                        "Heart = '" + Heart + "'," +
                        "RRi = '" + RRi + "' WHERE account LIKE '" + Note.account + "'");

                // 建立SQLiteOpenHelper物件
                sqlDataBaseHelper = new SqlDataBaseHelper(OutPutCSV.this, DataBaseName, null, DataBaseVersion, DataBaseTable2);
                DB2 = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                //取得今天日期
                String dateformat = "yyyy/MM/dd"; //日期的格式
                Calendar mCal = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat(dateformat);
                String today = df.format(mCal.getTime());
                //更新資料(存歷史紀錄)
                DB2.execSQL("INSERT INTO Data (account,time,RMSSD,sdNN,LFHF,LFn,HFn,Heart,RRi) " +
                        "VALUES('" + Note.account + "'," +
                        "'" + today + "'," +
                        "'" + RMSSD + "'," +
                        "'" + sdNN + "'," +
                        "'" + LFHF + "'," +
                        "'" + LFn + "'," +
                        "'" + HFn + "'," +
                        "'" + Heart + "'," +
                        "'" + RRi + "')");

                //清空陣列
                RRi.clear();

            } catch (Exception e) {
                Log.e("SQL", "SQL no use");
                e.toString();
            }

        }).start();

    }
}
