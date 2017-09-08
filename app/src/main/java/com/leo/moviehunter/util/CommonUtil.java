package com.leo.moviehunter.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

public class CommonUtil {
    private static final String TAG = "CommonUtil";

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

    public static Spanned toHtmlColorSpanned(String color, String text) {
        return Html.fromHtml("<font color='" + color + "'>" + text + "</font>");
    }

    public static String parseBundleInfo(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        sb.append("+Bundle\n");
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                Object obj = bundle.get(key);
                sb.append("\t" + key + ":");
                if (obj != null && obj.getClass().isArray()) {
                    if (obj instanceof Object[]) {
                        sb.append(Arrays.toString((Object[]) obj));
                    } else if (obj instanceof byte[]) {
                        sb.append(Arrays.toString((byte[]) obj));
                    } else if (obj instanceof short[]) {
                        sb.append(Arrays.toString((short[]) obj));
                    } else if (obj instanceof int[]) {
                        sb.append(Arrays.toString((int[]) obj));
                    } else if (obj instanceof long[]) {
                        sb.append(Arrays.toString((long[]) obj));
                    } else if (obj instanceof float[]) {
                        sb.append(Arrays.toString((float[]) obj));
                    } else if (obj instanceof double[]) {
                        sb.append(Arrays.toString((double[]) obj));
                    } else if (obj instanceof boolean[]) {
                        sb.append(Arrays.toString((boolean[]) obj));
                    } else if (obj instanceof char[]) {
                        sb.append(Arrays.toString((char[]) obj));
                    }
                } else {
                    sb.append("" + obj);
                }
                sb.append("\n");
            }
        }
        sb.append("-Bundle\n");
        return sb.toString();
    }

    public static String parseIntentInfo(Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("+Intent\n");
        if (intent != null) {
            sb.append("\taction:" + intent.getAction() + "\n");
            sb.append("\tpackage:" + intent.getPackage() + "\n");
            sb.append("\tclass:" + intent.getClass() + "\n");
            sb.append("\tdata:" + intent.getData() + "\n");
            sb.append("\tcomponent:" + intent.getComponent() + "\n");
            sb.append("\tflag: " + String.format(Locale.ENGLISH, "%X", intent.getFlags()) + "\n");
            if (intent.getCategories() != null) {
                for (String c : intent.getCategories()) {
                    sb.append("\tcategory:" + c + "\n");
                }
            }
            sb.append(parseBundleInfo(intent.getExtras()));
        }
        sb.append("-Intent\n");
        return sb.toString();
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo()
                    .isConnected();
        } catch (Exception e) {
            return false;
        }
    }
}
