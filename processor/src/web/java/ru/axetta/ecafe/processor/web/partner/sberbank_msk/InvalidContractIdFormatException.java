/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

/**
 * Created by i.semenov on 31.07.2019.
 */
public class InvalidContractIdFormatException extends Exception {
    public InvalidContractIdFormatException(String msg){
        super(msg);
    }
}
