package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfRegistry {

    private long idofregistry;

    public long getIdofregistry() {
        return idofregistry;
    }

    public void setIdofregistry(long idofregistry) {
        this.idofregistry = idofregistry;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    private long clientregistryversion;

    public long getClientregistryversion() {
        return clientregistryversion;
    }

    public void setClientregistryversion(long clientregistryversion) {
        this.clientregistryversion = clientregistryversion;
    }

    private String smsid;

    public String getSmsid() {
        return smsid;
    }

    public void setSmsid(String smsid) {
        this.smsid = smsid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfRegistry that = (CfRegistry) o;

        if (clientregistryversion != that.clientregistryversion) {
            return false;
        }
        if (idofregistry != that.idofregistry) {
            return false;
        }
        if (version != that.version) {
            return false;
        }
        if (smsid != null ? !smsid.equals(that.smsid) : that.smsid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofregistry ^ (idofregistry >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (int) (clientregistryversion ^ (clientregistryversion >>> 32));
        result = 31 * result + (smsid != null ? smsid.hashCode() : 0);
        return result;
    }
}
