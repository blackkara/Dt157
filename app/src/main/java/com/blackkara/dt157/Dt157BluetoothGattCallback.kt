package com.blackkara.dt157

import android.bluetooth.*
import android.util.Log

/**
 * Created by blackkara on 2/21/2018.
 */
class Dt157BluetoothGattCallback(private var listener: Dt157BluetoothGattCallback.Listener) : BluetoothGattCallback() {

    interface Listener{
        fun onBytesReceived(bytes: ByteArray)
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                Log.i(Constants.TAG, "GATT state changed : STATE_CONNECTED")
                gatt.discoverServices()
            }
            BluetoothProfile.STATE_DISCONNECTED -> Log.e(Constants.TAG, "GATT state changed : STATE DISCONNECTED")
            else -> Log.e(Constants.TAG, "GATT state changed : OTHER")
        }
    }

    // Where the magic happens
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        gatt.services.forEach {
            val serviceUUID = it.uuid
            Log.d(Constants.TAG, "SERVICE $serviceUUID")
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

    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        listener.onBytesReceived(characteristic!!.value)
    }
}