package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;

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
}
