/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.soap;

import junit.framework.TestCase;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.web.bo.client.Result;


import javax.xml.ws.BindingProvider;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 16.07.12
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class WSClientTest extends TestCase {

    public void testGetSummary() throws Exception {
        ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService service = new ru.axetta.ecafe.processor.web.bo.client.ClientRoomControllerWSService();
        ru.axetta.ecafe.processor.web.bo.client.ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/processor/soap/client");


        String password = Client.encryptPassword( "888");
        Result ra = port.authorizeClient(200485L, password);
        System.out.println("AUTH CLIENT: " + ra.getResultCode()+":"+ra.getDescription());
        ru.axetta.ecafe.processor.web.bo.client.ClientSummaryResult r = port.getSummary(9800012L);
        ru.axetta.ecafe.processor.web.bo.client.ClientSummaryExt summaryExt=r.getClientSummary();
        System.out.println("CLIENT: " + summaryExt.getLastFreePayTime()+" "+summaryExt.getFreePayCount()+" "+summaryExt.getFreePayMaxCount()+" "+summaryExt.getLimit()+" "+summaryExt.getPhone()+" "+summaryExt.getAddress()+" "+summaryExt.getDiscountMode());
       // Result ra1=port.changePassword(200204L,password);
        //System.out.println("CHANGE PASSWORRD "+ra1.getResultCode());
       // System.out.println( port.getSummaryByGuardSan("333333333333333333333333333333").size()==0)  ;
       // ChronopayConfigResult chronopayConfigResult= port.getChronopayConfig();
       // System.out.println("sharedSec: "+chronopayConfigResult.getChronopayConfig().getSharedSec());






    }


    }


