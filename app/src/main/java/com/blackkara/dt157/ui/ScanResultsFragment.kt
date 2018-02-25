package com.blackkara.dt157.ui


import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blackkara.dt157.R
import kotlinx.android.synthetic.main.fragment_scan.*


class ScanResultsFragment : Fragment(), ScanResultsAdapter.Listener {
    private lateinit var mScanResults: ArrayList<BluetoothDevice>
    private lateinit var mScanResultsAdapter: ScanResultsAdapter

    companion object {
        private val PARAM = "PARAM_RESULT"
        fun newInstance(result: ArrayList<BluetoothDevice>): ScanResultsFragment {
            val fragment = ScanResultsFragment()
            val arguments = Bundle()
            arguments.putParcelableArrayList(PARAM, result)
            fragment.arguments = arguments
            return fragment
        }

        fun newInstance(): ScanResultsFragment {
            return ScanResultsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_scan, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if(arguments != null && arguments.containsKey(PARAM)){
            mScanResults = arguments.getParcelableArrayList(PARAM)
        } else{
            mScanResults = ArrayList<BluetoothDevice>()
        }

        mScanResultsAdapter = ScanResultsAdapter(mScanResults, this)
        recyclerViewScanResults.adapter = mScanResultsAdapter
    }

    override fun onDeviceSelected(device: BluetoothDevice) {

    }

    fun addDevice(device: BluetoothDevice){
        mScanResultsAdapter.addDevice(device)
    }
}