/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.crypter;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * Exception safe, annotated and simplified
 * versions of gzip encode and decode.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class GZP
{
    /**
     * GZIP encode binary raw data.
     *
     * @param data binary raw data.
     * @return GZIP encoded data or null on failure.
     */
    @Nullable
    public static byte[] enGzip(byte[] data)
    {
        if (data == null)
        {
            Err.err();
            return null;
        }

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

    /**
     * GZIP decode GZIP encoded data.
     *
     * @param gzip GZIP encoded data.
     * @return binary raw data or null on failure.
     */
    @Nullable
    public static byte[] unGzip(byte[] gzip)
    {
        if (gzip == null)
        {
            Err.err();
            return null;
        }

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
