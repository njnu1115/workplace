package cn.cycletec.badland;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DistoAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    private final static String TAG = DistoAlarmReceiver.class.getSimpleName();


    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
//        final String mDistoAddress = intent.getStringExtra("DistoAddress");
        Toast.makeText(context, "I am running again",
                Toast.LENGTH_LONG).show();
        Log.i("DistoAlarmReceiver", "Alarm Received");

        Intent i = new Intent(context, DistoIntentService.class);
        context.startService(i);
    }
}
