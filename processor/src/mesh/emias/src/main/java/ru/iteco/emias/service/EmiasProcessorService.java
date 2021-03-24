/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.emias.service;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.iteco.emias.kafka.Request.PersonExemption;
import ru.iteco.emias.kafka.Request.PersonExemptionItem;
import ru.iteco.emias.models.Client;
import ru.iteco.emias.models.EMIAS;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EmiasProcessorService {

    private final Logger log = LoggerFactory.getLogger(EmiasProcessorService.class);
    private final ServiceBD serviceBD;

    public EmiasProcessorService(ServiceBD serviceBD) {
        this.serviceBD = serviceBD;
    }

    public void processEmiasRequest(PersonExemption request) {
        Date created_at;
        try {
            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            created_at = formater.parse(request.getCreated_at());
        } catch (Exception e)
        {
            serviceBD.writeRecord(request.getId(), request.getPerson_id(), "Ошибка при форматировании даты из СОУР "
                    + e.getMessage());
            return;
        }

        Client client = serviceBD.getClientByMeshGuid(request.getPerson_id());;
        if (client == null)
        {
            serviceBD.writeRecord(request.getId(), request.getPerson_id(), "Клиент с таким MeshGuid не найден");
            return;
        }

        //Получаем список уже имеющихся данных по этому клиенту
        List<EMIAS> emiasInBD = serviceBD.getEmiasByGuid(request.getPerson_id());

        List<PersonExemptionItem> emiasItems = request.getItems();
        if (emiasItems == null || emiasItems.isEmpty())
        {
            //Значик клиент уже выздоровел т.е. проставляем ему флаг Архивный
            serviceBD.setArchivedFlag(emiasInBD, true);
            return;
        }
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        for (PersonExemptionItem personExemptionItem: emiasItems)
        {
            Date dateTo;
            try {
                dateTo = formater.parse(personExemptionItem.getTo());
            } catch (Exception e)
            {
                serviceBD.writeRecord(request.getId(), request.getPerson_id(), "Ошибка при форматировании даты To "
                        + e.getMessage());
                continue;
            }
            Date dateFrom;
            try {
                dateFrom = formater.parse(personExemptionItem.getFrom());
            } catch (Exception e)
            {
                serviceBD.writeRecord(request.getId(), request.getPerson_id(), "Ошибка при форматировании даты From "
                        + e.getMessage());
                continue;
            }

            if (endOfDay(dateTo).before(new Date()))
            {
                //Если конечная дата меньше текущей, то такие записи не обрабатываем
                continue;
            }
            boolean reWrite = false;
            //Если нашли такой период в БД
            Iterator<EMIAS> emiasIterator = emiasInBD.iterator();
            while (emiasIterator.hasNext()) {
                EMIAS emias = emiasIterator.next();//получаем следующий элемент
                if (emias.getStartdateliberate().equals(dateFrom.getTime()) &&
                        emias.getEnddateliberate().equals(dateTo.getTime()) &&
                        !emias.getArchive())
                {
                    reWrite = true;
                    serviceBD.changeRecord(emias, request.getId(), created_at, personExemptionItem.getHazard_level_id());
                    //Удаляем из списка подлежащих архивации
                    emiasIterator.remove();
                    break;
                }

            }
            if (!reWrite)
            serviceBD.writeRecord(request.getId(), request.getPerson_id(), created_at, dateFrom, dateTo,
                    personExemptionItem.getHazard_level_id());
        }
        //Старым периодам ставим флаг архивный
        serviceBD.setArchivedFlag(emiasInBD, true);
    }

    public static Date endOfDay(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 999);
        c.set(Calendar.MILLISECOND, 999);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.HOUR_OF_DAY, 23);
        return c.getTime();
    }
}
