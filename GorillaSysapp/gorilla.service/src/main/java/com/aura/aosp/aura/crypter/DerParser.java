package com.aura.aosp.aura.crypter;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.io.ByteArrayInputStream;

class DerParser
{
    public static final int CONSTRUCTED = 0x20;
    public static final int INTEGER = 0x02;
    public static final int SEQUENCE = 0x10;

    /**
     * Lower 7 bits mask.
     */
    public static final byte LOWER_7_BITS = (byte) 0x7F;
    /**
     * Byte max value.
     */
    public static final int BYTE_MAX = (int) 0xFF;
    /**
     * We can't handle length longer than 4 bytes.
     */
    public static final int MAX_NUMBER_OF_BYTES = 4;

    /**
     * The DER encoded input stream.
     */
    private final InputStream in;

    /**
     * Create a new DER decoder from an input stream.
     *
     * @param in DER encoded stream
     */
    public DerParser(InputStream in)
    {
        this.in = in;
    }

    /**
     * Create a new DER decoder from a byte array.
     *
     * @param bytes encoded bytes
     */
    public DerParser(byte[] bytes)
    {
        this(new ByteArrayInputStream(bytes));
    }

    /**
     * Read next object. If it's constructed, the value holds encoded content
     * and it should be parsed by a new parser from
     * <code>Asn1Object.getParser</code>.
     *
     * @return Asn1Object read
     * @throws IOException if DER parsing error (format).
     */
    public Asn1Object read() throws IOException
    {
        int tag = in.read();

        if (tag == -1)
        {
            throw new IOException("Invalid DER: stream too short, missing tag"); //$NON-NLS-1$
        }

        int length = getLength();

        byte[] value = new byte[length];
        int n = in.read(value);
        if (n < length)
        {
            throw new IOException("Invalid DER: stream too short, missing value"); //$NON-NLS-1$
        }

        Asn1Object o = new Asn1Object(tag, length, value);

        return o;
    }

    private int getLength() throws IOException
    {

        int i = in.read();
        if (i == -1)
        {
            throw new IOException("Invalid DER: length missing"); //$NON-NLS-1$
        }

        // A single byte short length
        if ((i & ~LOWER_7_BITS) == 0)
        {
            return i;
        }

        int num = i & LOWER_7_BITS;

        if (i >= BYTE_MAX || num > MAX_NUMBER_OF_BYTES)
        {
            throw new IOException("Invalid DER: length field too big (" + i + ")"); //$NON-NLS-1$
        }

        byte[] bytes = new byte[num];
        int n = in.read(bytes);
        if (n < num)
        {
            throw new IOException("Invalid DER: length too short"); //$NON-NLS-1$
        }

        return new BigInteger(1, bytes).intValue();
    }
}