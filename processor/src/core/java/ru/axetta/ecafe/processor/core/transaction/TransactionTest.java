/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.transaction;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 25.01.12
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class TransactionTest {

    private static final Logger logger= LoggerFactory.getLogger(TransactionTest.class);
    
    @PostConstruct
    public void init() throws Exception{
        logger.info("TransactionTest init");
    }
}
