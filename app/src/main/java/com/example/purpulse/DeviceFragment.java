package com.example.purpulse;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.example.purpulse.connection.ConnectionActivity;

import java.util.ArrayList;

public class DeviceFragment extends ListFragment{

    private enum ScanState {NONE, LE_SCAN, DISCOVERY, DISCOVERY_FINISHED}
    private ScanState scanState = ScanState.NONE;
    private static final long LE_SCAN_PERIOD = 10000;
    private static final int REQUEST_ENABLE_BT = 2;
    private final Handler leScanStopHandler = new Handler();
    private final BluetoothAdapter.LeScanCallback leScanCallback;
    private final Runnable leScanStopCallBack;
    private final BroadcastReceiver discoveryBroadcastReceiver;
    private final IntentFilter discoveryIntentFilter;

    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<BluetoothDevice> listItems = new ArrayList<>();
    private ArrayAdapter<BluetoothDevice> listAdapter;

    private TextView txt_btstatus,text3;
    private Button btn_btsearch, btn_btstop;

    private enum Connected {False,Pending,True}
    private Connected connected = Connected.False;
    private SerialService service;
    private boolean initialStart = true;
    private BluetoothDevice device;
    private String deviceAddress;

    public DeviceFragment() {
        leScanCallback = (device, rssi, scanRecord) -> {
            if (device != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateScan(device);
                });
            }
        };
        discoveryBroadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_CLASSIC && getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateScan(device));
                    }
                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                    scanState = ScanState.DISCOVERY_FINISHED;
                    stopScan();
                }
            }
        };
        discoveryIntentFilter = new IntentFilter();
        discoveryIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        leScanStopCallBack = this::stopScan;
    }

    /** LifeCycle **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        /**確認手機是否支援藍牙*/
        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        txt_btstatus = getActivity().findViewById(R.id.txt_btstatus);
        btn_btsearch = getActivity().findViewById(R.id.btn_btsearch);
        btn_btstop = getActivity().findViewById(R.id.btn_btstop);
        btn_btsearch.setEnabled(false);
        btn_btstop.setEnabled(false);
        btn_btsearch.setOnClickListener(lis);
        btn_btstop.setOnClickListener(lis);
        listAdapter = new ArrayAdapter<BluetoothDevice>(getActivity(), 0, listItems) {
            @SuppressLint("MissingPermission")
            @NonNull
            @Override
            public View getView(int position, View view, @NonNull ViewGroup parent) {
                BluetoothDevice device = listItems.get(position);
                if (view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.item_devicelist, parent, false);
                TextView text1 = view.findViewById(R.id.text1);
                TextView text2 = view.findViewById(R.id.text2);
                text3 = view.findViewById(R.id.text3);
                if (device.getName() == null || device.getName().isEmpty())
                    text1.setText("");
                else
                    text1.setText(device.getName());
                text2.setText(device.getAddress());
                text3.setText("未連接");
                return view;
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(discoveryBroadcastReceiver, discoveryIntentFilter);
        if (bluetoothAdapter == null) {
            txt_btstatus.setText("設備不支持藍芽");
        } else if (!bluetoothAdapter.isEnabled()) {
            txt_btstatus.setText("尚未開啟藍芽");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            btn_btsearch.setEnabled(false);
        } else {
            txt_btstatus.setText("已開啟藍芽，點擊搜尋裝置來重新整理裝置列表");
            btn_btsearch.setEnabled(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(null);
        setListAdapter(listAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScan();
        getActivity().unregisterReceiver(discoveryBroadcastReceiver);
    }


    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_btsearch:
                    startScan();
                    break;
                case R.id.btn_btstop:
                    stopScan();
                    break;
            }
        }
    };

    @SuppressLint({"StaticFieldLeak"})
// AsyncTask needs reference to this fragment
    private void startScan() {
        if (scanState != ScanState.NONE)
            return;
        scanState = ScanState.LE_SCAN;
        /**確認是否已開啟取得手機位置功能以及權限*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            }
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                scanState = DeviceFragment.ScanState.NONE;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.location_permission_title);
                builder.setMessage(R.string.location_permission_message);
                builder.setPositiveButton(android.R.string.ok,
                        (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0));
                builder.show();
                return;
            }
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = false;
            try {
                locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ignored) {
            }
            try {
                locationEnabled |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ignored) {
            }
            if (!locationEnabled) {
                scanState = ScanState.DISCOVERY;
            }
        }
        listItems.clear();
        listAdapter.notifyDataSetChanged();
        setEmptyText("正在掃描中");
        btn_btsearch.setVisibility(View.INVISIBLE);
        btn_btsearch.setEnabled(false);
        btn_btstop.setVisibility(View.VISIBLE);
        btn_btstop.setEnabled(true);
        if (scanState == ScanState.LE_SCAN) {
            leScanStopHandler.postDelayed(leScanStopCallBack, LE_SCAN_PERIOD);
            new AsyncTask<Void, Void, Void>() {
                @SuppressLint("MissingPermission")
                @Override
                protected Void doInBackground(Void[] params) {
                    bluetoothAdapter.startLeScan(null, leScanCallback);
                    return null;
                }
            }.execute();
        } else {
            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ignore requestCode as there is only one in this fragment
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new Handler(Looper.getMainLooper()).postDelayed(this::startScan,1); // run after onResume to avoid wrong empty-text
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getText(R.string.location_denied_title));
            builder.setMessage(getText(R.string.location_denied_message));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
    }

    private void updateScan(BluetoothDevice device) {
        if (scanState == ScanState.NONE)
            return;
        if (!listItems.contains(device)) {
            listItems.add(device);
            listAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("MissingPermission")
    private void stopScan() {
        if (scanState == ScanState.NONE)
            return;
        setEmptyText("未尋找到藍芽裝置");
        btn_btsearch.setVisibility(View.VISIBLE);
        btn_btsearch.setEnabled(true);
        btn_btstop.setVisibility(View.INVISIBLE);
        btn_btstop.setEnabled(false);
        switch (scanState) {
            case LE_SCAN:
                leScanStopHandler.removeCallbacks(leScanStopCallBack);
                bluetoothAdapter.stopLeScan(leScanCallback);
                break;
            case DISCOVERY:
                bluetoothAdapter.cancelDiscovery();
                break;
            default:
        }
        scanState = ScanState.NONE;
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id){
        stopScan();
        device = listItems.get(position);
        Intent intent = new Intent();
        intent.setClass(getActivity(), ConnectionActivity.class);
        Note.adress = device.getAddress();
        startActivity(intent);
    }
}
