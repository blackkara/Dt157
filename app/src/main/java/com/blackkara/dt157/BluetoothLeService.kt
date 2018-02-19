package com.blackkara.dt157

import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BluetoothLeService : Service() {
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBluetoothGatt: BluetoothGatt
    private lateinit var mBluetoothManager: BluetoothManager
    private lateinit var mBluetoothDeviceAddress: String


    private val STATE_DISCONNECTED = 0
    private val STATE_CONNECTING = 1
    private val STATE_CONNECTED = 2
    private var mConnectionState = STATE_DISCONNECTED

    companion object {
        val ACTION_GATT_CONNECTED = "com.blackkara.dt157.ACTION_GATT_CONNECTED"
        val ACTION_GATT_DISCONNECTED = "com.blackkara.dt157.ACTION_GATT_DISCONNECTED"
        val ACTION_GATT_SERVICES_DISCOVERED = "com.blackkara.dt157.ACTION_GATT_SERVICES_DISCOVERED"
        val ACTION_DATA_AVAILABLE = "com.blackkara.dt157.ACTION_DATA_AVAILABLE"
        val EXTRA_DATA = "com.blackkara.dt157.EXTRA_DATA"
    }


    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }


}
