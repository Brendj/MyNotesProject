/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions;

import ru.axetta.ecafe.processor.core.persistence.ZeroTransactionCriteriaEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 25.03.16
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransactionItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private Date date;
    private ZeroTransactionCriteriaEnum idOfCriteria;
    private Integer targetLevel;
    private Integer actualLevel;
    private Integer criteriaLevel;
    private Integer idOfReason;
    private String comment;
    private String errorMessage;
    private Integer resCode;
    private Long idOfOrg;

    private static class EMSetter implements ISetErrorMessage {
        private String str;
        public EMSetter(String str) {
            this.setStr(str);
        }
        @Override
        public void setCompositeErrorMessage(String message) {
            setStr(getStr() + message + "\n");
        }

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    public static ZeroTransactionItem build(Node itemNode, Long orgOwner) {
        Date date = null;
        ZeroTransactionCriteriaEnum idOfCriteria = null;
        Integer targetLevel = null;
        Integer actualLevel = null;
        Integer criteriaLevel = null;
        Integer idOfReason = null;
        String comment = null;
        String errorMessage = null;
        Integer resCode = null;
        Long orgOwnerId = null;

        EMSetter emSetter = new EMSetter("");

        String strDate = XMLUtils.getAttributeValue(itemNode, "Date");
        if(StringUtils.isNotEmpty(strDate)){
            try {
                date = CalendarUtils.parseDate(strDate);
            } catch (Exception e){
                emSetter.setCompositeErrorMessage("Attribute Date not found or incorrect");
            }
        } else {
            emSetter.setCompositeErrorMessage("Attribute Date not found");
        }

        String strIdOfCriteria = XMLUtils.getAttributeValue(itemNode, "IdOfCriteria");
        if(StringUtils.isNotEmpty(strIdOfCriteria)){
            try {
                idOfCriteria = ZeroTransactionCriteriaEnum.fromInteger(Integer.parseInt(strIdOfCriteria));
            } catch (NumberFormatException e){
                emSetter.setCompositeErrorMessage("NumberFormatException IdOfCriteria is incorrect");
            } catch (IllegalArgumentException e) {
                emSetter.setCompositeErrorMessage("Not valid IdOfCriteria value");
            }
        } else {
            emSetter.setCompositeErrorMessage("Attribute IdOfCriteria not found");
        }

        targetLevel = getIntValue(itemNode, "TargetLevel", emSetter, true);
        actualLevel = getIntValue(itemNode, "ActualLevel", emSetter, true);
        criteriaLevel = getIntValue(itemNode, "CriteriaLevel", emSetter, true);
        idOfReason = getIntValue(itemNode, "IdOfReason", emSetter, false);
        comment = XMLUtils.getAttributeValue(itemNode, "Comment");

        return new ZeroTransactionItem(date, idOfCriteria, targetLevel, actualLevel, criteriaLevel, idOfReason, comment, orgOwner, emSetter.getStr());
    }

    private static Integer getIntValue(Node itemNode, String nodeName, ISetErrorMessage www, boolean checkExists) {
        String str = XMLUtils.getAttributeValue(itemNode, nodeName);
        Integer result = null;
        if(StringUtils.isNotEmpty(str)){
            try {
                result =  Integer.parseInt(str);
            } catch (NumberFormatException e){
                www.setCompositeErrorMessage(String.format("NumberFormatException %s is incorrect", nodeName));
            }
        } else {
            if (checkExists) {
                www.setCompositeErrorMessage(String.format("Attribute %s not found", nodeName));
            }
        }
        return result;
    }

    public ZeroTransactionItem() {

    }

    private ZeroTransactionItem(Date date, ZeroTransactionCriteriaEnum idOfCriteria, Integer targetLevel, Integer actualLevel, Integer
                criteriaLevel, Integer idOfReason, String comment, Long orgOwnerId, String errorMessage) {
        this.setDate(date);
        this.setIdOfCriteria(idOfCriteria);
        this.setTargetLevel(targetLevel);
        this.setActualLevel(actualLevel);
        this.setCriteriaLevel(criteriaLevel);
        this.setIdOfReason(idOfReason);
        this.setComment(comment);
        this.setIdOfOrg(orgOwnerId);
        this.setErrorMessage(errorMessage);
        if (errorMessage.equals("")) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
    }

    private interface ISetErrorMessage {
        public void setCompositeErrorMessage(String message);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ZeroTransactionCriteriaEnum getIdOfCriteria() {
        return idOfCriteria;
    }

    public void setIdOfCriteria(ZeroTransactionCriteriaEnum idOfCriteria) {
        this.idOfCriteria = idOfCriteria;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }

    public Integer getActualLevel() {
        return actualLevel;
    }

    public void setActualLevel(Integer actualLevel) {
        this.actualLevel = actualLevel;
    }

    public Integer getCriteriaLevel() {
        return criteriaLevel;
    }

    public void setCriteriaLevel(Integer criteriaLevel) {
        this.criteriaLevel = criteriaLevel;
    }

    public Integer getIdOfReason() {
        return idOfReason;
    }

    public void setIdOfReason(Integer idOfReason) {
        this.idOfReason = idOfReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
