package com.blackkara.dt157.cem;

import com.blackkara.dt157.Util;

/**
 * Created by blackkara on 2/21/2018.
 */

public class ICTTLogEndObj extends BaseIcttDataObj {
    public void ICTTLogEndObj(byte[] var1) {
        if(var1 != null && var1.length > 0 && this.bytesBoolean(Util.dec_hex(var1)).booleanValue()) {
            this.setDataObjMode(DataObjMode.ICTTDataLoggerEndObjMode);
        }
    }

    public Boolean bytesBoolean(String var1) {
        int var2 = 0;
        int var3 = 0;

        int var5;
        for(int var4 = 0; var4 < 16; var4 = var5 + 2) {
            if(var4 == 14) {
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
}
