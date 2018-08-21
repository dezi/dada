package com.aura.aosp.aura.crypter;

import java.security.SecureRandom;

public class RND
{
    public static byte[] randomBytes(int size)
    {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[size];
        random.nextBytes(bytes);

        return bytes;
    }
}
