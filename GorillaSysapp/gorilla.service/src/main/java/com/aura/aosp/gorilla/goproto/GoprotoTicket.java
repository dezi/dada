package com.aura.aosp.gorilla.goproto;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Log;
import com.aura.aosp.aura.simple.Simple;

@SuppressWarnings("WeakerAccess")
public class GoprotoTicket
{
    private int Idsmask;

    private byte[] MessageUUID;

    private byte[] SenderUserUUID;
    private byte[] SenderDeviceUUID;

    private byte[] ReceiverUserUUID;
    private byte[] ReceiverDeviceUUID;

    private byte[] AppUUID;

    private byte[] Payload;

    public byte[] getMessageUUID()
    {
        return MessageUUID;
    }

    public void setMessageUUID(byte[] messageUUID)
    {
        Idsmask |= GoprotoDefs.HasMessageUUID;
        this.MessageUUID = messageUUID;
    }

    public byte[] getSenderUserUUID()
    {
        return SenderUserUUID;
    }

    public void setSenderUserUUID(byte[] senderUserUUID)
    {
        Idsmask |= GoprotoDefs.HasSenderUserUUID;
        this.SenderUserUUID = senderUserUUID;
    }

    public byte[] getSenderDeviceUUID()
    {
        return SenderDeviceUUID;
    }

    public void setSenderDeviceUUID(byte[] senderDeviceUUID)
    {
        Idsmask |= GoprotoDefs.HasSenderDeviceUUID;
        this.SenderDeviceUUID = senderDeviceUUID;
    }
    public byte[] getReceiverUserUUID()
    {
        return ReceiverUserUUID;
    }

    public void setReceiverUserUUID(byte[] receiverUserUUID)
    {
        Idsmask |= GoprotoDefs.HasReceiverUserUUID;
        this.ReceiverUserUUID = receiverUserUUID;
    }

    public byte[] getReceiverDeviceUUID()
    {
        return ReceiverDeviceUUID;
    }

    public void setReceiverDeviceUUID(byte[] receiverDeviceUUID)
    {
        Idsmask |= GoprotoDefs.HasReceiverDeviceUUID;
        this.ReceiverDeviceUUID = receiverDeviceUUID;
    }

    public byte[] getAppUUID()
    {
        return AppUUID;
    }

    public void setAppUUID(byte[] appUUID)
    {
        Idsmask |= GoprotoDefs.HasAppUUID;
        this.AppUUID = appUUID;
    }

    public byte[] getPayload()
    {
        return Payload;
    }

    public void setPayload(byte[] payload)
    {
        this.Payload = payload;
    }

    public int getRoutingSize()
    {
        int usiz = GoprotoDefs.GorillaUUIDSize;

        int size = 0;

        //
        // Variable fields.
        //

        if ((Idsmask & GoprotoDefs.HasMessageUUID) != 0)
        {
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverUserUUID) != 0)
        {
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverDeviceUUID) != 0)
        {
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderUserUUID) != 0)
        {
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderDeviceUUID) != 0)
        {
            size += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasAppUUID) != 0)
        {
            size += usiz;
        }

        return size;
    }

    public int getTicketSize()
    {
        //
        // Idsmask itself.
        //

        int size = 2;

        size += getRoutingSize();

        if (Payload != null)
        {
            size += Payload.length;
        }

        return size;
    }

    @NonNull
    @SuppressWarnings("PointlessBitwiseExpression")
    public byte[] marshall()
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

        System.arraycopy(Payload,0, bytes, offset, Payload.length);

        return bytes;
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public Err unmarshall(byte[] bytes)
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
            MessageUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverUserUUID) != 0)
        {
            ReceiverUserUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasReceiverDeviceUUID) != 0)
        {
            ReceiverDeviceUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderUserUUID) != 0)
        {
            SenderUserUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasSenderDeviceUUID) != 0)
        {
            SenderDeviceUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GoprotoDefs.HasAppUUID) != 0)
        {
            AppUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        Payload = Simple.sliceBytes(bytes, offset,bytes.length - offset);

        return null;
    }

    public void dumpTicket()
    {
        Log.d("Idsmask=%d", Idsmask);

        Log.d("MessageUUID=%s", Simple.getHexBytesToString(MessageUUID));
        Log.d("SenderUserUUID=%s", Simple.getHexBytesToString(SenderUserUUID));
        Log.d("SenderDeviceUUID=%s", Simple.getHexBytesToString(SenderDeviceUUID));
        Log.d("ReceiverUserUUID=%s", Simple.getHexBytesToString(ReceiverUserUUID));
        Log.d("ReceiverDeviceUUID=%s", Simple.getHexBytesToString(ReceiverDeviceUUID));
        Log.d("AppUUID=%s", Simple.getHexBytesToString(AppUUID));

        Log.d("Payload=%s", Simple.getHexBytesToString(Payload));
    }
}
