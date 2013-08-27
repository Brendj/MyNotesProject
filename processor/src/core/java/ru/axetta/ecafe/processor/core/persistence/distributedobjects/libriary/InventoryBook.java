/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 18.08.12
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
public class InventoryBook extends DistributedObject {

    @Override
    protected void appendAttributes(Element element) {
        //setAttribute(element, "BookName", bookName);
    }

    @Override
    public InventoryBook parseAttributes(Node node) throws Exception {
        String bookName = XMLUtils.getStringAttributeValue(node, "BookName", 256);
        if (bookName != null)
            setBookName(bookName);
        setSendAll(SendToAssociatedOrgs.DontSend);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setBookName(((InventoryBook) distributedObject).getBookName());
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public String toString() {
        return String.format("InventoryBook{bookName='%s'}", bookName);
    }

    private String bookName;
    private Set<Instance> instanceInternal;

    public Set<Instance> getInstanceInternal() {
        return instanceInternal;
    }

    public void setInstanceInternal(Set<Instance> instanceInternal) {
        this.instanceInternal = instanceInternal;
    }
}
