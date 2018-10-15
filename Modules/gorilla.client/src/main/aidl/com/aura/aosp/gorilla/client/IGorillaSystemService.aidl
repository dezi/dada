/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */
package com.aura.aosp.gorilla.client;

/**
 * The interface {@code IGorillaSystemService} is base of
 * the service connection from any third party app to
 * the gorilla system service app.
 * <p>
 * All methods are called by the client app and served by
 * the system app. The client app must give its true apk
 * name as the first parameter, otherwise the service
 * connection is not validated and services will be denied.
 * <p>
 * Each method call is secured with a checksum, which is
 * a SHA256 hash over the server signature, the corresponding
 * client signature and all parametes in the string representation.
 * <p>
 * All UUIDs and binary items in this interface are base64 encoded.
 * <p>
 * If the server implementation cannot verify the client generated
 * checksum, it will return either false or null.
 *
 * @author Dennis Zierahn
 */
interface IGorillaSystemService
{
    /**
     * Request from service client to return the
     * service servers signature tobe used to validate
     * the bi-directional connect between both apps.
     *
     * @param apkname the apk name of requesting app.
     * @return the per app different random signature of the server.
     */
    String returnYourSignature(String apkname);

    /**
     * Try to validate the service connect. This will fail
     * as long as the opposite service is not yet connected
     * or the opposite signature has not yet been received.
     *
     * @param apkname  the apk name of requesting app.
     * @param checksum parameters checksum.
     * @return true if the checksum matches the local solution,
     * means both signatures have been exchanged succesfully.
     */
    boolean validateConnect(String apkname, String checksum);

    /**
     * Request from service client to return the current
     * uplink status, means if the system app is currently
     * connected to the gorilla cloud backend.
     *
     * @param apkname  the apk name of requesting app.
     * @param checksum parameters checksum.
     * @return true if uplink is active.
     */
    boolean getUplinkStatus(String apkname, String checksum);

    /**
     * Request from service client to return the current
     * active device owner UUID in base64 encoding.
     *
     * @param apkname  the apk name of requesting app.
     * @param checksum parameters checksum.
     * @return device owner UUID or null if no owner selected.
     */
    String getOwnerUUID(String apkname, String checksum);

    /**
     * Request from client service to indicate, that is is
     * now ready to receive any persisted payload. The server
     * will push all outstanding payloads via the client service
     * interface.
     *
     * @param apkname  the apk name of requesting app.
     * @param checksum parameters checksum.
     * @return true if request valid.
     */
    boolean requestPersisted(String apkname, String checksum);

    String sendPayload(String apkname, String userUUID, String deviceUUID, String payload, String checksum);
    boolean sendPayloadRead(String apkname, String userUUID, String deviceUUID, String messageUUID, String checksum);

    String sendPayloadQuer(String apkname, String toapkname, String userUUID, String deviceUUID, String payload, String checksum);
    boolean sendPayloadReadQuer(String apkname, String toapkname, String userUUID, String deviceUUID, String messageUUID, String checksum);

    boolean putAtom(String apkname, String atomJSON, String checksum);

    boolean putAtomSharedBy(String apkname, String userUUID, String atomJSON, String checksum);

    boolean putAtomSharedWith(String apkname, String userUUID, String atomJSON, String checksum);

    String getAtom(String apkname, String atomUUID, String checksum);

    String getAtomSharedBy(String apkname, String userUUID, String atomUUID, String checksum);

    String getAtomSharedWith(String apkname, String userUUID, String atomUUID, String checksum);

    String queryAtoms(String apkname, String atomType, long timeFrom, long timeUpto, String checksum);

    String queryAtomsSharedBy(String apkname, String userUUID, String atomType, long timeFrom, long timeUpto, String checksum);

    String queryAtomsSharedWith(String apkname, String userUUID, String atomType, long timeFrom, long timeUpto, String checksum);

    String suggestActions(String apkname, String checksum);

    String suggestActionsDomain(String apkname, String actionDomain, String checksum);

    String suggestActionsDomainContext(String apkname, String actionDomain, String subContext, String checksum);

    boolean registerActionEvent(String apkname, String actionDomain, String checksum);

    boolean registerActionEventDomain(String apkname, String actionDomain, String subAction, String checksum);

    boolean registerActionEventDomainContext(String apkname, String actionDomain, String subContext, String subAction, String checksum);

    /**
     * Request all contacts of device owner.
     *
     * @param apkname  the apk name of requesting app.
     * @param checksum parameters checksum.
     * @return JSON array string with contacts UUIDs or null.
     */
    String requestContacts(String apkname, String checksum);

    /**
     * @param apkname     the apk name of requesting app.
     * @param contactUUID the contacts UUID.
     * @param checksum    parameters checksum.
     * @return JSON object string of type GorillaContact or null.
     */
    String requestContactData(String apkname, String contactUUID, String checksum);
}
