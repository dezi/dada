package com.aura.aosp.aura.crypter;

/****************************************************************************
 * Amazon Modifications: Copyright 2016 Amazon.com, Inc. or its affiliates.
 * All Rights Reserved.
 *****************************************************************************
 * Copyright (c) 1998-2010 AOL Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ****************************************************************************/
// http://oauth.googlecode.com/svn/code/branches/jmeter/jmeter/src/main/java/org/apache/jmeter/protocol/oauth/sampler/PrivateKeyReader.java

// http://www.itu.int/ITU-T/studygroups/com17/languages/X.690-0207.pdf
// http://en.wikipedia.org/wiki/Distinguished_Encoding_Rules#DER_encoding

import java.io.IOException;
import java.math.BigInteger;

/**
 * An ASN.1 TLV. The object is not parsed. It can only handle integers and
 * strings.
 */
class Asn1Object
{
    /**
     * Type: This is actually called tag in ASN.1. It indicates data type
     * (Integer, String) or a construct (sequence, choice, set).
     */
    private final int type;
    /**
     * Length of the field.
     */
    private final int length;
    /**
     * Encoded octet string for the field.
     */
    private final byte[] value;
    /**
     * Tag or identifier.
     */
    private final int tag;

    public Asn1Object(int tag, int length, byte[] value)
    {
        this.tag = tag;
        this.type = tag & 0x1f;
        this.length = length;
        this.value = value;
    }

    /**
     * getType returns the Asn1Object type.
     *
     * @return type
     */
    public int getType()
    {
        return type;
    }

    public boolean isConstructed()
    {
        return (tag & DerParser.CONSTRUCTED) == DerParser.CONSTRUCTED;
    }

    /**
     * For constructed field, return a parser for its content.
     *
     * @return A parser for the construct.
     * @throws IOException if DER cannot be parsed.
     */
    public DerParser getParser() throws IOException
    {
        if (!isConstructed())
        {
            throw new IOException("Invalid DER: can't parse primitive entity"); //$NON-NLS-1$
        }

        return new DerParser(value);
    }

    /**
     * Get the value as integer.
     *
     * @return value
     * @throws IOException if DER is not an integer.
     */
    public BigInteger getBigInteger() throws IOException
    {
        if (type != DerParser.INTEGER)
        {
            throw new IOException("Invalid DER: object is not integer"); //$NON-NLS-1$
        }

        return new BigInteger(value);
    }
}
