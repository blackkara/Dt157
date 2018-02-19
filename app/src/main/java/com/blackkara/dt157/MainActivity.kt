package com.blackkara.dt157

import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.SimpleExpandableListAdapter


class MainActivity : AppCompatActivity(), ScanResultsFragment.Listener {

    val mTextScan: String by lazy {
        getString(R.string.scan)
    }

    val mTextScanning: String by lazy {
        getString(R.string.scanning)
    }

    val mTextStop: String by lazy {
        getString(R.string.stop)
    }

    override fun onDeviceSelected(device: BluetoothDevice) {

    }

    private val TAG = "DT-157"
    private val REQUEST_ENABLE_BT = 2000
    private val SCAN_PERIOD = 1000 * 10L

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mBluetoothScanner: BluetoothLeScanner? = null
    private var mBluetoothScanSettings: ScanSettings? = null
    private var mBluetoothScanFilters: MutableList<ScanFilter>? = null
    private lateinit var mBluetoothManager: BluetoothManager
    private val mHandler = Handler()

    private lateinit var mButtonScan: Button
    private var mScanning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!hasBluetooth()){
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter

        mButtonScan = findViewById(R.id.buttonScan)
        mButtonScan.setOnClickListener {
            if(!mScanning){
                mScanning = true
                mButtonScan.text = mTextScanning
                scanLeDevice(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handleBluetooth()
    }

    override fun onPause() {
        if(mBluetoothAdapter!= null && mBluetoothAdapter!!.isEnabled){
            scanLeDevice(false)
        }
        super.onPause()
    }

    override fun onDestroy() {
        if(mBluetoothGatt != null){
            mBluetoothGatt!!.close()
            mBluetoothGatt = null
        }
        super.onDestroy()
    }

    private fun hasBluetooth(): Boolean{
        return packageManager.hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)
    }

    private fun handleBluetooth(){
        mBluetoothAdapter?.isEnabled.let {
            if (Build.VERSION.SDK_INT >= 21) {
                mBluetoothScanner = mBluetoothAdapter!!.bluetoothLeScanner
                mBluetoothScanSettings  = ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                mBluetoothScanFilters = ArrayList<ScanFilter>()
            }

            scanLeDevice(true)
            return
        }

        //val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)

    }

    private val mScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.i(TAG, callbackType.toString())
            Log.i(TAG, result.toString())
            val btDevice = result!!.device
            if (btDevice.name.equals("DT-157 CTT")){
                connectToDevice(btDevice)
            }
            //connectToDevice(btDevice)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            if (results != null) {
                for (sr in results) {
                    Log.i(TAG, sr.toString())
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "Error Code: " + errorCode)
        }
    }

    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            mHandler.postDelayed({
                mBluetoothScanner!!.stopScan(mScanCallback)
            }, SCAN_PERIOD)
            mBluetoothScanner!!.startScan(mBluetoothScanFilters, mBluetoothScanSettings, mScanCallback)
        } else {
            mBluetoothScanner!!.stopScan(mScanCallback)
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (mBluetoothGatt == null) {
            mBluetoothGatt = device.connectGatt(this, false, gattCallback)
            scanLeDevice(false)// will stop after first device detection
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.i(TAG, "onConnectionStateChange Status: " + status)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.i(TAG, "gattCallback STATE_CONNECTED")
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> Log.e(TAG, "gattCallback STATE_DISCONNECTED")
                else -> Log.e(TAG, "gattCallback STATE_OTHER")
            }

        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val services = gatt.services
            Log.i(TAG, "onServicesDiscovered " + services.toString())
            displayGattServices(gatt, services)

            //gatt.readCharacteristic(services[1].characteristics[0])
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt,
                                          characteristic: BluetoothGattCharacteristic, status: Int) {
            //
          //  val value = readData(characteristic.value)
            val value = SampleGattAttributes.byteArrayToHexString(characteristic.value)
           // val value2 = SampleGattAttributes.
            Log.i(TAG, value)
            //gatt.disconnect()
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)
            val readValue = descriptor!!.value
            val value = SampleGattAttributes.byteArrayToHexString(readValue)
            Log.i(TAG, value)
        }
    }


    private lateinit var mGattCharacteristics: ArrayList<ArrayList<BluetoothGattCharacteristic>>

    private val LIST_NAME = "NAME"
    private val LIST_UUID = "UUID"

    private fun displayGattServices(gatt: BluetoothGatt, gattServices: MutableList<BluetoothGattService>){
        var uuid: String? = null
        val unknownServiceString = resources.getString(R.string.unknown_service)
        val unknownCharaString = resources.getString(R.string.unknown_characteristic)
        val gattServiceData = ArrayList<HashMap<String, String>>()
        val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String>>>()
        mGattCharacteristics = ArrayList<ArrayList<BluetoothGattCharacteristic>>()
        for (gattService in gattServices) {

            val currentServiceData = HashMap<String, String>()
            uuid = gattService.getUuid().toString()
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString))
            currentServiceData.put(LIST_UUID, uuid)
            gattServiceData.add(currentServiceData)

            val gattCharacteristicGroupData = ArrayList<HashMap<String, String>>()
            val gattCharacteristics = gattService.getCharacteristics()
            val charas = ArrayList<BluetoothGattCharacteristic>()
            // Loops through available Characteristics.
            for (gattCharacteristic in gattCharacteristics) {

                Log.d(Constants.TAG, "Characteristic address : ${gattCharacteristic.uuid} "
                        +"writable ${gattCharacteristic.writeType != BluetoothGatt.GATT_WRITE_NOT_PERMITTED} "
                        +"encrypted ${gattCharacteristic.permissions == BluetoothGatt.GATT_READ_NOT_PERMITTED}")


                for (descriptor in gattCharacteristic.descriptors){
                    gatt.writeDescriptor(descriptor)
                }

                gatt.readCharacteristic(gattCharacteristic)
                gatt.setCharacteristicNotification(gattCharacteristic, true)
                
                charas.add(gattCharacteristic)
                val currentCharaData = HashMap<String, String>()
                uuid = gattCharacteristic.getUuid().toString()
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString))
               currentCharaData.put(LIST_UUID, uuid)
                gattCharacteristicGroupData.add(currentCharaData)

                for(desc in gattCharacteristic.descriptors){
                    desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                    gatt.writeDescriptor(desc);
                }
            }
            mGattCharacteristics.add(charas)
            gattCharacteristicData.add(gattCharacteristicGroupData)
        }



        val gattServiceAdapter = SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                arrayOf(LIST_NAME, LIST_UUID),
                intArrayOf(android.R.id.text1, android.R.id.text2),
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                arrayOf(LIST_NAME, LIST_UUID),
                intArrayOf(android.R.id.text1, android.R.id.text2)
        )

       // gatt_services_list!!.setAdapter(gattServiceAdapter)
    }

    private fun readData(data: ByteArray): String{
        var value = ""
        if (data != null && data!!.size > 0) {
            val stringBuilder = StringBuilder(data!!.size)
            for (byteChar in data!!)
                stringBuilder.append(String.format("%02X ", byteChar))
            value = String(data!!) + "\n" +
                    stringBuilder.toString()
        }

        return value
    }

}
