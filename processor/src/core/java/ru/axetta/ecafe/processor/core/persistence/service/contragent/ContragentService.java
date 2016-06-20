package ru.axetta.ecafe.processor.core.persistence.service.contragent;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.ContragentSync;
import ru.axetta.ecafe.processor.core.persistence.dao.contragent.ContragentSyncWritableRepository;
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
    private ContragentSyncWritableRepository contragentSyncWritableRepository;

    public static ContragentService getInstance() {
        return RuntimeContext.getAppContext().getBean(ContragentService.class);
    }

    @Transactional
    public void setLastRNIPUpdate(Contragent contragent, Date date) {
        ContragentSync contragentSync = contragentSyncWritableRepository.findOne(contragent.getIdOfContragent());
        contragentSync.setLastRNIPUpdate(CalendarUtils.dateTimeToString(date));
        contragentSyncWritableRepository.saveEntity(contragentSync);
    }

    @Transactional
    public void setLastModifiesUpdate(Contragent contragent, Date date) {
        ContragentSync contragentSync = contragentSyncWritableRepository.findOne(contragent.getIdOfContragent());
        contragentSync.setLastModifiesUpdate(CalendarUtils.dateTimeToString(date));
        contragentSyncWritableRepository.saveEntity(contragentSync);
    }

}
