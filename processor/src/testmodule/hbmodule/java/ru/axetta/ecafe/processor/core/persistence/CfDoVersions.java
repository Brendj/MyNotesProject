package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDoVersions {

    private long idofdoobject;

    public long getIdofdoobject() {
        return idofdoobject;
    }

    public void setIdofdoobject(long idofdoobject) {
        this.idofdoobject = idofdoobject;
    }

    private String distributedobjectclassname;

    public String getDistributedobjectclassname() {
        return distributedobjectclassname;
    }

    public void setDistributedobjectclassname(String distributedobjectclassname) {
        this.distributedobjectclassname = distributedobjectclassname;
    }

    private long currentversion;

    public long getCurrentversion() {
        return currentversion;
    }

    public void setCurrentversion(long currentversion) {
        this.currentversion = currentversion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDoVersions that = (CfDoVersions) o;

        if (currentversion != that.currentversion) {
            return false;
        }
        if (idofdoobject != that.idofdoobject) {
            return false;
        }
        if (distributedobjectclassname != null ? !distributedobjectclassname.equals(that.distributedobjectclassname)
                : that.distributedobjectclassname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofdoobject ^ (idofdoobject >>> 32));
        result = 31 * result + (distributedobjectclassname != null ? distributedobjectclassname.hashCode() : 0);
        result = 31 * result + (int) (currentversion ^ (currentversion >>> 32));
        return result;
    }
}
