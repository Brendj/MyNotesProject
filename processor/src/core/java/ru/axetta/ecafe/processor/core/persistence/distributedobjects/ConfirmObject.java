/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.06.12
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class ConfirmObject extends DistributedObject {

    private String action;
    private String nodeName;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    protected void appendAttributes(Element element) {}

    @Override
    public Element toElement(Element element) {
        setAttribute(element,"GID",String.valueOf(globalId));
        setAttribute(element, "V", String.valueOf(globalVersion));
        setAttribute(element, "D", status);
        if(getLocalID()!=null) setAttribute(element,"LID",String.valueOf(getLocalID()));
        return element;
    }

    @Override
    public DistributedObject build(Node node) {
        return null;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
