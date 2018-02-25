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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blackkara.dt157.*
import com.blackkara.dt157.cem.BaseIcttDataObj
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private val mTextScan: String by lazy { getString(R.string.scan) }
    private val mTextStop: String by lazy { getString(R.string.stop) }
    private val mHandler = Handler()
    private var mScanning = false
    private var mScanResultsFragment: ScanResultsFragment? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private lateinit var mBluetoothManager: BluetoothManager
    private lateinit var mBluetoothScanner: BluetoothLeScanner
    private lateinit var mBluetoothScanSettings: ScanSettings
    private lateinit var mBluetoothScanFilters: MutableList<ScanFilter>

    private var mBluetoothScanListener: Dt157BluetoothScanListener? = null
    private var mBluetoothGattCallback: Dt157BluetoothGattCallback? = null
    private var mBluetoothBytesHandler: Dt157BluetoothBytesHandler? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mBluetoothManager = context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mBluetoothScanner = mBluetoothAdapter!!.bluetoothLeScanner
        mBluetoothScanFilters = ArrayList()
        mBluetoothScanSettings  = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        mBluetoothScanListener = Dt157BluetoothScanListener(mBluetooth157ScanListenerCallback)
        mBluetoothGattCallback = Dt157BluetoothGattCallback(mBluetoothGattCallbackListener)
        mBluetoothBytesHandler = Dt157BluetoothBytesHandler(mDt157BytesHandlerListener)

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
    }


    private var mBluetoothGattCallbackListener = object : Dt157BluetoothGattCallback.Listener {
        override fun onBytesReceived(bytes: ByteArray) {
            mBluetoothBytesHandler?.handleBytes(bytes)
        }
    }

    private var mDt157BytesHandlerListener = object : Dt157BluetoothBytesHandler.Listener {
        override fun onBytesCompleted(bytes: List<Byte>) {}
        override fun onDt157DataReady(value: BaseIcttDataObj) {}
    }

    private var mBluetooth157ScanListenerCallback = object :Dt157BluetoothScanListener.Listener {
        override fun onBluetoothDevicesFound(device: Array<BluetoothDevice>) {}
        override fun onScanFailed(errorCode: Int) {}
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
