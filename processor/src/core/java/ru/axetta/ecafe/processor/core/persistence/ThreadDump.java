/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by i.semenov on 11.05.2017.
 */
public class ThreadDump {
    private Long idOfThreadDump;
    private Date dateTime;
    private String node;
    private Long totalCPUTime;
    private Long duration;
    private String problemStacks;
    private String dumpStack;

    public ThreadDump() {

    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public Long getTotalCPUTime() {
        return totalCPUTime;
    }

    public void setTotalCPUTime(Long totalCPUTime) {
        this.totalCPUTime = totalCPUTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getProblemStacks() {
        return problemStacks;
    }

    public void setProblemStacks(String problemStacks) {
        this.problemStacks = problemStacks;
    }

    public String getDumpStack() {
        return dumpStack;
    }

    public void setDumpStack(String dumpStack) {
        this.dumpStack = dumpStack;
    }

    public Long getIdOfThreadDump() {
        return idOfThreadDump;
    }

    public void setIdOfThreadDump(Long idOfThreadDump) {
        this.idOfThreadDump = idOfThreadDump;
    }
}
