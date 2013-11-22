/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 23.12.2009
 * Time: 11:35:52
 * To change this template use File | Settings | File Templates.
 */
public class ExecutorServiceWrappedTask implements Runnable {

    private final ExecutorService executorService;
    private final Runnable runnable;

    public ExecutorServiceWrappedTask(ExecutorService executorService, Runnable runnable) {
        this.executorService = executorService;
        this.runnable = runnable;
    }

    public void run() {
        executorService.submit(runnable);
    }
}