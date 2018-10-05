package com.aura.aosp.gorilla.goproto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.crypter.AES;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.gorilla.service.GorillaSender;

import org.json.JSONObject;

@SuppressWarnings("WeakerAccess")
public class GoprotoTicket implements Json.JsonMarshaller
{
    private int Idsmask;

    private byte[] MessageUUID;

    private byte[] SenderUserUUID;
    private byte[] SenderDeviceUUID;

    private byte[] ReceiverUserUUID;
    private byte[] ReceiverDeviceUUID;

    private byte[] AppUUID;

    private GoprotoMetadata Metadata;

    private byte[] Payload = new byte[0];

    public int getIdsmask()
    {
        return Idsmask;
    }

    public void setIdsmask(int idsmask)
    {
        this.Idsmask = idsmask;
    }

    public byte[] getMessageUUID()
    {
        return MessageUUID;
    }

    public String getMessageUUIDBase64()
    {
        return Simple.encodeBase64(MessageUUID);
    }

    public void setMessageUUID(byte[] messageUUID)
    {
        this.Idsmask |= GoprotoDefs.HasMessageUUID;
        this.MessageUUID = messageUUID;
    }

    public byte[] getSenderUserUUID()
    {
        return SenderUserUUID;
    }

    public void setSenderUserUUID(byte[] senderUserUUID)
    {
        this.Idsmask |= GoprotoDefs.HasSenderUserUUID;
        this.SenderUserUUID = senderUserUUID;
    }

    public byte[] getSenderDeviceUUID()
    {
        return SenderDeviceUUID;
    }

    public void setSenderDeviceUUID(byte[] senderDeviceUUID)
    {
        this.Idsmask |= GoprotoDefs.HasSenderDeviceUUID;
        this.SenderDeviceUUID = senderDeviceUUID;
    }
    public byte[] getReceiverUserUUID()
    {
        return ReceiverUserUUID;
    }

    public void setReceiverUserUUID(byte[] receiverUserUUID)
    {
        this.Idsmask |= GoprotoDefs.HasReceiverUserUUID;
        this.ReceiverUserUUID = receiverUserUUID;
    }

    public byte[] getReceiverDeviceUUID()
    {
        return ReceiverDeviceUUID;
    }

    public void setReceiverDeviceUUID(byte[] receiverDeviceUUID)
    {
        this.Idsmask |= GoprotoDefs.HasReceiverDeviceUUID;
        this.ReceiverDeviceUUID = receiverDeviceUUID;
    }

    public byte[] getAppUUID()
    {
        return AppUUID;
    }

    public void setAppUUID(byte[] appUUID)
    {
        this.Idsmask |= GoprotoDefs.HasAppUUID;
        this.AppUUID = appUUID;
    }

    @Nullable
    public Long getTimeStamp()
    {
        if (Metadata == null)
        {
            Err.errp();
            return null;
        }

        return Metadata.getTimeStamp();
    }

    @Nullable
    public void setTimeStamp(long timeStamp)
    {
        if (Metadata == null)
        {
            this.Idsmask |= GoprotoDefs.HasMetadata;
            Metadata = new GoprotoMetadata();
        }

        Metadata.setTimeStamp(timeStamp);
    }

    @Nullable
    public Integer getStatus()
    {
        if (Metadata == null)
        {
            Err.errp();
            return null;
        }

        return Metadata.getStatus();
    }

    public void setStatus(int status)
    {
        if (Metadata == null)
        {
            this.Idsmask |= GoprotoDefs.HasMetadata;
            Metadata = new GoprotoMetadata();
        }

        Metadata.setStatus(status);
    }

    @Nullable
    public GoprotoMetadata getMetadata()
    {
        return Metadata;
    }

    public void setMetadata(GoprotoMetadata metadata)
    {
        this.Idsmask |= GoprotoDefs.HasMetadata;
        this.Metadata = metadata;
    }

    public byte[] getPayload()
    {
        return Payload;
    }

    public void setPayload(byte[] payload)
    {
        this.Payload = payload;
    }

    public GoprotoTicket prepareStatus(int status)
    {
        GoprotoTicket statusTicket = new GoprotoTicket();

        statusTicket.Idsmask = this.Idsmask;

        statusTicket.Idsmask &= ~ GoprotoDefs.HasSenderUserUUID;
        statusTicket.Idsmask &= ~ GoprotoDefs.HasSenderDeviceUUID;
        statusTicket.Idsmask |= GoprotoDefs.HasReceiverUserUUID;
        statusTicket.Idsmask |= GoprotoDefs.HasReceiverDeviceUUID;

        statusTicket.AppUUID = this.AppUUID;
        statusTicket.MessageUUID = this.MessageUUID;
        statusTicket.ReceiverUserUUID = this.SenderUserUUID;
        statusTicket.ReceiverDeviceUUID = this.SenderDeviceUUID;

        statusTicket.setStatus(status);
        statusTicket.setTimeStamp(System.currentTimeMillis());

        return statusTicket;
    }

