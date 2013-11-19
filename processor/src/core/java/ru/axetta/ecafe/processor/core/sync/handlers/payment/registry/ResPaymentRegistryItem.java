package ru.axetta.ecafe.processor.core.sync.handlers.payment.registry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: Developer
 * Date: 28.10.13
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class ResPaymentRegistryItem {
    private final long idOfOrder;
    private final int result;
    private final String error;

    public ResPaymentRegistryItem(long idOfOrder, int result, String error) {
        this.idOfOrder = idOfOrder;
        this.result = result;
        this.error = error;
    }

    public long getIdOfOrder() {
        return idOfOrder;
    }

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("RPT");
        element.setAttribute("IdOfOrder", Long.toString(this.idOfOrder));
        element.setAttribute("Result", Integer.toString(this.result));
        if (null != this.error) {
            element.setAttribute("Error", this.error);
        }
        return element;
    }

    @Override
    public String toString() {
        return "Item{" + "idOfOrder=" + idOfOrder + ", result=" + result + ", error='" + error + '\'' + '}';
    }
}
