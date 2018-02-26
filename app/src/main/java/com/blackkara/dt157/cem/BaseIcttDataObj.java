package com.blackkara.dt157.cem;

import java.io.Serializable;

/**
 * Created by blackkara on 2/21/2018.
 * This file has decompiled methods, so variables aren't meaningful
 */

public class BaseIcttDataObj implements Serializable {
    public DataObjMode dataObjMode;
    protected String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    protected String asciiToString(String var1) {
        StringBuffer var2 = new StringBuffer();
        String[] var3 = var1.split(",");

        for(int var4 = 0; var4 < var3.length; ++var4) {
            var2.append((char)Integer.parseInt(var3[var4], 16));
        }

        return var2.toString();
    }

    public String byteArrayToHexString(byte[] var1) {
        StringBuffer var2 = new StringBuffer();

        for(int var3 = 0; var3 < var1.length; ++var3) {
            var2.append(this.byteToHexString(var1[var3]));
        }

        return var2.toString();
    }

    protected String byteToHexString(byte var1) {
        int var2 = var1;
        if(var1 < 0) {
            var2 = var1 + 256;
        }

        int var3 = var2 / 16;
        int var4 = var2 % 16;
        return this.hexDigits[var3] + this.hexDigits[var4];
    }

    public DataObjMode getDataObjMode() {
        return this.dataObjMode;
    }

    public void setDataObjMode(DataObjMode var1) {
        this.dataObjMode = var1;
    }

    protected String splitString(String var1) {
        StringBuffer var2 = new StringBuffer();

        for(int var3 = 0; var3 < var1.length(); var3 += 2) {
            var2.append(var1.substring(var3, var3 + 2)).append(",");
        }

        return var2.toString();
    }
}
