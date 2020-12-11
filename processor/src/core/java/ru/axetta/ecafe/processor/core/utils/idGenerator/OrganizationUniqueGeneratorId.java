/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.idGenerator;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class OrganizationUniqueGeneratorId implements IIdGenerator<Long> {

    private static volatile HashMap<Long, OrganizationUniqueGeneratorId> instances = new LinkedHashMap<>();
    private final IIdGenerator<Long> idGenerator;
    // 2016.01.01 00:00:00 in TimeStamp
    private static final Date epoch = new Date(1451606400000L);
    //for one object
    private final ReentrantLock locker = new ReentrantLock();
    //for get instances of object
    private static final ReentrantLock concurrentHashMapLocker = new ReentrantLock();
    private final long idOfOrg;

    private OrganizationUniqueGeneratorId(long orgId){
        this.idOfOrg = orgId;
        long tempNum = (orgId*397)^Thread.currentThread().hashCode();
        String numStr = Long.toString(tempNum);
        int generatorId = numStr.length() < 5? Integer.parseInt(numStr): Integer.parseInt(numStr.substring(0,5));
        idGenerator = new SnowflakeIdGenerator(generatorId, epoch, GetCustomMaskConfig());
    }

    //get singleton for org
    public static OrganizationUniqueGeneratorId getInstance(Long orgId){
        if(!instances.containsKey(orgId)){
            concurrentHashMapLocker.lock();
            try {
                if(!instances.containsKey(orgId)) {
                    instances.put(orgId, new OrganizationUniqueGeneratorId(orgId));
                }
            }
            finally {
                concurrentHashMapLocker.unlock();
            }
        }
        return instances.get(orgId);
    }


    @Override
    public Long createId() {
        this.locker.lock();
        try{
            return idGenerator.createId();
        }
        finally {
            locker.unlock();
        }
    }

    private static MaskConfig GetCustomMaskConfig() {
        final int maskPartTimestampBits = 41;
        final int maskPartGeneratorIdBits = 17;
        final int maskPartSequenceIdsBits = 5;
        return new MaskConfig((byte)maskPartTimestampBits, (byte)maskPartGeneratorIdBits, (byte)maskPartSequenceIdsBits);
    }
}
