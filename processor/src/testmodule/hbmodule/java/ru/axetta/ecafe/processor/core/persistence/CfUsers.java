package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfUsers {

    private long idofuser;

    public long getIdofuser() {
        return idofuser;
    }

    public void setIdofuser(long idofuser) {
        this.idofuser = idofuser;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private long lastchange;

    public long getLastchange() {
        return lastchange;
    }

    public void setLastchange(long lastchange) {
        this.lastchange = lastchange;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private long idofcontragent;

    public long getIdofcontragent() {
        return idofcontragent;
    }

    public void setIdofcontragent(long idofcontragent) {
        this.idofcontragent = idofcontragent;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfUsers cfUsers = (CfUsers) o;

        if (idofcontragent != cfUsers.idofcontragent) {
            return false;
        }
        if (idofuser != cfUsers.idofuser) {
            return false;
        }
        if (lastchange != cfUsers.lastchange) {
            return false;
        }
        if (version != cfUsers.version) {
            return false;
        }
        if (email != null ? !email.equals(cfUsers.email) : cfUsers.email != null) {
            return false;
        }
        if (password != null ? !password.equals(cfUsers.password) : cfUsers.password != null) {
            return false;
        }
        if (phone != null ? !phone.equals(cfUsers.phone) : cfUsers.phone != null) {
            return false;
        }
        if (username != null ? !username.equals(cfUsers.username) : cfUsers.username != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofuser ^ (idofuser >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (int) (lastchange ^ (lastchange >>> 32));
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (int) (idofcontragent ^ (idofcontragent >>> 32));
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    private Collection<CfPermissions> cfPermissionsesByIdofuser;

    public Collection<CfPermissions> getCfPermissionsesByIdofuser() {
        return cfPermissionsesByIdofuser;
    }

    public void setCfPermissionsesByIdofuser(Collection<CfPermissions> cfPermissionsesByIdofuser) {
        this.cfPermissionsesByIdofuser = cfPermissionsesByIdofuser;
    }

    private CfContragents cfContragentsByIdofcontragent;

    public CfContragents getCfContragentsByIdofcontragent() {
        return cfContragentsByIdofcontragent;
    }

    public void setCfContragentsByIdofcontragent(CfContragents cfContragentsByIdofcontragent) {
        this.cfContragentsByIdofcontragent = cfContragentsByIdofcontragent;
    }
}
