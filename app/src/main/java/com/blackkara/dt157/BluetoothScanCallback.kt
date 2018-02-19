package com.blackkara.dt157

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log

/**
 * Created by blackkara on 2/18/2018.
 */
class BluetoothScanCallback(private var results :HashMap<String, BluetoothDevice>):ScanCallback(){

    private fun addScanResult(result: ScanResult) {
        val device = result.device
        val deviceAddress = device.address
        results.put(deviceAddress, device)
        Log.d(Constants.TAG, "Device found[${device.address}] ${device.name}")
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        result?.let {
            addScanResult(it)
        }
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        super.onBatchScanResults(results)
        results?.let {
            it.forEach {
                addScanResult(it)
            }
        }
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        Log.d(Constants.TAG, "Scan failed with code : " + errorCode)
    }
}