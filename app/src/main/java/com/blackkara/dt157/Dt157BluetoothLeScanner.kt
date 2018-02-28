package com.blackkara.dt157

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult

class Dt157BluetoothLeScanner(
        private var showOnlyNamedDevices : Boolean = false,
        private var listener: Dt157BluetoothLeScanner.Listener) : ScanCallback(){

    interface Listener{
        fun onBluetoothDevicesFound(device: Array<BluetoothDevice>)
        fun onScanFailed(errorCode: Int)
    }

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        if(isDeviceCandidate(result.device)){
            listener.onBluetoothDevicesFound(arrayOf(result.device))
        }
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        super.onBatchScanResults(results)
        val devices = ArrayList<BluetoothDevice>()
        results.forEach {
            if(isDeviceCandidate(it.device)){
                devices.add(it.device)
            }
        }

        listener.onBluetoothDevicesFound(devices.toTypedArray())
    }

    override fun onScanFailed(errorCode: Int) {
        listener.onScanFailed(errorCode)
    }


    private fun isDeviceCandidate(device: BluetoothDevice): Boolean{
        if(showOnlyNamedDevices and !deviceHasName(device)){
            return false
        }

        return true
    }

    private fun deviceHasName(device: BluetoothDevice): Boolean = !device.name.isNullOrEmpty()
}