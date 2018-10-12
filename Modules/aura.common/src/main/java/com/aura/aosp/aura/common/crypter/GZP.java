package com.aura.aosp.aura.common.crypter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

public class GZP
{
    @Nullable
    public static byte[] enGzip(@NonNull byte[] data)
    {
        try
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(data);
            gos.close();

            byte[] gzip = os.toByteArray();
            os.close();

            return gzip;
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    @Nullable
    public static byte[] unGzip(@NonNull byte[] gzip)
    {
        try
        {
            ByteArrayInputStream is = new ByteArrayInputStream(gzip);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            GZIPInputStream gis = new GZIPInputStream(is, 1024);

            byte[] buff = new byte[1024];
            int xfer;

            while ((xfer = gis.read(buff)) != -1)
            {
                os.write(buff, 0, xfer);
            }

            gis.close();
            is.close();

            byte[] data = os.toByteArray();
            os.close();

            return data;
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }
}
