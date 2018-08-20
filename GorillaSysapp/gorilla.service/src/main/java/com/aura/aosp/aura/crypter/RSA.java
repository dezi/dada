package com.aura.aosp.aura.crypter;

import android.support.annotation.Nullable;

import android.util.Base64;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.KeyFactory;
import java.math.BigInteger;
import java.io.IOException;

import javax.crypto.Cipher;

public class RSA
{
    @Nullable
    public static RSAPrivateKey RSAUnMarshalPrivateKeyBase64(String pkcs1base64)
    {
        if (pkcs1base64 == null) return null;

        return RSAUnMarshalPrivateKey(Base64.decode(pkcs1base64, 0));
    }

    @Nullable
    public static RSAPrivateKey RSAUnMarshalPrivateKey(byte[] pkcs1)
    {
        if (pkcs1 == null) return null;

        try
        {
            RSAPrivateCrtKeySpec privateKeySpec = newRSAPrivateCrtKeySpec(pkcs1);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static RSAPublicKey RSAUnMarshalPublicKeyBase64(String pkcs1base64)
    {
        if (pkcs1base64 == null) return null;

        return RSAUnMarshalPublicKey(Base64.decode(pkcs1base64, 0));
    }

    @Nullable
    public static RSAPublicKey RSAUnMarshalPublicKey(byte[] pkcs1)
    {
        if (pkcs1 == null) return null;

        try
        {
            RSAPublicKeySpec publicKeySpec = newRSAPublicKeySpec(pkcs1);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static byte[] RSAEncodeBuffer(RSAPublicKey publicKey, byte[] plain)
    {
        try
        {
            final Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding","AndroidOpenSSL");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plain);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static byte[] RSADecodeBuffer(RSAPrivateKey privateKey, byte[] crypt)
    {
        try
        {
            final Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding","AndroidOpenSSL");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(crypt);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    private static RSAPublicKeySpec newRSAPublicKeySpec(byte[] keyInPkcs1) throws IOException
    {
        DerParser parser = new DerParser(keyInPkcs1);

        Asn1Object sequence = parser.read();

        if (sequence.getType() != DerParser.SEQUENCE)
        {
            throw new IllegalArgumentException("Invalid DER: not a sequence");
        }

        // Parse inside the sequence
        parser = sequence.getParser();

        BigInteger modulus = parser.read().getInteger();
        BigInteger publicExp = parser.read().getInteger();

        return new RSAPublicKeySpec(modulus, publicExp);
    }

    private static RSAPrivateCrtKeySpec newRSAPrivateCrtKeySpec(byte[] keyInPkcs1) throws IOException
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
        BigInteger modulus = parser.read().getInteger();
        BigInteger publicExp = parser.read().getInteger();
        BigInteger privateExp = parser.read().getInteger();
        BigInteger prime1 = parser.read().getInteger();
        BigInteger prime2 = parser.read().getInteger();
        BigInteger exp1 = parser.read().getInteger();
        BigInteger exp2 = parser.read().getInteger();
        BigInteger crtCoef = parser.read().getInteger();

        return new RSAPrivateCrtKeySpec(
                modulus, publicExp, privateExp, prime1, prime2,
                exp1, exp2, crtCoef);
    }
}
