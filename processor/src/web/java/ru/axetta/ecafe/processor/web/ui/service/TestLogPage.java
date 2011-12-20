/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class TestLogPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(TestLogPage.class);

    private String text;

    public String getPageFilename() {
        return "service/test_log";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void fill(Session session) throws Exception {

    }

    public void writeTextToLog(Session session) throws Exception {
        logger.error(text);
    }
}