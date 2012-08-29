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
 * Date: 18.08.12
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
public class InventoryBook extends DistributedObject {

    private String bookName;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "Guid", guid);
    }

    @Override
    public InventoryBook parseAttributes(Node node) throws Exception{

        String bookName = getStringAttributeValue(node, "bookName", 256);
        if (bookName != null) {
            setBookName(bookName);
        }
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
        return "InventoryBook{" +
                "bookName='" + bookName + '\'' +
                '}';
    }
}
