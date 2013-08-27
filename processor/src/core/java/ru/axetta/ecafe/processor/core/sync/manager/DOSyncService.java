/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConfirm;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConflict;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 19.08.13
 * Time: 12:50
 */

@Service("doSyncService")
public class DOSyncService {

    @Autowired
    private DistributedObjectDAO doDAO;

    @Transactional(readOnly = true)
    public <T extends DistributedObject> T findByGuid(Class<T> doClass, String guid) {
        return doDAO.findByGuid(doClass, guid);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T extends DistributedObject> T update(T distributedObject) {
        return doDAO.updateDO(distributedObject);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T extends DistributedObject> T createDO(T distributedObject) {
        distributedObject.setCreatedDate(new Date());
        return doDAO.updateDO(distributedObject);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T extends DistributedObject> DistributedObject mergeDO(T distributedObject, DOConflict doConflict) {
        if (doConflict != null) {
            doConflict.setCreateConflictDate(new Date());
            doDAO.saveDOConflict(doConflict);
        }
        return doDAO.updateDO(distributedObject);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T extends DistributedObject> Long updateDOVersion(Class<T> doClass) {
        Long version;
        DOVersion doVersion = doDAO.getDOVersion(doClass.getSimpleName());
        if (doVersion == null) {
            doVersion = new DOVersion();
            doVersion.setCurrentVersion(0L);
            doVersion.setDistributedObjectClassName(doClass.getSimpleName());
            version = 0L;
        } else {
            version = doVersion.getCurrentVersion() + 1;
            doVersion.setCurrentVersion(version);
        }
        doDAO.updateDOVersion(doVersion);
        return version;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addConfirm(DOConfirm doConfirm) {
        List<DOConfirm> docList = doDAO
                .getDOConfirms(doConfirm.getOrgOwner(), doConfirm.getDistributedObjectClassName(), doConfirm.getGuid());
        if (docList.isEmpty())
            doDAO.saveDOConfirm(doConfirm);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDOConfirms(List<DOConfirm> doConfirms) {
        for (DOConfirm confirm : doConfirms) {
            List<DOConfirm> list = doDAO.getDOConfirms(confirm.getOrgOwner(), confirm.getDistributedObjectClassName(), confirm.getGuid());
            for (DOConfirm doConfirm : list)
                doDAO.removeDOConfirm(doConfirm);
        }
    }

    @Transactional(readOnly = true)
    public List<DistributedObject> findConfirmedDO(Class<? extends DistributedObject> doClass, Long orgOwner) {
        List<String> guids = doDAO.findConfirmedGuids(orgOwner, doClass.getSimpleName());
        List<DistributedObject> doList = new ArrayList<DistributedObject>();
        if (!guids.isEmpty()) {
            List<? extends DistributedObject> res = doDAO.findDOByGuids(doClass, guids);
            doList.addAll(res);
        }
        return doList;
    }
}
