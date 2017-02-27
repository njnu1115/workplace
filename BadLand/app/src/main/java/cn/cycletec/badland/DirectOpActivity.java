package cn.cycletec.badland;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.R.attr.value;
import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED;


public class DirectOpActivity extends AppCompatActivity {

    private final static String TAG = "DirectOpActivity";

    private BluetoothDevice mRemoteBluetoothDevice;
    private String mRemoteDistoAddress;
    private BluetoothDevice mDistoDevice;
    private BluetoothGatt mDistoGatt;
    private List<BluetoothGattService> mDistoGattServices;
    private BluetoothGattService mDistoGattService;
    private BluetoothGattCharacteristic mDistoGattChar_DISTANCE;
    private BluetoothGattCharacteristic mDistoGattCharacteristic_Command;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private int mConnectionState = STATE_DISCONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_op);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Unable to find a BluetoothAdapter.");
            finish();
        } else if (!mBluetoothAdapter.isEnabled()) {
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

        if (mBluetoothAdapter == null) {
            Log.i(TAG, "Still Unable to find a BluetoothAdapter.");
        } else {
            final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice mRemoteBluetoothDevice : pairedDevices) {
                    if (mRemoteBluetoothDevice.getName().contains("DISTO")) {
                        mRemoteDistoAddress = mRemoteBluetoothDevice.getAddress();
                        Log.i(TAG, "DISTO Pair Found, address is " + mRemoteDistoAddress);
                    } else {
                        Log.i(TAG, mRemoteBluetoothDevice.getAddress());
                    }
                }
            }
        }

        if (mRemoteDistoAddress != null) {
            mDistoDevice = mBluetoothAdapter.getRemoteDevice(mRemoteDistoAddress);
        } else {
            Log.i(TAG, "DISTO Pair Not Found");
        }

        if (mDistoDevice != null) {
            mDistoGatt = mDistoDevice.connectGatt(this, false, mGattCallback);
        } else {
            Log.i(TAG, "Disto Device not found.  Unable to connect.");
            finish();
        }

        if (mDistoGatt != null) {
            if (mDistoGatt.discoverServices()) {
                mDistoGattServices = mDistoGatt.getServices();
                Log.i(TAG, "There are " + mDistoGattServices.size() + " services been found");
            } else {
                Log.i(TAG, "Discover Services Failed");
            }
        } else {
            Log.i(TAG, "Disto Gatt not found. unable to connect.");
            finish();
        }


//        BluetoothGattService mDistoService = mDistoGatt.getService(UUID.fromString(DistoGattAttributes.UUID_DISTO_SERVICE));
//        if (mDistoService == null) {
//            Log.i(TAG, "Disto GattService not found. ");
//            finish();
//        } else {
//            Log.i(TAG, "Disto GattService found. ");
//        }
//        BluetoothGattCharacteristic S910TriggerCommand = mDistoGattService.getCharacteristic(UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND));
//        S910TriggerCommand.setValue("g");
//        mDistoGatt.writeCharacteristic(S910TriggerCommand);
    }

    public void trigger_command(View view){
        Log.i(TAG, "i clicked the button");
        mDistoGattCharacteristic_Command.setValue(value[0], BluetoothGattCharacteristic.FORMAT_SINT8, "g");
        mDistoGatt.writeCharacteristic(mDistoGattCharacteristic_Command);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Disto characteristic Read:" + characteristic.toString());
            }
            Log.i(TAG, "Disto characteristic Read!!:" + characteristic.toString());
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "Disto characteristic Read:" + characteristic.toString());
        }

        public void onConnectionStateChange(BluetoothGatt mBluetoothGatt, int status, int newState) {
            Log.i(TAG, "Disto newState Read:" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            }
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.w(TAG, "Disto descriptor Read:" + descriptor.toString());
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.i(TAG, "Disto rssi Read:" + rssi);
        }

        public void onServicesDiscovered(BluetoothGatt mDistoGatt, int status) {
            Log.i(TAG, "Disto ServicesDiscovered:" + mDistoGatt.toString());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mDistoGattServices = mDistoGatt.getServices();
                if (mDistoGattServices == null) {
                    Log.i(TAG, "Disto GattServices not found. ");
                    return;
                } else if (mDistoGattServices.size() <= 0) {
                    Log.i(TAG, "Disto GattServices == 0");
                    return;
                } else {
                    Log.i(TAG, "mDistoGattServices found");
                    for (BluetoothGattService gattService : mDistoGattServices) {
//                        Log.i(TAG, "Gatt service found " + gattService.getUuid().toString());
                        final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics){
//                            Log.i(TAG, "BluetoothGattCharacteristicfound " + gattCharacteristic.getUuid().toString());
                            if (gattCharacteristic.getUuid().toString().equals(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND)){
//                                Log.i(TAG, "Found the Command");
                                mDistoGattCharacteristic_Command = gattCharacteristic;
                            }
                        }
                    }
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }


    };


}
