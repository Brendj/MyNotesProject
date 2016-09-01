/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientphoto;

import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 10:31
 */
public class ClientPhotosItem {
    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;
    public static final Integer ERROR_CODE_PHOTO_NOT_SAVED = 110;

    private Long idOfClient;
    private BufferedImage imageData;
    private String errorMessage;
    private Integer resCode;

    public static ClientPhotosItem build(Node itemNode, Long orgOwner) {
        Long idOfClient = null;
        BufferedImage imageData = null;

        EMSetter emSetter = new EMSetter("");

        idOfClient = getLongValue(itemNode, "IdOfClient", emSetter, true);
        if(idOfClient != null) {
            Client client = DAOService.getInstance().findClientById(idOfClient);
            if (client == null) {
                emSetter.setCompositeErrorMessage(String.format("Клиент с ИД=%s не найден", idOfClient));
            } else {
                List<Long> orgsIds = DAOService.getInstance().findFriendlyOrgsIds(orgOwner);
                if(!orgsIds.contains(client.getOrg().getIdOfOrg())){
                    emSetter.setCompositeErrorMessage(String.format("Клиент с ИД=%s не принадлежит организации", idOfClient));
                }
            }

        }

        String imageString = XMLUtils.getAttributeValue(itemNode, "ImageData");
        if(StringUtils.isNotEmpty(imageString)){
            try {
                imageData = ImageUtils.getImageFromString(imageString);
                if(imageData == null) {
                    emSetter.setCompositeErrorMessage("Атрибут ImageData некорректен");
                }
                //ImageUtils.validateImage(imageData, null, null);
            //} catch (IOException e) {
            //    emSetter.setCompositeErrorMessage("Attribute ImageData is incorrect: " + e.getMessage());
            //} catch (ImageUtils.ImageUtilsException e){
            //    emSetter.setCompositeErrorMessage("Attribute ImageData is incorrect: " + e.getMessage());
            } catch (Exception e){
                emSetter.setCompositeErrorMessage("Атрибут ImageData некорректен");
            }
        } else {
            emSetter.setCompositeErrorMessage("Атрибут ImageData не найден");
        }

        return new ClientPhotosItem(idOfClient, imageData, emSetter.getStr());
    }

    private static Long getLongValue(Node itemNode, String nodeName, ISetErrorMessage www, boolean checkExists) {
        String str = XMLUtils.getAttributeValue(itemNode, nodeName);
        Long result = null;
        if(StringUtils.isNotEmpty(str)){
            try {
                result =  Long.parseLong(str);
            } catch (NumberFormatException e){
                www.setCompositeErrorMessage(String.format("NumberFormatException %s некорректен", nodeName));
            }
        } else {
            if (checkExists) {
                www.setCompositeErrorMessage(String.format("Атрибут %s не найден", nodeName));
            }
        }
        return result;
    }

    private static class EMSetter implements ISetErrorMessage {
        private String str;
        public EMSetter(String str) {
            this.setStr(str);
        }
        @Override
        public void setCompositeErrorMessage(String message) {
            setStr(str + message + ", ");
        }

        public String getStr() {
            if(str.length() > 2){
                return str.substring(0, str.length() - 2);
            } else {
                return str;
            }
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    private interface ISetErrorMessage {
        public void setCompositeErrorMessage(String message);
    }

    public ClientPhotosItem() {
    }

    public ClientPhotosItem(Long idOfClient, BufferedImage imageData, String errorMessage) {
        this.idOfClient = idOfClient;
        this.imageData = imageData;
        this.errorMessage = errorMessage;
        if(errorMessage.isEmpty() || errorMessage == null){
            this.resCode = ERROR_CODE_ALL_OK;
        } else {
            this.resCode = ERROR_CODE_NOT_VALID_ATTRIBUTE;
        }
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public BufferedImage getImageData() {
        return imageData;
    }

    public void setImageData(BufferedImage imageData) {
        this.imageData = imageData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }
}