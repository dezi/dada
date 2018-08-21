package com.aura.aosp.aura.crypter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZP
{

    public static byte[] enGzip(byte[] data)
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
            ex.printStackTrace();
        }

        return null;
    }

    public static byte[] unGzip(byte[] gzip)
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
            ex.printStackTrace();
        }

        return null;
    }
}