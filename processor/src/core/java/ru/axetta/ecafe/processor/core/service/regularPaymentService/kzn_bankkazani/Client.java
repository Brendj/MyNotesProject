/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani;

import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.AsynchronousPaymentRequest;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.SchoolCard;
import ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan.SchoolCardService;

import javax.xml.bind.JAXBException;
import java.math.BigInteger;

/**
 * User: Shamil
 * Date: 12.12.14
 */
public class Client {

    public static void main(String[] args) throws JAXBException {
        AsynchronousPaymentRequest.Requestex.Cards cards = new AsynchronousPaymentRequest.Requestex.Cards();
        cards.setAction(BigInteger.valueOf(0L));
        cards.setAmount(BigInteger.valueOf(100L));
        cards.setIdaction(4L);
        cards.setContractid(220819801L);
        AsynchronousPaymentRequest.Requestex requestex = new AsynchronousPaymentRequest.Requestex();
        requestex.getCards().add(cards);
        AsynchronousPaymentRequest request = new AsynchronousPaymentRequest();
        request.setRequestex(requestex);

        SchoolCardService schoolCardService  =  new SchoolCardService();
        SchoolCard sc = schoolCardService.getSchoolCardPort();
        sc.asynchronousPayment(request);
    }
}
