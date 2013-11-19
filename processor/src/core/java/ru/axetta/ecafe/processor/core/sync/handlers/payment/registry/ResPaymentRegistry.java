package ru.axetta.ecafe.processor.core.sync.handlers.payment.registry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Developer
 * Date: 28.10.13
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
public class ResPaymentRegistry {

    private final List<ResPaymentRegistryItem> items;

    public ResPaymentRegistry() {
        items = new ArrayList<ResPaymentRegistryItem>();
    }

    public void addItem(ResPaymentRegistryItem item) throws Exception {
        this.items.add(item);
    }

    public Iterator<ResPaymentRegistryItem> getItems() {
        return items.iterator();
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ResPaymentRegistry");
        for (ResPaymentRegistryItem item : this.items) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    @Override
    public String toString() {
        return "ResPaymentRegistry{" + "items=" + items + '}';
    }
}
