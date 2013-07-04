package ru.axetta.ecafe.processor.core.sync.handlers.temp.cards.operations;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */
public class ResTempCardOperation {

    private final long idOfTempCardOperation;
    private final int resOperation;
    private final String error;

    public ResTempCardOperation(long idOfTempCardOperation, int resOperation, String error) {
        this.idOfTempCardOperation = idOfTempCardOperation;
        this.resOperation = resOperation;
        this.error = error;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("RTCO");
        element.setAttribute("IdOfTempCardOperation", Long.toString(this.idOfTempCardOperation));
        element.setAttribute("ResOperation", Long.toString(this.resOperation));
        if (null != this.error) {
            element.setAttribute("Error", this.error);
        }
        return element;
    }
}
