package com.aura.aosp.aura.nlp.suggest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.os.Environment;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Simple;

import java.io.File;
import java.io.RandomAccessFile;

@SuppressWarnings("unused")
public class Utils
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

    /**
     * Get real UTF-8 rune length from byte array.
     * <p>
     * This method is faster than a call to
     * <p>
     * new String(byteString, 0, byteLength).length()
     *
     * @param byteString byte array with UTF-8 content.
     * @param byteLength usable length of byte array.
     * @return number of runes in string.
     */
    static int getRuneLength(byte[] byteString, int byteLength)
    {
        if (byteString == null)
        {
            Err.errp();
            return 0;
        }

        if (byteString.length < byteLength)
        {
            Err.errp("wrong length!");
            return 0;
        }

        int runeLenght = 0;

        for (int inx = 0; inx < byteLength; inx++)
        {
            if ((byteString[inx] & 0xc0) == 0x80)
            {
                //
                // Trailing UTF-8 character does not count.
                //

                continue;
            }

            runeLenght++;
        }

        return runeLenght;
    }

    /**
     * Fill internal rune array from string bytes.
     * <p>
     * Watcha la sticky: This returns NOT the true UTF-8 character codes.
     *
     * @param runes    preallocated rune array.
     * @param bytes    string bytes.
     * @param bytesLen string bytes usable length.
     * @return filled in runes length.
     */
    static int getRunesFromBytes(int[] runes, byte[] bytes, int bytesLen)
    {
        //
        // Convert byte array string into UTF-8 runes array.
        //
        // UTF-8 encoding:
        //
        // 0000 0000 – 0000 007F 	0xxxxxxx
        // 0000 0080 – 0000 07FF 	110xxxxx 10xxxxxx
        // 0000 0800 – 0000 FFFF 	1110xxxx 10xxxxxx 10xxxxxx
        // 0001 0000 – 0010 FFFF 	11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
        //

        int runesLen = 0;
        int rune;

        for (int inx = 0; inx < bytesLen; inx++)
        {
            //
            // Defuck signed byte.
            //

            rune = bytes[inx] & 0xff;

            if ((runesLen > 0) && (rune & 0xc0) == 0x80)
            {
                runes[runesLen - 1] = (runes[runesLen - 1] << 8) + rune;

                continue;
            }

            runes[runesLen++] = rune;
        }

        return runesLen;
    }

    /**
     * Fill string bytes from internal rune array.
     * <p>
     * Watcha la sticky: Use only with runes made
     * by methodgetRunesFromBytes.
     *
     * @param bytes    string bytes.
     * @param runes    string runes.
     * @param runesLen string runes usable length.
     * @return filled in bytes length.
     */
    @SuppressWarnings("PointlessBitwiseExpression")
    static int getBytesFromRunes(byte[] bytes, int[] runes, int runesLen)
    {
        int bytesLen = 0;

        for (int inx = 0; inx < runesLen; inx++)
        {
            int rune = runes[inx];

            if (rune >= 128)
            {
                if ((rune & 0xff000000) != 0)
                {
                    // formatter: off

                    bytes[bytesLen++] = (byte) ((rune >> 24) & 0xff);
                    bytes[bytesLen++] = (byte) ((rune >> 16) & 0xff);
                    bytes[bytesLen++] = (byte) ((rune >>  8) & 0xff);
                    bytes[bytesLen++] = (byte) ((rune >>  0) & 0xff);

                    // formatter: on

                    continue;
                }

                if ((rune & 0x00ff0000) != 0)
                {
                    // formatter: off

                    bytes[bytesLen++] = (byte) ((rune >> 16) & 0xff);
                    bytes[bytesLen++] = (byte) ((rune >>  8) & 0xff);
                    bytes[bytesLen++] = (byte) ((rune >>  0) & 0xff);

                    // formatter: on

                    continue;
                }

                if ((rune & 0x0000ff00) != 0)
                {
                    // formatter: off

                    bytes[bytesLen++] = (byte) ((rune >> 8) & 0xff);
                    bytes[bytesLen++] = (byte) ((rune >> 0) & 0xff);

                    // formatter: on

                    continue;
                }
            }

            bytes[bytesLen++] = (byte) rune;
        }

        return bytesLen;
    }

    /**
     * Package private helper class for maintaining a score.
     */
    static class Score
    {
        /**
         * The phrase.
         */
        final String phrase;

        /**
         * The score.
         */
        int score;

        /**
         * Create Score object.
         *
         * @param phrase the phrase.
         * @param score  the score.
         */
        Score(String phrase, int score)
        {
            this.phrase = phrase;
            this.score = score;
        }
    }
}
