/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;


public interface Listener extends MessageTypes, Error {

    public void received(MSG message, long receiveTime, long processTime);

    public void error();
}
