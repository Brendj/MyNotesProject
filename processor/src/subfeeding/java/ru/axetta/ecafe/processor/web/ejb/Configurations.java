package ru.axetta.ecafe.processor.web.ejb;

import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWSService;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.04.14
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class Configurations {

    private ClientRoomControllerWSService port;

    public void createClientRoomController(String wsdlUrl) throws Exception{
        this.port = new ClientRoomControllerWSService(new URL(wsdlUrl));
    }

    public ClientRoomController getPort() {
        return port.getClientRoomControllerWSPort();
    }
}
