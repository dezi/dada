/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.golang;

import android.support.annotation.Nullable;

import android.util.SparseLongArray;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Perf;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;

public class GolangCorrect
{
    private final static int READSIZE = 16 * 1024;
    private final static int MAXCACHESIZE = 256 * 1024;

    private final static Map<String, GolangCorrect> languages = new HashMap<>();

    private CorrectFile topFile;
    private CorrectFile botFile;

    private boolean inited;
    private String language;

    @Nullable
    static JSONObject phraseCorrect(String language, String phrase)
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

        if ((phrase == null) || phrase.isEmpty())
        {
            Err.err("null or empty phrase");
            return null;
        }

        //
        // Look up phrase in desired language.
        //

        //Perf perf = new Perf();
        //JSONObject result = gs.phraseCorrect(phrase);
        //Log.d("perf=%d result=%s", perf.elapsedTimeMillis(), (result == null) ? "null" : result.toString());
        //return result;

        return gs.phraseCorrect(phrase);
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
            topFile = new CorrectFile(correctTopFile);
            Err errtop = topFile.readFile();

            //
            // Read bot file.
            //

            File correctBotFile = new File(suggestDir, "correct.bot.json");
            botFile = new CorrectFile(correctBotFile);
            Err errbot = botFile.readFile();

