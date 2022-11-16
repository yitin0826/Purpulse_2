package com.example.purpulse;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    public String switchCheck = "";
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    Calendar cal = Calendar.getInstance();
    Context context;
    ArrayList<String> alarm = new ArrayList();

    public AlarmAdapter(Context c, ArrayList list){
        context = c;
        inflater = LayoutInflater.from(c);
        alarm = list;
    }

    @Override
    public int getCount() {
        return alarm.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View v = inflater.inflate(R.layout.item_alarmlist, parent,false);
        TextView text1 = v.findViewById(R.id.text1);
        Switch switchbtn = v.findViewById(R.id.switchbtn);
        String time = alarm.get(position);
        text1.setText(time);
        String hr = time.substring(0,2);
        String min = time.substring(3);
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hr));
        cal.set(Calendar.MINUTE, Integer.parseInt(min));
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        switchbtn.setChecked(true);
        switchCheck = "開啟";
        createNotificationChannel();
        setAlarm(cal);
        switchbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchCheck.equals("開啟")){
                    switchCheck = "關閉";
                    cancelAlarm();
                }else {
                    switchCheck = "開啟";
                    setAlarm(cal);
                }
            }
        });
        return v;
    }

    private void setAlarm(Calendar cal) {
        Intent intent = new Intent(context,AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void cancelAlarm() {
        Intent intent = new Intent(context,AlertReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context,0,intent,pendingIntent.FLAG_MUTABLE);
        if (alarmManager == null){
            alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("AlertTest",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
