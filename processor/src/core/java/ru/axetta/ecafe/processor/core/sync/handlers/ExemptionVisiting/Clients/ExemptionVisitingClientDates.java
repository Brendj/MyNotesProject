/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting.Clients;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ProcessResultEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ExemptionVisitingClientDates implements AbstractToElement {
    private Date date;
    private Boolean eat;
    private Long version;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("D");
        element.setAttribute("date", CalendarUtils.dateShortToStringFullYear(date));
        element.setAttribute("visiting", eat.toString());
        element.setAttribute("version", version.toString());
        return element;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getEat() {
        return eat;
    }

    public void setEat(Boolean eat) {
        this.eat = eat;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
