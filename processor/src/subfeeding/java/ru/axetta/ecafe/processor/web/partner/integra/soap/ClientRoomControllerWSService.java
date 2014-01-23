
/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import java.net.URL;


@WebServiceClient(name = "ClientRoomControllerWSService")
public class ClientRoomControllerWSService extends Service {

    public ClientRoomControllerWSService(URL wsdlLocation) {
        super(wsdlLocation, new QName("http://soap.integra.partner.web.processor.ecafe.axetta.ru/",
                "ClientRoomControllerWSService"));
    }

    @WebEndpoint(name = "ClientRoomControllerWSPort")
    public ClientRoomController getClientRoomControllerWSPort() {
        return super.getPort(
                new QName("http://soap.integra.partner.web.processor.ecafe.axetta.ru/", "ClientRoomControllerWSPort"),
                ClientRoomController.class);
    }

    @WebEndpoint(name = "ClientRoomControllerWSPort")
    public ClientRoomController getClientRoomControllerWSPort(WebServiceFeature... features) {
        return super.getPort(
                new QName("http://soap.integra.partner.web.processor.ecafe.axetta.ru/", "ClientRoomControllerWSPort"),
                ClientRoomController.class, features);
    }
}
