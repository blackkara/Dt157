package com.blackkara.dt157

import android.bluetooth.*
import android.util.Log

class Dt157BluetoothGatt(private var listener: Dt157BluetoothGatt.Listener) : BluetoothGattCallback() {
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
            Log.d(Constants.TAG, "SERVICE {${it.uuid}}")
            it.characteristics.forEach {
                if(it.properties == 16){
                    gatt.setCharacteristicNotification(it, true)
                    it.descriptors.forEach {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(it)
                    }
                }
            }
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        listener.onBytesReceived(characteristic!!.value)
    }
}