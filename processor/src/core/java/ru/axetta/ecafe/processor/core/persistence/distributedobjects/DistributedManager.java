/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.06.12
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class DistributedManager {

    private List<DistributedObject> distributedObjects = new LinkedList<DistributedObject>();

    public void addDistributedObject(DistributedObject distributedObject){
        distributedObjects.add(distributedObject);
    }

    public void sortByType(){
        Collections.sort(distributedObjects);
    }

    private Element getProductElement(Document document){
        return document.createElement("Pr");
    }

    public Element getElements(Document document){
        Element element = getProductElement(document);
        for (DistributedObject distributedObject: distributedObjects){
            if(distributedObject instanceof ProductGuide){
                ProductGuide productGuide = (ProductGuide) distributedObject;
                element.appendChild(productGuide.toElement(document));
            }
        }
        return element;
    }

    public void parseXML(Node node){

    }
}
