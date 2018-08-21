package com.aura.aosp.aura.simple;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.security.SecureRandom;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Simple
{
    private static final String LOGTAG = Simple.class.getSimpleName();

    public static int compareTo(String str1, String str2)
    {
        if ((str1 != null) && (str2 != null)) return str1.compareTo(str2);

        return 0;
    }

    @Nullable
    public static String getFileContent(File file)
    {
        byte[] bytes = getFileBytes(file);
        return (bytes == null) ? null : new String(bytes);
    }

    @Nullable
    public static byte[] getFileBytes(File file)
    {
        try
        {
            if (file.exists())
            {
                InputStream in = new FileInputStream(file);
                int len = (int) file.length();
                byte[] bytes = new byte[len];

                int xfer = 0;
                while (xfer < len) xfer += in.read(bytes, xfer, len - xfer);
                in.close();

                return bytes;
            }
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return null;
    }

    public static boolean putFileContent(File file, String content)
    {
        return (content != null) && putFileBytes(file, content.getBytes());
    }

    public static boolean putFileBytes(File file, byte[] bytes)
    {
        if (bytes == null) return false;

        try
        {
            OutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.close();

            return true;
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return false;
    }

    @Nullable
    public static String getAllInputString(InputStream input)
    {
        StringBuilder string = new StringBuilder();
        byte[] buffer = new byte[4096];
        int xfer;

        try
        {
            while ((xfer = input.read(buffer)) > 0)
            {
                string.append(new String(buffer, 0, xfer));
            }
        }
        catch (IOException ex)
        {
            Log.d(LOGTAG, ex.toString());

            return null;
        }

        return string.toString();
    }

    @Nullable
    public static byte[] getAllInputBytes(InputStream input)
    {
        byte[] buffer = new byte[0];
        byte[] chunk = new byte[8192];
        int xfer;

        try
        {
            while ((xfer = input.read(chunk)) > 0)
            {
                buffer = appendBytes(buffer, chunk, 0, xfer);
            }
        }
        catch (IOException ex)
        {
            Log.d(LOGTAG, ex.toString());

            return null;
        }

        return buffer;
    }

    @Nullable
    public static byte[] appendBytes(byte[] buffer, byte[] append)
    {
        if (append == null) return buffer;

        return appendBytes(buffer, append, 0, append.length);
    }

    @Nullable
    public static byte[] appendBytes(byte[] buffer, byte[] append, int offset, int size)
    {
        if (append == null) return buffer;
        if (buffer == null) return null;

        byte[] newbuf = new byte[buffer.length + size];

        System.arraycopy(buffer, 0, newbuf, 0, buffer.length);
        System.arraycopy(append, offset, newbuf, buffer.length, size);

        return newbuf;
    }

    @Nullable
    public static byte[] getHTTPBytes(String urlstr)
    {
        try
        {
            URL url = new URL(urlstr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            InputStream inputStream = conn.getInputStream();
            byte[] bytes = getAllInputBytes(inputStream);
            inputStream.close();

            return bytes;
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return null;
    }

    @Nullable
    public static String getHTTPString(String urlstr)
    {
        try
        {
            URL url = new URL(urlstr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            InputStream inputStream = conn.getInputStream();
            String string = getAllInputString(inputStream);
            inputStream.close();

            return string;
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return null;
    }

    @Nullable
    public static JSONArray getHTTPJSONArray(String urlstr)
    {
        String jstr = getHTTPString(urlstr);
        if (jstr == null) return null;

        return Json.fromStringArray(jstr);
    }

    @Nullable
    public static JSONObject getHTTPJSONObject(String urlstr)
    {
        String jstr = getHTTPString(urlstr);
        if (jstr == null) return null;

        return Json.fromStringObject(jstr);
    }

    public static void sleep(int millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (Exception ignore)
        {
        }
    }

    public static String encodeBase64(byte bytes[])
    {
        return Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
    }

    public static byte[] decodeBase64(String base64)
    {
        return Base64.decode(base64, 0);
    }

    public static String getHexBytesToString(byte[] bytes)
    {
        return getHexBytesToString(bytes, 0, bytes.length, true);
    }

    public static String getHexBytesToString(byte[] bytes, boolean space)
    {
        return getHexBytesToString(bytes, 0, bytes.length, space);
    }

    public static String getHexBytesToString(byte[] bytes, int offset, int length, boolean space)
    {
        int clen = (length << 1) + (space ? (length - 1) : 0);

        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[ clen ];

        int pos = 0;

        for (int inx = offset; inx < (length + offset); inx++)
        {
            if (space && (inx > offset)) hexChars[ pos++ ] = ' ';

            //noinspection PointlessArithmeticExpression
            hexChars[ pos++ ] = hexArray[ (bytes[ inx ] >> 4) & 0x0f ];
            //noinspection PointlessBitwiseExpression
            hexChars[ pos++ ] = hexArray[ (bytes[ inx ] >> 0) & 0x0f ];
        }

        return String.valueOf(hexChars);
    }

    public static byte[] sliceBytes(byte[] bytes, int from)
    {
        return sliceBytes(bytes,from, bytes.length);
    }

    public static byte[] sliceBytes(byte[] bytes, int from, int toto)
    {
        byte[] slice = new byte[toto - from];

        System.arraycopy(bytes, from, slice, 0, slice.length);

        return slice;
    }

    public static byte[] concatBuffers(byte[]... buffers)
    {
        int total = 0;

        for (byte[] buffer : buffers)
        {
            total += buffer.length;
        }

        byte[] result = new byte[total];
        int offset = 0;

        for (byte[] buffer : buffers)
        {
            System.arraycopy(buffer, 0, result, offset, buffer.length);
            offset += buffer.length;
        }

        return result;
    }
}
