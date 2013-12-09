/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConfirm;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOConflict;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DOVersion;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

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

    @Transactional(rollbackFor = Exception.class)
    public <T extends DistributedObject> T update(T distributedObject) {
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

}
