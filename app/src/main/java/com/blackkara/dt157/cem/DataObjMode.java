package com.blackkara.dt157.cem;

/**
 * Created by blackkara on 2/21/2018.
 */

public enum  DataObjMode {
    ICTTAddressObjMode,
    ICTTDataLoggerEndObjMode,
    ICTTDataLoggerObjMode,
    ICTTObjMode,
    None;

    static {
        DataObjMode[] var0 = new DataObjMode[]{None, ICTTObjMode, ICTTAddressObjMode, ICTTDataLoggerObjMode, ICTTDataLoggerEndObjMode};
    }
}
