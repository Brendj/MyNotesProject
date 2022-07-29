package ru.axetta.ecafe.processor.core.persistence.foodbox;

import ru.axetta.ecafe.processor.core.persistence.Org;

public class FoodBoxOrgReq {
    private Long orglockId;
    private Org org;
    private Long version;
    private Long currentversion;


    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Long getOrglockId() {
        return orglockId;
    }

    public void setOrglockId(Long orglockId) {
        this.orglockId = orglockId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getCurrentversion() {
        return currentversion;
    }

    public void setCurrentversion(Long currentversion) {
        this.currentversion = currentversion;
    }
}
