package com.blackkara.dt157.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blackkara.dt157.Constants
import com.blackkara.dt157.Dt157BluetoothGattCallback
import com.blackkara.dt157.Dt157BluetoothScanListener
import com.blackkara.dt157.R
import com.blackkara.dt157.cem.BaseIcttDataObj
import com.blackkara.dt157.cem.BleMeterDataClass
import com.blackkara.dt157.Dt157DataHandler
import com.blackkara.dt157.events.BluetoothDeviceFoundEvent
import com.blackkara.dt157.events.BluetoothDeviceSelectedEvent
import com.blackkara.dt157.events.BluetoothDevicesFoundEvent
import kotlinx.android.synthetic.main.fragment_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SearchFragment : Fragment() {
    private val mTextScan: String by lazy { getString(R.string.scan) }
    private val mTextStop: String by lazy { getString(R.string.stop) }
    private val mHandler = Handler()
    private var mScanning = false
    private var mScanResultsFragment: ScanResultsFragment? = null
    private var mData : Dt157DataHandler? = null
    private var mMeter : BleMeterDataClass? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private lateinit var mBluetoothManager: BluetoothManager
    private lateinit var mBluetoothScanner: BluetoothLeScanner
    private lateinit var mBluetoothScanSettings: ScanSettings
    private lateinit var mBluetoothScanFilters: MutableList<ScanFilter>
    private var mBluetoothScanListener: Dt157BluetoothScanListener? = null
    private var mBluetoothGattCallback: Dt157BluetoothGattCallback? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mBluetoothManager = context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mBluetoothScanner = mBluetoothAdapter!!.bluetoothLeScanner
        mBluetoothScanFilters = ArrayList()
        mBluetoothScanSettings  = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        mBluetoothScanListener = Dt157BluetoothScanListener()
        mBluetoothGattCallback = Dt157BluetoothGattCallback()

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

        mData = Dt157DataHandler.getInstance()
        mData?.setOnBluetoothDataCallback { var1 -> mMeter?.LoadData(var1) }

        mMeter = BleMeterDataClass.getInstance()
        mMeter?.setOnDataCallback(object : BleMeterDataClass.BleMeterDataCallback {
            override fun onMeterData(var1: BaseIcttDataObj?) {
                var a = 1
            }
        })
    }

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.fragment_search, container, false)

    private fun addDevice(device: BluetoothDevice){
        mScanResultsFragment?.addDevice(device)
    }

    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            mHandler.postDelayed({
                mScanning = false
                buttonScan.text = mTextScan
                mBluetoothScanner.stopScan(mBluetoothScanListener)
            }, Constants.SCAN_PERIOD)
            mBluetoothScanner.startScan(mBluetoothScanFilters, mBluetoothScanSettings, mBluetoothScanListener)
        } else {
            mBluetoothScanner.stopScan(mBluetoothScanListener)
        }
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BluetoothDeviceSelectedEvent) {
        Log.d(Constants.TAG, "Device selected : ${event.device.address}")
        connectToDevice(event.device)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BluetoothDeviceFoundEvent) {

    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BluetoothDevicesFoundEvent) {

    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (mBluetoothGatt == null) {
            mBluetoothGatt = device.connectGatt(
                    context.applicationContext,
                    false,
                    mBluetoothGattCallback)
            scanLeDevice(false)// will stop after first device detection
        }
    }
}
