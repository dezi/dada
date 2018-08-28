package com.aura.aosp.aura.common.crypter;

import android.support.annotation.Nullable;

import android.util.Base64;

import java.security.Signature;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateCrtKeySpec;

import javax.crypto.Cipher;

import com.aura.aosp.aura.common.simple.Err;

public class RSA
{
    private static boolean dryrunRSA;

    public static void setRSADryRun(boolean dryrun)
    {
        dryrunRSA = dryrun;
    }

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
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
        }

        return null;
    }

    @Nullable
    public static RSAPrivateKey unmarshalRSAPrivateKeyBase64(String pkcs1base64PrivateKey)
    {
        if (pkcs1base64PrivateKey == null)
        {
            Err.errp();
            return null;
        }

        return unmarshalRSAPrivateKey(Base64.decode(pkcs1base64PrivateKey, 0));
    }

    @Nullable
    public static RSAPublicKey unmarshalRSAPublicKey(byte[] pkcs1)
    {
        if (pkcs1 == null)
        {
            Err.errp();
            return null;
        }

        try
        {
            RSAPublicKeySpec publicKeySpec = Utils.newRSAPublicKeySpec(pkcs1);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
        }

        return null;
    }

    @Nullable
    public static RSAPublicKey unmarshalRSAPublicKeyBase64(String pkcs1base64)
    {
        if (pkcs1base64 == null)
        {
            Err.errp();
            return null;
        }

        return unmarshalRSAPublicKey(Base64.decode(pkcs1base64, 0));
    }

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
