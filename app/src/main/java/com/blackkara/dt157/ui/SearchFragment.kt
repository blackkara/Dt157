package com.blackkara.dt157.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blackkara.dt157.*
import com.blackkara.dt157.cem.BaseIcttDataObj
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(), ScanResultsAdapter.Listener {
    private val mTextScan: String by lazy { getString(R.string.scan) }
    private val mTextStop: String by lazy { getString(R.string.stop) }
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothScanner: BluetoothLeScanner? = null
    private var mBluetoothScanSettings: ScanSettings? = null
    private var mBluetoothScanFilters: MutableList<ScanFilter>? = null
    private var mDt157BluetoothGatt: Dt157BluetoothGatt? = null
    private var mDt157BluetoothLeScanner: Dt157BluetoothLeScanner? = null
    private var mDt157BluetoothBytesHandler: Dt157BluetoothBytesHandler? = null
    private val mHandler = Handler()
    private var mScanning = false

    private lateinit var mScanResults: ArrayList<BluetoothDevice>
    private lateinit var mScanResultsAdapter: ScanResultsAdapter


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mBluetoothManager = context.applicationContext
                .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager!!.adapter
        mBluetoothScanner = mBluetoothAdapter!!.bluetoothLeScanner
        mBluetoothScanFilters = ArrayList()
        mBluetoothScanSettings  = ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_LATENCY).build()
        mDt157BluetoothLeScanner = Dt157BluetoothLeScanner(true, mBluetooth157ScanListenerCallback)
        mDt157BluetoothGatt = Dt157BluetoothGatt(mBluetoothGattCallbackListener)
        mDt157BluetoothBytesHandler = Dt157BluetoothBytesHandler(mDt157BytesHandlerListener)


        mScanResults = arrayListOf()
        mScanResultsAdapter = ScanResultsAdapter(mScanResults, this)
        recyclerViewScanResults.adapter = mScanResultsAdapter

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
    }

    private var mBluetoothGattCallbackListener = object : Dt157BluetoothGatt.Listener {
        override fun onBytesReceived(bytes: ByteArray) {
            mDt157BluetoothBytesHandler?.handleBytes(bytes)
        }
    }

    private var mDt157BytesHandlerListener = object : Dt157BluetoothBytesHandler.Listener {
        override fun onBytesCompleted(bytes: List<Byte>) {}
        override fun onDt157DataReady(value: BaseIcttDataObj) {}
    }

    private var mBluetooth157ScanListenerCallback = object : Dt157BluetoothLeScanner.Listener {
        override fun onBluetoothDevicesFound(device: Array<BluetoothDevice>) {
            device.forEach {
                mScanResultsAdapter.addDevice(it)
            }
        }
        override fun onScanFailed(errorCode: Int) {
            Log.d(Constants.TAG, "Something went wrong when scanning devices")
        }
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
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.fragment_search, container, false)

    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            mHandler.postDelayed({
                mScanning = false
                buttonScan.text = mTextScan
                mBluetoothScanner?.stopScan(mDt157BluetoothLeScanner)
            }, Constants.SCAN_PERIOD)
            mBluetoothScanner?.startScan(mBluetoothScanFilters, mBluetoothScanSettings, mDt157BluetoothLeScanner)
        } else {
            mBluetoothScanner?.stopScan(mDt157BluetoothLeScanner)
        }
    }

    override fun onDeviceSelected(device: BluetoothDevice) {
        if (mBluetoothGatt == null) {
            mBluetoothGatt = device.connectGatt(context.applicationContext, false, mDt157BluetoothGatt)
            scanLeDevice(false)
        }
    }
}
