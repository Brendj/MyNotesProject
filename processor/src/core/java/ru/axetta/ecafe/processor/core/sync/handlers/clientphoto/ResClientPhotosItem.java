/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientphoto;

import ru.axetta.ecafe.processor.core.persistence.ClientPhoto;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 12:20
 */
public class ResClientPhotosItem {
    private Long idOfClient;
    private Long version;
    private String imageData;
    private Integer resCode;
    private String errorMessage;

    public ResClientPhotosItem() {
    }

    public ResClientPhotosItem(ClientPhoto clientPhoto){
        this.idOfClient = clientPhoto.getIdOfClient();
        this.version = clientPhoto.getVersion();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", idOfClient);
        XMLUtils.setAttributeIfNotNull(element, "ImageData", imageData);
        XMLUtils.setAttributeIfNotNull(element, "Res", resCode);
        if (resCode != null && resCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        return element;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
