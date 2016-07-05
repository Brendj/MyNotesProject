/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.08.13
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
public interface AbstractToElement {

    Element toElement(Document document) throws Exception;

}
