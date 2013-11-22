/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 23.12.2009
 * Time: 11:35:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class ExecutorServiceWrappedJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceWrappedJob.class);

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            Runnable runnable = getRunnable(context);
            getExecutorService(context).submit(runnable);
        } catch (Exception e) {
            logger.error("Failed to submit runnable into executors service", e);
        }
    }

    protected abstract ExecutorService getExecutorService(JobExecutionContext context) throws Exception;

    protected abstract Runnable getRunnable(JobExecutionContext context) throws Exception;
}