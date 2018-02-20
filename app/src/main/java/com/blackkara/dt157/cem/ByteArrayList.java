package com.blackkara.dt157.cem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by blackkara on 2/21/2018.
 */

public class ByteArrayList extends ArrayList {
    public ByteArrayList() {
    }

    public ByteArrayList(int var1) {
        super(var1);
    }

    public void AddRange(byte[] var1) {
        for(int var2 = 0; var2 < var1.length; ++var2) {
            super.add(Byte.valueOf(var1[var2]));
        }

    }

    public void AddRange(byte[] var1, int var2) {
        for(int var3 = 0; var3 < var2; ++var3) {
            super.add(Byte.valueOf(var1[var3]));
        }

    }

    public void AddRange(byte[] var1, int var2, int var3) {
        for(int var4 = var2; var4 < var3; ++var4) {
            super.add(Byte.valueOf(var1[var4]));
        }

    }

    public void AddRange(Byte[] var1) {
        super.addAll(Arrays.asList(var1));
    }

    public byte[] CopyTo(int var1) {
        byte[] var2 = new byte[var1];

        for(int var3 = 0; var3 < var1; ++var3) {
            var2[var3] = ((Byte)this.get(var3)).byteValue();
        }

        this.RemoveRange(0, var1);
        return var2;
    }

    public void RemoveRange(int var1, int var2) {
        super.removeRange(var1, var2);
    }
}
