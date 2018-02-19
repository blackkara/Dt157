package com.blackkara.dt157


import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class ScanResultsFragment : Fragment(), ScanResultsAdapter.Listener {
    private lateinit var mListener: Listener
    private lateinit var mRecyclerViewScanResults: RecyclerView
    private lateinit var mScanResults: ArrayList<BluetoothDevice>
    private lateinit var mScanResultsAdapter: ScanResultsAdapter

    interface Listener{
        fun onDeviceSelected(device: BluetoothDevice)
    }

    companion object {
        private val PARAM = "PARAM_RESULT"
        fun newInstance(result: ArrayList<BluetoothDevice>): ScanResultsFragment{
            val fragment = ScanResultsFragment()
            val arguments = Bundle()
            arguments.putParcelableArrayList(PARAM, result)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mScanResults = arguments.getParcelableArrayList(PARAM)

        val layout = inflater.inflate(R.layout.fragment_scan, container, false)
        mRecyclerViewScanResults = layout.findViewById(R.id.recyclerViewScanResults)
        mScanResultsAdapter = ScanResultsAdapter(mScanResults, this)
        return layout
    }

    override fun onDeviceSelected(device: BluetoothDevice) {
        mListener.onDeviceSelected(device)
    }

}