/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

public class SendResult implements Error {
  public final int err;
  public final String id;

    public SendResult(int err, String id) {
        this.err = err;
        this.id = id;
    }
}
