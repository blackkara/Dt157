package com.blackkara.dt157.cem;

/**
 * Created by blackkara on 2/21/2018.
 */

public class BleMeterDataClass {
    private static BleMeterDataClass mymeterclass;
    private BleMeterDataClass.BleMeterDataCallback datacallback;
    private int secondCode;
    private int sixCode;

    public static BleMeterDataClass getInstance() {
        Class var2 = BleMeterDataClass.class;
        synchronized(BleMeterDataClass.class){}

        BleMeterDataClass var1;
        try {
            if(mymeterclass == null) {
                mymeterclass = new BleMeterDataClass();
            }

            var1 = mymeterclass;
        } finally {
            ;
        }

        return var1;
    }

    public void LoadData(byte[] var1) {
        this.secondCode = this.getShort(var1[1]);
        this.sixCode = this.getShort(var1[6]);
        Object var2 = null;

        if(this.secondCode == 11 && this.sixCode == 0) {
            var2 = new ICTTObj(var1);
        }

        if(this.datacallback != null) {
            this.datacallback.onMeterData((BaseIcttDataObj)var2);
        }

    }

    public int getShort(byte var1) {
        return var1 & 255;
    }

    public void setOnDataCallback(BleMeterDataClass.BleMeterDataCallback var1) {
        this.datacallback = var1;
    }

    public interface BleMeterDataCallback {
        void onMeterData(BaseIcttDataObj var1);
    }
}
