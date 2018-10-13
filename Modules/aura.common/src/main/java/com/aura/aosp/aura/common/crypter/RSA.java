/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.crypter;

import android.support.annotation.Nullable;

import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.KeyFactory;
import java.security.Signature;

import javax.crypto.Cipher;

import com.aura.aosp.aura.common.simple.Err;

/**
 * Exception safe, annotated and simplified
 * RSA encryption and signing methods.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class RSA
{
    /**
     * Test flag for disabling all RSA encryption.
     */
    private static boolean dryrunRSA;

    /**
     * Set dry run flag.
     *
     * @param dryrun dryrun enable flag.
     */
    public static void setRSADryRun(boolean dryrun)
    {
        dryrunRSA = dryrun;
    }

    /**
     * Unmarshal a binary private key spec in PKCS1 format.
     *
     * @param pkcs1PrivateKey binary private key spec in PKCS1 format.
     * @return RSAPrivateKey or null on failure.
     */
    @Nullable
    public static RSAPrivateKey unmarshalRSAPrivateKey(byte[] pkcs1PrivateKey)
    {
        if (pkcs1PrivateKey == null)
        {
            Err.errp();
            return null;
        }

        try
        {
            RSAPrivateCrtKeySpec privateKeySpec = Utils.newRSAPrivateCrtKeySpec(pkcs1PrivateKey);
            if (privateKeySpec == null) return null;

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Unmarshal a binary private key spec in PKCS1 format base64 encoded.
     *
     * @param pkcs1base64PrivateKey binary private key spec in PKCS1 format base64 encoded.
     * @return RSAPrivateKey or null on failure.
     */
    @Nullable
    public static RSAPrivateKey unmarshalRSAPrivateKeyBase64(String pkcs1base64PrivateKey)
    {
        if (pkcs1base64PrivateKey == null)
        {
            Err.errp();
            return null;
        }

        return unmarshalRSAPrivateKey(B64.decode(pkcs1base64PrivateKey));
    }

    /**
     * Unmarshal a binary public key spec in PKCS1 format.
     *
     * @param pkcs1PublicKey binary public key spec in PKCS1 format.
     * @return RSAPublicKey or null on failure.
     */
    @Nullable
    public static RSAPublicKey unmarshalRSAPublicKey(byte[] pkcs1PublicKey)
    {
        if (pkcs1PublicKey == null)
        {
            Err.errp();
            return null;
        }

        try
        {
            RSAPublicKeySpec publicKeySpec = Utils.newRSAPublicKeySpec(pkcs1PublicKey);
            if (publicKeySpec == null) return null;

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Unmarshal a binary public key spec in PKCS1 format base64 encoded.
     *
     * @param pkcs1base64PublicKey binary public key spec in PKCS1 format base64 encoded.
     * @return RSAPublicKey or null on failure.
     */
    @Nullable
    public static RSAPublicKey unmarshalRSAPublicKeyBase64(String pkcs1base64PublicKey)
    {
        if (pkcs1base64PublicKey == null)
        {
            Err.errp();
            return null;
        }

        return unmarshalRSAPublicKey(B64.decode(pkcs1base64PublicKey));
    }

    /**
     * Encode binary message with RSA public key.
     *
     * @param publicKey RSA public key.
     * @param plain plain text binary message.
     * @return encoded binary message or null on failure.
     */
    @Nullable
    public static byte[] encodeRSABuffer(RSAPublicKey publicKey, byte[] plain)
    {
        try
        {
            final Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding","AndroidOpenSSL");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plain);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Decode binary message with RSA private key.
     *
     * @param privateKey RSA private key.
     * @param crypt crypted binary message.
     * @return decoded plain text binary message or null on failure.
     */
    @Nullable
    public static byte[] decodeRSABuffer(RSAPrivateKey privateKey, byte[] crypt)
    {
        try
        {
            final Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding","AndroidOpenSSL");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(crypt);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Create a signature of binary message with RSA private key.
     *
     * @param privateKey RSA private key.
     * @param buffers arbitry number of binary buffers.
     * @return 256 byte binary signature or null on failure.
     */
    @Nullable
    public static byte[] createRSASignature(RSAPrivateKey privateKey, byte[]... buffers)
    {
        if (dryrunRSA)
        {
            return new byte[256];
        }

        try
        {
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(privateKey);

            for (byte[] buffer : buffers)
            {
                signer.update(buffer);
            }

            return signer.sign();
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Verify a RSA signature against public key.
     *
     * @param publicKey RSA public key.
     * @param signature signature to be verified.
     * @param buffers arbitrary numbers of binary buffers.
     * @return null if signature is verified or error code.
     */
    public static Err verifyRSASignature(RSAPublicKey publicKey, byte[] signature, byte[]... buffers)
    {
        if (dryrunRSA)
        {
            return null;
        }

        try
        {
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initVerify(publicKey);

            for (byte[] buffer : buffers)
            {
                signer.update(buffer);
            }

            return signer.verify(signature) ? null : Err.errp("signature fail");
        }
        catch (Exception ex)
        {
            return Err.errp(ex);
        }
    }
}
