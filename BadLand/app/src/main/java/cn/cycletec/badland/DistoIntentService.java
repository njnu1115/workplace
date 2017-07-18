package cn.cycletec.badland;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;


public class DistoIntentService extends IntentService {
    public static final String TAG = "DistoIntentService";
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private int DistoCounter = 0;

    private String mBluetoothDeviceAddress;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDistoDevice;
    private BluetoothGatt mDistoGatt;
    private BluetoothGattDescriptor mDistoGattDescriptor;
    private BluetoothGattService mDistoGattService;
    private S910BluetoothService mBluetoothLeService;
    private List<BluetoothGattService> mDistoGattServices;
    private BluetoothGattCharacteristic mDistoGattCharacteristic_DISTANCE = new BluetoothGattCharacteristic(
            UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND), 4, 0);
    private BluetoothGattCharacteristic mDistoGattCharacteristic_Command = new BluetoothGattCharacteristic(
            UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_DISTANCE), 34, 0);
    private String mDistoDeviceAddress;
    private String mRemoteBluetoothDeviceAddress;
    private int mConnectionState = STATE_DISCONNECTED;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((S910BluetoothService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public DistoIntentService() {
        super("DistoIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String mDistoAddress = intent.getStringExtra("DistoAddress");
        Log.i(TAG, "the address get from intent is " + mDistoAddress + "and the count is" + DistoCounter);
        //add the command here
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Intent S910ServiceIntent = new Intent(this, S910BluetoothService.class);
        bindService(S910ServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(mServiceConnection);
    }


}
