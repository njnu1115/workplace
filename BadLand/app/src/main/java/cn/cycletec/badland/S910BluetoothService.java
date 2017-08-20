package cn.cycletec.badland;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;

public class S910BluetoothService extends Service {
    public final static String ACTION_GATT_CONNECTED =
            "cn.cycletec.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "cn.cycletec.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "cn.cycletec.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "cn.cycletec.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "cn.cycletec.bluetooth.le.EXTRA_DATA";

    private final static String TAG = S910BluetoothService.class.getSimpleName();
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static int FOREGROUND_ID=1338;
    private int counter = 0;
    private final IBinder mBinder = new LocalBinder();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mDistoGatt;
    private BluetoothDevice mDistoDevice;
    private BluetoothGattService mDistoGattService;
    private List<BluetoothGattService> mDistoGattServices;
    private BluetoothGattCharacteristic mDistoGattChar_DISTANCE = new BluetoothGattCharacteristic(UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_DISTANCE), PROPERTY_READ, PERMISSION_READ);
    private BluetoothGattCharacteristic mDistoGattCharacteristic_Command = new BluetoothGattCharacteristic(UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND), PROPERTY_WRITE_NO_RESPONSE, PERMISSION_WRITE);


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
                        Log.i(TAG, "Gatt service found " + gattService.getUuid().toString());
                        if (gattService.getUuid().toString().equals(DistoGattAttributes.UUID_DISTO_SERVICE)) {
                            mDistoGattService = gattService;
                            Log.i(TAG, "mDistoGattService found " + mDistoGattService.getUuid().toString());
                        }
                        final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                            Log.i(TAG, "BluetoothGattCharacteristicfound " + gattCharacteristic.getUuid().toString());
                            if (gattCharacteristic.getUuid().toString().equals(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND)) {
                                Log.i(TAG, "Found the Command");
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
    private String mBluetoothDeviceAddress;
    private String DistoAddress;
    private String AlreadyConnectedBluetoothDeviceAddress;
    private String mRemoteDistoAddress;
    private int mConnectionState = STATE_DISCONNECTED;
    private NotificationManager mNM;
    private Method mStartForeground;
    private Method mStopForeground;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (device.getName().contains("DISTO")) {
                        Log.i(TAG, "DISTO Scanned, address is " + device.getAddress());
                        device.createBond();
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }
            };

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onCreate(){
        Log.i(TAG, "Service Create");
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
    }

    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        Log.i(TAG, "Started and startid is "+startId);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean DistoInitialize() {
        Log.i(TAG, "Counter is "+counter);
        counter++;

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

//Hardcode the DISTO address here
        mRemoteDistoAddress = DistoGattAttributes.S910Address;

        if (mRemoteDistoAddress != null) {
            mDistoDevice = mBluetoothAdapter.getRemoteDevice(mRemoteDistoAddress);
        } else {
            Log.i(TAG, "DISTO Pair Not Found");
            return false;
        }

        if (mDistoDevice != null) {
            Log.i(TAG, "name of mDistoDevice is "+mDistoDevice.getName());
            mDistoGatt = mDistoDevice.connectGatt(this, false, mGattCallback);
        } else {
            Log.i(TAG, "Disto Device not found.  Unable to connect.");
            return false;
        }

        if (mDistoGatt != null) {
            if (mDistoGatt.discoverServices()) {
                mDistoGattServices = mDistoGatt.getServices();
                Log.i(TAG, "There are " + mDistoGattServices.size() + " services been found");
                if(mDistoGattServices.size() < 1){
//                    return false;
                }
            } else {
                Log.i(TAG, "Discover Services Failed, but I am still going to write and read");
//                mDistoGatt.setCharacteristicNotification(characteristic, enabled);

     //           return false;
            }

            if(mDistoGatt.writeCharacteristic(mDistoGattCharacteristic_Command)){

            }
            else{
                Log.i(TAG, "gatt write failed");
            }
            if(mDistoGatt.readCharacteristic(mDistoGattChar_DISTANCE)){

            }else{
                Log.i(TAG,"GATT read failed");
            }

        } else {
            Log.i(TAG, "Disto Gatt not found. unable to connect.");
            return false;
        }

        return true;
    }

    /*Scan for remote BLE devices, looking for name containing DISTO */
    public boolean ScanForDisto() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            return true;
        }
        return false;
    }

    /*Scan all the paired(not remote) Bluetooth devices, looking for the name started with "DISTO"
    * Will deal with multi "DISTO"s later.
    * */
    public boolean FindPairedDisto() {
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
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
            return true;
        }
    }

    public String getRemoteDistoAddress() {
        return mRemoteDistoAddress;
    }

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mDistoGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mDistoGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mDistoGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mDistoGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mDistoGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mDistoGatt == null) {
            return;
        }
        mDistoGatt.close();
        mDistoGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mDistoGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mDistoGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mDistoGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mDistoGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_DISTANCE));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mDistoGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mDistoGatt == null) return null;

        return mDistoGatt.getServices();
    }

    /* The one-in-all call to perform one measure*/

    public void Trigger() {
        if (DistoInitialize() != true) {
            return;
        }
        if(mDistoGattService == null) {
            Log.i(TAG, "mDistoGattService is NULL");
            return;
        }else {
            readCharacteristic(mDistoGattService.getCharacteristic(UUID.fromString(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_DISTANCE)));
        }
        if (mDistoGattCharacteristic_Command != null) {
            mDistoGattCharacteristic_Command.setValue(DistoGattAttributes.sDistoCommandTable[6].getBytes());
        }

        if (mDistoGatt.writeCharacteristic(mDistoGattCharacteristic_Command)) {
            Log.i(TAG, "Disto characteristic write successfull " + mDistoGattCharacteristic_Command.getValue());
        } else {
            Log.i(TAG, "Disto characteristic write fail");
        }

        if (mDistoGattCharacteristic_Command != null) {
            mDistoGattCharacteristic_Command.setValue(DistoGattAttributes.sDistoCommandTable[5].getBytes());
        }

        if (mDistoGatt.writeCharacteristic(mDistoGattCharacteristic_Command)) {
            Log.i(TAG, "Disto characteristic write successfull " + mDistoGattCharacteristic_Command.getValue());
        } else {
            Log.i(TAG, "Disto characteristic write fail");
        }
    }

    public class LocalBinder extends Binder {
        S910BluetoothService getService() {
            return S910BluetoothService.this;
        }
    }


}
