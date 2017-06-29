package cn.cycletec.badland;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.UUID;


public class DistoIntentService extends IntentService {
    public static final String TAG = "DistoIntentService";
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                float f = ByteBuffer.wrap(characteristic.getValue()).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                Log.i(TAG, "Disto characteristic Read successful:" + f);
//                Log.i(TAG, "Disto characteristic Read UUID is " + characteristic.getUuid().toString());
            } else {
                Log.i(TAG, "Disto characteristic Read but fail " + characteristic.getValue().toString());
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "Disto characteristic changed:" + characteristic.toString());
//            onCharacteristicRead(gatt, characteristic, BluetoothGatt.GATT_SUCCESS);
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "Disto characteristic Write status is " + status + " " + characteristic.getValue());
        }

        public void onConnectionStateChange(BluetoothGatt mBluetoothGatt, int status, int newState) {
            Log.i(TAG, "Disto newState Read:" + newState);
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i(TAG, "Disto descriptor Read:" + descriptor.toString());
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.i(TAG, "Disto rssi Read:" + rssi);
        }

        public void onServicesDiscovered(BluetoothGatt mDistoGatt, int status) {
            Log.i(TAG, "Disto ServicesDiscovered in Callback");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mDistoGattService = mDistoGatt.getService(UUID.fromString(DistoGattAttributes.UUID_DISTO_SERVICE));
                if (mDistoGattService == null) {
                    Log.i(TAG, "mDistoGattService == null while discover");
                }
            }
        }
    };
    private String mBluetoothDeviceAddress;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDistoDevice;
    private BluetoothGatt mDistoGatt;
    private BluetoothGattDescriptor mDistoGattDescriptor;
    private BluetoothGattService mDistoGattService;
    private List<BluetoothGattService> mDistoGattServices;
    private BluetoothGattCharacteristic mDistoGattCharacteristic_DISTANCE = new BluetoothGattCharacteristic(
            UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND), 4, 0);
    private BluetoothGattCharacteristic mDistoGattCharacteristic_Command = new BluetoothGattCharacteristic(
            UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_DISTANCE), 34, 0);
    private String mDistoDeviceAddress;
    private String mRemoteBluetoothDeviceAddress;
    private int mConnectionState = STATE_DISCONNECTED;

    public DistoIntentService() {
        super("DistoIntentService");
    }

    public BluetoothGattCharacteristic getPreparedCommand() {
        mDistoGattCharacteristic_Command.setValue("g");
        mDistoGattCharacteristic_Command.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        return mDistoGattCharacteristic_Command;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String mDistoAddress = intent.getStringExtra("DistoAddress");
        Log.i(TAG, "the address get from intent is " + mDistoAddress);
        if (!initialize()) {
            Log.i(TAG, "initialize() failed");
            return;
        }
        if (!connect(mDistoAddress)) {
            Log.i(TAG, "connect() failed");
            return;
        }
        if (!setNotification()) {
            Log.i(TAG, "setNotification failed");
        }
        if (!sendCommand()) {
            Log.i(TAG, "senCommand failed");
            return;
        }
    }

    private boolean sendCommand() {
        if (mDistoGatt == null) {
            Log.i(TAG, "mDistoGatt is null while sendCommand");
            return false;
        }
        mDistoGattService = mDistoGatt.getService(UUID.fromString(DistoGattAttributes.UUID_DISTO_SERVICE));
        if (mDistoGattService == null) {
            Log.i(TAG, "mDistoGassService is null while sendCommand");
            return false;
        }
        mDistoGattCharacteristic_Command = mDistoGattService.getCharacteristic(UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND));
        if (mDistoGattCharacteristic_Command == null) {
            Log.i(TAG, "mDistoGassCharacteristic is null while sendCommand");
            return false;
        }
        mDistoGattCharacteristic_Command.setValue("g");
        if (mDistoGatt.writeCharacteristic(mDistoGattCharacteristic_Command)) {
            return true;
        } else {
            Log.i(TAG, "write Characteristic failed while sendCommand");
            return false;
        }
    }

    private boolean setNotification() {
        if (mDistoGattService == null || mDistoGatt == null) {
            Log.i(TAG, "mDistoService ==null || mDistoGatt == null");
            return false;
        }
        if(mDistoGattCharacteristic_Command == null){
            mDistoGattCharacteristic_Command = mDistoGattService.getCharacteristic(UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND));
        }
        if(mDistoGattCharacteristic_DISTANCE == null) {
            mDistoGattCharacteristic_DISTANCE = mDistoGattService.getCharacteristic(UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_DISTANCE));
        }
            //Set local notification
        if(mDistoGattCharacteristic_DISTANCE == null){return false;}
        mDistoGatt.setCharacteristicNotification(mDistoGattCharacteristic_DISTANCE, true);
        //Set remote notification
        final List<BluetoothGattDescriptor> descs = mDistoGattCharacteristic_DISTANCE.getDescriptors();
        if(null == descs){
            Log.i(TAG, "failed to get descriptorS while set notification");
        }else{
            Log.i(TAG, descs.size()+" descs found");
            if(descs.size() == 0){
                return false;
            }
            for(BluetoothGattDescriptor tempDesc : descs){
                Log.i(TAG, tempDesc.getUuid().toString()+" found as desc");
            }
        }

        final BluetoothGattDescriptor desc = mDistoGattCharacteristic_DISTANCE.getDescriptor(UUID.fromString(DistoGattAttributes.UUID_DISTO_DESCRIPTOR));
        if(null == desc){
            Log.i(TAG,"failed to get descriptor while set notification");
            return false;
        }
        desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mDistoGatt.writeDescriptor(desc);
        return true;
    }

    private boolean initialize() {
        // make sure the bluetooth works fine
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }


    public boolean connect(final String address) {
        //make sure Disto is ready
        if (mBluetoothAdapter == null || address == null) {
            Log.i(TAG, "Adapter is " + mBluetoothAdapter + " address is " + address);
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.i(TAG, "failed to get device while connect");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mDistoGatt = device.connectGatt(this, true, mGattCallback);
        if (mDistoGatt == null) {
            Log.i(TAG, "mDistoGatt is null while connectGatt");
        }
        if (!mDistoGatt.discoverServices()) {
            Log.i(TAG, "Discover Services Failed");
            return false;
        }
        mDistoGattService = mDistoGatt.getService(UUID.fromString(DistoGattAttributes.UUID_DISTO_SERVICE));
        if (mDistoGattService == null) {
            Log.i(TAG, "mDistoGattService == null while connect");
        }
        return true;
    }
}
