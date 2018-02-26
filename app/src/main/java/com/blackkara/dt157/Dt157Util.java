package com.blackkara.dt157;

/**
 * Created by blackkara on 2/21/2018.
 * This file has decompiled methods, so variables aren't meaningful
 */

public class Dt157Util {
    public static String dec_hex(byte[] var0) {
        String var1;
        if(var0 != null && var0.length > 0) {
            var1 = "";

            for (byte aVar0 : var0) {
                String var3 = Integer.toHexString(255 & aVar0);
                if (var3.length() == 1) {
                    var3 = '0' + var3;
                }

                var1 = var1 + var3.toUpperCase();
            }
        } else {
            var1 = null;
        }

        return var1;
    }

    public  static int getShort(byte var1) {
        return var1 & 255;
    }
}
