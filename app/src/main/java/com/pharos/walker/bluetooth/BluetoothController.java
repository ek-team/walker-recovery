package com.pharos.walker.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;


import com.pharos.walker.beans.BleBean;
import com.pharos.walker.application.MyApplication;
import com.pharos.walker.beans.CacheBean;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.database.BleDeviceInfoManager;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.tencent.mars.xlog.Log;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import okhttp3.Cache;

/**
 * 蓝牙控制类
 */
public class BluetoothController {
    private static final String TAG = "BluetoothController";

    private static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    public static final String HAND = "Walker_";
    public static final String HAND_NEW = "wk_";
    public static final String HAND_RIGHT = "R_";
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeScanner bleScan;
    private volatile static int RSSI;
    private volatile boolean isReceiveData = true;
    private SendThread mSendThread;
    //设备ID-蓝牙连接实体
    private ConcurrentHashMap<String, BleBean> gattMap;  // 同时连接的设备存在多个
    private static int connectCount = 1;  // 蓝牙出现断开，重连1次
    private ConcurrentLinkedQueue<CacheBean> cacheQueue = null;
    private long firstTemp = 0;
    private long connectedTimestamp = 0;

    public  void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    private   long lastTimestamp = 0;

    /**
     * 单例模式
     */
    private static volatile BluetoothController instance = null;
    private boolean isTimeFlag = true;
    private BluetoothGatt mGatt;
    private int delayTime = 60;
    private BluetoothController() {
        gattMap = new ConcurrentHashMap<>();
    }
    private BluetoothGatt mBluetoothGatt;
    public static BluetoothController getInstance() {
        if (instance == null) {
            synchronized (BluetoothController.class) {
                if (instance == null) {
                    instance = new BluetoothController();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化蓝牙
     *
     * @return
     */
    public void initBle() {
        BluetoothManager mBluetoothManager = (BluetoothManager) MyApplication.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            Toast.makeText(MyApplication.getInstance(), "您的手机不支持蓝牙",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();  // 获取 BluetoothAdapter
        if (mBluetoothAdapter == null) {
            Toast.makeText(MyApplication.getInstance(), "您的手机不支持蓝牙",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBluetoothAdapter.isEnabled()) {  // 是为了清空已连接的蓝牙设备
            mBluetoothAdapter.disable();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBluetoothAdapter.enable();
        } else {
            mBluetoothAdapter.enable();  // 开启蓝牙
        }
//        if (!mBluetoothAdapter.isEnabled()){
//            mBluetoothAdapter.enable();
//        }

    }

    /**
     * 开始扫描蓝牙
     */
    public void startScanBle() {
        if (mBluetoothAdapter == null){
            initBle();
            return;
        }
        bleScan = mBluetoothAdapter.getBluetoothLeScanner();
        if (bleScan == null) {
            return;
        }
        if (mBluetoothAdapter.isEnabled()){
            bleScan.startScan(null,setScanSetting(),scanCallback);
        }else {
            Log.e(TAG,"蓝牙还未打开");
        }
    }
    private ScanSettings setScanSetting(){
        ScanSettings.Builder builder = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        if (Build.VERSION.SDK_INT >= 23){
            builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
            builder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        }

        return builder.build();
    }
    /**
     * 停止扫描蓝牙设备
     */
    public void stopScanBle() {
        if (bleScan == null) {
            return;
        }
        if (mBluetoothAdapter.isEnabled()){
            bleScan.stopScan(scanCallback);
        }else {
            Log.e(TAG,"蓝牙还未打开");
        }
    }

    /**
     * 是否蓝牙打开
     *
     * @return
     */
    public boolean isBleOpen() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 连接蓝牙设备
     *
     * @param address 待连接的设备
     */
    public boolean connect(String address) {
        if (mBluetoothAdapter == null || TextUtils.isEmpty(address)) {
            return false;
        }
        mBluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        // 如果设置自动连接，则安卓底层会不停的跟对应Mac地址的设备反复连接，连接效率会变得很慢，
        // 而且容易发送阻塞，导致后边的设备一直在等前一个设备连接成功的回调，蓝牙设备的连接一定要分开逐个连接，尽量不要形成线程阻碍。
        mBluetoothGatt = device.connectGatt(MyApplication.getInstance(), false, bleGattCallback);
        if (mBluetoothGatt == null) {
            Exception ex = new Exception();
            Log.e(TAG, "open.mBluetoothGatt 为空: ", ex);
            //throw ex;
        }
        return true;
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG,"扫描失败onScanFailed， errorCode==" + errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult scanResult : results){
                Log.d(TAG,"批量扫描结果："+scanResult.getDevice().getAddress());
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String name = result.getDevice().getName();
            String address = result.getDevice().getAddress();
            android.util.Log.d(TAG, "搜索到的设备：" + name + "  " + address + "  " + result.getRssi() + " " + result.getDevice().getBondState());
            if (name != null && (name.startsWith(HAND) || name.startsWith(HAND_NEW))) {  // 过滤搜索到的蓝牙设备
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.BLE_SCAN_RESULT, new BleBean(1, name, address,result.getRssi())));
            }
        }
    };


    /**
     * 与蓝牙通信回调
     */
    private BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {

        /**
         * 连接状态改变
         */
        public void onConnectionStateChange(BluetoothGatt gatt, int oldStatus, int newStatus) {

            mGatt = gatt;
            BluetoothDevice device = gatt.getDevice();
            if (newStatus == BluetoothGatt.STATE_CONNECTED && oldStatus == BluetoothGatt.GATT_SUCCESS) {  // 成功执行连接操作 && 设备已经连接
                connectCount = 1;
                String address = device.getAddress();
                if (!gattMap.containsKey(address)) {
                    gattMap.put(address, new BleBean(gatt, device.getName()));
                }
                int type = 1;
                Log.e(TAG, "onConnectionStateChange: " + "连接了" + device.getName());
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_GATT_CONNECTED, new BleBean(type, device.getName(), device.getAddress())));
                gatt.discoverServices();  // 连接到蓝牙后查找可以读写的服务，蓝牙有很多服务
//                gatt.readRemoteRssi();
                return;
            }
            if (newStatus == BluetoothGatt.STATE_DISCONNECTED) {  // 断开连接或未连接成功
                closeGatt(gatt.getDevice().getAddress());
                gatt.close();
//                if (!Global.isStartReadData){
//                    Global.isConnected = false;
//                }
                if (Global.isReconnectBle) {  // 重连
                    connectCount--;
                    if (connectCount < 0) {
                        disConnected(device);
                        return;
                    }
                    connect(gatt.getDevice().getAddress());
                    Log.e(TAG, "onConnectionStateChange: " + "断开了,开始重连……");
                } else if (!Global.isStartReadData){
                    disConnected(device);
                    Log.e(TAG, "onConnectionStateChange: " + "非训练界面断开");
                }else {
//                    Global.isConnected = false;
                    Log.e(TAG, "onConnectionStateChange: " + "断开了");
                }
                return;
            }
            closeGatt(gatt.getDevice().getAddress());
        }

        /**
         * 蓝牙断开连接
         * @param device
         */
        private void disConnected(BluetoothDevice device) {
            Global.TempConnectedAddress = Global.ConnectedAddress;
            Global.isConnected = false;
            Global.ConnectedAddress = null;
            Global.ConnectedName = null;
            Global.ConnectStatus = "unconnected";
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_GATT_DISCONNECTED, new BleBean(1, device.getName(), device.getAddress())));
            stopDealThread();
        }

