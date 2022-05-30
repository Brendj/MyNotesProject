package ru.axetta.ecafe.processor.web.ui.guardianservice;

import ru.axetta.ecafe.processor.core.partner.mesh.json.Category;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import java.util.Arrays;
import java.util.List;

public class CGItem implements Comparable {
    private Long idOfClient;
    private Long idOfGuardin;
    private String fio;
    private String mobile;
    private Long cardno;
    private Integer state;
    private boolean sameOrg;
    private boolean processed;
    private Long idOfClientGuardian;
    private Long cardLastUpdate;
    private Long balance;
    private Long idOfClientGroup;
    private Long guardianLastUpdate;
    private Boolean deletedState;
    private Boolean disabled;
    private Long guardianOrg;
    private Long clientOrg;

    public static final List<Long> GROUPS = Arrays.asList(1100000000L, 1100000010L, 1100000050L, 1100000020L,
            1100000030L, 1100000100L, 1100000110L);

    public static final Long GROUP_PARENTS = 1100000030L;

    public CGItem(Long idOfClient, Long idOfGuardin, String fio, String mobile, Long cardno, Integer state,
                  boolean sameOrg, Long idOfClientGuardian, Long cardLastUpdate, Long balance,
                  Long idOfClientGroup, Long guardianLastUpdate, Boolean deletedState, Integer disabled,
                  Long guardianOrg, Long clientOrg) {
        this.idOfClient = idOfClient;
        this.idOfGuardin = idOfGuardin;
        this.fio = fio.toLowerCase().replaceAll("ё", "е");
        this.mobile = mobile;
        this.cardno = cardno;
        this.state = state;
        this.sameOrg = sameOrg;
        processed = false;
        this.idOfClientGuardian = idOfClientGuardian;
        this.cardLastUpdate = cardLastUpdate;
        this.balance = balance;
        this.idOfClientGroup = idOfClientGroup;
        this.guardianLastUpdate = guardianLastUpdate;
        this.deletedState = deletedState;
        this.disabled = disabled == 1 ? true : false;
        this.guardianOrg = guardianOrg;
        this.clientOrg = clientOrg;
    }

    public boolean isSotrudnik() {
        return guardianOrg.equals(ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue())
                || guardianOrg.equals(ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue())
                || guardianOrg.equals(ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue())
                || guardianOrg.equals(ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof CGItem)) {
            return 1;
        }
        CGItem item = (CGItem) o;
        int priorityThis = 0;
        int priorityItem = 0;
        if (this.idOfClientGroup == GROUP_PARENTS) {
            if (!this.deletedState) priorityThis = 1;
        } else {
            
        }

        /*if (!this.deletedState && item.getDeletedState()) return -1;
        if (this.deletedState && !item.getDeletedState()) return 1;
        int indexThis = GROUPS.indexOf(this.idOfClientGroup);
        int indexItem = GROUPS.indexOf(item.getIdOfClientGroup());
        if ((indexThis == -1) && (indexItem == -1)) return 0;
        if ((indexThis == -1) && (indexItem > -1)) return -1;
        if ((indexThis > -1) && (indexItem == -1)) return 1;

        int res = Integer.valueOf(indexThis).compareTo(indexItem);
        if (res == 0) {
            if (this.cardno != null && item.getCardno() == null) return -1;
            if (this.cardno == null && item.getCardno() != null) return 1;
            if (this.cardno != null && item.getCardno() != null) {
                int cardRes = -cardLastUpdate.compareTo(item.getCardLastUpdate());
                if (cardRes == 0) {
                    return -guardianLastUpdate.compareTo(item.getGuardianLastUpdate());
                } else {
                    return cardRes;
                }
            }
            return -guardianLastUpdate.compareTo(item.getGuardianLastUpdate());
        } else {
            return res;
        }*/
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CGItem)) {
            return false;
        }
        if (((CGItem) o).getIdOfGuardin().equals(this.getIdOfGuardin())) return true;
        return false;
    }

    public String getFioPlusMobile() {
        return fio.concat(mobile);
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfGuardin() {
        return idOfGuardin;
    }

    public void setIdOfGuardin(Long idOfGuardin) {
        this.idOfGuardin = idOfGuardin;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getCardno() {
        return cardno;
    }

    public void setCardno(Long cardno) {
        this.cardno = cardno;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public boolean isSameOrg() {
        return sameOrg;
    }

    public void setSameOrg(boolean sameOrg) {
        this.sameOrg = sameOrg;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Long getIdOfClientGuardian() {
        return idOfClientGuardian;
    }

    public void setIdOfClientGuardian(Long idOfClientGuardian) {
        this.idOfClientGuardian = idOfClientGuardian;
    }

    public Long getCardLastUpdate() {
        return cardLastUpdate;
    }

    public void setCardLastUpdate(Long cardLastUpdate) {
        this.cardLastUpdate = cardLastUpdate;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Long getGuardianLastUpdate() {
        return guardianLastUpdate;
    }

    public void setGuardianLastUpdate(Long guardianLastUpdate) {
        this.guardianLastUpdate = guardianLastUpdate;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Long getGuardianOrg() {
        return guardianOrg;
    }

    public void setGuardianOrg(Long guardianOrg) {
        this.guardianOrg = guardianOrg;
    }

    public Long getClientOrg() {
        return clientOrg;
    }

    public void setClientOrg(Long clientOrg) {
        this.clientOrg = clientOrg;
    }
}
