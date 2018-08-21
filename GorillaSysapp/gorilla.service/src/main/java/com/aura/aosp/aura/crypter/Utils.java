package com.aura.aosp.aura.crypter;

import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.math.BigInteger;
import java.io.IOException;

public class Utils
{
    static RSAPublicKeySpec newRSAPublicKeySpec(byte[] keyInPkcs1) throws IOException
    {
        DerParser parser = new DerParser(keyInPkcs1);

        Asn1Object sequence = parser.read();

        if (sequence.getType() != DerParser.SEQUENCE)
        {
            throw new IllegalArgumentException("Invalid DER: not a sequence");
        }

        // Parse inside the sequence
        parser = sequence.getParser();

        BigInteger modulus = parser.read().getBigInteger();
        BigInteger publicExp = parser.read().getBigInteger();

        return new RSAPublicKeySpec(modulus, publicExp);
    }

    static RSAPrivateCrtKeySpec newRSAPrivateCrtKeySpec(byte[] keyInPkcs1) throws IOException
    {
        DerParser parser = new DerParser(keyInPkcs1);

        Asn1Object sequence = parser.read();

        if (sequence.getType() != DerParser.SEQUENCE)
        {
            throw new IllegalArgumentException("Invalid DER: not a sequence");
        }

        // Parse inside the sequence
        parser = sequence.getParser();

        Asn1Object version = parser.read();
        BigInteger modulus = parser.read().getBigInteger();
        BigInteger publicExp = parser.read().getBigInteger();
        BigInteger privateExp = parser.read().getBigInteger();
        BigInteger prime1 = parser.read().getBigInteger();
        BigInteger prime2 = parser.read().getBigInteger();
        BigInteger exp1 = parser.read().getBigInteger();
        BigInteger exp2 = parser.read().getBigInteger();
        BigInteger crtCoef = parser.read().getBigInteger();

        return new RSAPrivateCrtKeySpec(
                modulus, publicExp, privateExp, prime1, prime2,
                exp1, exp2, crtCoef);
    }
}
