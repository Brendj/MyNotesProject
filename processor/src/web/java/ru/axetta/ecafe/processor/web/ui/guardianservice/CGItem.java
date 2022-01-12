package ru.axetta.ecafe.processor.web.ui.guardianservice;

import ru.axetta.ecafe.processor.core.partner.mesh.json.Category;

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

    public CGItem(Long idOfClient, Long idOfGuardin, String fio, String mobile, Long cardno, Integer state,
                  boolean sameOrg, Long idOfClientGuardian, Long cardLastUpdate, Long balance,
                  Long idOfClientGroup) {
        this.idOfClient = idOfClient;
        this.idOfGuardin = idOfGuardin;
        this.fio = fio;
        this.mobile = mobile;
        this.cardno = cardno;
        this.state = state;
        this.sameOrg = sameOrg;
        processed = false;
        this.idOfClientGuardian = idOfClientGuardian;
        this.cardLastUpdate = cardLastUpdate;
        this.balance = balance;
        this.idOfClientGroup = idOfClientGroup;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof CGItem)) {
            return 1;
        }
        CGItem item = (CGItem) o;
        if (this.getCardno() != null && item.getCardno() == null) return 1;
        if (this.getCardno() == null && item.getCardno() == null) return 0;
        if (this.getCardno() == null && item.getCardno() != null) return -1;
        if (this.getCardno() != null && item.getCardno() != null) {
            return this.getCardLastUpdate().compareTo(item.getCardLastUpdate());
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CGItem)) {
            return false;
        }
        if (((CGItem) o).getIdOfClientGuardian().equals(this.getIdOfClientGuardian())) return true;
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
}
