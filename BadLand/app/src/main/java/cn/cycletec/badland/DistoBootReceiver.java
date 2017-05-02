package cn.cycletec.badland;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.support.v4.content.WakefulBroadcastReceiver;

public class DistoBootReceiver extends BroadcastReceiver {
    DistoAlarmReceiver alarm = new DistoAlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            alarm.setAlarm(context);
        }
    }
}