        /**
         * 收到消息
         */
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic gattCharacteristic) {
            String result;
            result = new String(gattCharacteristic.getValue(), StandardCharsets.UTF_8);
            android.util.Log.d(TAG, "onCharacteristicChanged:-----> " + result );
            isReceiveData = true;
            if (DateFormatUtil.setTimeInterval(200))
                gatt.readRemoteRssi();
            if (Math.abs(System.currentTimeMillis() - connectedTimestamp) > 2500){//抛掉连接成功后，前几秒的脏数据
                sendData(gatt.getDevice().getAddress(), result,RSSI);
            }else {
                sendData(gatt.getDevice().getAddress(), ":1AA00000000A00",RSSI);
            }

        }

        /**
         * 读写characteristic时会调用到以下方法
         */
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic gattCharacteristic, int status) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                String result = MyFunc.bytes2HexString(gattCharacteristic.getValue());
////                Log.i("test", "onReceive 接收到蓝牙: " + gatt.getDevice().getAddress() + " " + result);
//                EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.ACTION_DATA_AVAILABLE, new BleBean(gatt.getDevice().getAddress(), gattCharacteristic.getValue())));
//            }
        }

        /**
         * 写操作回调
         * @param gatt
         * @param characteristic
         * @param status
         */
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            String address = gatt.getDevice().getAddress();
            Log.d(TAG, "写操作回调： address: " + address + ",Write: " + DataTransformUtil.bytes2HexString(characteristic.getValue()));
            if (Global.isConnected && !Global.isStartReadData){
                synchronized (this){
                    Global.ReadCount = 0;
                }
            }

