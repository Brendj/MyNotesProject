package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfClients {

    private long idofclient;

    public long getIdofclient() {
        return idofclient;
    }

    public void setIdofclient(long idofclient) {
        this.idofclient = idofclient;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long idofperson;

    public long getIdofperson() {
        return idofperson;
    }

    public void setIdofperson(long idofperson) {
        this.idofperson = idofperson;
    }

    private long idofcontractperson;

    public long getIdofcontractperson() {
        return idofcontractperson;
    }

    public void setIdofcontractperson(long idofcontractperson) {
        this.idofcontractperson = idofcontractperson;
    }

    private long idofclientgroup;

    public long getIdofclientgroup() {
        return idofclientgroup;
    }

    public void setIdofclientgroup(long idofclientgroup) {
        this.idofclientgroup = idofclientgroup;
    }

    private long clientregistryversion;

    public long getClientregistryversion() {
        return clientregistryversion;
    }

    public void setClientregistryversion(long clientregistryversion) {
        this.clientregistryversion = clientregistryversion;
    }

    private int flags;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
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

    private int notifyviaemail;

    public int getNotifyviaemail() {
        return notifyviaemail;
    }

    public void setNotifyviaemail(int notifyviaemail) {
        this.notifyviaemail = notifyviaemail;
    }

    private int notifyviasms;

    public int getNotifyviasms() {
        return notifyviasms;
    }

    public void setNotifyviasms(int notifyviasms) {
        this.notifyviasms = notifyviasms;
    }

    private long image;

    public long getImage() {
        return image;
    }

    public void setImage(long image) {
        this.image = image;
    }

    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    private long lastupdate;

    public long getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(long lastupdate) {
        this.lastupdate = lastupdate;
    }

    private long contractid;

    public long getContractid() {
        return contractid;
    }

    public void setContractid(long contractid) {
        this.contractid = contractid;
    }

    private long contractdate;

    public long getContractdate() {
        return contractdate;
    }

    public void setContractdate(long contractdate) {
        this.contractdate = contractdate;
    }

    private int contractstate;

    public int getContractstate() {
        return contractstate;
    }

    public void setContractstate(int contractstate) {
        this.contractstate = contractstate;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private int payforsms;

    public int getPayforsms() {
        return payforsms;
    }

    public void setPayforsms(int payforsms) {
        this.payforsms = payforsms;
    }

    private int freepaymaxcount;

    public int getFreepaymaxcount() {
        return freepaymaxcount;
    }

    public void setFreepaymaxcount(int freepaymaxcount) {
        this.freepaymaxcount = freepaymaxcount;
    }

    private int freepaycount;

    public int getFreepaycount() {
        return freepaycount;
    }

    public void setFreepaycount(int freepaycount) {
        this.freepaycount = freepaycount;
    }

    private long lastfreepaytime;

    public long getLastfreepaytime() {
        return lastfreepaytime;
    }

    public void setLastfreepaytime(long lastfreepaytime) {
        this.lastfreepaytime = lastfreepaytime;
    }

    private int discountmode;

    public int getDiscountmode() {
        return discountmode;
    }

    public void setDiscountmode(int discountmode) {
        this.discountmode = discountmode;
    }

    private long balance;

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    private long limit;

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    private long expenditurelimit;

    public long getExpenditurelimit() {
        return expenditurelimit;
    }

    public void setExpenditurelimit(long expenditurelimit) {
        this.expenditurelimit = expenditurelimit;
    }

    private String categoriesdiscounts;

    public String getCategoriesdiscounts() {
        return categoriesdiscounts;
    }

    public void setCategoriesdiscounts(String categoriesdiscounts) {
        this.categoriesdiscounts = categoriesdiscounts;
    }

    private String san;

    public String getSan() {
        return san;
    }

    public void setSan(String san) {
        this.san = san;
    }

    private String guardsan;

    public String getGuardsan() {
        return guardsan;
    }

    public void setGuardsan(String guardsan) {
        this.guardsan = guardsan;
    }

    private long externalid;

    public long getExternalid() {
        return externalid;
    }

    public void setExternalid(long externalid) {
        this.externalid = externalid;
    }

    private String clientguid;

    public String getClientguid() {
        return clientguid;
    }

    public void setClientguid(String clientguid) {
        this.clientguid = clientguid;
    }

    private String fax;

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfClients cfClients = (CfClients) o;

        if (balance != cfClients.balance) {
            return false;
        }
        if (clientregistryversion != cfClients.clientregistryversion) {
            return false;
        }
        if (contractdate != cfClients.contractdate) {
            return false;
        }
        if (contractid != cfClients.contractid) {
            return false;
        }
        if (contractstate != cfClients.contractstate) {
            return false;
        }
        if (discountmode != cfClients.discountmode) {
            return false;
        }
        if (expenditurelimit != cfClients.expenditurelimit) {
            return false;
        }
        if (externalid != cfClients.externalid) {
            return false;
        }
        if (flags != cfClients.flags) {
            return false;
        }
        if (freepaycount != cfClients.freepaycount) {
            return false;
        }
        if (freepaymaxcount != cfClients.freepaymaxcount) {
            return false;
        }
        if (idofclient != cfClients.idofclient) {
            return false;
        }
        if (idofclientgroup != cfClients.idofclientgroup) {
            return false;
        }
        if (idofcontractperson != cfClients.idofcontractperson) {
            return false;
        }
        if (idoforg != cfClients.idoforg) {
            return false;
        }
        if (idofperson != cfClients.idofperson) {
            return false;
        }
        if (image != cfClients.image) {
            return false;
        }
        if (lastfreepaytime != cfClients.lastfreepaytime) {
            return false;
        }
        if (lastupdate != cfClients.lastupdate) {
            return false;
        }
        if (limit != cfClients.limit) {
            return false;
        }
        if (notifyviaemail != cfClients.notifyviaemail) {
            return false;
        }
        if (notifyviasms != cfClients.notifyviasms) {
            return false;
        }
        if (payforsms != cfClients.payforsms) {
            return false;
        }
        if (version != cfClients.version) {
            return false;
        }
        if (address != null ? !address.equals(cfClients.address) : cfClients.address != null) {
            return false;
        }
        if (categoriesdiscounts != null ? !categoriesdiscounts.equals(cfClients.categoriesdiscounts)
                : cfClients.categoriesdiscounts != null) {
            return false;
        }
        if (clientguid != null ? !clientguid.equals(cfClients.clientguid) : cfClients.clientguid != null) {
            return false;
        }
        if (email != null ? !email.equals(cfClients.email) : cfClients.email != null) {
            return false;
        }
        if (fax != null ? !fax.equals(cfClients.fax) : cfClients.fax != null) {
            return false;
        }
        if (guardsan != null ? !guardsan.equals(cfClients.guardsan) : cfClients.guardsan != null) {
            return false;
        }
        if (mobile != null ? !mobile.equals(cfClients.mobile) : cfClients.mobile != null) {
            return false;
        }
        if (password != null ? !password.equals(cfClients.password) : cfClients.password != null) {
            return false;
        }
        if (phone != null ? !phone.equals(cfClients.phone) : cfClients.phone != null) {
            return false;
        }
        if (remarks != null ? !remarks.equals(cfClients.remarks) : cfClients.remarks != null) {
            return false;
        }
        if (san != null ? !san.equals(cfClients.san) : cfClients.san != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofclient ^ (idofclient >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idofperson ^ (idofperson >>> 32));
        result = 31 * result + (int) (idofcontractperson ^ (idofcontractperson >>> 32));
        result = 31 * result + (int) (idofclientgroup ^ (idofclientgroup >>> 32));
        result = 31 * result + (int) (clientregistryversion ^ (clientregistryversion >>> 32));
        result = 31 * result + flags;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + notifyviaemail;
        result = 31 * result + notifyviasms;
        result = 31 * result + (int) (image ^ (image >>> 32));
        result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (contractid ^ (contractid >>> 32));
        result = 31 * result + (int) (contractdate ^ (contractdate >>> 32));
        result = 31 * result + contractstate;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + payforsms;
        result = 31 * result + freepaymaxcount;
        result = 31 * result + freepaycount;
        result = 31 * result + (int) (lastfreepaytime ^ (lastfreepaytime >>> 32));
        result = 31 * result + discountmode;
        result = 31 * result + (int) (balance ^ (balance >>> 32));
        result = 31 * result + (int) (limit ^ (limit >>> 32));
        result = 31 * result + (int) (expenditurelimit ^ (expenditurelimit >>> 32));
        result = 31 * result + (categoriesdiscounts != null ? categoriesdiscounts.hashCode() : 0);
        result = 31 * result + (san != null ? san.hashCode() : 0);
        result = 31 * result + (guardsan != null ? guardsan.hashCode() : 0);
        result = 31 * result + (int) (externalid ^ (externalid >>> 32));
        result = 31 * result + (clientguid != null ? clientguid.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        return result;
    }

    private Collection<CfAccountRefund> cfAccountRefundsByIdofclient;

    public Collection<CfAccountRefund> getCfAccountRefundsByIdofclient() {
        return cfAccountRefundsByIdofclient;
    }

    public void setCfAccountRefundsByIdofclient(Collection<CfAccountRefund> cfAccountRefundsByIdofclient) {
        this.cfAccountRefundsByIdofclient = cfAccountRefundsByIdofclient;
    }

    private Collection<CfAccountTransfers> cfAccountTransfersesByIdofclient;

    public Collection<CfAccountTransfers> getCfAccountTransfersesByIdofclient() {
        return cfAccountTransfersesByIdofclient;
    }

    public void setCfAccountTransfersesByIdofclient(Collection<CfAccountTransfers> cfAccountTransfersesByIdofclient) {
        this.cfAccountTransfersesByIdofclient = cfAccountTransfersesByIdofclient;
    }

    private Collection<CfAccountTransfers> cfAccountTransfersesByIdofclient_0;

    public Collection<CfAccountTransfers> getCfAccountTransfersesByIdofclient_0() {
        return cfAccountTransfersesByIdofclient_0;
    }

    public void setCfAccountTransfersesByIdofclient_0(
            Collection<CfAccountTransfers> cfAccountTransfersesByIdofclient_0) {
        this.cfAccountTransfersesByIdofclient_0 = cfAccountTransfersesByIdofclient_0;
    }

    private Collection<CfCalls> cfCallsesByIdofclient;

    public Collection<CfCalls> getCfCallsesByIdofclient() {
        return cfCallsesByIdofclient;
    }

    public void setCfCallsesByIdofclient(Collection<CfCalls> cfCallsesByIdofclient) {
        this.cfCallsesByIdofclient = cfCallsesByIdofclient;
    }

    private Collection<CfCards> cfCardsesByIdofclient;

    public Collection<CfCards> getCfCardsesByIdofclient() {
        return cfCardsesByIdofclient;
    }

    public void setCfCardsesByIdofclient(Collection<CfCards> cfCardsesByIdofclient) {
        this.cfCardsesByIdofclient = cfCardsesByIdofclient;
    }

    private Collection<CfCirculations> cfCirculationsesByIdofclient;

    public Collection<CfCirculations> getCfCirculationsesByIdofclient() {
        return cfCirculationsesByIdofclient;
    }

    public void setCfCirculationsesByIdofclient(Collection<CfCirculations> cfCirculationsesByIdofclient) {
        this.cfCirculationsesByIdofclient = cfCirculationsesByIdofclient;
    }

    private Collection<CfClientpaymentorders> cfClientpaymentordersesByIdofclient;

    public Collection<CfClientpaymentorders> getCfClientpaymentordersesByIdofclient() {
        return cfClientpaymentordersesByIdofclient;
    }

    public void setCfClientpaymentordersesByIdofclient(
            Collection<CfClientpaymentorders> cfClientpaymentordersesByIdofclient) {
        this.cfClientpaymentordersesByIdofclient = cfClientpaymentordersesByIdofclient;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }

    private CfPersons cfPersonsByIdofcontractperson;

    public CfPersons getCfPersonsByIdofcontractperson() {
        return cfPersonsByIdofcontractperson;
    }

    public void setCfPersonsByIdofcontractperson(CfPersons cfPersonsByIdofcontractperson) {
        this.cfPersonsByIdofcontractperson = cfPersonsByIdofcontractperson;
    }

    private CfPersons cfPersonsByIdofperson;

    public CfPersons getCfPersonsByIdofperson() {
        return cfPersonsByIdofperson;
    }

    public void setCfPersonsByIdofperson(CfPersons cfPersonsByIdofperson) {
        this.cfPersonsByIdofperson = cfPersonsByIdofperson;
    }

    private Collection<CfClientsCategorydiscounts> cfClientsCategorydiscountsesByIdofclient;

    public Collection<CfClientsCategorydiscounts> getCfClientsCategorydiscountsesByIdofclient() {
        return cfClientsCategorydiscountsesByIdofclient;
    }

    public void setCfClientsCategorydiscountsesByIdofclient(
            Collection<CfClientsCategorydiscounts> cfClientsCategorydiscountsesByIdofclient) {
        this.cfClientsCategorydiscountsesByIdofclient = cfClientsCategorydiscountsesByIdofclient;
    }

    private Collection<CfClientsms> cfClientsmsesByIdofclient;

    public Collection<CfClientsms> getCfClientsmsesByIdofclient() {
        return cfClientsmsesByIdofclient;
    }

    public void setCfClientsmsesByIdofclient(Collection<CfClientsms> cfClientsmsesByIdofclient) {
        this.cfClientsmsesByIdofclient = cfClientsmsesByIdofclient;
    }

    private Collection<CfContragentclientaccounts> cfContragentclientaccountsesByIdofclient;

    public Collection<CfContragentclientaccounts> getCfContragentclientaccountsesByIdofclient() {
        return cfContragentclientaccountsesByIdofclient;
    }

    public void setCfContragentclientaccountsesByIdofclient(
            Collection<CfContragentclientaccounts> cfContragentclientaccountsesByIdofclient) {
        this.cfContragentclientaccountsesByIdofclient = cfContragentclientaccountsesByIdofclient;
    }

    private Collection<CfDiaryvalues> cfDiaryvaluesesByIdofclient;

    public Collection<CfDiaryvalues> getCfDiaryvaluesesByIdofclient() {
        return cfDiaryvaluesesByIdofclient;
    }

    public void setCfDiaryvaluesesByIdofclient(Collection<CfDiaryvalues> cfDiaryvaluesesByIdofclient) {
        this.cfDiaryvaluesesByIdofclient = cfDiaryvaluesesByIdofclient;
    }

    private Collection<CfDishProhibitions> cfDishProhibitionsesByIdofclient;

    public Collection<CfDishProhibitions> getCfDishProhibitionsesByIdofclient() {
        return cfDishProhibitionsesByIdofclient;
    }

    public void setCfDishProhibitionsesByIdofclient(Collection<CfDishProhibitions> cfDishProhibitionsesByIdofclient) {
        this.cfDishProhibitionsesByIdofclient = cfDishProhibitionsesByIdofclient;
    }

    private Collection<CfLibvisits> cfLibvisitsesByIdofclient;

    public Collection<CfLibvisits> getCfLibvisitsesByIdofclient() {
        return cfLibvisitsesByIdofclient;
    }

    public void setCfLibvisitsesByIdofclient(Collection<CfLibvisits> cfLibvisitsesByIdofclient) {
        this.cfLibvisitsesByIdofclient = cfLibvisitsesByIdofclient;
    }

    private Collection<CfLinkingTokens> cfLinkingTokensesByIdofclient;

    public Collection<CfLinkingTokens> getCfLinkingTokensesByIdofclient() {
        return cfLinkingTokensesByIdofclient;
    }

    public void setCfLinkingTokensesByIdofclient(Collection<CfLinkingTokens> cfLinkingTokensesByIdofclient) {
        this.cfLinkingTokensesByIdofclient = cfLinkingTokensesByIdofclient;
    }

    private Collection<CfNotifications> cfNotificationsesByIdofclient;

    public Collection<CfNotifications> getCfNotificationsesByIdofclient() {
        return cfNotificationsesByIdofclient;
    }

    public void setCfNotificationsesByIdofclient(Collection<CfNotifications> cfNotificationsesByIdofclient) {
        this.cfNotificationsesByIdofclient = cfNotificationsesByIdofclient;
    }

    private Collection<CfOrders> cfOrdersesByIdofclient;

    public Collection<CfOrders> getCfOrdersesByIdofclient() {
        return cfOrdersesByIdofclient;
    }

    public void setCfOrdersesByIdofclient(Collection<CfOrders> cfOrdersesByIdofclient) {
        this.cfOrdersesByIdofclient = cfOrdersesByIdofclient;
    }

    private Collection<CfReaders> cfReadersesByIdofclient;

    public Collection<CfReaders> getCfReadersesByIdofclient() {
        return cfReadersesByIdofclient;
    }

    public void setCfReadersesByIdofclient(Collection<CfReaders> cfReadersesByIdofclient) {
        this.cfReadersesByIdofclient = cfReadersesByIdofclient;
    }

    private Collection<CfTransactions> cfTransactionsesByIdofclient;

    public Collection<CfTransactions> getCfTransactionsesByIdofclient() {
        return cfTransactionsesByIdofclient;
    }

    public void setCfTransactionsesByIdofclient(Collection<CfTransactions> cfTransactionsesByIdofclient) {
        this.cfTransactionsesByIdofclient = cfTransactionsesByIdofclient;
    }
}
