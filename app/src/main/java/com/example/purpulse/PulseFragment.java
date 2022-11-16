package com.example.purpulse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpulse.result.ResultActivity;

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
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.me.berndporr.iirj.Butterworth;

public class PulseFragment extends Fragment implements ServiceConnection, SerialListener{

    private enum Connected {False, Pending, True}

    private String deviceAddress;
    private SerialService service;

    private TextView receive_text,progress_text,txt_end;
    private Button btn_start,btn_resultconfirm,btn_yes,btn_no;
    private ProgressBar progressbar;
    int i = 0;

    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean receiveData = true;
    private String newline = TextUtil.newline_crlf;
    private List<String> saveList = new ArrayList<>();
    private List<Integer> integerList = new ArrayList<>();
    private String msg;
    private int keepDraw;

    private WaveView wave_view;
    private WaveUtil waveUtil;

    private Spinner spinner;
    Dialog dialog;
    View dialogView;

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
    private ArrayList<Float> RRi = new ArrayList<>();
    private String success;

    /** Lifecycle **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        deviceAddress = Note.adress;
    }

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if (service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
        Toast.makeText(getActivity(),"1",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        try {
            getActivity().unbindService(this);
        } catch (Exception ignored) {
        }
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }


    /** 服務連接 **/
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if (initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    /** UI **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pulse, container, false);
        receive_text = view.findViewById(R.id.receive_text);
        btn_start = view.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(lis);
        progressbar = view.findViewById(R.id.progressbar);
        progress_text = view.findViewById(R.id.progress_text);
        txt_end = view.findViewById(R.id.txt_end);
        btn_resultconfirm = view.findViewById(R.id.btn_resultconfirm);
        btn_resultconfirm.setPaintFlags(btn_resultconfirm.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return view;
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_start:{
                    getDialog();
                    break;
                }
                case R.id.btn_resultconfirm:{
                    Intent intent = new Intent(getActivity(), ResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("saveList", (ArrayList<String>) saveList);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            }
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wave_view = getView().findViewById(R.id.wave_view);
        waveUtil = new WaveUtil();
    }

    public class WaveUtil {
        Handler mHandler;
        Runnable mRunnable;

        public void showWaveData(int val) {
            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    wave_view.showLine(val);
                }
            };
            mHandler.postDelayed(mRunnable, 3000);
        }
    }

    /** 連接裝置 **/
    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status("連接中...");
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
    }

    /** 接收資料 **/
    private void receive(byte[] data) {
        new Thread(()->{
        if (hexEnabled) {
            receive_text.append(TextUtil.toHexString(data) + '\n');
        } else {
            msg = new String(data);
            for (int j = 0; j < msg.length(); j++) {
                msg = msg.trim();
                msg = msg.replace("\r", "");
            }
            /** 資料處理分割，並加入LIST*/
            if (receiveData == true) {
                String[] buffer = msg.split("\n");
                for (int i = 0; i < buffer.length; i++) {
                    try {
                        saveList.add(buffer[i]);
                        integerList.add(Integer.valueOf(buffer[i]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("joinData", "joinData: " + buffer[i]);
                }
                for (int i = 0; i < integerList.size(); i++) {
                    try {
                        keepDraw = integerList.get(i);
//                        Log.d("keepdraw", "run: "+keepDraw);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            /** 重要 **/
            (getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    waveUtil.showWaveData(keepDraw);
                }
            });
        }
        }).start();
    }

    private void status(String str) {
         SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
         spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
         receive_text.append(spn);
    }

    /** SerialListener **/
    @Override
    public void onSerialConnect() {
        status("連接成功，點擊開始測量");
        connected = Connected.True;
        btn_start.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }


    public void initProgressBar(){
        progressbar.setVisibility(View.VISIBLE);
        progress_text.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i<=100){
                    progress_text.setText(""+i+"%");
                    progressbar.setProgress(i);
                    i+=1;
                    handler.postDelayed(this,900);
                }
                else {
                    handler.removeCallbacks(this);
                }

                if (i>100){
                    /** 停止接收數據 **/
                    receiveData = false;
                    txt_end.setVisibility(View.VISIBLE);
                    btn_resultconfirm.setVisibility(View.VISIBLE);
                    btn_resultconfirm.setOnClickListener(lis);
                }
            }
        },200);
    }

    /** 確認用戶測量狀態 **/
    public void getDialog(){
        dialog = new Dialog(getActivity(),R.style.custom_dialog);
        dialogView = getLayoutInflater().inflate(R.layout.dialog_view,null);
        dialog.setContentView(dialogView);
        btn_yes = dialogView.findViewById(R.id.btn_yes);
        btn_no = dialogView.findViewById(R.id.btn_no);
        spinner = dialogView.findViewById(R.id.spinner);
        dialog.show();
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),R.array.status_array,R.layout.spinnertext);
        adapter.setDropDownViewResource(R.layout.spinnertext);
        spinner.setAdapter(adapter);
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note.state = spinner.getSelectedItem().toString();
                dialog.dismiss();
                receive_text.setVisibility(View.INVISIBLE);
                btn_start.setVisibility(View.INVISIBLE);
                wave_view.setVisibility(View.VISIBLE);
                initProgressBar();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}