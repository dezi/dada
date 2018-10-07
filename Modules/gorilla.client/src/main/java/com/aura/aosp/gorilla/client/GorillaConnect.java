/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * The class {@code GorillaConnect} is a package local static singleton.
 * It includes methods or keeping the state of a {@code Gorilla}
 * bi-directional service connection.
 *
 * @author Dennis Zierahn
 */
class GorillaConnect
{
    /**
     * The service connection interface implementation
     * for the actual connect to Gorilla system services.
     */
    private static IGorillaSystemService systemService;

    /**
     * Server and client secrect for signing and validating
     * params of system service calls.
     * <p>
     * Both signatures are initialized to random values.
     * <p>
     * The serverSignature is overwritten by the servers response.
     * The clientSignature is send to the server.
     * <p>
     * A system call can only be autorized, if both client
     * and server share the same signature.
     */
    private static byte[] serverSignature = newSignature();
    private static byte[] clientSignature = newSignature();

    /**
     * The currently active device owner UUID in base 64 encoding.
     */
    private static String ownerUUIDBase64;

    /**
     * The status of service link to local
     * Gorilla system app.
     */
    private static boolean svlink;

    /**
     * The status of uplink from local Gorilla
     * system app to Gorilla cloud system.
     */
    private static boolean uplink;

    /**
     * Get a new signature from secure random.
     *
     * @return signature bytes.
     */
    private static byte[] newSignature()
    {
        byte[] signature = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(signature);

        return signature;
    }

    /**
     * Set server signature bytes as base64 encoded string.
     *
     * @param signatureBase64 server signature bytes as base64 encoded string.
     */
    static void setServerSignatureBase64(@NonNull String signatureBase64)
    {
        serverSignature = Base64.decode(signatureBase64, Base64.DEFAULT);
    }

    /**
     * Get server signature bytes as base64 encoded string.
     *
     * @return server signature bytes as base64 encoded string.
     */
    @NonNull
    static String getServerSignatureBase64()
    {
        return Base64.encodeToString(serverSignature, Base64.NO_WRAP);
    }

    /**
     * Get client signature bytes as base64 encoded string.
     *
     * @return client signature bytes as base64 encoded string.
     */
    @NonNull
    static String getClientSignatureBase64()
    {
        return Base64.encodeToString(clientSignature, Base64.NO_WRAP);
    }

    /**
     * Register or de-register current service connection.
     *
     * @param service service interface or null of not connected.
     */
    static void setSystemService(@Nullable IGorillaSystemService service)
    {
        systemService = service;
    }

    /**
     * Get current Gorilla service connection status.
     *
     * @return Service interface or null if not connected.
     */
    @Nullable
    static IGorillaSystemService getSystemService()
    {
        return systemService;
    }

    /**
     * Set service status.
     *
     * @param svlinkNew the new service link status.
     * @return true if service link status has changed.
     */
    static boolean setServiceStatus(boolean svlinkNew)
    {
        boolean change = (svlink != svlinkNew);
        svlink = svlinkNew;
        return change;
    }

    /**
     * Get service status.
     *
     * @return the current service link status.
     */
    static boolean getServiceStatus()
    {
        return svlink;
    }

    /**
     * Set uplink status.
     *
     * @param uplinkNew the new uplink status.
     * @return true if uplink status has changed.
     */
    static boolean setUplinkStatus(boolean uplinkNew)
    {
        boolean change = (uplink != uplinkNew);
        uplink = uplinkNew;
        return change;
    }

    /**
     * Get uplink status.
     *
     * @return the current service uplink status.
     */
    static boolean getUplinkStatus()
    {
        return uplink;
    }

    /**
     * Register or de-register current device owner UUID in base 64 encoding.
     *
     * @param ownerUUIDBase64New device owner UUID in base 64 encoding or null of not connected.
     */
    static boolean setOwnerUUID(@Nullable String ownerUUIDBase64New)
    {
        boolean change = (ownerUUIDBase64New != null) && !ownerUUIDBase64New.equals(ownerUUIDBase64);
        ownerUUIDBase64 = ownerUUIDBase64New;
        return change;
    }

    /**
     * Get current device owner UUID in base 64 encoding.
     *
     * @return the current device owner UUID in base 64 encoding or null.
     */
    @Nullable
    static String getownerUUIDBase64()
    {
        return ownerUUIDBase64;
    }

    /**
     * Create a SHA-256 signature prefixed with server and client
     * signatures over the string representation of all given params
     *
     * @param params variable object parameter list.
     * @return base64 SHA signature or null on failure.
     */
    @Nullable
    static String createSHASignatureBase64(Object... params)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(serverSignature);
            md.update(clientSignature);

            for (Object param : params)
            {
                if (param != null)
                {
                    md.update(param.toString().getBytes());
                }
            }

            return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
}
