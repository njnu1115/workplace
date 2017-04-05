package leica.disto.transferBLE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Stack;
import java.util.UUID;

public class DistoBluetoothService
{
  public static final int BT_ERROR_CANT_CLOSE = 2;
  public static final int BT_ERROR_CREATE_SOCKET = 1;
  public static final int BT_ERROR_CREATE_STREAMS = 3;
  public static final int BT_ERROR_WRITE = 4;
  public static final int BT_MODE_BLE = 1;
  public static final int BT_MODE_SPP = 0;
  public static final int BT_MODE_UNKNOWN = -1;
  private static final boolean D = false;
  public static final UUID DISTO_CHARACTERISTIC_COMMAND = UUID.fromString("3ab10109-f831-4395-b29d-570977d5bf94");
  public static final UUID DISTO_CHARACTERISTIC_DISTANCE;
  public static final UUID DISTO_CHARACTERISTIC_DISTANCE_DISPLAY_UNIT;
  public static final UUID DISTO_CHARACTERISTIC_GEOGRAPHIC_DIRECTION;
  public static final UUID DISTO_CHARACTERISTIC_GEOGRAPHIC_DIRECTION_DISTPLAY_UNIT;
  public static final UUID DISTO_CHARACTERISTIC_HORIZONTAL_INCLINE;
  public static final UUID DISTO_CHARACTERISTIC_INCLINATION;
  public static final UUID DISTO_CHARACTERISTIC_INCLINATION_DISPLAY_UNIT;
  public static final UUID DISTO_CHARACTERISTIC_STATE_RESPONSE = UUID.fromString("3ab1010A-f831-4395-b29d-570977d5bf94");
  public static final UUID DISTO_CHARACTERISTIC_VERTICAL_INCLINE;
  public static final UUID DISTO_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
  public static final UUID DISTO_SERVICE;
  private static final UUID DISTO_SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  public static final int STATE_CONNECTED = 3;
  public static final int STATE_CONNECTING = 2;
  public static final int STATE_LISTEN = 1;
  public static final int STATE_NONE = 0;
  private static final String TAG = "DistoBluetoothService";
  public static int m_BtMode = -1;
  public static final boolean m_bGattModePoll = false;
  public static boolean m_bSupressNextAngleMeasurement = false;
  private Context context;
  public BluetoothAdapter m_BluetoothAdapter;
  public BluetoothGatt m_BluetoothGatt;
  private ConnectThreadSPP m_ConnectThreadSPP;
  private ConnectedThreadSPP m_ConnectedThreadSPP;
  private BluetoothDevice m_CurrentBluetoothDevice;
  public DistoInterpreter m_DistoMachine = new DistoInterpreter();
  private Handler m_Handler;
  public boolean m_bDisableAutoReconnect = false;
  private boolean m_bForceStopped;
  private boolean m_btReEnabled = false;
  private int m_iState;
  private int m_nTryReconnect = 0;
  
  static
  {
    DISTO_SERVICE = UUID.fromString("3ab10100-f831-4395-b29d-570977d5bf94");
    DISTO_CHARACTERISTIC_DISTANCE = UUID.fromString("3ab10101-f831-4395-b29d-570977d5bf94");
    DISTO_CHARACTERISTIC_DISTANCE_DISPLAY_UNIT = UUID.fromString("3ab10102-f831-4395-b29d-570977d5bf94");
    DISTO_CHARACTERISTIC_INCLINATION = UUID.fromString("3ab10103-f831-4395-b29d-570977d5bf94");
    DISTO_CHARACTERISTIC_INCLINATION_DISPLAY_UNIT = UUID.fromString("3ab10104-f831-4395-b29d-570977d5bf94");
    DISTO_CHARACTERISTIC_GEOGRAPHIC_DIRECTION = UUID.fromString("3ab10105-f831-4395-b29d-570977d5bf94");
    DISTO_CHARACTERISTIC_GEOGRAPHIC_DIRECTION_DISTPLAY_UNIT = UUID.fromString("3ab10106-f831-4395-b29d-570977d5bf94");
    DISTO_CHARACTERISTIC_HORIZONTAL_INCLINE = UUID.fromString("3ab10107-f831-4395-b29d-570977d5bf94");
    DISTO_CHARACTERISTIC_VERTICAL_INCLINE = UUID.fromString("3ab10108-f831-4395-b29d-570977d5bf94");
  }
  
  public DistoBluetoothService(Context paramContext, Handler paramHandler)
  {
    this.context = paramContext;
    this.m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    this.m_iState = 0;
    this.m_Handler = paramHandler;
    this.m_DistoMachine.UpdateSettings(PreferenceManager.getDefaultSharedPreferences(this.context), this.context.getResources());
  }
  
  private void connectionFailed()
  {
    setState(1);
    Message localMessage = this.m_Handler.obtainMessage(4);
    Bundle localBundle = new Bundle();
    localBundle.putString("DISTO transfer", "Unable to connect DISTO");
    localMessage.setData(localBundle);
    this.m_Handler.sendMessage(localMessage);
    if ((this.m_nTryReconnect > 0) && (!this.m_bDisableAutoReconnect))
    {
      this.m_nTryReconnect -= 1;
      connectSPP(this.m_CurrentBluetoothDevice);
    }
  }
  
  private void connectionLost()
  {
    setState(1);
    if (!this.m_bForceStopped)
    {
      Message localMessage = this.m_Handler.obtainMessage(4);
      Bundle localBundle = new Bundle();
      localBundle.putString("DISTO transfer", "DISTO connection was lost");
      localMessage.setData(localBundle);
      this.m_Handler.sendMessage(localMessage);
    }
    for (this.m_bForceStopped = false;; this.m_bForceStopped = true)
    {
      if (!this.m_bDisableAutoReconnect)
      {
        this.m_nTryReconnect = 5;
        connectSPP(this.m_CurrentBluetoothDevice);
      }
      return;
    }
  }
  
