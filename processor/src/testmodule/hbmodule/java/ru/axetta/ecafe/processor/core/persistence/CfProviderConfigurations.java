package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfProviderConfigurations {

    private long idofconfigurationprovider;

    public long getIdofconfigurationprovider() {
        return idofconfigurationprovider;
    }

    public void setIdofconfigurationprovider(long idofconfigurationprovider) {
        this.idofconfigurationprovider = idofconfigurationprovider;
    }

    private String nameofconfigurationprovider;

    public String getNameofconfigurationprovider() {
        return nameofconfigurationprovider;
    }

    public void setNameofconfigurationprovider(String nameofconfigurationprovider) {
        this.nameofconfigurationprovider = nameofconfigurationprovider;
    }

    private long idofusercreate;

    public long getIdofusercreate() {
        return idofusercreate;
    }

    public void setIdofusercreate(long idofusercreate) {
        this.idofusercreate = idofusercreate;
    }

    private long idofuseredit;

    public long getIdofuseredit() {
        return idofuseredit;
    }

    public void setIdofuseredit(long idofuseredit) {
        this.idofuseredit = idofuseredit;
    }

    private long idofuserdelete;

    public long getIdofuserdelete() {
        return idofuserdelete;
    }

    public void setIdofuserdelete(long idofuserdelete) {
        this.idofuserdelete = idofuserdelete;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private long lastupdate;

    public long getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(long lastupdate) {
        this.lastupdate = lastupdate;
    }

    private long deletedate;

    public long getDeletedate() {
        return deletedate;
    }

    public void setDeletedate(long deletedate) {
        this.deletedate = deletedate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfProviderConfigurations that = (CfProviderConfigurations) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (deletedate != that.deletedate) {
            return false;
        }
        if (idofconfigurationprovider != that.idofconfigurationprovider) {
            return false;
        }
        if (idofusercreate != that.idofusercreate) {
            return false;
        }
        if (idofuserdelete != that.idofuserdelete) {
            return false;
        }
        if (idofuseredit != that.idofuseredit) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (nameofconfigurationprovider != null ? !nameofconfigurationprovider.equals(that.nameofconfigurationprovider)
                : that.nameofconfigurationprovider != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofconfigurationprovider ^ (idofconfigurationprovider >>> 32));
        result = 31 * result + (nameofconfigurationprovider != null ? nameofconfigurationprovider.hashCode() : 0);
        result = 31 * result + (int) (idofusercreate ^ (idofusercreate >>> 32));
        result = 31 * result + (int) (idofuseredit ^ (idofuseredit >>> 32));
        result = 31 * result + (int) (idofuserdelete ^ (idofuserdelete >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        return result;
    }
}
