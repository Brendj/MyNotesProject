package ru.iteco.emias.service;

import org.springframework.stereotype.Service;
import ru.iteco.emias.models.EMIAS;
import ru.iteco.emias.repo.EMIASRepository;

import java.util.Date;
import java.util.List;

@Service
public class ServiceBD {
    private final EMIASRepository emiasRepository;

    public ServiceBD(EMIASRepository emiasRepository) {
        this.emiasRepository = emiasRepository;
    }

    public List<EMIAS> getEmiasByGuid(String guid) {
        return emiasRepository.findEmiasbyGuid(guid);
    }

    public boolean setArchivedFlag (List<EMIAS> emiasList, boolean archived)
    {
        try {
            for (EMIAS emias : emiasList) {
                emias.setArchive(archived);
                emiasRepository.save(emias);
            }
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public boolean writeRecord (Long emiasId, String personId, Date createDate, Date dateFrom, Date dateTo, Integer hazardLevelId)
    {
        try {
            EMIAS emias = new EMIAS();
            emias.setArchive(false);
            emias.setKafka(true);
            emias.setGuid(personId);
            emias.setIdeventemias(emiasId);
            emias.setDateliberate(createDate.getTime());
            emias.setStartdateliberate(dateFrom.getTime());
            emias.setEnddateliberate(dateTo.getTime());
            emias.setHazard_level_id(hazardLevelId);
            Long maxVersion = emiasRepository.getMaxVersion();
            if (maxVersion == null)
                emias.setVersion(1L);
            else
                emias.setVersion(maxVersion+1);
            emiasRepository.save(emias);
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
