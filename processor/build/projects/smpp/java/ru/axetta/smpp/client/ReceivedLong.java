/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

import org.smpp.pdu.Address;

class ReceivedLong {

    public final String messageId;
    public final byte[] bData;
    public final short seg_id, seg_count, seg_num;
    public final Address source;
    public final byte msg_type;

    public ReceivedLong(byte[] bData, short seg_id, short seg_count, short seg_num, Address source, byte msg_type, String messageId) {
        this.bData = bData;
        this.seg_id = seg_id;
        this.seg_count = seg_count;
        this.seg_num = seg_num;
        this.source = source;
        this.msg_type = msg_type;
        this.messageId = messageId;
    }

    public boolean IsPartOfThisMessage(ReceivedLong rec) {
        return rec.seg_id == seg_id && rec.source.getAddress().equals(source.getAddress());
    }
}
