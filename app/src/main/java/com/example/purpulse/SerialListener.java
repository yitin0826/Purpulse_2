package com.example.purpulse;

import android.content.ComponentName;
import android.os.IBinder;

public interface SerialListener {

    void onSerialConnect();

    void onSerialConnectError(Exception e);

    void onSerialRead(byte[] data);

    void onSerialIoError(Exception e);
}
