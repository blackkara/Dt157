package com.blackkara.dt157

import com.blackkara.dt157.cem.BaseIcttDataObj
import com.blackkara.dt157.cem.ICTTObj

class Dt157BluetoothBytesHandler(private var listener: Listener) {

    companion object {
        const val START_BYTE : Byte = -86
    }

    interface Listener{
        fun onBytesCompleted(bytes: List<Byte>)
        fun onDt157DataReady(value: BaseIcttDataObj)
    }

    private var mDataLength = 11
    private var mData = mutableListOf<Byte>()

    private var mSecondCode: Int = 0
    private var mSixCode: Int = 0

    fun handleBytes(bytes: ByteArray){
        bytes.forEach {
            mData.add(it)
        }

        if(mData.first() == null){
            mData.clear()
        } else{
            process()
        }
    }

    private fun process(){
        if(mData.size >= 6){
            if(mData.first() == START_BYTE){
                val length = mData[1].toInt()
                mDataLength = when (length) {
                    11 -> 11
                    9 -> 9
                    else -> length
                }

                if(mDataLength <= 0){
                    mData.clear()
                    return
                }

                if(mData.size >= mDataLength){
                    val data = copy(mDataLength)
                    process()

                    listener.onBytesCompleted(data)
                    mData.clear()
                }
            }
        }
    }

    private fun copy(length: Int) : MutableList<Byte>{
        val returnList = mutableListOf<Byte>()
        val newList = mutableListOf<Byte>()

        for(x in 0..mData.size){
            if(x <= length - 1){
                returnList.add(mData[x])
            } else{
                newList.add(mData[x])
            }
        }

        mData = newList
        return returnList
    }

    private fun processData(bytes: List<Byte>){
        mSecondCode = Dt157Util.getShort(bytes[1])
        mSixCode = Dt157Util.getShort(bytes[6])

        var d : ICTTObj? = null

        if(mSecondCode == 11 && mSixCode == 0){
            d = ICTTObj(bytes.toByteArray())
        }

        listener.onDt157DataReady(d as BaseIcttDataObj)
    }
}