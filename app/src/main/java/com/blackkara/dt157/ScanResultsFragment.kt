package com.blackkara.dt157


import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_scan.*


class ScanResultsFragment : Fragment(), ScanResultsAdapter.Listener {
    private lateinit var mListener: Listener
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_scan, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mScanResults = arguments.getParcelableArrayList(PARAM)
        mScanResultsAdapter = ScanResultsAdapter(mScanResults, this)
        recyclerViewScanResults.adapter = mScanResultsAdapter
    }

    override fun onDeviceSelected(device: BluetoothDevice) {
        mListener.onDeviceSelected(device)
    }
}