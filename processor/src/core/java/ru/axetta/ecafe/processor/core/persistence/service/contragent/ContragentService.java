package ru.axetta.ecafe.processor.core.persistence.service.contragent;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.dao.contragent.ContragentWritableRepository;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * User: shamil
 * Date: 25.03.15
 * Time: 16:06
 */
@Service
public class ContragentService {
    @Autowired
    private ContragentWritableRepository contragentWritableRepository;

    public static ContragentService getInstance() {
        return RuntimeContext.getAppContext().getBean(ContragentService.class);
    }

    @Transactional
    public void setLastRNIPUpdate(Contragent contragent, Date date) {
        Contragent contragent1 = contragentWritableRepository.findOne(contragent.getIdOfContragent());
        contragent1.setLastRNIPUpdate(CalendarUtils.dateTimeToString(date));
        contragentWritableRepository.saveEntity(contragent1);
    }

}
