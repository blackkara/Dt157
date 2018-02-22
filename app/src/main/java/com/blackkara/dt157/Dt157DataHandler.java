package com.blackkara.dt157;

import android.util.Log;

import com.blackkara.dt157.cem.ByteArrayList;

/**
 * Created by blackkara on 2/21/2018.
 */

public class Dt157DataHandler {
    private static Dt157DataHandler icttHandle;
    private Dt157DataHandler.BluetoothDataCallback callback = null;
    private byte dataStartByte = -86;
    private int datalength = 11;
    private boolean debug = true;
    private ByteArrayList inputbuffer = new ByteArrayList();
    private boolean isDataLogger;

    public static Dt157DataHandler getInstance() {
        Class var2 = Dt157DataHandler.class;
        synchronized(Dt157DataHandler.class){}

        Dt157DataHandler var1;
        try {
            if(icttHandle == null) {
                icttHandle = new Dt157DataHandler();
            }

            var1 = icttHandle;
        } finally {
            ;
        }

        return var1;
    }

    private void protocaldata() {
        if(this.inputbuffer.size() >= 6) {
            if(((Byte)this.inputbuffer.get(0)).byteValue() == this.dataStartByte) {
                if(((Byte)this.inputbuffer.get(1)).byteValue() == 11) {
                    this.datalength = 11;
                } else if(((Byte)this.inputbuffer.get(1)).byteValue() == 9) {
                    this.datalength = 9;
                } else {
                    this.datalength = ((Byte)this.inputbuffer.get(1)).byteValue();
                    this.isDataLogger = true;
                }

                if(this.datalength <= 0) {
                    this.inputbuffer.clear();
                    return;
                }

                if(this.inputbuffer.size() >= this.datalength) {
                    byte[] var2 = this.inputbuffer.CopyTo(this.datalength);
                    this.protocaldata();
                    if(this.callback != null) {
                        this.callback.onCompleteBytes(var2);
                        this.inputbuffer.clear();
                    }
                }
            } else {
                while(this.inputbuffer.size() > 0) {
                    if(((Byte)this.inputbuffer.get(0)).byteValue() == this.dataStartByte) {
                        return;
                    }

                    this.inputbuffer.remove(0);
                }
            }
        }

    }

    private void showLog(String var1) {
        if(this.debug) {
            Log.e("Dt157DataHandler====", var1);
        }

    }

    public void AddBytes(byte[] var1) {
        this.AddBytes(var1, var1.length);
    }

    private void AddBytes(byte[] var1, int var2) {
        this.inputbuffer.AddRange(var1, var2);
        if(this.inputbuffer.get(0) == null) {
            this.inputbuffer.clear();
        } else {
            this.protocaldata();
        }
    }

    public void setOnBluetoothDataCallback(Dt157DataHandler.BluetoothDataCallback var1) {
        this.callback = var1;
    }

    public interface BluetoothDataCallback {
        void onCompleteBytes(byte[] var1);
    }
}
