package cn.cycletec.badland;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import static android.content.ContentValues.TAG;


import static android.content.ContentValues.TAG;

public class DistoIntentService extends IntentService {
    public static final String LOG_TAG = "DistoIntentService";
    private S910BluetoothService mBluetoothLeService;
    private String mDeviceAddress;

    public DistoIntentService() {
        super("DistoIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent gattServiceIntent = new Intent(this, S910BluetoothService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.i(LOG_TAG, "Service running");
        unbindService(mServiceConnection);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((S910BluetoothService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
}
