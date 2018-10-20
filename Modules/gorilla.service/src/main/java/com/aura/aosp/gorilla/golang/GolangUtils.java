package com.aura.aosp.gorilla.golang;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Simple;

import java.io.File;
import java.io.RandomAccessFile;

public class GolangUtils
{
    private final static Object mutex = new Object();

    @Nullable
    static File getStorageDir(@NonNull String language, @NonNull String area, boolean create)
    {
        //noinspection ConstantConditions
        if ((language == null) || (area == null))
        {
            Err.errp();
            return null;
        }

        File appfilesdir = Environment.getExternalStorageDirectory();
        File golangdir = new File(appfilesdir, "golang");
        File langdir = new File(golangdir, language);
        File areadir = new File(langdir, area);

        if (create)
        {
            synchronized (mutex)
            {
                Err err = Simple.mkdirs(appfilesdir, golangdir, langdir, areadir);
                if (err != null) return null;
            }
        }

        return areadir;
    }

    /**
     * UTF-8 safe read line from random access file.
     * <p>
     * Max line size is READSIZE.
     *
     * @return UTF-8 string or null on error.
     */
    @Nullable
    static String readLineUTFSafe(RandomAccessFile raFile)
    {
        byte[] bytes = new byte[2048];
        int xfer = 0;

        try
        {
            while (true)
            {
                byte byt = raFile.readByte();
                if (byt == '\n') break;

                bytes[xfer++] = byt;
                if (xfer >= bytes.length) break;
            }

            return new String(bytes, 0, xfer);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    static int levenshtein(byte[] s1, byte[] s2, int s2len)
    {
        int rows = s1.length;
        int cols = s2len;

        int rowsn = rows + 1;
        int colsn = cols + 1;

        int d1;
        int d2;
        int d3;
        int m1;
        int i;
        int j;

        int[] dist = new int[rowsn * colsn];

        for (i = 0; i < rowsn; i++)
        {
            dist[i * colsn] = i;
        }

        for (j = 0; j < colsn; j++)
        {
            dist[j] = j;
        }

        for (j = 0; j < cols; j++)
        {
            for (i = 0; i < rows; i++)
            {
                if (s1[i] == s2[j])
                {
                    dist[((i + 1) * colsn) + (j + 1)] = dist[(i * colsn) + j];
                }
                else
                {
                    d1 = dist[(i * colsn) + (j + 1)] + 1;
                    d2 = dist[((i + 1) * colsn) + j] + 1;
                    d3 = dist[(i * colsn) + j] + 1;

                    m1 = (d2 < d3) ? d2 : d3;

                    dist[((i + 1) * colsn) + (j + 1)] = (d1 < m1) ? d1 : m1;
                }
            }
        }

        return dist[(colsn * rowsn) - 1];
    }

    static int levenshtein(String s1, String s2)
    {
        int rows = s1.length();
        int cols = s2.length();

        int rowsn = rows + 1;
        int colsn = cols + 1;

        int d1;
        int d2;
        int d3;
        int m1;
        int i;
        int j;

        int[] dist = new int[rowsn * colsn];

        for (i = 0; i < rowsn; i++)
        {
            dist[i * colsn] = i;
        }

        for (j = 0; j < colsn; j++)
        {
            dist[j] = j;
        }

        for (j = 0; j < cols; j++)
        {
            for (i = 0; i < rows; i++)
            {
                if (s1.charAt(i) == s2.charAt(j))
                {
                    dist[((i + 1) * colsn) + (j + 1)] = dist[(i * colsn) + j];
                }
                else
                {
                    d1 = dist[(i * colsn) + (j + 1)] + 1;
                    d2 = dist[((i + 1) * colsn) + j] + 1;
                    d3 = dist[(i * colsn) + j] + 1;

                    m1 = (d2 < d3) ? d2 : d3;

                    dist[((i + 1) * colsn) + (j + 1)] = (d1 < m1) ? d1 : m1;
                }
            }
        }

        return dist[(colsn * rowsn) - 1];
    }

    static int levenshtein2(String s1, String s2)
    {
        int rows = s1.length() + 1;
        int cols = s2.length() + 1;

        //char[] r1 = new char[rows];
        //char[] r2 = new char[cols];

        int d1;
        int d2;
        int d3;
        int m1;
        int i;
        int j;

        int[] dist = new int[rows * cols];

        for (i = 0; i < rows; i++)
        {
            dist[i * cols] = i;

            //if ((i + 1) < rows) r1[i] = s1.charAt(i);
        }

        for (j = 0; j < cols; j++)
        {
            dist[j] = j;

            //if ((j + 1) < cols) r2[j] = s2.charAt(j);
        }

        for (j = 1; j < cols; j++)
        {
            for (i = 1; i < rows; i++)
            {
                //if (r1[i - 1] == r2[j - 1])
                if (s1.charAt(i - 1) == s2.charAt(j - 1))
                {
                    dist[(i * cols) + j] = dist[((i - 1) * cols) + (j - 1)];
                }
                else
                {
                    d1 = dist[((i - 1) * cols) + j] + 1;
                    d2 = dist[(i * cols) + (j - 1)] + 1;
                    d3 = dist[((i - 1) * cols) + (j - 1)] + 1;

                    m1 = (d2 < d3) ? d2 : d3;

                    dist[(i * cols) + j] = (d1 < m1) ? d1 : m1;
                }
            }
        }

        return dist[(cols * rows) - 1];
    }
}
