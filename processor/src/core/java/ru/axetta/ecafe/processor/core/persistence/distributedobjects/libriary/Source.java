/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;

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

    private String sourceName;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "SourceName", sourceName);
    }

    @Override
    public Source parseAttributes(Node node) throws Exception{

        String sourceName = getStringAttributeValue(node, "SourceName", 127);
        if (sourceName != null) {
            setSourceName(sourceName);
        }
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setSourceName(((Source) distributedObject).getSourceName());
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
                "sourceName='" + sourceName + '\'' +
                '}';
    }
}
