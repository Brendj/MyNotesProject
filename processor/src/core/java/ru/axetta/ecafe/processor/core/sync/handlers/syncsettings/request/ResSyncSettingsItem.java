/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResSyncSettingsItem implements AbstractToElement {
    private Integer contentTypeInt;
    private Long version;
    private ProcessResultEnum result;

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("ReSS");
        element.setAttribute("ContentType", contentTypeInt.toString());
        if(version != null) {
            element.setAttribute("V", version.toString());
        }
        element.setAttribute("Res", result.getCode().toString());
        element.setAttribute("Error", result.toString());

        return element;
    }

    public Integer getContentTypeInt() {
        return contentTypeInt;
    }

    public void setContentTypeInt(Integer contentTypeInt) {
        this.contentTypeInt = contentTypeInt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public ProcessResultEnum getResult() {
        return result;
    }

    public void setResult(ProcessResultEnum result) {
        this.result = result;
    }
}
