/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.golang;

import android.support.annotation.Nullable;
import android.util.SparseLongArray;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Perf;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import java.io.RandomAccessFile;
import java.io.File;

public class GolangCorrect
{
    private final static int READSIZE = 16 * 1024;

    private final static Map<String, GolangCorrect> languages = new HashMap<>();

    private SparseLongArray topIndex;
    private RandomAccessFile topFile;
    private long topFileSize;

    private SparseLongArray botIndex;
    private RandomAccessFile botFile;
    private long botFileSize;

    private boolean inited;
    private String language;

    @Nullable
    public static JSONObject correctWord(String language, String word)
    {
        GolangCorrect gs = languages.get(language);

        if (gs == null)
        {
            //
            // Engine not yet initialized.
            //

            gs = new GolangCorrect(language);
            languages.put(language, gs);
        }

        //
        // Look up phrase in desired language.
        //

        JSONObject result = gs.correctWord(word);

        if (result != null)
        {
            //
            // Have results in desired language.
            //

            return result;
        }

        //
        // Fallback into english.
        //

        if (language.equals("en"))
        {
            //
            // Was already english.
            //

            return null;
        }

        return correctWord("en", word);
    }

    /**
     * Dezi's test patterns.
     */
    public static void testDat()
    {
        Perf startTime;
        JSONObject result;

        startTime = new Perf();
        result = correctWord("de", "");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = correctWord("de", "bltte");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());

        startTime = new Perf();
        result = correctWord("de", "Visibilifät");
        Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), (result == null) ? "null" : result.toString());
    }

    /**
     * Create a GolangCorrect object for a specified language.
     *
     * @param language two digit language tag.
     */
    private GolangCorrect(String language)
    {
        this.language = language;

        File suggestDir = GolangUtils.getStorageDir(language, "suggest", false);

        try
        {

            //
            // Read top file.
            //

            File correctTopFile = new File(suggestDir, "correct.top.json");

            topFile = new RandomAccessFile(correctTopFile, "r");
            topFileSize = topFile.length();
            topIndex = new SparseLongArray();

            Err errtop = readFile(correctTopFile, topFile, topIndex);

            //
            // Read bot file.
            //

            File correctBotFile = new File(suggestDir, "correct.bot.json");

            botFile = new RandomAccessFile(correctBotFile, "r");
            botFileSize = botFile.length();
            botIndex = new SparseLongArray();

            Err errbot = readFile(correctBotFile, botFile, botIndex);

            inited = (errtop == null) && (errbot == null);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
        }
    }

    @Nullable
    private Err readFile(File file, RandomAccessFile raFile, SparseLongArray raIndex)
    {
        try
        {
            Perf startTime = new Perf();

            byte[] buffer = new byte[ READSIZE ];
            byte[] wrdbuf = new byte[ READSIZE ];

            boolean inword = true;
            boolean utfccc = false;

            int wordsize = 0;
            long wordpos = 0;
            long seekpos = 0;

            int lastsize = 0;
            long total = 0;

            while (true)
            {
                int xfer = raFile.read(buffer);

                for (int inx = 0; inx < xfer; inx++, seekpos++)
                {
                    if (buffer[ inx ] == ':')
                    {
                        if (utfccc)
                        {
                            String runeStr = new String (wrdbuf, 0, wordsize);
                            wordsize = runeStr.length();
                        }

                        if (wordsize != lastsize)
                        {
                            raIndex.put(wordsize, wordpos);
                            lastsize = wordsize;

                            //Log.d("wordsize=%d wordpos=%d", wordsize, wordpos);
                        }

                        inword = false;
                        continue;
                    }

                    if (buffer[ inx ] == '\n')
                    {
                        wordsize = 0;
                        wordpos = seekpos + 1;
                        total += 1;

                        inword = true;
                        continue;
                    }

                    if (inword)
                    {
                        wrdbuf[ wordsize++ ] = buffer[ inx ];

                        if ((buffer[ inx ] & 0x80) == 0x80)
                        {
                            utfccc = true;
                        }
                    }
                }

                if (xfer != READSIZE) break;
            }

            Log.d("file=%s total=%d elapsed=%d", file.toString(), total, startTime.elapsedTimeMillis());

            return null;
        }
        catch (Exception ex)
        {
            return Err.errp(ex);
        }
    }

    @Nullable
    private JSONObject correctWord(String word)
    {
        if (!inited)
        {
            Err.errp("uninitialized!");
            return null;
        }

        JSONObject result;

        result =  correctWord(word, topFile, topIndex, topFileSize);
        if (result != null)
        {
            return result;
        }

        result =  correctWord(word, botFile, botIndex, botFileSize);
        if (result != null)
        {
            return result;
        }

        return null;
    }

    @Nullable
    private JSONObject correctWord(String word, RandomAccessFile raFile, SparseLongArray raIndex, long raSize)
    {
        if (word.length() < 3)
        {
            return null;
        }

        int wordlen = word.length();

        int wordmin = (wordlen > 3) ? wordlen - 1 : wordlen;
        int wordmax = wordlen + 2;

        byte[] wordBytes = word.getBytes();
        boolean wordIsUTF = (word.length() < wordBytes.length);

        for (int checklen = wordmin; checklen <= wordmax; checklen++)
        {
            long seekPos = raIndex.get(checklen, -1);
            if (seekPos < 0) continue;

            long lastPos = getLastPosition(checklen + 1, raIndex, raSize);

            int chunkSize = (int) (lastPos - seekPos);

            byte[] buffer = new byte[chunkSize];

            //Log.d("word=%s checklen=%d seekPos=%d chunkSize=%d", word, checklen, seekPos, chunkSize);

            try
            {
                raFile.seek(seekPos);
                raFile.read(buffer);
            }
            catch (Exception ex)
            {
                Err.errp(ex);
                continue;
            }

            byte[] wbyte = new byte[ 100 ];
            byte[] pbyte = new byte[ 100 ];

            boolean isutf = false;
            boolean inword = true;

            int winx = 0;
            int pinx = 0;
            int dist;

            for (int inx = 0; inx < chunkSize; inx++)
            {
                if (buffer[ inx ] == ':')
                {
                    inword = false;
                    continue;
                }

                if (buffer[ inx ] == '\n')
                {
                    if (isutf || wordIsUTF)
                    {
                        dist = GolangUtils.levenshtein(word, new String(wbyte, 0, winx));
                    }
                    else
                    {
                        dist = GolangUtils.levenshtein(wordBytes, wbyte, winx);
                    }

                    if (dist <= 2)
                    {
                        String target = new String(wbyte, 0, winx);
                        String pstring = new String(pbyte, 0, pinx);

                        int percent = Integer.parseInt(pstring);

                        if (percent > 10)
                        {
                            Log.d("word=%s target=%s dist=%d percent=%s", word, target, dist, percent);
                        }
                    }

                    winx = 0;
                    pinx = 0;
                    isutf = false;
                    inword = true;
                    continue;
                }

                if (inword)
                {
                    if (((wbyte[ winx++ ] = buffer[ inx ]) & 0x80) == 0x80) isutf = true;
                }
                else
                {
                    pbyte[ pinx++ ] = buffer[ inx ];
                }
            }
        }

        return null;
    }

    private long getLastPosition(int index, SparseLongArray indexArray, long fileSize)
    {
        for (int inx = 0; inx < 50; inx++)
        {
            long seekpos = indexArray.get(index, -1);
            if (seekpos >= 0) return  seekpos;

            index++;
        }

        return fileSize;
    }
}
