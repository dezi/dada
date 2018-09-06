package com.aura.aosp.aura.common.crypter;

import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigInteger;

public class Utils
{
    static RSAPublicKeySpec newRSAPublicKeySpec(byte[] keyInPkcs1) throws IOException
    {
        DER parser = new DER(keyInPkcs1);

        ASN sequence = parser.read();

        if (sequence.getType() != DER.SEQUENCE)
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
        DER parser = new DER(keyInPkcs1);

        ASN sequence = parser.read();

        if (sequence.getType() != DER.SEQUENCE)
        {
            throw new IllegalArgumentException("Invalid DER: not a sequence");
        }

        parser = sequence.getParser();

        ASN version = parser.read();
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

    private static class DER
    {
        static final int INTEGER = 0x02;
        static final int SEQUENCE = 0x10;
        static final int CONSTRUCTED = 0x20;

        private static final byte LOWER_7_BITS = 0x7F;
        private static final int MAX_NUMBER_OF_BYTES = 4;

        private final InputStream in;

        private DER(byte[] bytes)
        {
            in = new ByteArrayInputStream(bytes);
        }

        private ASN read() throws IOException
        {
            int tag = in.read();

            if (tag == -1)
            {
                throw new IOException("Invalid DER: stream too short, missing tag");
            }

            int length = getLength();

            byte[] value = new byte[length];
            int n = in.read(value);

            if (n < length)
            {
                throw new IOException("Invalid DER: stream too short, missing value");
            }

            return new ASN(tag, value);
        }

        private int getLength() throws IOException
        {
            int len = in.read();

            if (len == -1)
            {
                throw new IOException("Invalid DER: length missing");
            }

            if ((len & ~LOWER_7_BITS) == 0)
            {
                return len;
            }

            int num = len & LOWER_7_BITS;

            if (num > MAX_NUMBER_OF_BYTES)
            {
                throw new IOException("Invalid DER: length field too big (" + len + ")");
            }

            byte[] bytes = new byte[num];

            int xfer = in.read(bytes);

            if (xfer < num)
            {
                throw new IOException("Invalid DER: length too short");
            }

            return new BigInteger(1, bytes).intValue();
        }
    }

    private static class ASN
    {
        private final int tag;
        private final int type;
        private final byte[] value;

        private ASN(int tag, byte[] value)
        {
            this.tag = tag;
            this.type = tag & 0x1f;
            this.value = value;
        }

        private int getType()
        {
            return type;
        }

        private DER getParser() throws IOException
        {
            if ((tag & DER.CONSTRUCTED) != DER.CONSTRUCTED)
            {
                throw new IOException("Invalid DER: can't parse primitive entity"); 
            }

            return new DER(value);
        }

        private BigInteger getBigInteger() throws IOException
        {
            if (type != DER.INTEGER)
            {
                throw new IOException("Invalid DER: object is not integer"); 
            }

            return new BigInteger(value);
        }
    }
}
