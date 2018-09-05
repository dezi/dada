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

        contacts.add(createContact("abi",       "DE","1ZTtJ8s5TLezIXJI1Hdtpg==", "DChxgM55SHq5KNAnrV1Z3Q=="));
        contacts.add(createContact("andreas",   "DE","PGw2419YRMyIIIubEYIWdQ==", "A6vvjQRTR5uNQPafdLrqgw=="));
        contacts.add(createContact("dezi",      "DE","KutRTwvxSt6sA8nDoqVrOg==", "tB13AgMRRJeEQiAJkuSjZQ=="));
        contacts.add(createContact("diana",     "DE","BeFrH3xeSyuiLiZwyB20Kw==", "ME4r3IxSQq681SgnskwHZg=="));
        contacts.add(createContact("laurie",    "DE","78cV5W8dQ8eb4Ff0YwUQwg==", "pg8QyFc3T5ePKjGq3cgPrw=="));
        contacts.add(createContact("malte",     "DE","4uccKtZ8RG2XhLw8IpxQjw==", "bsVarLYnTCWk2qmryH1DbQ=="));
        contacts.add(createContact("matthias",  "DE","sntNpaRYRHmmL521CzeapQ==", "5Se5xssgS7uai1zgKMghiA=="));
        contacts.add(createContact("mr.hoi",    "DE","rqJcDULVSXqUGDJs3pQmBQ==", "gJMoxnr5TsuoQZsfJlsqgw=="));
        contacts.add(createContact("patrick",   "DE","UNYkTd3KS1maAHEMEvtnwQ==", "jE7VdkVTTZueaWPt9U7Q8Q=="));
        contacts.add(createContact("test1",     "DE","YwltULGaRNS0PxLsUdyL3Q==", "B17gP6EHR7iG56T6yixdGQ=="));
        contacts.add(createContact("test2",     "DE","oIJc9YHDT6emPCMlkocc9g==", "pdR2r1XRQaebhawDFJg1gA=="));
        contacts.add(createContact("test3",     "DE","ZnSYB8ehTE2G4YYrPY1Yig==", "GtUB7VmiTYiJ2vj6vIWIvw=="));
        contacts.add(createContact("test4",     "DE","zfpLhMdCSkamwMnSGUVURg==", "rTj8YejkSbSZZKuL7JYRDQ=="));
        contacts.add(createContact("test5",     "DE","WrajnIYPSiqHhOQj9uTWeA==", "CAdgnoxURLOur76gmb05ng=="));

        // @formatter:on

        return contacts;
    }

    @NonNull
    public static Identity createContact(String nick, String country, String user, String device)
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
