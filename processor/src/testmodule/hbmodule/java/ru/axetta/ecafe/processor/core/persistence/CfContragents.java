package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfContragents {

    private long idofcontragent;

    public long getIdofcontragent() {
        return idofcontragent;
    }

    public void setIdofcontragent(long idofcontragent) {
        this.idofcontragent = idofcontragent;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    private long idofcontactperson;

    public long getIdofcontactperson() {
        return idofcontactperson;
    }

    public void setIdofcontactperson(long idofcontactperson) {
        this.idofcontactperson = idofcontactperson;
    }

    private int parentid;

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    private String contragentname;

    public String getContragentname() {
        return contragentname;
    }

    public void setContragentname(String contragentname) {
        this.contragentname = contragentname;
    }

    private int classid;

    public int getClassid() {
        return classid;
    }

    public void setClassid(int classid) {
        this.classid = classid;
    }

    private int flags;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String fax;

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    private String inn;

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    private String bank;

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    private String bic;

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    private String corraccount;

    public String getCorraccount() {
        return corraccount;
    }

    public void setCorraccount(String corraccount) {
        this.corraccount = corraccount;
    }

    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    private String publickey;

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    private int needaccounttranslate;

    public int getNeedaccounttranslate() {
        return needaccounttranslate;
    }

    public void setNeedaccounttranslate(int needaccounttranslate) {
        this.needaccounttranslate = needaccounttranslate;
    }

    private String publickeygostalias;

    public String getPublickeygostalias() {
        return publickeygostalias;
    }

    public void setPublickeygostalias(String publickeygostalias) {
        this.publickeygostalias = publickeygostalias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfContragents that = (CfContragents) o;

        if (classid != that.classid) {
            return false;
        }
        if (createddate != that.createddate) {
            return false;
        }
        if (flags != that.flags) {
            return false;
        }
        if (idofcontactperson != that.idofcontactperson) {
            return false;
        }
        if (idofcontragent != that.idofcontragent) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (needaccounttranslate != that.needaccounttranslate) {
            return false;
        }
        if (parentid != that.parentid) {
            return false;
        }
        if (version != that.version) {
            return false;
        }
        if (account != null ? !account.equals(that.account) : that.account != null) {
            return false;
        }
        if (address != null ? !address.equals(that.address) : that.address != null) {
            return false;
        }
        if (bank != null ? !bank.equals(that.bank) : that.bank != null) {
            return false;
        }
        if (bic != null ? !bic.equals(that.bic) : that.bic != null) {
            return false;
        }
        if (contragentname != null ? !contragentname.equals(that.contragentname) : that.contragentname != null) {
            return false;
        }
        if (corraccount != null ? !corraccount.equals(that.corraccount) : that.corraccount != null) {
            return false;
        }
        if (email != null ? !email.equals(that.email) : that.email != null) {
            return false;
        }
        if (fax != null ? !fax.equals(that.fax) : that.fax != null) {
            return false;
        }
        if (inn != null ? !inn.equals(that.inn) : that.inn != null) {
            return false;
        }
        if (mobile != null ? !mobile.equals(that.mobile) : that.mobile != null) {
            return false;
        }
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) {
            return false;
        }
        if (publickey != null ? !publickey.equals(that.publickey) : that.publickey != null) {
            return false;
        }
        if (publickeygostalias != null ? !publickeygostalias.equals(that.publickeygostalias)
                : that.publickeygostalias != null) {
            return false;
        }
        if (remarks != null ? !remarks.equals(that.remarks) : that.remarks != null) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcontragent ^ (idofcontragent >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (int) (idofcontactperson ^ (idofcontactperson >>> 32));
        result = 31 * result + parentid;
        result = 31 * result + (contragentname != null ? contragentname.hashCode() : 0);
        result = 31 * result + classid;
        result = 31 * result + flags;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
        result = 31 * result + (inn != null ? inn.hashCode() : 0);
        result = 31 * result + (bank != null ? bank.hashCode() : 0);
        result = 31 * result + (bic != null ? bic.hashCode() : 0);
        result = 31 * result + (corraccount != null ? corraccount.hashCode() : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (publickey != null ? publickey.hashCode() : 0);
        result = 31 * result + needaccounttranslate;
        result = 31 * result + (publickeygostalias != null ? publickeygostalias.hashCode() : 0);
        return result;
    }

    private Collection<CfAddpayments> cfAddpaymentsesByIdofcontragent;

    public Collection<CfAddpayments> getCfAddpaymentsesByIdofcontragent() {
        return cfAddpaymentsesByIdofcontragent;
    }

    public void setCfAddpaymentsesByIdofcontragent(Collection<CfAddpayments> cfAddpaymentsesByIdofcontragent) {
        this.cfAddpaymentsesByIdofcontragent = cfAddpaymentsesByIdofcontragent;
    }

    private Collection<CfAddpayments> cfAddpaymentsesByIdofcontragent_0;

    public Collection<CfAddpayments> getCfAddpaymentsesByIdofcontragent_0() {
        return cfAddpaymentsesByIdofcontragent_0;
    }

    public void setCfAddpaymentsesByIdofcontragent_0(Collection<CfAddpayments> cfAddpaymentsesByIdofcontragent_0) {
        this.cfAddpaymentsesByIdofcontragent_0 = cfAddpaymentsesByIdofcontragent_0;
    }

    private Collection<CfClientpaymentorders> cfClientpaymentordersesByIdofcontragent;

    public Collection<CfClientpaymentorders> getCfClientpaymentordersesByIdofcontragent() {
        return cfClientpaymentordersesByIdofcontragent;
    }

    public void setCfClientpaymentordersesByIdofcontragent(
            Collection<CfClientpaymentorders> cfClientpaymentordersesByIdofcontragent) {
        this.cfClientpaymentordersesByIdofcontragent = cfClientpaymentordersesByIdofcontragent;
    }

    private Collection<CfClientpayments> cfClientpaymentsesByIdofcontragent;

    public Collection<CfClientpayments> getCfClientpaymentsesByIdofcontragent() {
        return cfClientpaymentsesByIdofcontragent;
    }

    public void setCfClientpaymentsesByIdofcontragent(Collection<CfClientpayments> cfClientpaymentsesByIdofcontragent) {
        this.cfClientpaymentsesByIdofcontragent = cfClientpaymentsesByIdofcontragent;
    }

    private Collection<CfClientpayments> cfClientpaymentsesByIdofcontragent_0;

    public Collection<CfClientpayments> getCfClientpaymentsesByIdofcontragent_0() {
        return cfClientpaymentsesByIdofcontragent_0;
    }

    public void setCfClientpaymentsesByIdofcontragent_0(
            Collection<CfClientpayments> cfClientpaymentsesByIdofcontragent_0) {
        this.cfClientpaymentsesByIdofcontragent_0 = cfClientpaymentsesByIdofcontragent_0;
    }

    private Collection<CfContragentclientaccounts> cfContragentclientaccountsesByIdofcontragent;

    public Collection<CfContragentclientaccounts> getCfContragentclientaccountsesByIdofcontragent() {
        return cfContragentclientaccountsesByIdofcontragent;
    }

    public void setCfContragentclientaccountsesByIdofcontragent(
            Collection<CfContragentclientaccounts> cfContragentclientaccountsesByIdofcontragent) {
        this.cfContragentclientaccountsesByIdofcontragent = cfContragentclientaccountsesByIdofcontragent;
    }

    private Collection<CfContragentpayments> cfContragentpaymentsesByIdofcontragent;

    public Collection<CfContragentpayments> getCfContragentpaymentsesByIdofcontragent() {
        return cfContragentpaymentsesByIdofcontragent;
    }

    public void setCfContragentpaymentsesByIdofcontragent(
            Collection<CfContragentpayments> cfContragentpaymentsesByIdofcontragent) {
        this.cfContragentpaymentsesByIdofcontragent = cfContragentpaymentsesByIdofcontragent;
    }

    private CfPersons cfPersonsByIdofcontactperson;

    public CfPersons getCfPersonsByIdofcontactperson() {
        return cfPersonsByIdofcontactperson;
    }

    public void setCfPersonsByIdofcontactperson(CfPersons cfPersonsByIdofcontactperson) {
        this.cfPersonsByIdofcontactperson = cfPersonsByIdofcontactperson;
    }

    private Collection<CfCurrentpositions> cfCurrentpositionsesByIdofcontragent;

    public Collection<CfCurrentpositions> getCfCurrentpositionsesByIdofcontragent() {
        return cfCurrentpositionsesByIdofcontragent;
    }

    public void setCfCurrentpositionsesByIdofcontragent(
            Collection<CfCurrentpositions> cfCurrentpositionsesByIdofcontragent) {
        this.cfCurrentpositionsesByIdofcontragent = cfCurrentpositionsesByIdofcontragent;
    }

    private Collection<CfCurrentpositions> cfCurrentpositionsesByIdofcontragent_0;

    public Collection<CfCurrentpositions> getCfCurrentpositionsesByIdofcontragent_0() {
        return cfCurrentpositionsesByIdofcontragent_0;
    }

    public void setCfCurrentpositionsesByIdofcontragent_0(
            Collection<CfCurrentpositions> cfCurrentpositionsesByIdofcontragent_0) {
        this.cfCurrentpositionsesByIdofcontragent_0 = cfCurrentpositionsesByIdofcontragent_0;
    }

    private Collection<CfOrders> cfOrdersesByIdofcontragent;

    public Collection<CfOrders> getCfOrdersesByIdofcontragent() {
        return cfOrdersesByIdofcontragent;
    }

    public void setCfOrdersesByIdofcontragent(Collection<CfOrders> cfOrdersesByIdofcontragent) {
        this.cfOrdersesByIdofcontragent = cfOrdersesByIdofcontragent;
    }

    private Collection<CfOrgs> cfOrgsesByIdofcontragent;

    public Collection<CfOrgs> getCfOrgsesByIdofcontragent() {
        return cfOrgsesByIdofcontragent;
    }

    public void setCfOrgsesByIdofcontragent(Collection<CfOrgs> cfOrgsesByIdofcontragent) {
        this.cfOrgsesByIdofcontragent = cfOrgsesByIdofcontragent;
    }

    private Collection<CfPos> cfPosesByIdofcontragent;

    public Collection<CfPos> getCfPosesByIdofcontragent() {
        return cfPosesByIdofcontragent;
    }

    public void setCfPosesByIdofcontragent(Collection<CfPos> cfPosesByIdofcontragent) {
        this.cfPosesByIdofcontragent = cfPosesByIdofcontragent;
    }

    private Collection<CfSettlements> cfSettlementsesByIdofcontragent;

    public Collection<CfSettlements> getCfSettlementsesByIdofcontragent() {
        return cfSettlementsesByIdofcontragent;
    }

    public void setCfSettlementsesByIdofcontragent(Collection<CfSettlements> cfSettlementsesByIdofcontragent) {
        this.cfSettlementsesByIdofcontragent = cfSettlementsesByIdofcontragent;
    }

    private Collection<CfSettlements> cfSettlementsesByIdofcontragent_0;

    public Collection<CfSettlements> getCfSettlementsesByIdofcontragent_0() {
        return cfSettlementsesByIdofcontragent_0;
    }

    public void setCfSettlementsesByIdofcontragent_0(Collection<CfSettlements> cfSettlementsesByIdofcontragent_0) {
        this.cfSettlementsesByIdofcontragent_0 = cfSettlementsesByIdofcontragent_0;
    }

    private Collection<CfUsers> cfUsersesByIdofcontragent;

    public Collection<CfUsers> getCfUsersesByIdofcontragent() {
        return cfUsersesByIdofcontragent;
    }

    public void setCfUsersesByIdofcontragent(Collection<CfUsers> cfUsersesByIdofcontragent) {
        this.cfUsersesByIdofcontragent = cfUsersesByIdofcontragent;
    }
}
