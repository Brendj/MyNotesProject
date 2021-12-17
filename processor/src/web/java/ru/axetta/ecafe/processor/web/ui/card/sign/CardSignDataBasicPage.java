/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card.sign;

import ru.axetta.ecafe.processor.core.persistence.CardSign;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 29.09.2017.
 */
public class CardSignDataBasicPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(CardSignDataBasicPage.class);
    protected byte[] signData;
    protected String signTypeCard;
    protected String signTypeProvider;
    protected Integer manufacturerCode;
    protected String manufacturerName;
    protected Boolean newProvider;

    public static final String NEW_TYPE_PROVIDER = "По новому типу";
    public static final String TYPE_PROVIDER = "По старому типу";

    public void fileUploadListener(FileUploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UploadedFile item = event.getUploadedFile();
        InputStream inputStream = null;
        try {
            byte[] data = item.getData();
            inputStream = new ByteArrayInputStream(data);
            signData = IOUtils.toByteArray(inputStream);

            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Данные ключа успешно загружены из файла", null));
        } catch (Exception e) {
            logger.error("Failed to load orgs from file", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при загрузке файла ключа: " + e.getMessage(),
                            null));
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.error("failed to close input stream", e);
                }
            }
        }
    }

    public List<SelectItem> getTypes(int type) {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(0, CardSign.CARDSIGN_SCRIPT_TYPE));
        items.add(new SelectItem(1, CardSign.CARDSIGN_ECDSA_TYPE));
        if (type == 2)
            items.add(new SelectItem(2, CardSign.CARDSIGN_GOST2012_TYPE));
        return items;
    }

    public List<SelectItem> getTypesRegister() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(true, NEW_TYPE_PROVIDER));
        items.add(new SelectItem(false, TYPE_PROVIDER));
        return items;
    }

    public String getSignDataSize() {
        if (signData == null) return "{нет}";
        return String.format("{Двоичные данные, размер %s байт}", signData.length);
    }

    public byte[] getSignData() {
        return signData;
    }

    public void setSignData(byte[] signData) {
        this.signData = signData;
    }


    public Integer getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(Integer manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getSignTypeCard() {
        return signTypeCard;
    }

    public void setSignTypeCard(String signTypeCard) {
        this.signTypeCard = signTypeCard;
    }

    public String getSignTypeProvider() {
        return signTypeProvider;
    }

    public void setSignTypeProvider(String signTypeProvider) {
        this.signTypeProvider = signTypeProvider;
    }

    public Boolean getNewProvider() {
        if (newProvider == null)
            return false;
        return newProvider;
    }

    public void setNewProvider(Boolean newProvider) {
        this.newProvider = newProvider;
    }
}
