/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgContractId;

import org.hibernate.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    Cache cacheMaster;
    Cache cacheReports;

    public void invalidateCache(long id) {
        if (cacheMaster == null) {
            cacheMaster = RuntimeContext.getSessionFactory().getCache();
            cacheReports = RuntimeContext.getReportsSessionFactory().getCache();
        }
        runInvalidateCache(cacheMaster, id);
        runInvalidateCache(cacheReports, id);

        logger.info("Invalidated cache for org id = " + id);
    }

    private void runInvalidateCache(Cache cache, long id) {
        if (cache.containsEntity(Org.class, id)) {
            cache.evictEntity(Org.class, id);
        }
        if (cache.containsEntity(OrgContractId.class, id)) {
            cache.evictEntity(OrgContractId.class, id);
        }
    }
}
