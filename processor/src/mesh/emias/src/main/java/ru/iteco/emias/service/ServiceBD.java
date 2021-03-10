package ru.iteco.emias.service;

import org.springframework.stereotype.Service;
import ru.iteco.emias.models.Client;
import ru.iteco.emias.models.EMIAS;
import ru.iteco.emias.repo.ClientRepository;
import ru.iteco.emias.repo.EMIASRepository;

import java.util.Date;
import java.util.List;

@Service
public class ServiceBD {
    private final EMIASRepository emiasRepository;
    private final ClientRepository clientRepository;

    public ServiceBD(EMIASRepository emiasRepository, ClientRepository clientRepository) {
        this.emiasRepository = emiasRepository;
        this.clientRepository = clientRepository;
    }

    public List<EMIAS> getEmiasByGuid(String meshguid) {
        return emiasRepository.findEmiasbyMeshGuidClient(meshguid);
    }

    public Client getClientByMeshGuid(String meshguid) {
        return clientRepository.findFirstByMeshGuid(meshguid);
    }

    public boolean setArchivedFlag (List<EMIAS> emiasList, boolean archived)
    {
        try {
            for (EMIAS emias : emiasList) {
                emias.setArchive(archived);
                Long maxVersion = emiasRepository.getMaxVersion();
                if (maxVersion == null)
                    emias.setVersion(1L);
                else
                    emias.setVersion(maxVersion+1);
                emiasRepository.save(emias);
            }
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }


    public boolean writeRecord (String emiasId, String personId, String errorMessage)
    {
        try {
            EMIAS emias = new EMIAS();
            emias.setGuid(personId);
            emias.setIdemias(emiasId);
            emias.setErrormessage(errorMessage);
            Long maxVersion = emiasRepository.getMaxVersion();
            if (maxVersion == null)
                emias.setVersion(1L);
            else
                emias.setVersion(maxVersion+1);
            emias.setProcessed(false);
            emiasRepository.save(emias);
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public boolean writeRecord (String emiasId, String personId, Date createDate, Date dateFrom, Date dateTo, Integer hazardLevelId)
    {
        try {
            EMIAS emias = new EMIAS();
            emias.setArchive(false);
            emias.setGuid(personId);
            emias.setIdemias(emiasId);
            emias.setDateliberate(createDate.getTime());
            emias.setStartdateliberate(dateFrom.getTime());
            emias.setEnddateliberate(dateTo.getTime());
            emias.setHazard_level_id(hazardLevelId);
            Long maxVersion = emiasRepository.getMaxVersion();
            if (maxVersion == null)
                emias.setVersion(1L);
            else
                emias.setVersion(maxVersion+1);
            emias.setProcessed(true);
            emiasRepository.save(emias);
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public boolean changeRecord (EMIAS emias, String emiasId, Date createDate, Integer hazardLevelId)
    {
        try {
            emias.setIdemias(emiasId);
            emias.setDateliberate(createDate.getTime());
            Long maxVersion = emiasRepository.getMaxVersion();
            emias.setHazard_level_id(hazardLevelId);
            if (maxVersion == null)
                emias.setVersion(1L);
            else
                emias.setVersion(maxVersion+1);
            emias.setProcessed(true);
            emiasRepository.save(emias);
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
