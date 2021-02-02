/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 06.02.12
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class FeedingSetting {

    private Long idOfSetting;
    private String settingName;
    private Long limit;
    private Long discount;
    private Boolean useDiscount;
    private Boolean useDiscountBuffet;
    private Date lastUpdate;
    private Set<Org> orgsInternal = new HashSet<Org>();
    private User user;

    public FeedingSetting() {

    }

    public FeedingSetting(String settingName, Long limit, Long discount, Boolean useDiscount, Boolean useDiscountBuffet, Set<Org> orgs) {
        this.settingName = settingName;
        this.limit = limit;
        this.discount = discount;
        this.useDiscountBuffet = useDiscountBuffet;
        this.useDiscount = useDiscount;
        this.orgsInternal = orgs;
        this.lastUpdate = new Date();
        this.user = DAOReadonlyService.getInstance().getUserFromSession();
    }

    public boolean isValidByOrg() {
        if (orgsInternal == null || orgsInternal.size() == 0) return true;
        List<Long> list = new ArrayList<Long>();
        for (Org org : orgsInternal) {
            list.add(org.getIdOfOrg());
        }
        return !DAOReadonlyService.getInstance().orgInFeedingSettingFound(list, idOfSetting);
    }

    @Override
    public String toString() {
        return "FeedingSetting{" +
                "idOfSetting=" + idOfSetting +
                ", settingName='" + settingName + '\'' +
                ", limit=" + limit +
                ", discount=" + discount +
                ", lastUpdate=" + CalendarUtils.dateTimeToString(lastUpdate) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FeedingSetting that = (FeedingSetting) o;

        if (idOfSetting != that.idOfSetting) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idOfSetting ^ (idOfSetting >>> 32));
        result = 31 * result + (settingName != null ? settingName.hashCode() : 0);
        return result;
    }

    public Long getIdOfSetting() {
        return idOfSetting;
    }

    public void setIdOfSetting(Long idOfSetting) {
        this.idOfSetting = idOfSetting;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<Org> getOrgsInternal() {
        return orgsInternal;
    }

    public void setOrgsInternal(Set<Org> orgsInternal) {
        this.orgsInternal = orgsInternal;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Boolean getUseDiscount() {
        return useDiscount;
    }

    public void setUseDiscount(Boolean useDiscount) {
        this.useDiscount = useDiscount;
    }

    public Boolean getUseDiscountBuffet() {
        return useDiscountBuffet;
    }

    public void setUseDiscountBuffet(Boolean useDiscountBuffet) {
        this.useDiscountBuffet = useDiscountBuffet;
    }
}
