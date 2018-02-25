package com.blackkara.dt157

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult

class Dt157BluetoothScanListener(private var listener: Dt157BluetoothScanListener.Listener) : ScanCallback(){
    interface Listener{
        fun onBluetoothDevicesFound(device: Array<BluetoothDevice>)
        fun onScanFailed(errorCode: Int)
    }

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        listener.onBluetoothDevicesFound(arrayOf(result.device))
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        super.onBatchScanResults(results)
        val devices = ArrayList<BluetoothDevice>()
        results.mapTo(devices) { it.device }

        listener.onBluetoothDevicesFound(devices.toTypedArray())
    }

    override fun onScanFailed(errorCode: Int) {
        listener.onScanFailed(errorCode)
    }
}