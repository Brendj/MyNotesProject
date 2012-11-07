/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

public interface DecryptableMSG {

    public byte[] getDecryptedBody(byte[] kic) throws Exception;
    public byte[] getDecryptedCNTR(byte[] kic) throws Exception;
    public byte[] getDecryptedChecksum(byte[] kic) throws Exception;
}
