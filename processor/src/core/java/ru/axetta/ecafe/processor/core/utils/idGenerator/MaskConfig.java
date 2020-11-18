/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.idGenerator;

import java.sql.Timestamp;
import java.util.Date;

public class MaskConfig {
    private byte timestampBits;
    private byte generatorIdBits;
    private byte sequenceBits;

    public static MaskConfig Default(){
        return new MaskConfig((byte)41, (byte)10, (byte)12);
    }

    public MaskConfig(byte timestampBits, byte generatorIdBits, byte sequenceBits){
        this.timestampBits = timestampBits;
        this.generatorIdBits = generatorIdBits;
        this.sequenceBits = sequenceBits;
    }

    public byte getTimestampBits() {
        return timestampBits;
    }

    public byte getGeneratorIdBits() {
        return generatorIdBits;
    }

    public byte getSequenceBits() {
        return sequenceBits;
    }

    public int getTotalBits(){
        return (int) this.timestampBits + (int) this.generatorIdBits + (int) this.sequenceBits;
    }

    public long getMaxIntervals(){
        return 1L << (int) this.timestampBits;
    }

    public long getMaxGenerators(){
        return 1L << (int) this.generatorIdBits;
    }

    public long getMaxSequenceIds(){
        return 1L << (int) this.sequenceBits;
    }

    public Date wraparoundDate(Date epoch){
        return new Date(epoch.getTime() + this.getMaxIntervals());
    }

    public Timestamp wraparoundInterval(){
        return new Timestamp(this.getMaxIntervals());
    }
}
