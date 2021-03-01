/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.emias.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.iteco.emias.kafka.Request.PersonExemption;
import ru.iteco.emias.kafka.Request.PersonExemptionItem;
import ru.iteco.emias.models.EMIAS;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class EmiasProcessorService {

    private final Logger log = LoggerFactory.getLogger(EmiasProcessorService.class);
    private final ServiceBD serviceBD;

    public EmiasProcessorService(ServiceBD serviceBD) {
        this.serviceBD = serviceBD;
    }

    public void processEmiasRequest(PersonExemption request) {
        //Получаем список уже имеющихся данных по этому клиенту
        List<EMIAS> emiasInBD = serviceBD.getEmiasByGuid(request.getPersonId());

        List<PersonExemptionItem> emiasItems = request.getItems();
        if (emiasItems == null || emiasItems.isEmpty())
        {
            //Значик клиент уже выздоровел т.е. проставляем ему флаг Архивный
            serviceBD.setArchivedFlag(emiasInBD, true);
            return;
        }
        //Если пришли новые периоды
        serviceBD.setArchivedFlag(emiasInBD, true);
        for (PersonExemptionItem personExemptionItem: emiasItems)
        {
            Date dateTo = Date.from(personExemptionItem.getTo().atStartOfDay(ZoneId.systemDefault()).toInstant());
            if (dateTo.before(new Date()))
            {
                //Если конечная дата меньше текущей, то такие записи не обрабатываем
                break;
            }
            Date createDate = Date.from(request.getCreatedAt().toInstant());
            Date dateFrom = Date.from(personExemptionItem.getFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
            serviceBD.writeRecord(request.getId(), request.getPersonId(), createDate, dateFrom, dateTo,
                    personExemptionItem.getHazardLevelId());
        }
    }
}
