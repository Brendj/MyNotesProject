/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: fazero
 * Date: 4/29/15
 * Time: 2:54 PM
 */
public class SyncHistoryCalc {

    /**
     * dataType - тип данных сохраняемых в таблице
     * 0 - успешных синхронизаций
     * 1 - отбитых синхронизаций
     * 3 - ошибок (при нормальной работе алгоритма их быть не должно)
     * 4 - Среднее время до следующей синхронизации
     * todo переписать для нормальной работы с enum
     */
    public static final int SUCCESSFUL_SYNC_COUNT_POSITION = 0;
    public static final int FILTERED_SYNC_COUNT_POSITION = 1;
    public static final int ERROR_SYNC_COUNT_POSITION = 2;
    public static final int AVG_RESYNC_TIME_POSITION = 3;
    private Integer dataType;
    private Long idOfSyncHistoryCalc;
    private Long idOfOrg;
    private Date syncDay;
    private String value;

    protected SyncHistoryCalc() {
    }

    public SyncHistoryCalc(Long idOfOrg, Date syncDay, Integer dataType, String value) {
        this.idOfOrg = idOfOrg;
        this.syncDay = syncDay;
        this.dataType = dataType;
        this.value = value;
    }

    public Long getIdOfSyncHistoryCalc() {
        return idOfSyncHistoryCalc;
    }

    public void setIdOfSyncHistoryCalc(Long idOfSyncHistoryCalc) {
        this.idOfSyncHistoryCalc = idOfSyncHistoryCalc;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getSyncDay() {
        return syncDay;
    }

    public void setSyncDay(Date syncDay) {
        this.syncDay = syncDay;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
