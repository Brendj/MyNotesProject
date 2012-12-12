package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfPos {

    private long idofpos;

    public long getIdofpos() {
        return idofpos;
    }

    public void setIdofpos(long idofpos) {
        this.idofpos = idofpos;
    }

    private long idofcontragent;

    public long getIdofcontragent() {
        return idofcontragent;
    }

    public void setIdofcontragent(long idofcontragent) {
        this.idofcontragent = idofcontragent;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private int flags;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    private String publickey;

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfPos cfPos = (CfPos) o;

        if (createddate != cfPos.createddate) {
            return false;
        }
        if (flags != cfPos.flags) {
            return false;
        }
        if (idofcontragent != cfPos.idofcontragent) {
            return false;
        }
        if (idofpos != cfPos.idofpos) {
            return false;
        }
        if (state != cfPos.state) {
            return false;
        }
        if (description != null ? !description.equals(cfPos.description) : cfPos.description != null) {
            return false;
        }
        if (name != null ? !name.equals(cfPos.name) : cfPos.name != null) {
            return false;
        }
        if (publickey != null ? !publickey.equals(cfPos.publickey) : cfPos.publickey != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofpos ^ (idofpos >>> 32));
        result = 31 * result + (int) (idofcontragent ^ (idofcontragent >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + state;
        result = 31 * result + flags;
        result = 31 * result + (publickey != null ? publickey.hashCode() : 0);
        return result;
    }

    private Collection<CfOrders> cfOrdersesByIdofpos;

    public Collection<CfOrders> getCfOrdersesByIdofpos() {
        return cfOrdersesByIdofpos;
    }

    public void setCfOrdersesByIdofpos(Collection<CfOrders> cfOrdersesByIdofpos) {
        this.cfOrdersesByIdofpos = cfOrdersesByIdofpos;
    }

    private CfContragents cfContragentsByIdofcontragent;

    public CfContragents getCfContragentsByIdofcontragent() {
        return cfContragentsByIdofcontragent;
    }

    public void setCfContragentsByIdofcontragent(CfContragents cfContragentsByIdofcontragent) {
        this.cfContragentsByIdofcontragent = cfContragentsByIdofcontragent;
    }
}
