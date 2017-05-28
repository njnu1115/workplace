package cn.cycletec.badland;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.util.List;
import java.util.concurrent.RunnableFuture;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final static String TAG = MainActivity.class.getSimpleName();
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private S910BluetoothService mS910Service;
    private String mDeviceAddress;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        Intent gattServiceIntent = new Intent(this, S910BluetoothService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //DistoLoop.run();
        mHandler.post(new Runnable() {
            @Override
            public void run () {
                Log.d("Handlers", "Called on main thread");
            }
        });
    }

    private Runnable DistoLoop = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d("Handlers", "Called on main thread");
//            mS910Service.triggerCommand(DistoGattAttributes.sDistoCommandTable[6].getBytes());
            mHandler.postDelayed(DistoLoop, 2000);
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mS910Service = ((S910BluetoothService.LocalBinder) service).getService();
            if (!mS910Service.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                /* Maybe I can try to re-connect again and again */
 //               finish();
            }
            Log.e(TAG, "Service Init Finished");

            if(!mS910Service.ScanForDisto()){
                Log.e(TAG, "Unable to Scan for Disto");
 //               finish();
            }
            Log.e(TAG, "Scan for Disto Done");
            DistoLoop.run();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mS910Service = null;
        }
    };
}