//            HermesEventBus.getDefault().post(new MessageEvent(MessageEvent.ACTION_GATT_WRITE_SUCCESS));
        }

        /**
         * 通知写入回调
         * @param gatt
         * @param descriptor
         * @param status
         */
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor gattDescriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS){
                Global.isConnected = true;
                connectedTimestamp = System.currentTimeMillis();
                Log.e(TAG, "onDescriptorWrite: 传输通道开启");
            }else {
                Log.e(TAG, "onDescriptorWrite: 传输通道开启失败 失败状态：" + status);
            }
            startDealThread();
            lastTimestamp = 0;
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.GATT_TRANSPORT_OPEN, status));

        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            android.util.Log.d(TAG, "onReadRemoteRssi： rssi: " + rssi + ",paramAnonymousInt2: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS){
                synchronized (this){
                    RSSI = rssi;
                }
            }
        }

        public void onReliableWriteCompleted(BluetoothGatt gatt, int paramAnonymousInt) {
        }

        /**
         * 成功发现设备的services时，调用此方法
         */
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS){
                enableTXNotification(true);
            }else {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.GATT_TRANSPORT_OPEN, status));
            }
        }

        /**
         * 发送数据
         *
         * @param address
         * @param msg
         */
        private long lastTempTest = 0;
        private long lastTemp = 0;
        private float lastValue = -1;
        private synchronized void sendData(String address, String msg, int rssi) {  // FFFFA00A007B001800C2
            if (!msg.startsWith(Global.Header)) {  // 判断数据包是否以帧头开始
                return;
            }
            if (msg.length() < 15) {  // 最小长度为15，才合法
                return;
            }
            msg = msg.replace("\r\n", "");
            if (msg.length() == 15){//:1AA4010041  :1AA4040000231B
                if (cacheQueue == null){
                    cacheQueue = new ConcurrentLinkedQueue();
                }
                if (firstTemp == 0){
                    firstTemp = System.currentTimeMillis();
                    lastTemp = firstTemp;
                }else {
                    if (Math.abs(System.currentTimeMillis() - lastTemp) > 120){
                        firstTemp = lastTemp + 100;
                    } else {
                        firstTemp = System.currentTimeMillis();
                    }
                }
                int weight = Integer.parseInt(msg.substring(9,13),16);
                int index = Integer.parseInt(msg.substring(4,8),16);
//                if (Global.isOpenTest){
//                    long currentTemp = System.currentTimeMillis();
//                    long timeDiff = currentTemp - lastTempTest;
//                    lastTempTest = currentTemp;
//                    if (timeDiff > 200){
//                        Log.e("receive data--------->","序号：" + index + "   延时"+ timeDiff + "+++++++++++++++++++++++++++" );
//                    }else {
//                        Log.e("receive data--------->","序号：" + index + "   延时"+ timeDiff);
//                    }
//                }

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                float value = (float)weight / 100;
                if (value < 1){
                    value = 0;
                }
                if (value > 150){//解决传感器第一次启动  重量异常波动问题
                    return;
                }

                cacheQueue.offer(new CacheBean(firstTemp,value));
//                if (lastValue >= 0){
//                    cacheQueue.offer(new CacheBean((firstTemp + lastTemp)/2 ,(value + lastValue)/2));
//                }
//                lastValue = value;
                lastTemp = firstTemp;
//                String weightStr = decimalFormat.format(value);
//                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_READ_DATA, new BleBean(address, rssi, weightStr)));
                if (lastTimestamp <= 0){
                    lastTimestamp = System.currentTimeMillis();
                }
                long currentTimestamp = System.currentTimeMillis();
                BleDeviceInfoManager.getInstance().insert(rssi,Math.abs(currentTimestamp-lastTimestamp),currentTimestamp);//插入蓝牙信息数据
                lastTimestamp = currentTimestamp;
            }else if (msg.length() == 17){
                int battery = Integer.valueOf(msg.substring(13, 15),16);
                Global.BLE_BATTERY = battery;
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_READ_DEVICE, battery));
            }
        }
    };
    public void startDealThread(){
        if (mSendThread == null){
            isTimeFlag = true;
            firstTemp = 0;
            mSendThread = new SendThread();
            mSendThread.start();
        }
    }
    public void stopDealThread(){
        if (mSendThread != null) {
            mSendThread.interrupt();
            isTimeFlag = false;
            mSendThread = null;
        }
    }

    /**
     * 发送数据的线程
     */
    private class SendThread extends Thread {

        private long lastTempTest = 0;

        @Override
        public void run() {
            super.run();
            while (isTimeFlag) {
                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                if (Global.isOpenTest){
//                    long currentTemp = System.currentTimeMillis();
//                    long timeDiff = currentTemp - lastTempTest;
//                    lastTempTest = currentTemp;
//                    if (timeDiff > 200){
//                        Log.e("receive data--------->","序号： 延时"+ timeDiff + "+++++++++++++++++++++++++++" );
//                    }else {
//                        Log.e("receive data--------->","序号： 延时"+ timeDiff);
//                    }
//                }
                dealCache();
            }
        }

        private boolean isFetchData = true;
        private CacheBean cacheResult = null;
        public synchronized void dealCache() {
            if (cacheQueue != null && !cacheQueue.isEmpty()) {
                if (isFetchData){
                    cacheResult = cacheQueue.poll();
                }
//                Log.e("cacheQueue---->",cacheQueue.size()+"");
                if ((System.currentTimeMillis() - cacheResult.getTimestamp()) < 300){
                    isFetchData = false;
                }else {
                    isFetchData = true;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    String weightStr = decimalFormat.format(cacheResult.getValue());
                    android.util.Log.e("读取数据---->",weightStr);
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_READ_DATA, new BleBean(weightStr)));
                }
            }
        }
    }

    /**
     * Enable TXNotification  与固件建立连接
     *
     * @return
     */
    public void enableTXNotification(boolean isEnable) {
        for (String address : gattMap.keySet()) {
            BluetoothGatt gatt = gattMap.get(address).getGatt();
            BluetoothGattService RxService = gatt.getService(SERVICE_UUID);
            if (RxService == null) {
                closeGatt(address);
                return;
            }
            BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);  // 获取特征码
            if (TxChar == null) {
                closeGatt(address);
                return;
            }

            boolean isEnableNotification = gatt.setCharacteristicNotification(TxChar, isEnable);
            BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
            if (isEnable) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            } else {
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            }
            gatt.writeDescriptor(descriptor);  // 向蓝牙设备注册监听实现实时读取蓝牙设备的数据
        }
    }


    /**
     * 往蓝牙数据通道的写入数据
     *
     * @param address
     * @param value
     */
    public void writeRXCharacteristic(String address, byte[] value) {
        if (gattMap == null || TextUtils.isEmpty(address) || gattMap.get(address) == null) return;
        BluetoothGatt gatt = gattMap.get(address).getGatt();

        BluetoothGattService RxService = gatt.getService(SERVICE_UUID);
        if (RxService == null) {
//            Toast.makeText(MyApplication.getInstance(), "设备不支持GATT", Toast.LENGTH_SHORT).show();
//            closeGatt(address);
            return;
        }
        BluetoothGattCharacteristic rxChar = RxService.getCharacteristic(RX_CHAR_UUID);
        if (rxChar == null) {
//            Toast.makeText(MyApplication.getInstance(), "设备不支持GATT", Toast.LENGTH_SHORT).show();
//            closeGatt(address);
            return;
        }
        rxChar.setValue(value);
        boolean status = gatt.writeCharacteristic(rxChar);
    }
    public void clearPairAllDevices(){
        if (mBluetoothAdapter != null){
            Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : bondedDevices) {
                unpairDevice(device);
            }
        }
    }
    public  void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
    public void closeGatt(String address) {
        if (gattMap.containsKey(address)) {
            gattMap.get(address).getGatt().close();
            gattMap.remove(address);
        }

    }
    public void disconnectGatt() {
       if (mBluetoothGatt != null){
           mBluetoothGatt.disconnect();
       }

    }
    public void sendReadDeviceInfoCmd(){
        int a = 0x1A;
        int b = 0x04 | 0x30;
        int c = 0x00;
        int d = 0xFF - (a + b + c) + 1;
        String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
        writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
    }
    public void clearConnectedStatus(){
        Global.isConnected = false;
        Global.ConnectedAddress = null;
        Global.ConnectedName = null;
        Global.ConnectStatus = "unconnected";
    }
    public void close() {
        for (Map.Entry<String, BleBean> entry : gattMap.entrySet()) {
            entry.getValue().getGatt().close();
        }
        gattMap.clear();
        stopScanBle();
    }
}
