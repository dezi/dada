package com.aura.aosp.aura.common.univid;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

import java.util.ArrayList;
import java.util.List;

public class Contacts
{
    private static List<Identity> contacts = createContacts();

    private static List<Identity> createContacts()
    {
        List<Identity> contacts = new ArrayList<>();

        // @formatter:off

        contacts.add(createContact("abi",       "DE","5d2hoSCETUK6gZ/2R0faZg==", "9qxRX4afQjmA5AgJD1hlFQ=="));
        contacts.add(createContact("andreas",   "DE","ISz1SevwQxSgtvvY90nEQQ==", "kTpaAi0rS12ez6UCGzTm5Q=="));
        contacts.add(createContact("dezi",      "DE","r0Z7g7cnTF6Mi5/NRyU4Yw==", "lfTBPb1qQ9akd3ltWLWxaw=="));
        contacts.add(createContact("diana",     "DE","p+D96IBkRQKEAfvOcrmycg==", "zpfrcO2bTnOVTKCPSt6zYA=="));
        contacts.add(createContact("laurie",    "DE","WVLHKpFJRNOxisodPZSLkw==", "dgaQPUdXRD2SydYPS762BA=="));
        contacts.add(createContact("malte",     "DE","suHhYFrMTdC2+HcU8G6mMQ==", "x4MP2kuSQk6CK5vZgyzXfA=="));
        contacts.add(createContact("matthias",  "DE","KTnJt4OoQFeH//++sSGF+w==", "fSaNQ5XySVeTGWnXb7kAGQ=="));
        contacts.add(createContact("mr.hoi",    "DE","PGuS7XsrR1a+HMNCkosPJQ==", "5J1oeaPYRMeygtMvu6TxVw=="));
        contacts.add(createContact("patrick",   "DE","Au+LSTjkTLyp1B9gDNJLmg==", "dHCFAHZ+TKik1DzFVFOAiQ=="));

        // @formatter:on

        return contacts;
    }

    @NonNull
    private static Identity createContact(String nick, String country, String user, String device)
    {
        return new Identity(nick, country, user, device, Fakekey.fakeRSAPublicKey, Fakekey.fakeRSAPrivateKey);
    }

    @NonNull
    public static List<Identity> getAllContacts()
    {
        return contacts;
    }

    @Nullable
    public static Identity getContact(String userUUID)
    {
        if (userUUID == null)
        {
            Err.err();
            return null;
        }

        for (Identity identity : contacts)
        {
            if (identity.getUserUUIDBase64().equals(userUUID))
            {
                return identity;
            }
        }

        Err.err("user uuid=%s not found.", userUUID);

        return null;
    }
}
