package com.blackkara.dt157

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import com.blackkara.dt157.events.BluetoothDeviceFoundEvent
import com.blackkara.dt157.events.BluetoothDevicesFoundEvent
import org.greenrobot.eventbus.EventBus

class Dt157BluetoothScanListener : ScanCallback(){
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        val event = BluetoothDeviceFoundEvent(result.device)
        EventBus.getDefault().post(event)
        Log.i(Constants.TAG, "Device found : ${result.device}")
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        super.onBatchScanResults(results)
        val devices = ArrayList<BluetoothDevice>()
        for (result in results) {
            devices.add(result.device)
            Log.i(Constants.TAG, "Device found : ${result.device}")
        }

        val event = BluetoothDevicesFoundEvent(devices)
        EventBus.getDefault().post(event)
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        Log.e(Constants.TAG, "ScanCallback error code: " + errorCode)
    }
}