            inited = (errtop == null) && (errbot == null);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
        }
    }

    @Nullable
    private JSONObject phraseCorrect(String phrase)
    {
        if (!inited)
        {
            Err.errp("uninitialized!");
            return null;
        }

        String word = phrase.trim();

        if (word.contains(" "))
        {
            int lastSpace = word.lastIndexOf(" ");
            word = word.substring(lastSpace).trim();
        }

        JSONObject result;

        result = phraseCorrect(word, topFile);
        if (result != null)
        {
            return result;
        }

        result = phraseCorrect(word, botFile);
        if (result != null)
        {
            return result;
        }

        return null;
    }

    @Nullable
    private JSONObject phraseCorrect(String word, CorrectFile raFile)
    {
        //
        // Check word length and compute minimum and
        // maximum compare word lengths.
        //

        int wordlen = word.length();

        if (wordlen < 2)
        {
            Err.err("too short! word=%s", word);
            return null;
        }

        int wordmin = (wordlen > 3) ? wordlen - 1 : wordlen;
        int wordmax = wordlen + 1;

        //
        // Convert target word into rune format.
        //

        byte[] wordBytes = word.getBytes();
        int[] wordRunes = new int[wordBytes.length];
        int wordRuneLen = GolangUtils.getRunesFromBytes(wordRunes, wordBytes, wordBytes.length);

        //
        // Allocate score array.
        //

        List<GolangUtils.Score> targetScores = new ArrayList<>();
        int totalScore = 0;

        //
        // Start searching.
        //

        for (int checklen = wordmin; checklen <= wordmax; checklen++)
        {
            //
            // Retrieve absolute file positions of requests word length words.
            //

            long seekPos = raFile.index.get(checklen, -1);
            long lastPos = raFile.getLastPosition(checklen);

            if ((seekPos < 0) || (lastPos < 0))
            {
                //
                // No words with matching length in file.
                //

                continue;
            }

            //
            // Provide in memory buffer of this range.
            //

            byte[] buffer;
            int loopMin;
            int loopMax;

            if (raFile.cache == null)
            {
                //
                // File is not cached.
                //

                int chunkSize = (int) (lastPos - seekPos);

                buffer = new byte[chunkSize];

                try
                {
                    raFile.file.seek(seekPos);
                    raFile.file.read(buffer);
                }
                catch (Exception ex)
                {
                    Err.errp(ex);
                    continue;
                }

                //Log.d("read word=%s checklen=%d chunkSize=%s", word, checklen, chunkSize);

                //
                // Modifiy loop parameters for
                // read in buffer.
                //

                loopMin = 0;
                loopMax = chunkSize;
            }
            else
            {
                //
                // Directly use cache buffer with
                // original loop parameters.
                //

                buffer = raFile.cache;

                loopMin = (int) seekPos;
                loopMax = (int) lastPos;
            }

            int[] wrunes = new int[100];

            byte[] wbytes = new byte[100];
            byte[] pbytes = new byte[100];

            int wlen = 0;
            int plen = 0;

            boolean inword = true;

            Integer dist;
            int rune;

            for (int inx = loopMin; inx < loopMax; inx++)
            {
                if (buffer[inx] == ':')
                {
                    inword = false;
                    continue;
                }

                if (buffer[inx] == '\n')
                {
                    dist = GolangUtils.levenshtein(wordRunes, wordRuneLen, wrunes, wlen, 2);

                    if ((dist != null) && (0 <= dist) && (dist <= 2))
                    {
                        wlen = GolangUtils.getBytesFromRunes(wbytes, wrunes, wlen);

                        String wString = new String(wbytes, 0, wlen);
                        String pString = new String(pbytes, 0, plen);

                        int percent = Integer.parseInt(pString);
                        if (dist > 1) percent /= dist;

                        targetScores.add(new GolangUtils.Score(wString, percent));
                        totalScore += percent;
                    }

                    wlen = 0;
                    plen = 0;
                    inword = true;
                    continue;
                }

                if (inword)
                {
                    if (wlen < wrunes.length)
                    {
                        //
                        // Defuck signed byte.
                        //

                        rune = buffer[inx] & 0xff;

                        if ((wlen > 0) && (rune & 0xc0) == 0x80)
                        {
                            wrunes[wlen - 1] = (wrunes[wlen - 1] << 8) + rune;
                        }
                        else
                        {
                            wrunes[wlen++] = rune;
                        }
                    }
                }
                else
                {
                    if (plen < pbytes.length)
                    {
                        pbytes[plen++] = buffer[inx];
                    }
                }
            }
        }

        if (targetScores.size() == 0)
        {
            //
            // No valid hints found.
            //

            return null;
        }

        //
        // We have a least one valid hint.
        //

        Collections.sort(targetScores, new Comparator<GolangUtils.Score>()
        {
            @Override
            public int compare(GolangUtils.Score score1, GolangUtils.Score score2)
            {
                return score2.score - score1.score;
            }
        });

        JSONObject resultJson = new JSONObject();
        Json.put(resultJson, "phrase", word);
        Json.put(resultJson, "language", language);

        JSONObject hintsJson = new JSONObject();

        int limit = 0;

        for (GolangUtils.Score targetScore : targetScores)
        {
            //Log.d("word=%s target=%s percent=%d", word, targetScore.phrase, targetScore.score);

            Json.put(hintsJson, targetScore.phrase, targetScore.score);

            limit += targetScore.score;
            if (limit > (totalScore * 0.5f)) break;
        }

        Json.put(resultJson, "hints", hintsJson);
        return resultJson;
    }

    private class CorrectFile
    {
        final private File path;
        final private RandomAccessFile file;
        final private long size;
        final private SparseLongArray index;
        final private byte[] cache;

        private CorrectFile(File path) throws IOException
        {
            this.path = path;
            this.file = new RandomAccessFile(path, "r");
            this.size = file.length();
            this.index = new SparseLongArray();
            this.cache = (this.size < MAXCACHESIZE) ? new byte[ (int) this.size ] : null;
        }

        @Nullable
        private Err readFile()
        {
            try
            {
                byte[] buffer = (cache != null) ? cache : new byte[READSIZE];
                byte[] wrdbuf = new byte[128];

                boolean inword = true;

                long wordpos = 0;
                long seekpos = 0;

                int wordsize = 0;
                int lastsize = 0;
                int runesize;

                long total = 0;

                Perf startTime = new Perf();

                while (true)
                {
                    int xfer = file.read(buffer);

                    for (int inx = 0; inx < xfer; inx++, seekpos++)
                    {
                        if (buffer[inx] == ':')
                        {
                            runesize = GolangUtils.getRuneLength(wrdbuf, wordsize);

                            if (runesize != lastsize)
                            {
                                index.put(runesize, wordpos);
                                lastsize = runesize;
                            }

                            inword = false;
                            continue;
                        }

                        if (buffer[inx] == '\n')
                        {
                            wordsize = 0;
                            wordpos = seekpos + 1;
                            total += 1;

                            inword = true;
                            continue;
                        }

                        if (inword)
                        {
                            if (wordsize < wrdbuf.length)
                            {
                                wrdbuf[wordsize++] = buffer[inx];
                            }
                        }
                    }

                    if (cache != null)
                    {
                        //
                        // File was read in one chunk.
                        //

                        break;
                    }

                    if (xfer != READSIZE)
                    {
                        //
                        // File is at end.
                        //

                        break;
                    }
                }

                Log.d("path=%s total=%d elapsed=%d", path.toString(), total, startTime.elapsedTimeMillis());

                return null;
            }
            catch (Exception ex)
            {
                return Err.errp(ex);
            }
        }

        /**
         * Get last position of chunk for given word length.
         *
         * @param wordlenght given word length.
         * @return end of chunk for given word length.
         */
        private long getLastPosition(int wordlenght)
        {
            if (wordlenght < 0)
            {
                return -1;
            }

            for (int inx = 0; inx < 50; inx++)
            {
                wordlenght++;

                long seekpos = index.get(wordlenght, -1);
                if (seekpos >= 0) return seekpos;
            }

            return size;
        }
    }
}
