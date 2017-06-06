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
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_INDICATE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;


public class DistoIntentService extends IntentService {
    public static final String TAG = "DistoIntentService";

    private BluetoothDevice mDistoDevice;
    private BluetoothGatt mDistoGatt;
    private BluetoothGattCharacteristic mDistoGattCharacteristic_DISTANCE;
    private BluetoothGattCharacteristic mDistoGattCharacteristic_Command;
    private BluetoothGattDescriptor mDistoGattDescriptor;
    private BluetoothGattService mDistoGattService;
    private List<BluetoothGattService> mDistoGattServices;
    private String mDistoDeviceAddress;
    private String mRemoteBluetoothDeviceAddress;

    public DistoIntentService() {
        super("DistoIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null){
            return;
        }
        else{
            final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice mRemoteBluetoothDevice : pairedDevices) {
                    if (mRemoteBluetoothDevice.getName().contains("DISTO")) {
                        mDistoDeviceAddress = mRemoteBluetoothDevice.getAddress();
                        Log.i(TAG, "DISTO Pair Found, address is " + mDistoDeviceAddress);
                    }
                }
            }
        }

        if(mDistoDeviceAddress != null) {
            if (mDistoDevice != null && mDistoDevice.getAddress().equals(mDistoDeviceAddress)) {
                Log.i(TAG, "Already connected to " + mDistoDeviceAddress);
            }else {
                mDistoDevice = mBluetoothAdapter.getRemoteDevice(mDistoDeviceAddress);
            }
        } else {
            Log.i(TAG, "DISTO Pair Not Found");
        }

        if (mDistoDevice != null) {
            mDistoGatt = mDistoDevice.connectGatt(this, false, mGattCallback);
        } else {
            Log.i(TAG, "Disto Device not found.  Unable to connect.");
            return ;
        }

        if (mDistoGatt != null) {
            if (mDistoGatt.discoverServices()) {
                mDistoGattServices = mDistoGatt.getServices();
//                Log.i(TAG, "There are " + mDistoGattServices.size() + " services been found");
            } else {
                Log.i(TAG, "Discover Services Failed");
            }
        } else {
            Log.i(TAG, "Disto Gatt not found. unable to connect.");
            return;
        }
    }

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
                        if (gattService.getUuid().toString().equals(DistoGattAttributes.UUID_DISTO_SERVICE)) {
                            mDistoGattService = gattService;
                            Log.i(TAG, "mDistoGattService found " + mDistoGattService.getUuid().toString());
                        }
                        final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                            Log.i(TAG, "BluetoothGattCharacteristicfound " + gattCharacteristic.getUuid().toString());

                            if (gattCharacteristic.getUuid().toString().equals(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_DISTANCE)) {
                                Log.i(TAG, "Found the Distance, Uuid is " +
                                        gattCharacteristic.getUuid().toString()+ " Propertis is "+
                                        gattCharacteristic.getProperties()+" Permission is "+
                                        gattCharacteristic.getPermissions() + " GattService is " +
                                        gattService.getUuid().toString());
                                mDistoGattCharacteristic_DISTANCE = gattCharacteristic;

                                /* Open Notification Locally*/
                                mDistoGatt.setCharacteristicNotification(mDistoGattCharacteristic_DISTANCE, true);

                                /* Open Notification Remotely*/
                                BluetoothGattDescriptor descriptor = mDistoGattCharacteristic_DISTANCE.getDescriptor(UUID.fromString(DistoGattAttributes.UUID_DISTO_DESCRIPTOR));
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                if(mDistoGatt.writeDescriptor(descriptor)){
                                    Log.i(TAG, "Descriptor update successful");
                                }else{
                                    Log.i(TAG, "Descriptor update Failed");
                                }

                                /* read distance */
                                if(mDistoGatt.readCharacteristic(mDistoGattCharacteristic_DISTANCE)){
                                    Log.i(TAG, "mDistoGattCharacteristic_DISTANCE read successfully");
                                }else{
                                    Log.i(TAG, "mDistoGattCharacteristic_DISTANCE read failed");
                                }
                            }

                            if (gattCharacteristic.getUuid().toString().equals(DistoGattAttributes.UUID_DISTO_CHARACTERISTIC_COMMAND)) {
                                Log.i(TAG, "Found the Command, Uuid is " +
                                        gattCharacteristic.getUuid().toString()+" Propertis is "+
                                        gattCharacteristic.getProperties()+" Permission is "+
                                        gattCharacteristic.getPermissions() + " GattService is " +
                                        gattService.getUuid().toString());
                                mDistoGattCharacteristic_Command = gattCharacteristic;
                                mDistoGattCharacteristic_Command.setValue(DistoGattAttributes.sDistoCommandTable[5].getBytes());
                                mDistoGattCharacteristic_Command.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                                /* write command and trigger a measure */
                                if (mDistoGatt.writeCharacteristic(mDistoGattCharacteristic_Command)) {
                                    Log.i(TAG, "Disto characteristic write successfull ");
                                }else{
                                    Log.i(TAG, "Disto characteristic write failed ");
                                }
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
