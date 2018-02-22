package com.blackkara.dt157.ui

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blackkara.dt157.Constants
import com.blackkara.dt157.R
import kotlinx.android.synthetic.main.scan_result_item.view.*

/**
 * Created by blackkara on 2/18/2018.
 */
class ScanResultsAdapter(
        private var devices: MutableList<BluetoothDevice>,
        private var listener: Listener) : RecyclerView.Adapter<ScanResultsAdapter.ScanResultsViewHolder>() {

    interface Listener{
        fun onDeviceSelected(device: BluetoothDevice)
    }

    public fun addDevice(device: BluetoothDevice){
        if(!devices.contains(device)){
            devices.add(device)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: ScanResultsViewHolder, position: Int) {
        val device = devices[position]
        holder.bind(device, listener)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanResultsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_result_item, parent,false)
        return ScanResultsViewHolder(view)
    }

    class ScanResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(device: BluetoothDevice, listener: Listener) = with(itemView){
            itemView.textViewDeviceName.text = if(device.name != null) device.name else "UNKNOWN DEVICE"
            itemView.textViewDeviceAddress.text = if(device.address != null) device.address else "UNKNOWN ADDRESS"

            setOnClickListener{
                if(device.address != null){
                    listener.onDeviceSelected(device)
                } else{
                    Log.d(Constants.TAG, "Not possible to connect")
                }
            }
        }
    }
}