    public int getRoutingSize()
    {
        int usiz = GoprotoDefs.GorillaUUIDSize;

        int size = 0;

        if ((Idsmask & GoprotoDefs.HasMessageUUID) != 0)
        {
            Log.d("HasMessageUUID");
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverUserUUID) != 0)
        {
            Log.d("HasReceiverUserUUID");
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverDeviceUUID) != 0)
        {
            Log.d("HasReceiverDeviceUUID");
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderUserUUID) != 0)
        {
            Log.d("HasSenderUserUUID");
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderDeviceUUID) != 0)
        {
            Log.d("HasSenderDeviceUUID");
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasAppUUID) != 0)
        {
            Log.d("HasAppUUID");
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasMetadata) != 0)
        {
            Log.d("HasMetadata");
            size += usiz;
        }

        return size;
    }

    public int getTicketSize()
    {
        int size = 0;

        size += 2;

        size += getRoutingSize();

        if (Payload != null)
        {
            size += Payload.length;
        }

        return size;
    }

    public Err unMarshalCrypted(AES.AESBlock aesblock, byte[] bytes)
    {
        if (bytes == null) return Err.errp();

        int csize = getRoutingSize() + AES.AESBlockSize;

        if (bytes.length < csize)
        {
            return Err.errp("wrong size=%d fail!", bytes.length);
        }

        Log.d("bytes=%d csize=%d", bytes.length, csize);

        byte[] crypt = new byte[ csize ];
        System.arraycopy(bytes,0, crypt, 0, crypt.length);

        byte[] plain = AES.decryptAESBlock(aesblock, crypt);
        if (plain == null) return Err.getLastErr();

        Err err = unMarshallRouting(plain);
        if (err != null) return err;

        setPayload(Simple.sliceBytes(bytes, csize));

        return null;
    }

    public byte[] marshalRouting(AES.AESBlock aesblock)
    {
        int usiz = GoprotoDefs.GorillaUUIDSize;

        byte[] bytes = new byte[ getRoutingSize() ];

        int offset = 0;

        if ((Idsmask & GoprotoDefs.HasMessageUUID) != 0)
        {
            System.arraycopy(MessageUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverUserUUID) != 0)
        {
            System.arraycopy(ReceiverUserUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverDeviceUUID) != 0)
        {
            System.arraycopy(ReceiverDeviceUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderUserUUID) != 0)
        {
            System.arraycopy(SenderUserUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderDeviceUUID) != 0)
        {
            System.arraycopy(SenderDeviceUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasAppUUID) != 0)
        {
            System.arraycopy(AppUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasMetadata) != 0)
        {
            System.arraycopy(Metadata.marshal(),0, bytes, offset, usiz);
            //offset += usiz;
        }

        return AES.encryptAESBlock(aesblock, bytes);
    }

    @NonNull
    @SuppressWarnings("PointlessBitwiseExpression")
    public byte[] marshal()
    {
        int usiz = GoprotoDefs.GorillaUUIDSize;

        byte[] bytes = new byte[ getTicketSize() ];

        bytes[ 0 ] = (byte) ((Idsmask >> 8) & 0xff);
        bytes[ 1 ] = (byte) ((Idsmask >> 0) & 0xff);

        int offset = 2;

        if ((Idsmask & GoprotoDefs.HasMessageUUID) != 0)
        {
            System.arraycopy(MessageUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverUserUUID) != 0)
        {
            System.arraycopy(ReceiverUserUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverDeviceUUID) != 0)
        {
            System.arraycopy(ReceiverDeviceUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderUserUUID) != 0)
        {
            System.arraycopy(SenderUserUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderDeviceUUID) != 0)
        {
            System.arraycopy(SenderDeviceUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasAppUUID) != 0)
        {
            System.arraycopy(AppUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasMetadata) != 0)
        {
            System.arraycopy(Metadata.marshal(),0, bytes, offset, usiz);
            offset += usiz;
        }

        System.arraycopy(Payload,0, bytes, offset, Payload.length);

        return bytes;
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public Err unMarshal(byte[] bytes)
    {
        int usiz = GoprotoDefs.GorillaUUIDSize;

        if (bytes == null) return Err.errp();

        if (bytes.length < 2)
        {
            return Err.errp("wrong size=%d", bytes.length);
        }

        Idsmask = ((bytes[ 0 ] & 0xff) << 8)
                + ((bytes[ 1 ] & 0xff) << 0);

        if (getTicketSize() < bytes.length)
        {
            return Err.errp("wrong size=%d", bytes.length);
        }

        int offset = 2;

        if ((Idsmask & GoprotoDefs.HasMessageUUID) != 0)
        {
            MessageUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverUserUUID) != 0)
        {
            ReceiverUserUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverDeviceUUID) != 0)
        {
            ReceiverDeviceUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderUserUUID) != 0)
        {
            SenderUserUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderDeviceUUID) != 0)
        {
            SenderDeviceUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasAppUUID) != 0)
        {
            AppUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasMetadata) != 0)
        {
            Metadata = new GoprotoMetadata();
            Metadata.unMarshal(Simple.sliceBytes(bytes, offset, offset+usiz));

            offset += usiz;
        }

        Payload = Simple.sliceBytes(bytes, offset);

        return null;
    }

    public Err unMarshallRouting(byte[] bytes)
    {
        int usiz = GoprotoDefs.GorillaUUIDSize;

        if (bytes == null) return Err.errp();

        if (getRoutingSize() < bytes.length)
        {
            return Err.errp("wrong size=%d", bytes.length);
        }

        int offset = 0;

        if ((Idsmask & GoprotoDefs.HasMessageUUID) != 0)
        {
            MessageUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverUserUUID) != 0)
        {
            ReceiverUserUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverDeviceUUID) != 0)
        {
            ReceiverDeviceUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderUserUUID) != 0)
        {
            SenderUserUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderDeviceUUID) != 0)
        {
            SenderDeviceUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasAppUUID) != 0)
        {
            AppUUID = Simple.sliceBytes(bytes, offset, offset+usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasMetadata) != 0)
        {
            Metadata = new GoprotoMetadata();
            Metadata.unMarshal(Simple.sliceBytes(bytes, offset, offset+usiz));

            offset += usiz;
        }

        Payload = Simple.sliceBytes(bytes, offset);

        return null;
    }

    @Nullable
    public JSONObject getTicketResult()
    {
        Integer status = getStatus();

        if ((status == null) || (status == 0))
        {
            Err.errp("ticket has no status");
            return null;
        }

        JSONObject result = new JSONObject();
        JSONObject load = new JSONObject();

        Json.put(result, "uuid", getMessageUUIDBase64());
        Json.put(result, "time", getTimeStamp());
        Json.put(result, "load", load);

        switch (status)
        {
            case GoprotoDefs.MsgStatusQueued:
                Json.put(load, "status", "queued");
                return result;

            case GoprotoDefs.MsgStatusSend:
                Json.put(load, "status", "send");
                return result;

            case GoprotoDefs.MsgStatusPersisted:
                Json.put(load, "status", "persisted");
                return result;

            case GoprotoDefs.MsgStatusReceived:
                Json.put(load, "status", "received");
                return result;

            case GoprotoDefs.MsgStatusRead:
                Json.put(load, "status", "read");
                return result;
        }

        Err.errp("unknown status=%04x", status);

        return null;
    }

    public void dumpTicket()
    {
        Log.d("----------");
        Log.d("Idsmask=%04x", Idsmask);

        Log.d("MessageUUID=%s", Simple.encodeBase64(MessageUUID));
        Log.d("SenderUserUUID=%s", Simple.encodeBase64(SenderUserUUID));
        Log.d("SenderDeviceUUID=%s", Simple.encodeBase64(SenderDeviceUUID));
        Log.d("ReceiverUserUUID=%s", Simple.encodeBase64(ReceiverUserUUID));
        Log.d("ReceiverDeviceUUID=%s", Simple.encodeBase64(ReceiverDeviceUUID));
        Log.d("AppUUID=%s", Simple.encodeBase64(AppUUID));

        if (Metadata != null)
        {
            Log.d("TimeStamp=%s", Metadata.getTimeStamp());
            Log.d("Status=%04x", Metadata.getStatus());
        }

        Log.d("Payload=%s", Simple.encodeBase64(Payload));
        Log.d("----------");
    }

    @Override
    @Nullable
    public JSONObject marshalJSON()
    {
        return Json.marshalJSON(this);
    }

    @Override
    @Nullable
    public Err unmarshalJSON(JSONObject json)
    {
        Metadata = new GoprotoMetadata();

        return Json.unmarshalJSON(this, json);
    }
}
