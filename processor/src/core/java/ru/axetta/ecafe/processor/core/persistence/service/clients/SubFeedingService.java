/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.clients;

import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientItem;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.SubFeedingRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.persistence.service.org.OrgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * User: shamil
 * Date: 22.10.14
 * Time: 15:30
 */
@Service
public class SubFeedingService {
    @Autowired
    OrgService orgService;

    @Autowired
    SubFeedingRepository subFeedingRepository;


    /*  Если школа имеют одну из данных категорий:
    *  1;"Школа полного дня"
    *  2;"Школа здоровья"
    *  3;"Школа здоровья + полдник началка"
    *  7;"Школа здоровья + полдник все"
    *  8;"Школа Здоровья завтрак"
    *  то в данной школе кушают все.
    *
    *  Если:
    *  9;"Дошкольное отделение"
    *  то необходимо наличие:
    *  105;"Дошкольное отделение 1.5-3"
    *  106;"Дошкольное отделение 3-7"
    *
    *
    *  Если школа не относиться к заданным категориям то выбираются дети по правилу:
    *  -90;"Начальные классы"  - предоставляется бесплатный завтрак всегда
    *  1;"Многодетные"
    *  2;"Малообеспеченные"
    *  3;"Сироты"
    *  4;"Опека"
    *  5;"Инвалидность"
    *  101;"Спортсмен"
    *  104;"Социально-незащищенные"
    *
    *
    * */
    public List<ClientItem> getClientItems(long orgId){
        List<BigInteger> orgTypes = orgService.findOrgCategories(orgId);
        List<ClientItem> result = new ArrayList<ClientItem>();
        if(orgTypes.size() > 1){
            new Exception("У организации обнаруженно две и более категории");
        }if(orgTypes.size() == 1){
            switch (orgTypes.get(0).intValue()) {
                case 1:
                case 2:
                case 3:
                case 7:
                case 8:
                    result.addAll(subFeedingRepository.getClientAllClientsInOrg(orgId));
                    result.addAll(subFeedingRepository.getClientAllClientsInOrgReserve(orgId));
                return  result;

            case 9:
                result.addAll(subFeedingRepository.getClientInPlan(orgId));
                result.addAll(subFeedingRepository.getClientInReserve(orgId));
                break;
            default:
                new Exception("У организации не поддерживаемая категория");
        }
        }else {
            result.addAll(subFeedingRepository.getClientInPlan(orgId));
            result.addAll(subFeedingRepository.getClientInReserve(orgId));
        }
        return result;
    }

    public ClientItem getClientItem(Long idOfOrg, OrderItem orderItem) {
        return subFeedingRepository.getClient(idOfOrg, orderItem.getIdOfClient());
    }
}
