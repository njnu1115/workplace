package cn.cycletec.badland;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DistoAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "I am running again",
                Toast.LENGTH_LONG).show();
        Intent i = new Intent(context, DistoIntentService.class);
        final String mDistoAddress = intent.getStringExtra("DistoAddress");
        i.putExtra("DistoAddress", mDistoAddress);
        context.startService(i);
    }
}
