package com.blackkara.dt157

import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blackkara.dt157.Constants.TAG
import com.blackkara.dt157.cem.BaseIcttDataObj
import com.blackkara.dt157.cem.BleDataHandleClass
import com.blackkara.dt157.cem.BleMeterDataClass
import com.blackkara.dt157.events.BluetoothDeviceSelectedEvent
import kotlinx.android.synthetic.main.fragment_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SearchFragment : Fragment() {

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    private val mTextScan: String by lazy { getString(R.string.scan) }
    private val mTextStop: String by lazy { getString(R.string.stop) }

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private lateinit var mBluetoothScanner: BluetoothLeScanner
    private lateinit var mBluetoothScanSettings: ScanSettings
    private lateinit var mBluetoothScanFilters: MutableList<ScanFilter>
    private lateinit var mBluetoothManager: BluetoothManager
    private val mHandler = Handler()
    private var mScanning = false

    private var mScanResultsFragment: ScanResultsFragment? = null
    
    var mData : BleDataHandleClass? = null
    var mMeter : BleMeterDataClass? = null

    private fun showSearchResults(){
        val tag = ScanResultsFragment::class.java.simpleName
        val fragmentManager = activity.supportFragmentManager

        val searchResultsFragment = fragmentManager.findFragmentByTag(tag)
        if(searchResultsFragment == null){
            mScanResultsFragment = ScanResultsFragment.newInstance()
            val transaction = fragmentManager.beginTransaction()
            transaction.add(R.id.frameLayoutScanResults, mScanResultsFragment, tag)
            transaction.commit()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mBluetoothManager = context.applicationContext.
                getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mBluetoothScanner = mBluetoothAdapter!!.bluetoothLeScanner
        mBluetoothScanFilters = ArrayList<ScanFilter>()
        mBluetoothScanSettings  = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        buttonScan.setOnClickListener {
            if(!mScanning){
                mScanning = true
                buttonScan.text = mTextStop
            } else{
                mScanning = false
                buttonScan.text = mTextScan
            }

            scanLeDevice(mScanning)
        }

        showSearchResults()
        
        mData = BleDataHandleClass.getInstance()
        mData?.setOnBluetoothDataCallback(object : BleDataHandleClass.BluetoothDataCallback {
            override fun onCompleteBytes(var1: ByteArray?) {
                mMeter?.LoadData(var1)
            }
        })

        mMeter = BleMeterDataClass.getInstance()
        mMeter?.setOnDataCallback(object : BleMeterDataClass.BleMeterDataCallback {
            override fun onMeterData(var1: BaseIcttDataObj?) {
                var a = 1
            }
        })
    }


    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        mBluetoothAdapter?.let {
            if(it.isEnabled){
                scanLeDevice(false)
            }
        }
        super.onPause()
    }

    override fun onDestroy() {
        mBluetoothGatt?.let {
            it.close()
            mBluetoothGatt = null
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_search, container, false)
    }

    private fun addDevice(device: BluetoothDevice){
        mScanResultsFragment?.addDevice(device)
    }

    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            mHandler.postDelayed({
                mScanning = false
                buttonScan.text = mTextScan
                mBluetoothScanner.stopScan(mScanCallback)
            }, Constants.SCAN_PERIOD)
            mBluetoothScanner.startScan(mBluetoothScanFilters, mBluetoothScanSettings, mScanCallback)
        } else {
            mBluetoothScanner.stopScan(mScanCallback)
        }
    }

    private val mScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.i(Constants.TAG, result.toString())
            addDevice(result.device)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            if (results != null) {
                for (result in results) {
                    Log.i(Constants.TAG, result.toString())
                    addDevice(result.device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(Constants.TAG, "Error Code: " + errorCode)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BluetoothDeviceSelectedEvent) {
        Log.d(Constants.TAG, "Device selected : ${event.device.address}")
        connectToDevice(event.device)
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (mBluetoothGatt == null) {
            mBluetoothGatt = device.connectGatt(context.applicationContext, false, gattCallback)
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
            gatt.services.forEach {
                val serviceUUID = it.uuid
                Log.d(TAG, "SERVICE $serviceUUID")
                it.characteristics.forEach {
                    val characteristic = it
                    val properties = it.properties
                    if(properties == 16){
                        gatt.setCharacteristicNotification(characteristic, true)
                        it.descriptors.forEach {
                            val descriptor = it
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                            gatt.writeDescriptor(descriptor)
                        }
                    }
                }
            }
        }
    }
}
