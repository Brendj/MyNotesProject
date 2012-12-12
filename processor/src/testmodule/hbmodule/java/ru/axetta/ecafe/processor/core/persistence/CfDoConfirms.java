package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDoConfirms {

    private long idofdoconfirm;

    public long getIdofdoconfirm() {
        return idofdoconfirm;
    }

    public void setIdofdoconfirm(long idofdoconfirm) {
        this.idofdoconfirm = idofdoconfirm;
    }

    private String distributedobjectclassname;

    public String getDistributedobjectclassname() {
        return distributedobjectclassname;
    }

    public void setDistributedobjectclassname(String distributedobjectclassname) {
        this.distributedobjectclassname = distributedobjectclassname;
    }

    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    private long orgowner;

    public long getOrgowner() {
        return orgowner;
    }

    public void setOrgowner(long orgowner) {
        this.orgowner = orgowner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDoConfirms that = (CfDoConfirms) o;

        if (idofdoconfirm != that.idofdoconfirm) {
            return false;
        }
        if (orgowner != that.orgowner) {
            return false;
        }
        if (distributedobjectclassname != null ? !distributedobjectclassname.equals(that.distributedobjectclassname)
                : that.distributedobjectclassname != null) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofdoconfirm ^ (idofdoconfirm >>> 32));
        result = 31 * result + (distributedobjectclassname != null ? distributedobjectclassname.hashCode() : 0);
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        return result;
    }
}
