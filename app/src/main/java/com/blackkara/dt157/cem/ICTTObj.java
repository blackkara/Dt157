package com.blackkara.dt157.cem;

import com.blackkara.dt157.Dt157Util;

import java.math.BigInteger;

/**
 * Created by blackkara on 2/21/2018.
 * This file has decompiled methods, so variables aren't meaningful
 */

public class ICTTObj extends BaseIcttDataObj {
    private int batch;
    private int intNo;
    private float result;
    private String type;
    private int year;

    public ICTTObj(byte[] var1) {
        if(var1 != null && var1.length > 0) {
            String var2 = Dt157Util.dec_hex(var1);
            if(this.bytesBoolean(var2).booleanValue()) {
                this.setDataObjMode(DataObjMode.ICTTObjMode);
                String var3 = Integer.toBinaryString(Integer.parseInt(var2.substring(14, 18), 16));
                if(var3.length() > 15) {
                    String var7 = (new BigInteger(var3.substring(1, 16), 2)).toString();

                    try {
                        this.type = "NFE";
                        this.result = Float.parseFloat(var7) / 10.0F;
                    } catch (Exception var10) {
                        ;
                    }
                } else {
                    String var4 = (new BigInteger(var3, 2)).toString();

                    try {
                        this.result = Float.parseFloat(var4) / 10.0F;
                        this.type = "FE";
                    } catch (Exception var9) {
                        ;
                    }
                }

                this.intNo = Integer.parseInt(var2.substring(8, 12), 16);
                int var6 = Integer.parseInt(var2.substring(4, 8), 16);
                this.year = var6 / 10;
                this.batch = var6 % 10;
            }
        }

    }

    public Boolean bytesBoolean(String var1) {
        int var2 = 0;
        int var3 = 0;

        int var5;
        for(int var4 = 0; var4 < 20; var4 = var5 + 2) {
            if(var4 == 18) {
                var5 = var4 + 4;
                var3 = Integer.parseInt(var1.substring(var4, var5), 16);
            } else {
                var3 = Integer.parseInt(var1.substring(var4, var4 + 2), 16);
                var2 += var3;
                var5 = var4;
            }
        }

        if(var3 == var2 && var2 != 0) {
            return Boolean.valueOf(true);
        } else {
            return Boolean.valueOf(false);
        }
    }

    public int getBatch() {
        return this.batch;
    }

    public int getIntNo() {
        return this.intNo;
    }

    public float getResult() {
        return this.result;
    }

    public String getType() {
        return this.type;
    }

    public int getYear() {
        return this.year;
    }

    public void setBatch(int var1) {
        this.batch = var1;
    }

    public void setIntNo(int var1) {
        this.intNo = var1;
    }

    public void setResult(float var1) {
        this.result = var1;
    }

    public void setType(String var1) {
        this.type = var1;
    }

    public void setYear(int var1) {
        this.year = var1;
    }
}
