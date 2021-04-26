package ru.iteco.restservice.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iteco.restservice.controller.menu.responsedto.MenuItem;
import ru.iteco.restservice.db.repo.readonly.ClientReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.ProhibitionMenuReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.WtMenuReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ProhibitionMenu;
import ru.iteco.restservice.repository.MenuRepository;
import ru.iteco.restservice.servise.data.ProhibitionData;

import java.util.*;

@Service
public class MenuService {


    @Autowired
    MenuRepository menuRepository;
    @Autowired
    ClientReadOnlyRepo clientRepository;
    @Autowired
    WtMenuReadOnlyRepo wtMenuReadOnlyRepo;
    @Autowired
    ProhibitionMenuReadOnlyRepo prohibitionMenuReadOnlyRepo;

    public List<MenuItem> getMenuList(Date date, Long contractId) {
        Client client = clientRepository.getClientByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        List<Long> menus = wtMenuReadOnlyRepo.getWtMenuByDateAndOrg(date, client.getOrg());
        ProhibitionData prohibitionData = getProhibitionData(client);
        List<MenuItem> result = menuRepository.generateWtMenuDetailWithProhibitions(client, menus, date, prohibitionData);
        return result;
    }

    public ProhibitionData getProhibitionData(Client client) {
        ProhibitionData prohibitionData = new ProhibitionData();

        List<ProhibitionMenu> prohibitions = prohibitionMenuReadOnlyRepo.findByClientAndDeletedState(client);

        for (ProhibitionMenu prohibition : prohibitions) {
            switch (prohibition.getProhibitionFilterType()) {
                case PROHIBITION_BY_FILTER:
                    prohibitionData.getProhibitByFilter().put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GOODS_NAME:
                    prohibitionData.getProhibitByName().put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GROUP_NAME:
                    prohibitionData.getProhibitByGroup().put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
            }
        }
        return prohibitionData;
    }

}
