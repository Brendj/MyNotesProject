package ru.axetta.ecafe.processor.core.persistence.service.clients;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: regal
 * Date: 09.01.15
 * Time: 1:37
 */
@Service
public class ClientService {

    public static ClientService getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientService.class);
    }
    @Autowired
    private ClientDao clientDao;

    /*
    * Находит всех клиентов с указанным паролем и заменяет на №контракта пользователя
    * */
    public List<Long> modifyPasswords(String password) throws IOException, NoSuchAlgorithmException {
        String cypheredPassword = Client.encryptPassword(password);

        List<Client> allByPassword = clientDao.findAllByPassword(cypheredPassword);
        List<Long> modifiedClientsIds = new ArrayList<Long>();
        for (Client client : allByPassword) {
            try {
                client.setPassword("" + client.getContractId());
                clientDao.update(client);
                modifiedClientsIds.add(client.getIdOfClient());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return modifiedClientsIds;
    }
}
