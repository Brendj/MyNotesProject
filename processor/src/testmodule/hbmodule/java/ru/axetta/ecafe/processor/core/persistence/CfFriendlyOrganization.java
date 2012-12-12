package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfFriendlyOrganization {

    private long idoffriendlyorg;

    public long getIdoffriendlyorg() {
        return idoffriendlyorg;
    }

    public void setIdoffriendlyorg(long idoffriendlyorg) {
        this.idoffriendlyorg = idoffriendlyorg;
    }

    private long currentorg;

    public long getCurrentorg() {
        return currentorg;
    }

    public void setCurrentorg(long currentorg) {
        this.currentorg = currentorg;
    }

    private long friendlyorg;

    public long getFriendlyorg() {
        return friendlyorg;
    }

    public void setFriendlyorg(long friendlyorg) {
        this.friendlyorg = friendlyorg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfFriendlyOrganization that = (CfFriendlyOrganization) o;

        if (currentorg != that.currentorg) {
            return false;
        }
        if (friendlyorg != that.friendlyorg) {
            return false;
        }
        if (idoffriendlyorg != that.idoffriendlyorg) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoffriendlyorg ^ (idoffriendlyorg >>> 32));
        result = 31 * result + (int) (currentorg ^ (currentorg >>> 32));
        result = 31 * result + (int) (friendlyorg ^ (friendlyorg >>> 32));
        return result;
    }

    private CfOrgs cfOrgsByCurrentorg;

    public CfOrgs getCfOrgsByCurrentorg() {
        return cfOrgsByCurrentorg;
    }

    public void setCfOrgsByCurrentorg(CfOrgs cfOrgsByCurrentorg) {
        this.cfOrgsByCurrentorg = cfOrgsByCurrentorg;
    }

    private CfOrgs cfOrgsByFriendlyorg;

    public CfOrgs getCfOrgsByFriendlyorg() {
        return cfOrgsByFriendlyorg;
    }

    public void setCfOrgsByFriendlyorg(CfOrgs cfOrgsByFriendlyorg) {
        this.cfOrgsByFriendlyorg = cfOrgsByFriendlyorg;
    }
}
