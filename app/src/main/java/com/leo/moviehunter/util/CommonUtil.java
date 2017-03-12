package com.leo.moviehunter.util;

import java.io.Closeable;
import java.io.IOException;

public class CommonUtil {
    public static void closeCursor(Closeable closeable) {
        if (closeable != null)  {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static String[] intArray2StringArray(int[] intAry) {
        if (intAry == null) {
            return null;
        }
        String[] strAry = new String[intAry.length];
        for (int i = 0; i < intAry.length; i++) {
            strAry[i] = String.valueOf(intAry[i]);
        }
        return strAry;
    }
}
