package com.blackkara.dt157.cem;

import android.util.Log;

/**
 * Created by blackkara on 2/21/2018.
 */

public class BleDataHandleClass {
    private static BleDataHandleClass icttHandle;
    private BleDataHandleClass.BluetoothDataCallback callback = null;
    private byte dataStartByte = -86;
    private int datalength = 11;
    private boolean debug = true;
    private String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private ByteArrayList inputbuffer = new ByteArrayList();
    private boolean isDataLogger;
    private int syncsize = 0;

    private String bytes2HexString(byte[] var1) {
        String var2 = "";

        for(int var3 = 0; var3 < var1.length; ++var3) {
            String var4 = Integer.toHexString(255 & var1[var3]);
            if(var4.length() == 1) {
                var4 = '0' + var4;
            }

            var2 = var2 + var4.toUpperCase();
        }

        return var2;
    }

    public static BleDataHandleClass getInstance() {
        Class var2 = BleDataHandleClass.class;
        synchronized(BleDataHandleClass.class){}

        BleDataHandleClass var1;
        try {
            if(icttHandle == null) {
                icttHandle = new BleDataHandleClass();
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
            Log.e("BleDataHandleClass====", var1);
        }

    }

    public void AddBytes(byte[] var1) {
        this.AddBytes(var1, var1.length);
    }

    public void AddBytes(byte[] var1, int var2) {
        this.inputbuffer.AddRange(var1, var2);
        if(this.inputbuffer.get(0) == null) {
            this.inputbuffer.clear();
        } else {
            this.protocaldata();
        }
    }

    public String byteToHexString(byte var1) {
        int var2 = var1;
        if(var1 < 0) {
            var2 = var1 + 256;
        }

        int var3 = var2 / 16;
        int var4 = var2 % 16;
        return this.hexDigits[var3] + this.hexDigits[var4];
    }

    public void setOnBluetoothDataCallback(BleDataHandleClass.BluetoothDataCallback var1) {
        this.callback = var1;
    }

    public interface BluetoothDataCallback {
        void onCompleteBytes(byte[] var1);
    }
}
