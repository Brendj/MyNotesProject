/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.support;

import ru.axetta.ecafe.processor.core.mail.File;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 16.12.2009
 * Time: 11:22:22
 * To change this template use File | Settings | File Templates.
 */
public interface SupportEmailSender {

    void postSupportEmail(String address, String subject, String text, List<File> files) throws Exception;

}