package com.aura.aosp.gorilla.client;

interface IGorillaSystemService
{
    String returnYourSecret(String apkname);

    boolean validateConnect(String apkname, String checksum);

    boolean getUplinkStatus(String apkname, String checksum);

    String getOwnerUUID(String apkname, String checksum);

    boolean requestPersisted(String apkname, String checksum);

    String sendPayload(String apkname, String userUUID, String deviceUUID, String payload, String checksum);

    boolean sendPayloadRead(String apkname, String userUUID, String deviceUUID, String messageUUID, String checksum);

    boolean putAtom(String apkname, String atomJSON, String checksum);
    boolean putAtomSharedBy(String apkname, String userUUID, String atomJSON, String checksum);
    boolean putAtomSharedWith(String apkname, String userUUID, String atomJSON, String checksum);

    String getAtom(String apkname, String atomUUID, String checksum);
    String getAtomSharedBy(String apkname, String userUUID, String atomUUID, String checksum);
    String getAtomSharedWith(String apkname, String userUUID, String atomUUID, String checksum);

    String queryAtoms(String apkname, String atomType, long timeFrom, long timeTo);
    String queryAtomsSharedBy(String apkname, String userUUID, String atomType, long timeFrom, long timeTo);
    String queryAtomsSharedWith(String apkname, String userUUID, String atomType, long timeFrom, long timeTo);

    boolean pmaiRegisterActionEvent(String apkname, String actionDomain, String subAction);

    String pmaiSuggestActions(String apkname);
}
