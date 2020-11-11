/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.idGenerator;

import org.apache.commons.lang.NullArgumentException;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public class SnowflakeIdGenerator implements IIdGenerator<Long> {
    // In UTC 1/1/2015 12:00:00 AM
    private static final Date defaultEpoch = new Date(1420070400000L);
    private long lastgen = -1;
    //lock object
    private ReentrantLock locker = new ReentrantLock();
    private int sequence;
    private Date epoch;
    private final MaskConfig maskConfig;
    private final long generatorId;
    private final long maskSequence;
    private final long maskTime;
    private final long maskGenerator;
    private final int shiftTime;
    private final int shiftGenerator;

    public SnowflakeIdGenerator(int generatorId){
        this(generatorId, defaultEpoch);
    }


    public SnowflakeIdGenerator(int generatorId, Date epoch) {
        this(generatorId, epoch, MaskConfig.Default());
    }

    public SnowflakeIdGenerator(int generatorId, Date epoch, MaskConfig maskConfig) {
        if (maskConfig == null)
            throw new NullArgumentException(maskConfig.getClass().getName());
        if (maskConfig.getTotalBits() != 63)
            throw new IllegalArgumentException("Number of bits used to generate Id's is not equal to 63");
        if (maskConfig.getGeneratorIdBits() > (byte) 31)
            throw new IllegalArgumentException("GeneratorId cannot have more than 31 bits");
        if (maskConfig.getSequenceBits() > (byte) 31)
            throw new IllegalArgumentException("Sequence cannot have more than 31 bits");
        if (epoch.after(new Date()))
            throw new IllegalArgumentException("Epoch in future");
        this.maskTime = SnowflakeIdGenerator.GetMask(maskConfig.getTimestampBits());
        this.maskGenerator = SnowflakeIdGenerator.GetMask(maskConfig.getGeneratorIdBits());
        this.maskSequence = SnowflakeIdGenerator.GetMask(maskConfig.getSequenceBits());
        if (generatorId < 0 || (long) generatorId > this.maskGenerator)
            throw new IllegalArgumentException(String.format("GeneratorId must be between 0 and %d (inclusive).", this.maskGenerator));
        this.shiftTime = (int) maskConfig.getGeneratorIdBits() + (int) maskConfig.getSequenceBits();
        this.shiftGenerator = (int) maskConfig.getSequenceBits();
        this.maskConfig = maskConfig;
        this.epoch = epoch;
        this.generatorId = (long) generatorId;
    }


    @Override
    public Long createId() {
        locker.lock();
        try {
            long num = this.GetTimestamp() & this.maskTime;
            if (num < this.lastgen)
                throw new IllegalArgumentException(String
                        .format("Clock moved backwards or wrapped around. Refusing to generate id for %d milliseconds", (this.lastgen - num)));
            if (num == this.lastgen)
            {
                if ((long) this.sequence >= this.maskSequence)
                    throw new IllegalArgumentException("Sequence overflow. Refusing to generate id for rest of millisecond");
                ++this.sequence;
            }
            else
            {
                this.sequence = 0;
                this.lastgen = num;
            }
            return (num << this.shiftTime) + (this.generatorId << this.shiftGenerator) + (long) this.sequence;
        }
        finally {
            locker.unlock();
        }
    }

    private static long GetMask(byte bits)
    {
        return (1L << (int) bits) - 1L;
    }

    private long GetTimestamp()
    {
        return (long) (new Date().getTime() - this.epoch.getTime());
    }
}
