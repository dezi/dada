package com.aura.aosp.gorilla.golang;

import android.content.Context;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.service.GorillaBase;

public class AndroidSpell
{
    public static void testSpell()
    {
        final TextServicesManager tsm = (TextServicesManager) GorillaBase.getAppContext().getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        if (tsm == null)
        {
            Log.d("nix hier....");
            return;
        }

        SpellCheckerSession.SpellCheckerSessionListener listener = new SpellCheckerSession.SpellCheckerSessionListener()
        {
            @Override
            public void onGetSuggestions(SuggestionsInfo[] results)
            {

            }

            @Override
            public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results)
            {
                final StringBuilder sb = new StringBuilder();

                for (int i = 0; i < results.length; ++i)
                {
                    final SentenceSuggestionsInfo ssi = results[i];
                    for (int j = 0; j < ssi.getSuggestionsCount(); ++j)
                    {
                        dumpSuggestionsInfoInternal(sb, ssi.getSuggestionsInfoAt(j), ssi.getOffsetAt(j), ssi.getLengthAt(j));
                    }

                    Log.d("results=%s", sb.toString());
                }
            }
        };

        SpellCheckerSession mScs = tsm.newSpellCheckerSession(null, null, listener, true);

        if (mScs == null)
        {
            Log.d("nix da....");
            return;
        }

        Log.d("start....");
        mScs.getSentenceSuggestions(new TextInfo[] {new TextInfo("tgisis")}, 3);
    }

    private static void dumpSuggestionsInfoInternal(final StringBuilder sb, final SuggestionsInfo si, final int length, final int offset)
    {
        // Returned suggestions are contained in SuggestionsInfo
        final int len = si.getSuggestionsCount();
        sb.append('\n');
        for (int j = 0; j < len; ++j)
        {
            if (j != 0)
            {
                sb.append(", ");
            }
            sb.append(si.getSuggestionAt(j));
        }

        sb.append(" (" + len + ")");

        if (length != -1)
        {
            sb.append(" length = " + length + ", offset = " + offset);
        }
    }
}
