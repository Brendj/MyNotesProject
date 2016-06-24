/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.internal.FrontController.FrontControllerException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 31.05.16
 * Time: 15:40
 */
public class MigrateRequest {
    private Long orgVisit;
    private Long migrateClientId;
    private Date startDate;
    private Date endDate;
    private String resolutionCause;
    private Long idOfClientResol;
    private String contactInfo;

    public MigrateRequest() {
    }

    public MigrateRequest(Long orgVisit, Long migrateClientId, Date startDate, Date endDate, String resolutionCause,
            Long idOfClientResol, String contactInfo) {
        this.orgVisit = orgVisit;
        this.migrateClientId = migrateClientId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.resolutionCause = resolutionCause;
        this.idOfClientResol = idOfClientResol;
        this.contactInfo = contactInfo;
    }

    public void validateMigrateRequest() throws FrontControllerException{
        Date date = CalendarUtils.startOfDay(new Date());
        if(startDate.after(CalendarUtils.AFTER_DATE) || date.after(startDate)){
            throw new FrontControllerException("Неверно введена начальная дата для клиента с ИД=" + migrateClientId);
        }
        if(endDate.after(CalendarUtils.AFTER_DATE)){
            throw new FrontControllerException("Неверно введена конечная дата для клиента с ИД=" + migrateClientId);
        }
        if(startDate.after(endDate)){
            throw new FrontControllerException("Начальная дата превышает конечную дату для клиента с ИД=" + migrateClientId);
        }
        if(resolutionCause.length() > 300){
            throw new FrontControllerException("Причина посещения клиента с ИД=" + migrateClientId + " превышает 300 символов");
        }
        if(contactInfo.length() > 100){
            throw new FrontControllerException("Контактные данные клиента с ИД=" + migrateClientId + " превышают 100 символов");
        }
    }

    public static String formRequestNumber(Long idOfOrg, Long idOfOrgVisit, Long idOfFirstRequest, Date startDate){
        return String.format("C:%s/%s-%s-%s", idOfOrg, idOfOrgVisit, (idOfFirstRequest * -1L), CalendarUtils.dateShortToString(startDate));
    }

    public static Map<Long, List<MigrateRequest>> sortMigrateRequestsByOrg(List<MigrateRequest> migrateRequests){
        Map<Long, List<MigrateRequest>> map = new HashMap<Long, List<MigrateRequest>>();
        for(MigrateRequest request : migrateRequests){
            Long idOfOrg = request.getOrgVisit();
            if(!map.containsKey(idOfOrg)){
                List<MigrateRequest> requestList = new ArrayList<MigrateRequest>();
                requestList.add(request);
                map.put(idOfOrg, requestList);
            } else {
                map.get(idOfOrg).add(request);
            }
        }
        return map;
    }

    public Long getOrgVisit() {
        return orgVisit;
    }

    public void setOrgVisit(Long orgVisit) {
        this.orgVisit = orgVisit;
    }

    public Long getMigrateClientId() {
        return migrateClientId;
    }

    public void setMigrateClientId(Long migrateClientId) {
        this.migrateClientId = migrateClientId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getResolutionCause() {
        return resolutionCause;
    }

    public void setResolutionCause(String resolutionCause) {
        this.resolutionCause = resolutionCause;
    }

    public Long getIdOfClientResol() {
        return idOfClientResol;
    }

    public void setIdOfClientResol(Long idOfClientResol) {
        this.idOfClientResol = idOfClientResol;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
