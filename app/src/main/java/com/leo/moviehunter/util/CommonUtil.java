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
}
