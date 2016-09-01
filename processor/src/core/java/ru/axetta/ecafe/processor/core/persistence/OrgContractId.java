package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 31.08.16
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
public class OrgContractId {
    private Long idOfOrg;
    private long lastClientContractId;
    private long version;
    private Org org;

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public long getLastClientContractId() {
        return lastClientContractId;
    }

    public void setLastClientContractId(long lastClientContractId) {
        this.lastClientContractId = lastClientContractId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "OrgContractId{" +
                "idOfOrg=" + idOfOrg +
                ", version=" + version +
                ", lastClientContractId=" + lastClientContractId +
                '}';
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }
}
