/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.special.dates;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 10:31
 */
public class SpecialDatesItem {
    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;
    public static final Integer ERROR_CODE_NOT_FOUND_GROUPNAME = 101;

    private Long idOfOrg;
    private Date date;
    private Boolean isWeekend;
    private String comment;
    private Boolean delete;
    private Long idOfOrgOwner;
    private String groupName;
    private String errorMessage;
    private Integer resCode;
    private String staffGuid;
    private Date armLastUpdate;

    public static SpecialDatesItem build(Node itemNode, Long orgOwner, List<Long> friendlyOrgs) {
        Long idOfOrg = null;
        Date date = null;
        Boolean isWeekend = null;
        String comment = null;
        String groupName = null;
        Boolean delete = false;
        Date armLastUpdate = null;
        String staffGuid = null;

        EMSetter emSetter = new EMSetter("");

        idOfOrg = getIntValue(itemNode, "IdOfOrg", emSetter, true).longValue();
        try {
            if (!friendlyOrgs.contains(idOfOrg)) {
                emSetter.setCompositeErrorMessage(
                        String.format("Org id=%s is not friendly to Org id=%s", idOfOrg, orgOwner));
            }
        } catch (NumberFormatException e){
            emSetter.setCompositeErrorMessage("NumberFormatException OrgId not found");
        }

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

        String strArmLastUpdate = XMLUtils.getAttributeValue(itemNode, "LastUpdate");
        if(StringUtils.isNotEmpty(strArmLastUpdate)){
            try {
                armLastUpdate = CalendarUtils.parseDateWithDayTime(strArmLastUpdate);
            } catch (Exception e){
                emSetter.setCompositeErrorMessage("Attribute LastUpdate not found or incorrect");
            }
        }

        staffGuid = XMLUtils.getAttributeValue(itemNode, "GuidOfStaff");

        Integer intIsWeekend = getIntValue(itemNode, "IsWeekend", emSetter, true);
        if(intIsWeekend == 1){
            isWeekend = true;
        } else if(intIsWeekend == 0){
            isWeekend = false;
        } else {
            emSetter.setCompositeErrorMessage("Attribute IsWeekend is incorrect");
        }

        comment = XMLUtils.getAttributeValue(itemNode, "Comment");
        groupName = XMLUtils.getAttributeValue(itemNode, "GroupName");

        Integer intDelete = getIntValue(itemNode, "D", emSetter, false);
        if(intDelete != null){
            if(intDelete == 1){
                delete = true;
            } else {
                emSetter.setCompositeErrorMessage("Attribute D is incorrect");
            }
        }

        return new SpecialDatesItem(idOfOrg, date, isWeekend, comment, groupName, delete, orgOwner, emSetter.getStr(), staffGuid, armLastUpdate);
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStaffGuid() {
        return staffGuid;
    }

    public void setStaffGuid(String staffGuid) {
        this.staffGuid = staffGuid;
    }

    public Date getArmLastUpdate() {
        return armLastUpdate;
    }

    public void setArmLastUpdate(Date armLastUpdate) {
        this.armLastUpdate = armLastUpdate;
    }

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

    private interface ISetErrorMessage {
        public void setCompositeErrorMessage(String message);
    }

    public SpecialDatesItem() {
    }

    public SpecialDatesItem(Long idOfOrg, Date date, Boolean isWeekend, String comment, String groupName, Boolean delete,
            Long idOfOrgOwner, String errorMessage, String staffGuid, Date armLastUpdate) {
        this.idOfOrg = idOfOrg;
        this.date = date;
        this.isWeekend = isWeekend;
        this.comment = comment;
        this.groupName = groupName;
        this.delete = delete;
        this.idOfOrgOwner = idOfOrgOwner;
        this.errorMessage = errorMessage;
        if(errorMessage.isEmpty() || errorMessage == null){
            this.resCode = ERROR_CODE_ALL_OK;
        } else {
            this.resCode = ERROR_CODE_NOT_VALID_ATTRIBUTE;
        }
        this.staffGuid = staffGuid;
        this.armLastUpdate = armLastUpdate;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Long getIdOfOrgOwner() {
        return idOfOrgOwner;
    }

    public void setIdOfOrgOwner(Long idOfOrgOwner) {
        this.idOfOrgOwner = idOfOrgOwner;
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