  private void setState(int paramInt)
  {
    try
    {
      this.m_iState = paramInt;
      this.m_Handler.obtainMessage(1, paramInt, -1).sendToTarget();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void SendCommand(int paramInt)
  {
    if (m_BtMode == 0) {
      try
      {
        if (this.m_iState != 3)
        {
          localObject1 = this.m_Handler.obtainMessage(4);
          Bundle localBundle = new Bundle();
          localBundle.putString("DISTO transfer", "Error SendCommand, DISTO not connected");
          ((Message)localObject1).setData(localBundle);
          this.m_Handler.sendMessage((Message)localObject1);
          return;
        }
        Object localObject1 = this.m_ConnectedThreadSPP;
        ((ConnectedThreadSPP)localObject1).SendCommand(paramInt);
        return;
      }
      finally {}
    }
    if ((1 == m_BtMode) && (this.m_DistoMachine.hasDistoCmd(paramInt))) {
      writeGattCharacteristic(this.m_DistoMachine.getDistoCmd(paramInt).getBytes());
    }
  }
  
  public void UpdateDistoSettings(Context paramContext)
  {
    if (this.m_DistoMachine != null) {
      this.m_DistoMachine.UpdateSettings(PreferenceManager.getDefaultSharedPreferences(paramContext), paramContext.getResources());
    }
  }
  
  void connect(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    m_BtMode = paramInt;
    if (m_BtMode == 0)
    {
      connectSPP(paramBluetoothDevice);
      return;
    }
    connectGATT(paramBluetoothDevice);
  }
  
  @SuppressLint({"NewApi"})
  public void connectGATT(BluetoothDevice paramBluetoothDevice)
  {
    this.m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    this.m_BluetoothGatt = paramBluetoothDevice.connectGatt(this.context, true, new BluetoothGattCallback()
    {
      BleDistoMeasurementFormat m_CurrentMeasure = new BleDistoMeasurementFormat();
      private Stack m_notificationStack = new Stack();
      
      private boolean enableNotification(BluetoothGatt paramAnonymousBluetoothGatt, BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic)
      {
        boolean bool1 = false;
        if (paramAnonymousBluetoothGattCharacteristic == null) {}
        boolean bool2;
        do
        {
          do
          {
            return bool1;
            paramAnonymousBluetoothGatt.setCharacteristicNotification(paramAnonymousBluetoothGattCharacteristic, true);
            paramAnonymousBluetoothGattCharacteristic = paramAnonymousBluetoothGattCharacteristic.getDescriptor(DistoBluetoothService.DISTO_DESCRIPTOR);
          } while (paramAnonymousBluetoothGattCharacteristic == null);
          paramAnonymousBluetoothGattCharacteristic.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
          bool2 = paramAnonymousBluetoothGatt.writeDescriptor(paramAnonymousBluetoothGattCharacteristic);
          bool1 = bool2;
        } while (bool2);
        Log.e("DistoBluetoothService", "gatt.writeDescriptor(clientConfig) failed");
        return bool2;
      }
      
      public void onCharacteristicChanged(BluetoothGatt paramAnonymousBluetoothGatt, BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic)
      {
        onCharacteristicRead(paramAnonymousBluetoothGatt, paramAnonymousBluetoothGattCharacteristic, 0);
      }
      
      public void onCharacteristicRead(BluetoothGatt paramAnonymousBluetoothGatt, BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic, int paramAnonymousInt)
      {
        if (paramAnonymousInt != 0) {}
        do
        {
          do
          {
            return;
            if (paramAnonymousBluetoothGattCharacteristic.getUuid().equals(DistoBluetoothService.DISTO_CHARACTERISTIC_DISTANCE))
            {
              f = ByteBuffer.wrap(paramAnonymousBluetoothGattCharacteristic.getValue()).order(ByteOrder.LITTLE_ENDIAN).getFloat();
              this.m_CurrentMeasure = new BleDistoMeasurementFormat();
              this.m_CurrentMeasure.m_Time = new Time();
              this.m_CurrentMeasure.m_Time.setToNow();
              this.m_CurrentMeasure.m_dBleDistance = f;
              this.m_CurrentMeasure.m_dBleAngle = 0.0D;
              this.m_CurrentMeasure.calcBleDistance(DistoBluetoothService.this.context);
              DistoBluetoothService.this.m_Handler.postDelayed(new Runnable()
              {
                public void run()
                {
                  DistoBluetoothService.m_bSupressNextAngleMeasurement = false;
                  DistoBluetoothService.this.m_Handler.obtainMessage(2, -1, -1, DistoBluetoothService.1.this.m_CurrentMeasure).sendToTarget();
                }
              }, 1250L);
              return;
            }
            if (paramAnonymousBluetoothGattCharacteristic.getUuid().equals(DistoBluetoothService.DISTO_CHARACTERISTIC_DISTANCE_DISPLAY_UNIT))
            {
              this.m_CurrentMeasure.m_iBleDistanceUnit = paramAnonymousBluetoothGattCharacteristic.getIntValue(18, 0).intValue();
              this.m_CurrentMeasure.calcBleDistance(DistoBluetoothService.this.context);
              return;
            }
            if (!paramAnonymousBluetoothGattCharacteristic.getUuid().equals(DistoBluetoothService.DISTO_CHARACTERISTIC_INCLINATION)) {
              break;
            }
            paramAnonymousBluetoothGatt = paramAnonymousBluetoothGattCharacteristic.getValue();
          } while (DistoBluetoothService.m_bSupressNextAngleMeasurement);
          float f = ByteBuffer.wrap(paramAnonymousBluetoothGatt).order(ByteOrder.LITTLE_ENDIAN).getFloat();
          if (this.m_CurrentMeasure == null) {
            this.m_CurrentMeasure = new BleDistoMeasurementFormat();
          }
          this.m_CurrentMeasure.m_dBleAngle = f;
          return;
        } while (!paramAnonymousBluetoothGattCharacteristic.getUuid().equals(DistoBluetoothService.DISTO_CHARACTERISTIC_INCLINATION_DISPLAY_UNIT));
        this.m_CurrentMeasure.m_iBleAngleUnit = paramAnonymousBluetoothGattCharacteristic.getIntValue(18, 0).intValue();
        this.m_CurrentMeasure.calcBleAngle();
      }
      
      public void onConnectionStateChange(BluetoothGatt paramAnonymousBluetoothGatt, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (paramAnonymousInt1 > 110)
        {
          DistoBluetoothService.this.setState(1);
          paramAnonymousBluetoothGatt = DistoBluetoothService.this.m_Handler.obtainMessage(4);
          localObject = new Bundle();
          ((Bundle)localObject).putString("DISTO transfer", DistoBluetoothService.this.context.getString(2131230788));
          paramAnonymousBluetoothGatt.setData((Bundle)localObject);
          DistoBluetoothService.this.m_Handler.sendMessage(paramAnonymousBluetoothGatt);
        }
        do
        {
          return;
          if (paramAnonymousInt2 == 2)
          {
            localObject = DistoBluetoothService.this.m_Handler.obtainMessage(3);
            Bundle localBundle = new Bundle();
            localBundle.putString("device_name", paramAnonymousBluetoothGatt.getDevice().getName());
            localBundle.putString("device_address", paramAnonymousBluetoothGatt.getDevice().getAddress());
            ((Message)localObject).setData(localBundle);
            DistoBluetoothService.this.m_Handler.sendMessage((Message)localObject);
            DistoBluetoothService.this.m_btReEnabled = false;
            DistoBluetoothService.this.setState(3);
            paramAnonymousBluetoothGatt.discoverServices();
            DistoBluetoothService.this.m_DistoMachine.mDistoType = 4;
            return;
          }
        } while (paramAnonymousInt2 != 0);
        DistoBluetoothService.this.setState(0);
        paramAnonymousBluetoothGatt = DistoBluetoothService.this.m_Handler.obtainMessage(4);
        Object localObject = new Bundle();
        ((Bundle)localObject).putString("DISTO transfer", DistoBluetoothService.this.context.getString(2131230788));
        paramAnonymousBluetoothGatt.setData((Bundle)localObject);
        DistoBluetoothService.this.m_Handler.sendMessage(paramAnonymousBluetoothGatt);
        DistoBluetoothService.this.reconnectThroughGatt();
      }
      
      public void onDescriptorWrite(BluetoothGatt paramAnonymousBluetoothGatt, BluetoothGattDescriptor paramAnonymousBluetoothGattDescriptor, int paramAnonymousInt)
      {
        if (!this.m_notificationStack.empty())
        {
          paramAnonymousBluetoothGattDescriptor = (BluetoothGattCharacteristic)this.m_notificationStack.pop();
          DistoBluetoothService.this.m_Handler.postDelayed(new DelayedEnableNotification(paramAnonymousBluetoothGatt, paramAnonymousBluetoothGattDescriptor), 100L);
        }
      }
      
      public void onServicesDiscovered(BluetoothGatt paramAnonymousBluetoothGatt, int paramAnonymousInt)
      {
        BluetoothGattService localBluetoothGattService = paramAnonymousBluetoothGatt.getService(DistoBluetoothService.DISTO_SERVICE);
        if (localBluetoothGattService != null)
        {
          this.m_notificationStack.push(localBluetoothGattService.getCharacteristic(DistoBluetoothService.DISTO_CHARACTERISTIC_INCLINATION));
          this.m_notificationStack.push(localBluetoothGattService.getCharacteristic(DistoBluetoothService.DISTO_CHARACTERISTIC_INCLINATION_DISPLAY_UNIT));
          this.m_notificationStack.push(localBluetoothGattService.getCharacteristic(DistoBluetoothService.DISTO_CHARACTERISTIC_DISTANCE_DISPLAY_UNIT));
          DistoBluetoothService.this.m_Handler.postDelayed(new DelayedEnableNotification(paramAnonymousBluetoothGatt, localBluetoothGattService.getCharacteristic(DistoBluetoothService.DISTO_CHARACTERISTIC_DISTANCE)), 950L);
        }
      }
      
      class DelayedEnableNotification
        implements Runnable
      {
        final BluetoothGattCharacteristic m_characteristic;
        final BluetoothGatt m_gatt;
        
        DelayedEnableNotification(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
        {
          this.m_gatt = paramBluetoothGatt;
          this.m_characteristic = paramBluetoothGattCharacteristic;
        }
        
        public void run()
        {
          DistoBluetoothService.1.this.enableNotification(this.m_gatt, this.m_characteristic);
        }
      }
    });
  }
  
  public void connectSPP(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      this.m_CurrentBluetoothDevice = paramBluetoothDevice;
      if ((this.m_iState == 2) && (this.m_ConnectThreadSPP != null))
      {
        this.m_ConnectThreadSPP.cancel();
        this.m_ConnectThreadSPP = null;
      }
      if (this.m_ConnectedThreadSPP != null)
      {
        this.m_ConnectedThreadSPP.cancel();
        this.m_ConnectedThreadSPP = null;
      }
      this.m_ConnectThreadSPP = new ConnectThreadSPP(paramBluetoothDevice);
      this.m_ConnectThreadSPP.start();
      setState(2);
      return;
    }
    finally {}
  }
  
  public void connectedSPP(BluetoothSocket paramBluetoothSocket, BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      if (this.m_ConnectThreadSPP != null)
      {
        this.m_ConnectThreadSPP.cancel();
        this.m_ConnectThreadSPP = null;
      }
      if (this.m_ConnectedThreadSPP != null)
      {
        this.m_ConnectedThreadSPP.cancel();
        this.m_ConnectedThreadSPP = null;
      }
      this.m_ConnectedThreadSPP = new ConnectedThreadSPP(paramBluetoothSocket);
      this.m_ConnectedThreadSPP.start();
      paramBluetoothSocket = this.m_Handler.obtainMessage(3);
      Bundle localBundle = new Bundle();
      localBundle.putString("device_name", paramBluetoothDevice.getName());
      paramBluetoothSocket.setData(localBundle);
      this.m_Handler.sendMessage(paramBluetoothSocket);
      setState(3);
      return;
    }
    finally {}
  }
  
  public String getGattStatusText(int paramInt)
  {
    String str = "(" + paramInt + ") ";
    switch (paramInt)
    {
    default: 
      return str + "GATT_????";
    case 0: 
      return str + "GATT_SUCCESS";
    case 1: 
      return str + "GATT_INVALID_HANDLE";
    case 2: 
      return str + "GATT_READ_NOT_PERMIT";
    case 3: 
      return str + "GATT_WRITE_NOT_PERMIT";
    case 4: 
      return str + "GATT_INVALID_PDU";
    case 5: 
      return str + "GATT_INSUF_AUTHENTICATION";
    case 6: 
      return str + "GATT_REQ_NOT_SUPPORTED";
    case 7: 
      return str + "GATT_INVALID_OFFSET";
    case 8: 
      return str + "GATT_INSUF_AUTHORIZATION";
    case 9: 
      return str + "GATT_PREPARE_Q_FULL";
    case 10: 
      return str + "GATT_NOT_FOUND";
    case 11: 
      return str + "GATT_NOT_LONG";
    case 12: 
      return str + "GATT_INSUF_KEY_SIZE";
    case 13: 
      return str + "GATT_INVALID_ATTR_LEN";
    case 14: 
      return str + "GATT_ERR_UNLIKELY";
    case 15: 
      return str + "GATT_INSUF_ENCRYPTION";
    case 16: 
      return str + "GATT_UNSUPPORT_GRP_TYPE";
    case 17: 
      return str + "GATT_INSUF_RESOURCE";
    case 135: 
      return str + "GATT_ILLEGAL_PARAMETER";
    case 128: 
      return str + "GATT_NO_RESOURCES";
    case 129: 
      return str + "GATT_INTERNAL_ERROR";
    case 130: 
      return str + "GATT_WRONG_STATE";
    case 131: 
      return str + "GATT_DB_FULL";
    case 132: 
      return str + "GATT_BUSY";
    case 133: 
      return str + "GATT_ERROR";
    case 134: 
      return str + "GATT_CMD_STARTED";
    case 136: 
      return str + "GATT_PENDING";
    case 137: 
      return str + "GATT_AUTH_FAIL";
    case 138: 
      return str + "GATT_MORE";
    case 139: 
      return str + "GATT_INVALID_CFG";
    case 140: 
      return str + "GATT_SERVICE_STARTED";
    case 141: 
      return str + "GATT_ENCRYPED_NO_MITM";
    }
    return str + "GATT_NOT_ENCRYPTED";
  }
  
  public int getState()
  {
    try
    {
      int i = this.m_iState;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void readSomeGattCharacteristics()
  {
    if (this.m_BluetoothGatt != null)
    {
      this.m_BluetoothGatt.readCharacteristic(this.m_BluetoothGatt.getService(DISTO_SERVICE).getCharacteristic(DISTO_CHARACTERISTIC_DISTANCE));
      this.m_BluetoothGatt.readCharacteristic(this.m_BluetoothGatt.getService(DISTO_SERVICE).getCharacteristic(DISTO_CHARACTERISTIC_DISTANCE_DISPLAY_UNIT));
    }
  }
  
  public void reconnectThroughGatt()
  {
    if (this.m_BluetoothGatt != null) {
      this.m_BluetoothGatt.connect();
    }
  }
  
  public void replaceHandler(Handler paramHandler)
  {
    this.m_Handler = paramHandler;
  }
  
  public void stop()
  {
    try
    {
      this.m_bForceStopped = true;
      if (this.m_BluetoothGatt != null) {
        this.m_BluetoothGatt.disconnect();
      }
      if (this.m_ConnectThreadSPP != null)
      {
        this.m_ConnectThreadSPP.cancel();
        this.m_ConnectThreadSPP = null;
      }
      if (this.m_ConnectedThreadSPP != null)
      {
        this.m_ConnectedThreadSPP.cancel();
        this.m_ConnectedThreadSPP = null;
      }
      setState(0);
      return;
    }
    finally {}
  }
  
  public void write(byte[] paramArrayOfByte)
  {
    try
    {
      if (this.m_iState != 3) {
        return;
      }
      ConnectedThreadSPP localConnectedThreadSPP = this.m_ConnectedThreadSPP;
      localConnectedThreadSPP.writeSPP(paramArrayOfByte);
      return;
    }
    finally {}
  }
  
  public void writeGattCharacteristic(byte[] paramArrayOfByte)
  {
    Object localObject = this.m_BluetoothGatt.getService(DISTO_SERVICE);
    if (localObject == null)
    {
      Log.d("DistoBluetoothService", "writeGattCharacteristic: DistoService null");
      return;
    }
    localObject = ((BluetoothGattService)localObject).getCharacteristic(DISTO_CHARACTERISTIC_COMMAND);
    if (localObject == null)
    {
      Log.d("DistoBluetoothService", "distoCharacteristicCommand null");
      return;
    }
    ((BluetoothGattCharacteristic)localObject).setValue(paramArrayOfByte);
    this.m_BluetoothGatt.writeCharacteristic((BluetoothGattCharacteristic)localObject);
  }
  
  private class ConnectThreadSPP
    extends Thread
  {
    private final BluetoothDevice m_BluetoothDevice;
    private final BluetoothSocket m_BluetoothSocket;
    
    public ConnectThreadSPP(BluetoothDevice paramBluetoothDevice)
    {
      this.m_BluetoothDevice = paramBluetoothDevice;
      paramBluetoothDevice = null;
      try
      {
        BluetoothSocket localBluetoothSocket = this.m_BluetoothDevice.createInsecureRfcommSocketToServiceRecord(DistoBluetoothService.DISTO_SPP_UUID);
        this$1 = localBluetoothSocket;
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          Log.e("DistoBluetoothService", "createInsecureRfcommSocketToServiceRecord() failed", localIOException);
          DistoBluetoothService.this.m_Handler.obtainMessage(7, 1, -1).sendToTarget();
          this$1 = paramBluetoothDevice;
        }
      }
      this.m_BluetoothSocket = DistoBluetoothService.this;
    }
    
    public void cancel()
    {
      try
      {
        this.m_BluetoothSocket.close();
        return;
      }
      catch (IOException localIOException)
      {
        Log.e("DistoBluetoothService", "m_BluetoothSocket.close() failed", localIOException);
        DistoBluetoothService.this.m_Handler.obtainMessage(7, 1, -1).sendToTarget();
      }
    }
    
    public void run()
    {
      Log.i("DistoBluetoothService", "BEGIN ConnectThreadSPP");
      setName("ConnectThreadSPP");
      if (DistoBluetoothService.this.m_BluetoothAdapter.isDiscovering()) {
        DistoBluetoothService.this.m_BluetoothAdapter.cancelDiscovery();
      }
      try
      {
        Thread.sleep(50L, 0);
        this.m_BluetoothSocket.connect();
      }
      catch (IOException localIOException1)
      {
        synchronized (DistoBluetoothService.this)
        {
          DistoBluetoothService.this.m_ConnectThreadSPP = null;
          DistoBluetoothService.this.connectedSPP(this.m_BluetoothSocket, this.m_BluetoothDevice);
          return;
          localIOException1 = localIOException1;
          Log.e("DistoBluetoothService", "m_BluetoothSocket.connect() failed", localIOException1);
          DistoBluetoothService.this.connectionFailed();
          try
          {
            this.m_BluetoothSocket.close();
            return;
          }
          catch (IOException localIOException2)
          {
            Log.e("DistoBluetoothService", "Error m_BluetoothSocket.close() socket during connection", localIOException2);
            DistoBluetoothService.this.m_Handler.obtainMessage(7, 1, -1).sendToTarget();
            return;
          }
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
    }
  }
  
  private class ConnectedThreadSPP
    extends Thread
  {
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final BluetoothSocket mmSocket;
    
    public ConnectedThreadSPP(BluetoothSocket paramBluetoothSocket)
    {
      Log.d("DistoBluetoothService", "create ConnectedThreadSPP");
      this.mmSocket = paramBluetoothSocket;
      Object localObject1 = null;
      localObject2 = null;
      try
      {
        InputStream localInputStream = paramBluetoothSocket.getInputStream();
        localObject1 = localInputStream;
        paramBluetoothSocket = paramBluetoothSocket.getOutputStream();
        this$1 = paramBluetoothSocket;
        localObject1 = localInputStream;
      }
      catch (IOException paramBluetoothSocket)
      {
        for (;;)
        {
          Log.e("DistoBluetoothService", "temp sockets not created", paramBluetoothSocket);
          DistoBluetoothService.this.m_Handler.obtainMessage(7, 3, -1).sendToTarget();
          this$1 = (DistoBluetoothService)localObject2;
        }
      }
      this.mmInStream = ((InputStream)localObject1);
      this.mmOutStream = DistoBluetoothService.this;
    }
    
    public boolean SendCommand(int paramInt)
    {
      if ((DistoBluetoothService.m_BtMode == 0) && (DistoBluetoothService.this.m_DistoMachine.hasDistoCmd(paramInt)))
      {
        writeSPP(DistoBluetoothService.this.m_DistoMachine.getDistoCmd(paramInt).getBytes());
        writeSPP("\r\n".getBytes());
        return true;
      }
      return false;
    }
    
    public void cancel()
    {
      try
      {
        this.mmSocket.close();
        return;
      }
      catch (IOException localIOException)
      {
        Log.e("DistoBluetoothService", "mmSocket.close() failed", localIOException);
      }
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: sipush 4048
      //   3: newarray <illegal type>
      //   5: astore 4
      //   7: new 117	leica/disto/transferBLE/Ringbuffer
      //   10: dup
      //   11: sipush 8000
      //   14: invokespecial 120	leica/disto/transferBLE/Ringbuffer:<init>	(I)V
      //   17: astore 5
      //   19: ldc2_w 121
      //   22: invokestatic 126	java/lang/Thread:sleep	(J)V
      //   25: aload_0
      //   26: iconst_5
      //   27: invokevirtual 128	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:SendCommand	(I)Z
      //   30: pop
      //   31: aload 5
      //   33: aload 4
      //   35: aload_0
      //   36: getfield 51	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:mmInStream	Ljava/io/InputStream;
      //   39: aload 4
      //   41: invokevirtual 134	java/io/InputStream:read	([B)I
      //   44: invokevirtual 138	leica/disto/transferBLE/Ringbuffer:insert	([BI)V
      //   47: aload 5
      //   49: bipush 13
      //   51: invokevirtual 142	leica/disto/transferBLE/Ringbuffer:contains	(B)Z
      //   54: ifeq -23 -> 31
      //   57: aload 5
      //   59: ldc -112
      //   61: ldc -110
      //   63: invokevirtual 149	java/lang/String:getBytes	(Ljava/lang/String;)[B
      //   66: invokevirtual 152	leica/disto/transferBLE/Ringbuffer:dequeueUntil	([B)I
      //   69: pop
      //   70: new 96	java/lang/String
      //   73: dup
      //   74: aload 4
      //   76: iconst_0
      //   77: aload 5
      //   79: aload 4
      //   81: bipush 13
      //   83: invokevirtual 156	leica/disto/transferBLE/Ringbuffer:getLine	([BB)I
      //   86: invokespecial 159	java/lang/String:<init>	([BII)V
      //   89: ldc -95
      //   91: invokevirtual 165	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
      //   94: astore 7
      //   96: bipush 10
      //   98: anewarray 9	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet
      //   101: astore 6
      //   103: iconst_0
      //   104: istore_1
      //   105: iconst_0
      //   106: istore_3
      //   107: iload_3
      //   108: aload 7
      //   110: arraylength
      //   111: if_icmplt +71 -> 182
      //   114: aload_0
      //   115: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   118: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   121: getfield 168	leica/disto/transferBLE/DistoInterpreter:m_SendConfirmation	I
      //   124: istore_1
      //   125: iload_1
      //   126: ifle +637 -> 763
      //   129: ldc2_w 121
      //   132: invokestatic 126	java/lang/Thread:sleep	(J)V
      //   135: aload_0
      //   136: bipush 9
      //   138: invokevirtual 128	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:SendCommand	(I)Z
      //   141: pop
      //   142: goto +621 -> 763
      //   145: iload_2
      //   146: aload 6
      //   148: arraylength
      //   149: if_icmpge -118 -> 31
      //   152: aload 6
      //   154: iload_2
      //   155: aaload
      //   156: ifnull +619 -> 775
      //   159: aload 6
      //   161: iload_2
      //   162: aaload
      //   163: getfield 172	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultDistance	Ljava/lang/String;
      //   166: ifnonnull +363 -> 529
      //   169: aload 6
      //   171: iload_2
      //   172: aaload
      //   173: getfield 175	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultAngle	Ljava/lang/String;
      //   176: ifnonnull +353 -> 529
      //   179: goto +596 -> 775
      //   182: aload_0
      //   183: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   186: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   189: aload 7
      //   191: iload_3
      //   192: aaload
      //   193: invokevirtual 179	leica/disto/transferBLE/DistoInterpreter:AnalyzeAndInterpretGsiItem	(Ljava/lang/String;)I
      //   196: pop
      //   197: iload_1
      //   198: istore_2
      //   199: aload_0
      //   200: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   203: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   206: getfield 182	leica/disto/transferBLE/DistoInterpreter:m_eResultType	I
      //   209: tableswitch	default:+573->782, 0:+575->784, 1:+79->288, 2:+165->374, 3:+251->460, 4:+282->491, 5:+39->248
      //   248: aload_0
      //   249: bipush 10
      //   251: invokevirtual 128	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:SendCommand	(I)Z
      //   254: pop
      //   255: aload_0
      //   256: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   259: iconst_3
      //   260: invokestatic 186	leica/disto/transferBLE/DistoBluetoothService:access$4	(Lleica/disto/transferBLE/DistoBluetoothService;I)V
      //   263: iload_1
      //   264: istore_2
      //   265: goto +519 -> 784
      //   268: astore 4
      //   270: ldc 29
      //   272: ldc -68
      //   274: aload 4
      //   276: invokestatic 59	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   279: pop
      //   280: aload_0
      //   281: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   284: invokestatic 192	leica/disto/transferBLE/DistoBluetoothService:access$5	(Lleica/disto/transferBLE/DistoBluetoothService;)V
      //   287: return
      //   288: aload 6
      //   290: iload_1
      //   291: aaload
      //   292: ifnonnull +16 -> 308
      //   295: aload 6
      //   297: iload_1
      //   298: new 9	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet
      //   301: dup
      //   302: aload_0
      //   303: aconst_null
      //   304: invokespecial 195	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:<init>	(Lleica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP;Lleica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet;)V
      //   307: aastore
      //   308: iload_1
      //   309: istore_2
      //   310: aload 6
      //   312: iload_1
      //   313: aaload
      //   314: getfield 172	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultDistance	Ljava/lang/String;
      //   317: ifnull +20 -> 337
      //   320: iload_1
      //   321: iconst_1
      //   322: iadd
      //   323: istore_2
      //   324: aload 6
      //   326: iload_2
      //   327: new 9	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet
      //   330: dup
      //   331: aload_0
      //   332: aconst_null
      //   333: invokespecial 195	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:<init>	(Lleica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP;Lleica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet;)V
      //   336: aastore
      //   337: aload 6
      //   339: iload_2
      //   340: aaload
      //   341: aload_0
      //   342: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   345: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   348: getfield 198	leica/disto/transferBLE/DistoInterpreter:m_ResultString	Ljava/lang/String;
      //   351: putfield 172	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultDistance	Ljava/lang/String;
      //   354: aload 6
      //   356: iload_2
      //   357: aaload
      //   358: aload_0
      //   359: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   362: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   365: getfield 201	leica/disto/transferBLE/DistoInterpreter:m_ResultUnitString	Ljava/lang/String;
      //   368: putfield 204	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultDistanceUnit	Ljava/lang/String;
      //   371: goto +413 -> 784
      //   374: aload 6
      //   376: iload_1
      //   377: aaload
      //   378: ifnonnull +16 -> 394
      //   381: aload 6
      //   383: iload_1
      //   384: new 9	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet
      //   387: dup
      //   388: aload_0
      //   389: aconst_null
      //   390: invokespecial 195	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:<init>	(Lleica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP;Lleica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet;)V
      //   393: aastore
      //   394: iload_1
      //   395: istore_2
      //   396: aload 6
      //   398: iload_1
      //   399: aaload
      //   400: getfield 175	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultAngle	Ljava/lang/String;
      //   403: ifnull +20 -> 423
      //   406: iload_1
      //   407: iconst_1
      //   408: iadd
      //   409: istore_2
      //   410: aload 6
      //   412: iload_2
      //   413: new 9	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet
      //   416: dup
      //   417: aload_0
      //   418: aconst_null
      //   419: invokespecial 195	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:<init>	(Lleica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP;Lleica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet;)V
      //   422: aastore
      //   423: aload 6
      //   425: iload_2
      //   426: aaload
      //   427: aload_0
      //   428: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   431: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   434: getfield 198	leica/disto/transferBLE/DistoInterpreter:m_ResultString	Ljava/lang/String;
      //   437: putfield 175	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultAngle	Ljava/lang/String;
      //   440: aload 6
      //   442: iload_2
      //   443: aaload
      //   444: aload_0
      //   445: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   448: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   451: getfield 201	leica/disto/transferBLE/DistoInterpreter:m_ResultUnitString	Ljava/lang/String;
      //   454: putfield 207	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultAngleUnit	Ljava/lang/String;
      //   457: goto +327 -> 784
      //   460: aload_0
      //   461: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   464: invokestatic 63	leica/disto/transferBLE/DistoBluetoothService:access$1	(Lleica/disto/transferBLE/DistoBluetoothService;)Landroid/os/Handler;
      //   467: iconst_5
      //   468: aload_0
      //   469: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   472: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   475: getfield 210	leica/disto/transferBLE/DistoInterpreter:m_ErrorCode	I
      //   478: iconst_m1
      //   479: aconst_null
      //   480: invokevirtual 213	android/os/Handler:obtainMessage	(IIILjava/lang/Object;)Landroid/os/Message;
      //   483: invokevirtual 74	android/os/Message:sendToTarget	()V
      //   486: iload_1
      //   487: istore_2
      //   488: goto +296 -> 784
      //   491: aload_0
      //   492: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   495: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   498: getfield 198	leica/disto/transferBLE/DistoInterpreter:m_ResultString	Ljava/lang/String;
      //   501: invokestatic 219	java/lang/Double:parseDouble	(Ljava/lang/String;)D
      //   504: d2i
      //   505: istore_2
      //   506: aload_0
      //   507: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   510: invokestatic 63	leica/disto/transferBLE/DistoBluetoothService:access$1	(Lleica/disto/transferBLE/DistoBluetoothService;)Landroid/os/Handler;
      //   513: bipush 6
      //   515: iload_2
      //   516: iconst_m1
      //   517: aconst_null
      //   518: invokevirtual 213	android/os/Handler:obtainMessage	(IIILjava/lang/Object;)Landroid/os/Message;
      //   521: invokevirtual 74	android/os/Message:sendToTarget	()V
      //   524: iload_1
      //   525: istore_2
      //   526: goto +258 -> 784
      //   529: new 221	leica/disto/transferBLE/BleDistoMeasurementFormat
      //   532: dup
      //   533: invokespecial 222	leica/disto/transferBLE/BleDistoMeasurementFormat:<init>	()V
      //   536: astore 7
      //   538: iconst_0
      //   539: istore_1
      //   540: aload 7
      //   542: new 224	android/text/format/Time
      //   545: dup
      //   546: invokespecial 225	android/text/format/Time:<init>	()V
      //   549: putfield 229	leica/disto/transferBLE/BleDistoMeasurementFormat:m_Time	Landroid/text/format/Time;
      //   552: aload 7
      //   554: getfield 229	leica/disto/transferBLE/BleDistoMeasurementFormat:m_Time	Landroid/text/format/Time;
      //   557: invokevirtual 232	android/text/format/Time:setToNow	()V
      //   560: aload 7
      //   562: aload_0
      //   563: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   566: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   569: getfield 236	leica/disto/transferBLE/DistoInterpreter:m_bResultMetric	Z
      //   572: putfield 239	leica/disto/transferBLE/BleDistoMeasurementFormat:m_bMetric	Z
      //   575: aload 7
      //   577: aload_0
      //   578: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   581: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   584: getfield 242	leica/disto/transferBLE/DistoInterpreter:m_nMetricResultDezimals	I
      //   587: putfield 245	leica/disto/transferBLE/BleDistoMeasurementFormat:m_nExcelFormatDezimals	I
      //   590: aload_0
      //   591: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   594: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   597: getfield 248	leica/disto/transferBLE/DistoInterpreter:m_bResultUseEnter	Z
      //   600: ifeq +9 -> 609
      //   603: aload 7
      //   605: iconst_1
      //   606: putfield 251	leica/disto/transferBLE/BleDistoMeasurementFormat:m_bEnter	Z
      //   609: aload_0
      //   610: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   613: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   616: getfield 254	leica/disto/transferBLE/DistoInterpreter:m_bResultUseTab	Z
      //   619: ifeq +9 -> 628
      //   622: aload 7
      //   624: iconst_1
      //   625: putfield 257	leica/disto/transferBLE/BleDistoMeasurementFormat:m_bTab	Z
      //   628: aload 6
      //   630: iload_2
      //   631: aaload
      //   632: getfield 172	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultDistance	Ljava/lang/String;
      //   635: ifnull +43 -> 678
      //   638: aload 7
      //   640: aload 6
      //   642: iload_2
      //   643: aaload
      //   644: getfield 172	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultDistance	Ljava/lang/String;
      //   647: putfield 260	leica/disto/transferBLE/BleDistoMeasurementFormat:m_Distance	Ljava/lang/String;
      //   650: aload_0
      //   651: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   654: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   657: getfield 263	leica/disto/transferBLE/DistoInterpreter:m_bResultUseUnit	Z
      //   660: ifeq +133 -> 793
      //   663: aload 7
      //   665: aload 6
      //   667: iload_2
      //   668: aaload
      //   669: getfield 204	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultDistanceUnit	Ljava/lang/String;
      //   672: putfield 266	leica/disto/transferBLE/BleDistoMeasurementFormat:m_DistanceUnit	Ljava/lang/String;
      //   675: goto +118 -> 793
      //   678: aload 6
      //   680: iload_2
      //   681: aaload
      //   682: getfield 175	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultAngle	Ljava/lang/String;
      //   685: ifnull +43 -> 728
      //   688: aload 7
      //   690: aload 6
      //   692: iload_2
      //   693: aaload
      //   694: getfield 175	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultAngle	Ljava/lang/String;
      //   697: putfield 269	leica/disto/transferBLE/BleDistoMeasurementFormat:m_Angle	Ljava/lang/String;
      //   700: aload_0
      //   701: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   704: getfield 85	leica/disto/transferBLE/DistoBluetoothService:m_DistoMachine	Lleica/disto/transferBLE/DistoInterpreter;
      //   707: getfield 263	leica/disto/transferBLE/DistoInterpreter:m_bResultUseUnit	Z
      //   710: ifeq +88 -> 798
      //   713: aload 7
      //   715: aload 6
      //   717: iload_2
      //   718: aaload
      //   719: getfield 207	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP$MeasurementSet:resultAngleUnit	Ljava/lang/String;
      //   722: putfield 272	leica/disto/transferBLE/BleDistoMeasurementFormat:m_AngleUnit	Ljava/lang/String;
      //   725: goto +73 -> 798
      //   728: iload_1
      //   729: ifeq +46 -> 775
      //   732: aload_0
      //   733: getfield 24	leica/disto/transferBLE/DistoBluetoothService$ConnectedThreadSPP:this$0	Lleica/disto/transferBLE/DistoBluetoothService;
      //   736: invokestatic 63	leica/disto/transferBLE/DistoBluetoothService:access$1	(Lleica/disto/transferBLE/DistoBluetoothService;)Landroid/os/Handler;
      //   739: iconst_2
      //   740: iconst_m1
      //   741: iconst_m1
      //   742: aload 7
      //   744: invokevirtual 213	android/os/Handler:obtainMessage	(IIILjava/lang/Object;)Landroid/os/Message;
      //   747: invokevirtual 74	android/os/Message:sendToTarget	()V
      //   750: goto +25 -> 775
      //   753: astore 6
      //   755: goto -730 -> 25
      //   758: astore 7
      //   760: goto -625 -> 135
      //   763: aload 6
      //   765: iconst_0
      //   766: aaload
      //   767: ifnull -736 -> 31
      //   770: iconst_0
      //   771: istore_2
      //   772: goto -627 -> 145
      //   775: iload_2
      //   776: iconst_1
      //   777: iadd
      //   778: istore_2
      //   779: goto -634 -> 145
      //   782: iload_1
      //   783: istore_2
      //   784: iload_3
      //   785: iconst_1
      //   786: iadd
      //   787: istore_3
      //   788: iload_2
      //   789: istore_1
      //   790: goto -683 -> 107
      //   793: iconst_1
      //   794: istore_1
      //   795: goto -117 -> 678
      //   798: iconst_1
      //   799: istore_1
      //   800: goto -72 -> 728
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	803	0	this	ConnectedThreadSPP
      //   104	696	1	i	int
      //   145	644	2	j	int
      //   106	682	3	k	int
      //   5	75	4	arrayOfByte	byte[]
      //   268	7	4	localIOException	IOException
      //   17	61	5	localRingbuffer	Ringbuffer
      //   101	615	6	arrayOfMeasurementSet	MeasurementSet[]
      //   753	11	6	localInterruptedException1	InterruptedException
      //   94	649	7	localObject	Object
      //   758	1	7	localInterruptedException2	InterruptedException
      // Exception table:
      //   from	to	target	type
      //   31	103	268	java/io/IOException
      //   107	125	268	java/io/IOException
      //   129	135	268	java/io/IOException
      //   135	142	268	java/io/IOException
      //   145	152	268	java/io/IOException
      //   159	179	268	java/io/IOException
      //   182	197	268	java/io/IOException
      //   199	248	268	java/io/IOException
      //   248	263	268	java/io/IOException
      //   295	308	268	java/io/IOException
      //   310	320	268	java/io/IOException
      //   324	337	268	java/io/IOException
      //   337	371	268	java/io/IOException
      //   381	394	268	java/io/IOException
      //   396	406	268	java/io/IOException
      //   410	423	268	java/io/IOException
      //   423	457	268	java/io/IOException
      //   460	486	268	java/io/IOException
      //   491	524	268	java/io/IOException
      //   529	538	268	java/io/IOException
      //   540	609	268	java/io/IOException
      //   609	628	268	java/io/IOException
      //   628	675	268	java/io/IOException
      //   678	725	268	java/io/IOException
      //   732	750	268	java/io/IOException
      //   19	25	753	java/lang/InterruptedException
      //   129	135	758	java/lang/InterruptedException
    }
    
    public void writeSPP(byte[] paramArrayOfByte)
    {
      try
      {
        this.mmOutStream.write(paramArrayOfByte);
        return;
      }
      catch (IOException paramArrayOfByte)
      {
        Log.e("DistoBluetoothService", "Exception during writeSPP", paramArrayOfByte);
        DistoBluetoothService.this.m_Handler.obtainMessage(7, 4, -1).sendToTarget();
      }
    }
    
    private class MeasurementSet
    {
      public String resultAngle = null;
      public String resultAngleUnit = null;
      public String resultDistance = null;
      public String resultDistanceUnit = null;
      
      private MeasurementSet() {}
    }
  }
}


/* Location:              C:\opt64\adt-bundle\Disto\classes-dex2jar.jar!\leica\disto\transferBLE\DistoBluetoothService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */