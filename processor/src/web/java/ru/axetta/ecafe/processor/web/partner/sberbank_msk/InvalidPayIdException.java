/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

public class InvalidPayIdException extends Exception{

    public InvalidPayIdException(String msg){
        super(msg);
    }
}
