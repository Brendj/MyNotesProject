/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.items;

import ru.axetta.ecafe.processor.core.persistence.*;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 30.09.13
 * Time: 12:30
 */

public class ClientPassItem implements Comparable {

    private String orgName;
    private Date enterTime;
    private String enterName;
    private String direction;
    private Long idOfOrg;
    private String shortAddress;
    private List<ClientChekerPassItem> chekerItemList = new ArrayList<ClientChekerPassItem>();

    public ClientPassItem(Session session, EnterEvent event) {
        this.orgName = event.getOrg().getShortName();
        this.idOfOrg = event.getOrg().getIdOfOrg();
        this.shortAddress = event.getOrg().getShortAddress();
        this.enterTime = event.getEvtDateTime();
        this.enterName = event.getEnterName();
        this.direction = getDirection(event.getPassDirection());
        Long checkerId = event.getChildPassCheckerId();
        Long guardianId = event.getGuardianId();
        if (checkerId != null) {
            Client cheker = (Client) session.get(Client.class, checkerId);
            this.chekerItemList.add(new ClientChekerPassItem(cheker.getIdOfClient(), cheker.getContractId(),
                    cheker.getPerson().getFullName(),
                    (null != cheker.getClientGroup()) ? cheker.getClientGroup().getGroupName() : ""));
        }
        if (guardianId != null) {
            Client guardian = (Client) session.get(Client.class, guardianId);
            this.chekerItemList.add(new ClientChekerPassItem(guardian.getIdOfClient(), guardian.getContractId(),
                    guardian.getPerson().getFullName(),
                    (null != guardian.getClientGroup()) ? guardian.getClientGroup().getGroupName() : ""));
        }
        if(checkerId == null && guardianId == null) {
            this.chekerItemList.add(new ClientChekerPassItem(0L, null, " ", " "));
        }
    }

    public ClientPassItem(ExternalEvent event) {
        this.orgName = event.getOrgName();
        this.enterTime = event.getEvtDateTime();
        this.enterName = event.getEnterName();
        if (event.getEvtType().equals(ExternalEventType.MUSEUM)) {
            if (event.getEvtStatus().equals(ExternalEventStatus.TICKET_GIVEN)) {
                this.direction = getDirection(EnterEvent.ENTRY);
            } else if (event.getEvtStatus().equals(ExternalEventStatus.TICKET_BACK)) {
                this.direction = getDirection(EnterEvent.PASSAGE_RUFUSAL);
            }
        }

    }

    @Override
    public int compareTo(Object o) {
        return enterTime.compareTo(((ClientPassItem)o).getEnterTime());
    }

    public String getOrgName() {
        return orgName;
    }

    public Date getEnterTime() {
        return enterTime;
    }

    public String getEnterName() {
        return enterName;
    }

    public String getDirection() {
        return direction;
    }

    public List<ClientChekerPassItem> getChekerItemList() {
        return chekerItemList;
    }

    public Integer getChekerItemListCount() {
        return chekerItemList.size() + 1;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    private String getDirection(int direction) {
        switch (direction) {
            case EnterEvent.ENTRY:
                return "вход";
            case EnterEvent.EXIT:
                return "выход";
            case EnterEvent.PASSAGE_IS_FORBIDDEN:
                return "проход запрещен";
            case EnterEvent.TURNSTILE_IS_BROKEN:
                return "взлом турникета";
            case EnterEvent.EVENT_WITHOUT_PASSAGE:
                return "событие без прохода";
            case EnterEvent.PASSAGE_RUFUSAL:
                return "отказ от прохода";
            case EnterEvent.RE_ENTRY:
                return "повторный вход";
            case EnterEvent.RE_EXIT:
                return "повторный выход";
            case EnterEvent.QUERY_FOR_ENTER:
                return "запрос на вход";
            case EnterEvent.QUERY_FOR_EXIT:
                return "запрос на выход";
            case EnterEvent.DETECTED_INSIDE:
                return "обнаружен на подносе карты внутри здания";
            case EnterEvent.CHECKED_BY_TEACHER_EXT:
                return "отмечен в классном журнале через внешнюю систему";
            case EnterEvent.CHECKED_BY_TEACHER_INT:
                return "отмечен учителем внутри здания";
            default:
                return "Ошибка обратитесь администратору";
        }
    }

    public static class ClientChekerPassItem {
        private Long idOfClient;
        private Long contractId;
        private String cheker;
        private String groupName;

        public ClientChekerPassItem(Long idOfClient, Long contractId, String cheker, String groupName) {
            this.idOfClient = idOfClient;
            this.contractId = contractId;
            this.cheker = cheker;
            this.groupName = groupName;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public Long getContractId() {
            return contractId;
        }

        public void setContractId(Long contractId) {
            this.contractId = contractId;
        }

        public String getCheker() {
            return cheker;
        }

        public void setCheker(String cheker) {
            this.cheker = cheker;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }
}
