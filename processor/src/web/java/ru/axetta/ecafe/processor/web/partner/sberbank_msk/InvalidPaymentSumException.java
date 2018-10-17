/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

public class InvalidPaymentSumException extends Exception{
    public InvalidPaymentSumException(String msg){
        super(msg);
    }
}
