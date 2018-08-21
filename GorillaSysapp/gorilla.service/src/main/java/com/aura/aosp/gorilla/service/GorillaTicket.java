package com.aura.aosp.gorilla.service;

import com.aura.aosp.aura.simple.Simple;

@SuppressWarnings("WeakerAccess")
public class GorillaTicket
{
    public int Idsmask;

    public byte[] MessageUUID;

    public byte[] SenderUserUUID;
    public byte[] SenderDeviceUUID;

    public byte[] ReceiverUserUUID;
    public byte[] ReceiverDeviceUUID;

    public byte[] AppUUID;

    public byte[] Head;
    public byte[] Payload;

    public int getTicketSize()
    {
        int usiz = GorillaMessage.GorillaUUIDSize;

        int offset = 0;

        //
        // Idsmask itself.
        //

        offset += 2;

        //
        // Variable fields.
        //

        if ((Idsmask & GorillaMessage.HasMessageUUID) != 0)
        {
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasReceiverUserUUID) != 0)
        {
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasReceiverDeviceUUID) != 0)
        {
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasSenderUserUUID) != 0)
        {
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasSenderDeviceUUID) != 0)
        {
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasAppUUID) != 0)
        {
            offset += usiz;
        }

        offset += GorillaMessage.GorillaHeaderSize;

        if (Payload != null)
        {
            offset += Payload.length;
        }

        return offset;
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public byte[] marshall()
    {
        int usiz = GorillaMessage.GorillaUUIDSize;

        byte[] bytes = new byte[ getTicketSize() ];

        bytes[ 0 ] = (byte) ((Idsmask >> 8) & 0xff);
        bytes[ 1 ] = (byte) ((Idsmask >> 0) & 0xff);

        int offset = 2;

        if ((Idsmask & GorillaMessage.HasMessageUUID) != 0)
        {
            System.arraycopy(MessageUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasReceiverUserUUID) != 0)
        {
            System.arraycopy(ReceiverUserUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasReceiverDeviceUUID) != 0)
        {
            System.arraycopy(ReceiverDeviceUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasSenderUserUUID) != 0)
        {
            System.arraycopy(SenderUserUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasSenderDeviceUUID) != 0)
        {
            System.arraycopy(SenderDeviceUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasAppUUID) != 0)
        {
            System.arraycopy(AppUUID,0, bytes, offset, usiz);
            offset += usiz;
        }

        System.arraycopy(Head,0, bytes, offset, GorillaMessage.GorillaHeaderSize);
        offset += GorillaMessage.GorillaHeaderSize;

        System.arraycopy(Payload,0, bytes, offset, Payload.length);

        return bytes;
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public boolean unmarshall(byte[] bytes)
    {
        int usiz = GorillaMessage.GorillaUUIDSize;

        if ((bytes == null) || (bytes.length < 2))
        {
            return false;
        }

        Idsmask = ((bytes[ 0 ] & 0xff) << 8)
                + ((bytes[ 1 ] & 0xff) << 0);

        if (getTicketSize() < bytes.length)
        {
            return false;
        }

        int offset = 2;

        if ((Idsmask & GorillaMessage.HasMessageUUID) != 0)
        {
            MessageUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasReceiverUserUUID) != 0)
        {
            ReceiverUserUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasReceiverDeviceUUID) != 0)
        {
            ReceiverDeviceUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasSenderUserUUID) != 0)
        {
            SenderUserUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasSenderDeviceUUID) != 0)
        {
            SenderDeviceUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        if ((Idsmask & GorillaMessage.HasAppUUID) != 0)
        {
            AppUUID = Simple.sliceBytes(bytes, offset, usiz);
            offset += usiz;
        }

        Head = Simple.sliceBytes(bytes, offset, GorillaMessage.GorillaHeaderSize);
        offset += GorillaMessage.GorillaHeaderSize;

        Payload = Simple.sliceBytes(bytes, offset,bytes.length - offset);

        return true;
    }
}
