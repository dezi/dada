/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.client;

import android.util.Log;

/**
 * The class {@code GorillaClientService} is a package local
 * implementation for remote services of the {@code IGorillaClientService}
 * interface.
 * <p>
 * None of the methods are intented to be called by third party apps.
 * <p>
 * All binary parameters are base 64 encoded.
 *
 * @author Dennis Zierahn
 */
class GorillaClientService extends IGorillaClientService.Stub
{
    /**
     * All purpose log tag.
     */
    private final static String LOGTAG = GorillaClientService.class.getSimpleName();

    /**
     * Called by Gorilla system app to establish secure communication b-directional
     * communication between Gorilla system and any third party app.
     *
     * @param apkname APK name of caller.
     * @return the random generated client signature.
     */
    @Override
    public String returnYourSignature(String apkname)
    {
        Log.d(LOGTAG, "returnYourSignature: impl" + " apkname=" + apkname
                + " clientSignature=" + GorillaConnect.getClientSignatureBase64());

        return GorillaConnect.getClientSignatureBase64();
    }

    /**
     * Method is called by Gorilla system app to check, if both signatures
     * have been successfully exchanged and if the bi-directional secure
     * service is now in operation.
     *
     * @param apkname  APK name of caller.
     * @param checksum checksum for this parameter block.
     * @return true if signatures exchanged and checksum valid.
     */
    @Override
    public boolean validateConnect(String apkname, String checksum)
    {
        String solution = GorillaConnect.createSHASignatureBase64(apkname);

        boolean svlink = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "validateConnect: impl" + " apkname=" + apkname
                + " serverSignature=" + GorillaConnect.getServerSignatureBase64()
                + " clientSignature=" + GorillaConnect.getClientSignatureBase64()
                + " svlink=" + svlink);

        if (!svlink) return false;

        if (GorillaConnect.setServiceStatus(true))
        {
            GorillaClient.getInstance().dispatchServiceStatus();
        }

        GorillaClient.getInstance().requestUplinkStatus();
        GorillaClient.getInstance().requestOwnerUUID();

        return true;
    }

    /**
     * Push notification from Gorilla system app that the owner identity
     * either has changed or ist now available.
     *
     * @param apkname   APK name of caller.
     * @param ownerUUID Owner identity.
     * @param checksum  checksum for this parameter block.
     * @return true if checksum is matching and call was processed.
     */
    @Override
    public boolean receiveOwnerUUID(String apkname, String ownerUUID, String checksum)
    {
        String solution = GorillaConnect.createSHASignatureBase64(apkname, ownerUUID);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receiveOwnerUUID: ownerUUID=" + ownerUUID + " valid=" + valid);

        if (valid)
        {
            if (GorillaConnect.setOwnerUUID(ownerUUID))
            {
                GorillaClient.getInstance().dispatchOwnerUUID();
            }
        }

        return valid;
    }

    /**
     * Push notification from Gorilla system app that the owner identity
     * either has changed or ist now available.
     *
     * @param apkname  APK name of caller.
     * @param uplink   uplink connection state.
     * @param checksum checksum for this parameter block.
     * @return true if checksum is matching and call was processed.
     */
    @Override
    public boolean receiveUplinkStatus(String apkname, boolean uplink, String checksum)
    {
        String solution = GorillaConnect.createSHASignatureBase64(apkname, uplink);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receiveOnlineStatus: uplink=" + uplink + " valid=" + valid);

        if (valid)
        {
            if (GorillaConnect.setUplinkStatus(uplink))
            {
                GorillaClient.getInstance().dispatchUplinkStatus();
            }
        }

        return valid;
    }

    /**
     * Push notification from Gorilla system app with a new message.
     *
     * @param apkname    APK name of caller.
     * @param time       timestamp in milliseconds of origin of message.
     * @param uuid       UUID of this message.
     * @param senderUUID identity of sending user.
     * @param deviceUUID identity of sending device.
     * @param payload    payload bytes of message in base 64 format.
     * @param checksum   checksum for this parameter block.
     * @return true if checksum is matching and call was processed.
     */
    @Override
    public boolean receivePayload(String apkname, long time, String uuid, String senderUUID, String deviceUUID, String payload, String checksum)
    {
        String solution = GorillaConnect.createSHASignatureBase64(apkname, time, uuid, senderUUID, deviceUUID, payload);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receivePayload: payload=" + payload + " valid=" + valid);

        if (valid)
        {
            GorillaClient.getInstance().receivePayload(time, uuid, senderUUID, deviceUUID, payload);
        }

        return valid;
    }

    /**
     * Push notification from Gorilla system app with a result for a message.
     *
     * @param apkname    APK name of caller.
     * @param resultJSON result in JSON string format.
     * @param checksum   checksum for this parameter block.
     * @return true if checksum is matching and call was processed.
     */
    @Override
    public boolean receivePayloadResult(String apkname, String resultJSON, String checksum)
    {
        String solution = GorillaConnect.createSHASignatureBase64(apkname, resultJSON);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receivePayloadResult: result=" + resultJSON + " valid=" + valid);

        if (valid)
        {
            GorillaClient.getInstance().receivePayloadResult(resultJSON);
        }

        return valid;
    }
}
