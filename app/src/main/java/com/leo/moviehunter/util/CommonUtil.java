package com.leo.moviehunter.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;

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

    /**
     * Function that get the size of an object.
     *
     * @param object
     * @return Size in bytes of the object or -1 if the object is null.
     * @throws IOException
     */
    public static final int sizeOf(Object object) throws IOException {
        if (object == null)
            return -1;

        // Special output stream use to write the content
        // of an output stream to an internal byte array.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Output stream that can write object
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        // Write object and close the output stream
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();

        // Get the byte array
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return byteArray == null ? 0 : byteArray.length;
    }
}
