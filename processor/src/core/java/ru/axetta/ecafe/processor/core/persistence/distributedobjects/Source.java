/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 17.08.12
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public class Source extends DistributedObject {

    private long idOfSource;
    private String sourceName;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public Source parseAttributes(Node node) {

        String sourceName = getStringAttributeValue(node, "sourceName", 1024);
        if (sourceName != null) {
            setSourceName(sourceName);
        }
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setSourceName(((Source) distributedObject).getSourceName());
    }

    public long getIdOfSource() {
        return idOfSource;
    }

    public void setIdOfSource(long idOfSource) {
        this.idOfSource = idOfSource;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "Source{" +
                "idOfSource=" + idOfSource +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }
}
