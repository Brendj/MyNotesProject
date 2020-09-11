package ru.iteco.meshsync.mesh.service.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.iteco.meshsync.models.ServiceJournal;
import ru.iteco.meshsync.repo.ServiceJournalRepo;

@Service
public class ServiceJournalService {
    private final ServiceJournalRepo serviceJournalRepo;
    private final Logger log = LoggerFactory.getLogger(ServiceJournalService.class);

    public ServiceJournalService(ServiceJournalRepo serviceJournalRepo){
        this.serviceJournalRepo = serviceJournalRepo;
    }

    private void writeRecord(String message, String exceptionClass, String personGUID, Boolean decided){
        try {
            if(StringUtils.isEmpty(personGUID)){
                throw new IllegalArgumentException("Get empty PersonGUID");
            }
            if(StringUtils.isEmpty(message)){
                throw new IllegalArgumentException("Record without message is not recorded");
            }
            ServiceJournal record = new ServiceJournal(message, exceptionClass, personGUID, decided);
            serviceJournalRepo.save(record);
        }catch (Exception e){
            log.error("Can't save record in Journal", e);
        }
    }

    public void writeMessage(String message, String personGUID){
        log.warn(String.format("Save in Journal- Msg: %s, Person %s",
                message, personGUID));
        writeRecord(message, null, personGUID, true);
    }

    public void writeError(Exception e, String personGUID){
        writeRecord(e.getMessage(), e.getClass().toString(), personGUID, false);
    }

    public void writeErrorWithUserMsg(Exception e, String msg, String personGUID){
        writeRecord(msg, e.getClass().toString(), personGUID, false);
    }

    public int decideAllRowsForPerson(String personGUID){
        return serviceJournalRepo.decideAllRowsForPerson(personGUID);
    }
}
