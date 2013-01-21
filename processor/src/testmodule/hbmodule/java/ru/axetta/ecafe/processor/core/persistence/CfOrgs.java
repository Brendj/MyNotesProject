package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfOrgs {

    private long idoforg;
    private Set<CfOrgs> friendlyOrg;

    public Set<CfOrgs> getFriendlyOrg() {
        return friendlyOrg;
    }

    public void setFriendlyOrg(Set<CfOrgs> friendlyOrg) {
        this.friendlyOrg = friendlyOrg;
    }

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    private String shortname;

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    private String officialname;

    public String getOfficialname() {
        return officialname;
    }

    public void setOfficialname(String officialname) {
        this.officialname = officialname;
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

    private long idofofficialperson;

    public long getIdofofficialperson() {
        return idofofficialperson;
    }

    public void setIdofofficialperson(long idofofficialperson) {
        this.idofofficialperson = idofofficialperson;
    }

    private String officialposition;

    public String getOfficialposition() {
        return officialposition;
    }

    public void setOfficialposition(String officialposition) {
        this.officialposition = officialposition;
    }

    private String contractid;

    public String getContractid() {
        return contractid;
    }

    public void setContractid(String contractid) {
        this.contractid = contractid;
    }

    private long contractdate;

    public long getContractdate() {
        return contractdate;
    }

    public void setContractdate(long contractdate) {
        this.contractdate = contractdate;
    }

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private long cardlimit;

    public long getCardlimit() {
        return cardlimit;
    }

    public void setCardlimit(long cardlimit) {
        this.cardlimit = cardlimit;
    }

    private String publickey;

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    private long idofpacket;

    public long getIdofpacket() {
        return idofpacket;
    }

    public void setIdofpacket(long idofpacket) {
        this.idofpacket = idofpacket;
    }

    private long lastclientcontractid;

    public long getLastclientcontractid() {
        return lastclientcontractid;
    }

    public void setLastclientcontractid(long lastclientcontractid) {
        this.lastclientcontractid = lastclientcontractid;
    }

    private String ssopassword;

    public String getSsopassword() {
        return ssopassword;
    }

    public void setSsopassword(String ssopassword) {
        this.ssopassword = ssopassword;
    }

    private String smssender;

    public String getSmssender() {
        return smssender;
    }

    public void setSmssender(String smssender) {
        this.smssender = smssender;
    }

    private long priceofsms;

    public long getPriceofsms() {
        return priceofsms;
    }

    public void setPriceofsms(long priceofsms) {
        this.priceofsms = priceofsms;
    }

    private long subscriptionprice;

    public long getSubscriptionprice() {
        return subscriptionprice;
    }

    public void setSubscriptionprice(long subscriptionprice) {
        this.subscriptionprice = subscriptionprice;
    }

    private long defaultsupplier;

    public long getDefaultsupplier() {
        return defaultsupplier;
    }

    public void setDefaultsupplier(long defaultsupplier) {
        this.defaultsupplier = defaultsupplier;
    }

    private String ogrn;

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    private String inn;

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    private String mailinglistreportsonnutrition;

    public String getMailinglistreportsonnutrition() {
        return mailinglistreportsonnutrition;
    }

    public void setMailinglistreportsonnutrition(String mailinglistreportsonnutrition) {
        this.mailinglistreportsonnutrition = mailinglistreportsonnutrition;
    }

    private String mailinglistreportsonvisits;

    public String getMailinglistreportsonvisits() {
        return mailinglistreportsonvisits;
    }

    public void setMailinglistreportsonvisits(String mailinglistreportsonvisits) {
        this.mailinglistreportsonvisits = mailinglistreportsonvisits;
    }

    private String mailinglistreports1;

    public String getMailinglistreports1() {
        return mailinglistreports1;
    }

    public void setMailinglistreports1(String mailinglistreports1) {
        this.mailinglistreports1 = mailinglistreports1;
    }

    private String mailinglistreports2;

    public String getMailinglistreports2() {
        return mailinglistreports2;
    }

    public void setMailinglistreports2(String mailinglistreports2) {
        this.mailinglistreports2 = mailinglistreports2;
    }

    private long idofconfigurationprovider;

    public long getIdofconfigurationprovider() {
        return idofconfigurationprovider;
    }

    public void setIdofconfigurationprovider(long idofconfigurationprovider) {
        this.idofconfigurationprovider = idofconfigurationprovider;
    }

    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    private long idofcontract;

    public long getIdofcontract() {
        return idofcontract;
    }

    public void setIdofcontract(long idofcontract) {
        this.idofcontract = idofcontract;
    }

    private long lastsucbalancesync;

    public long getLastsucbalancesync() {
        return lastsucbalancesync;
    }

    public void setLastsucbalancesync(long lastsucbalancesync) {
        this.lastsucbalancesync = lastsucbalancesync;
    }

    private long lastunsucbalancesync;

    public long getLastunsucbalancesync() {
        return lastunsucbalancesync;
    }

    public void setLastunsucbalancesync(long lastunsucbalancesync) {
        this.lastunsucbalancesync = lastunsucbalancesync;
    }

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String district;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String latitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    private String longitude;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    private int refectorytype;

    public int getRefectorytype() {
        return refectorytype;
    }

    public void setRefectorytype(int refectorytype) {
        this.refectorytype = refectorytype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfOrgs cfOrgs = (CfOrgs) o;

        if (cardlimit != cfOrgs.cardlimit) {
            return false;
        }
        if (contractdate != cfOrgs.contractdate) {
            return false;
        }
        if (defaultsupplier != cfOrgs.defaultsupplier) {
            return false;
        }
        if (idofconfigurationprovider != cfOrgs.idofconfigurationprovider) {
            return false;
        }
        if (idofcontract != cfOrgs.idofcontract) {
            return false;
        }
        if (idofofficialperson != cfOrgs.idofofficialperson) {
            return false;
        }
        if (idoforg != cfOrgs.idoforg) {
            return false;
        }
        if (idofpacket != cfOrgs.idofpacket) {
            return false;
        }
        if (lastclientcontractid != cfOrgs.lastclientcontractid) {
            return false;
        }
        if (lastsucbalancesync != cfOrgs.lastsucbalancesync) {
            return false;
        }
        if (lastunsucbalancesync != cfOrgs.lastunsucbalancesync) {
            return false;
        }
        if (priceofsms != cfOrgs.priceofsms) {
            return false;
        }
        if (refectorytype != cfOrgs.refectorytype) {
            return false;
        }
        if (state != cfOrgs.state) {
            return false;
        }
        if (subscriptionprice != cfOrgs.subscriptionprice) {
            return false;
        }
        if (version != cfOrgs.version) {
            return false;
        }
        if (address != null ? !address.equals(cfOrgs.address) : cfOrgs.address != null) {
            return false;
        }
        if (city != null ? !city.equals(cfOrgs.city) : cfOrgs.city != null) {
            return false;
        }
        if (contractid != null ? !contractid.equals(cfOrgs.contractid) : cfOrgs.contractid != null) {
            return false;
        }
        if (district != null ? !district.equals(cfOrgs.district) : cfOrgs.district != null) {
            return false;
        }
        if (guid != null ? !guid.equals(cfOrgs.guid) : cfOrgs.guid != null) {
            return false;
        }
        if (inn != null ? !inn.equals(cfOrgs.inn) : cfOrgs.inn != null) {
            return false;
        }
        if (latitude != null ? !latitude.equals(cfOrgs.latitude) : cfOrgs.latitude != null) {
            return false;
        }
        if (location != null ? !location.equals(cfOrgs.location) : cfOrgs.location != null) {
            return false;
        }
        if (longitude != null ? !longitude.equals(cfOrgs.longitude) : cfOrgs.longitude != null) {
            return false;
        }
        if (mailinglistreports1 != null ? !mailinglistreports1.equals(cfOrgs.mailinglistreports1)
                : cfOrgs.mailinglistreports1 != null) {
            return false;
        }
        if (mailinglistreports2 != null ? !mailinglistreports2.equals(cfOrgs.mailinglistreports2)
                : cfOrgs.mailinglistreports2 != null) {
            return false;
        }
        if (mailinglistreportsonnutrition != null ? !mailinglistreportsonnutrition
                .equals(cfOrgs.mailinglistreportsonnutrition) : cfOrgs.mailinglistreportsonnutrition != null) {
            return false;
        }
        if (mailinglistreportsonvisits != null ? !mailinglistreportsonvisits.equals(cfOrgs.mailinglistreportsonvisits)
                : cfOrgs.mailinglistreportsonvisits != null) {
            return false;
        }
        if (officialname != null ? !officialname.equals(cfOrgs.officialname) : cfOrgs.officialname != null) {
            return false;
        }
        if (officialposition != null ? !officialposition.equals(cfOrgs.officialposition)
                : cfOrgs.officialposition != null) {
            return false;
        }
        if (ogrn != null ? !ogrn.equals(cfOrgs.ogrn) : cfOrgs.ogrn != null) {
            return false;
        }
        if (phone != null ? !phone.equals(cfOrgs.phone) : cfOrgs.phone != null) {
            return false;
        }
        if (publickey != null ? !publickey.equals(cfOrgs.publickey) : cfOrgs.publickey != null) {
            return false;
        }
        if (shortname != null ? !shortname.equals(cfOrgs.shortname) : cfOrgs.shortname != null) {
            return false;
        }
        if (smssender != null ? !smssender.equals(cfOrgs.smssender) : cfOrgs.smssender != null) {
            return false;
        }
        if (ssopassword != null ? !ssopassword.equals(cfOrgs.ssopassword) : cfOrgs.ssopassword != null) {
            return false;
        }
        if (tag != null ? !tag.equals(cfOrgs.tag) : cfOrgs.tag != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (shortname != null ? shortname.hashCode() : 0);
        result = 31 * result + (officialname != null ? officialname.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (int) (idofofficialperson ^ (idofofficialperson >>> 32));
        result = 31 * result + (officialposition != null ? officialposition.hashCode() : 0);
        result = 31 * result + (contractid != null ? contractid.hashCode() : 0);
        result = 31 * result + (int) (contractdate ^ (contractdate >>> 32));
        result = 31 * result + state;
        result = 31 * result + (int) (cardlimit ^ (cardlimit >>> 32));
        result = 31 * result + (publickey != null ? publickey.hashCode() : 0);
        result = 31 * result + (int) (idofpacket ^ (idofpacket >>> 32));
        result = 31 * result + (int) (lastclientcontractid ^ (lastclientcontractid >>> 32));
        result = 31 * result + (ssopassword != null ? ssopassword.hashCode() : 0);
        result = 31 * result + (smssender != null ? smssender.hashCode() : 0);
        result = 31 * result + (int) (priceofsms ^ (priceofsms >>> 32));
        result = 31 * result + (int) (subscriptionprice ^ (subscriptionprice >>> 32));
        result = 31 * result + (int) (defaultsupplier ^ (defaultsupplier >>> 32));
        result = 31 * result + (ogrn != null ? ogrn.hashCode() : 0);
        result = 31 * result + (inn != null ? inn.hashCode() : 0);
        result = 31 * result + (mailinglistreportsonnutrition != null ? mailinglistreportsonnutrition.hashCode() : 0);
        result = 31 * result + (mailinglistreportsonvisits != null ? mailinglistreportsonvisits.hashCode() : 0);
        result = 31 * result + (mailinglistreports1 != null ? mailinglistreports1.hashCode() : 0);
        result = 31 * result + (mailinglistreports2 != null ? mailinglistreports2.hashCode() : 0);
        result = 31 * result + (int) (idofconfigurationprovider ^ (idofconfigurationprovider >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (idofcontract ^ (idofcontract >>> 32));
        result = 31 * result + (int) (lastsucbalancesync ^ (lastsucbalancesync >>> 32));
        result = 31 * result + (int) (lastunsucbalancesync ^ (lastunsucbalancesync >>> 32));
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + refectorytype;
        return result;
    }

    private Collection<CfAssortment> cfAssortmentsByIdoforg;

    public Collection<CfAssortment> getCfAssortmentsByIdoforg() {
        return cfAssortmentsByIdoforg;
    }

    public void setCfAssortmentsByIdoforg(Collection<CfAssortment> cfAssortmentsByIdoforg) {
        this.cfAssortmentsByIdoforg = cfAssortmentsByIdoforg;
    }

    private Collection<CfCategoryorgOrgs> cfCategoryorgOrgsesByIdoforg;

    public Collection<CfCategoryorgOrgs> getCfCategoryorgOrgsesByIdoforg() {
        return cfCategoryorgOrgsesByIdoforg;
    }

    public void setCfCategoryorgOrgsesByIdoforg(Collection<CfCategoryorgOrgs> cfCategoryorgOrgsesByIdoforg) {
        this.cfCategoryorgOrgsesByIdoforg = cfCategoryorgOrgsesByIdoforg;
    }

    private Collection<CfClientgroups> cfClientgroupsesByIdoforg;

    public Collection<CfClientgroups> getCfClientgroupsesByIdoforg() {
        return cfClientgroupsesByIdoforg;
    }

    public void setCfClientgroupsesByIdoforg(Collection<CfClientgroups> cfClientgroupsesByIdoforg) {
        this.cfClientgroupsesByIdoforg = cfClientgroupsesByIdoforg;
    }

    private Collection<CfClients> cfClientsesByIdoforg;

    public Collection<CfClients> getCfClientsesByIdoforg() {
        return cfClientsesByIdoforg;
    }

    public void setCfClientsesByIdoforg(Collection<CfClients> cfClientsesByIdoforg) {
        this.cfClientsesByIdoforg = cfClientsesByIdoforg;
    }

    private Collection<CfComplexinfo> cfComplexinfosByIdoforg;

    public Collection<CfComplexinfo> getCfComplexinfosByIdoforg() {
        return cfComplexinfosByIdoforg;
    }

    public void setCfComplexinfosByIdoforg(Collection<CfComplexinfo> cfComplexinfosByIdoforg) {
        this.cfComplexinfosByIdoforg = cfComplexinfosByIdoforg;
    }

    private Collection<CfComplexinfoDiscountdetail> cfComplexinfoDiscountdetailsByIdoforg;

    public Collection<CfComplexinfoDiscountdetail> getCfComplexinfoDiscountdetailsByIdoforg() {
        return cfComplexinfoDiscountdetailsByIdoforg;
    }

    public void setCfComplexinfoDiscountdetailsByIdoforg(
            Collection<CfComplexinfoDiscountdetail> cfComplexinfoDiscountdetailsByIdoforg) {
        this.cfComplexinfoDiscountdetailsByIdoforg = cfComplexinfoDiscountdetailsByIdoforg;
    }

    private Collection<CfFriendlyOrganization> cfFriendlyOrganizationsByIdoforg;

    public Collection<CfFriendlyOrganization> getCfFriendlyOrganizationsByIdoforg() {
        return cfFriendlyOrganizationsByIdoforg;
    }

    public void setCfFriendlyOrganizationsByIdoforg(
            Collection<CfFriendlyOrganization> cfFriendlyOrganizationsByIdoforg) {
        this.cfFriendlyOrganizationsByIdoforg = cfFriendlyOrganizationsByIdoforg;
    }

    private Collection<CfFriendlyOrganization> cfFriendlyOrganizationsByIdoforg_0;

    public Collection<CfFriendlyOrganization> getCfFriendlyOrganizationsByIdoforg_0() {
        return cfFriendlyOrganizationsByIdoforg_0;
    }

    public void setCfFriendlyOrganizationsByIdoforg_0(
            Collection<CfFriendlyOrganization> cfFriendlyOrganizationsByIdoforg_0) {
        this.cfFriendlyOrganizationsByIdoforg_0 = cfFriendlyOrganizationsByIdoforg_0;
    }

    private Collection<CfMenu> cfMenusByIdoforg;

    public Collection<CfMenu> getCfMenusByIdoforg() {
        return cfMenusByIdoforg;
    }

    public void setCfMenusByIdoforg(Collection<CfMenu> cfMenusByIdoforg) {
        this.cfMenusByIdoforg = cfMenusByIdoforg;
    }

    private Collection<CfMenuexchange> cfMenuexchangesByIdoforg;

    public Collection<CfMenuexchange> getCfMenuexchangesByIdoforg() {
        return cfMenuexchangesByIdoforg;
    }

    public void setCfMenuexchangesByIdoforg(Collection<CfMenuexchange> cfMenuexchangesByIdoforg) {
        this.cfMenuexchangesByIdoforg = cfMenuexchangesByIdoforg;
    }

    private Collection<CfMenuexchangerules> cfMenuexchangerulesesByIdoforg;

    public Collection<CfMenuexchangerules> getCfMenuexchangerulesesByIdoforg() {
        return cfMenuexchangerulesesByIdoforg;
    }

    public void setCfMenuexchangerulesesByIdoforg(Collection<CfMenuexchangerules> cfMenuexchangerulesesByIdoforg) {
        this.cfMenuexchangerulesesByIdoforg = cfMenuexchangerulesesByIdoforg;
    }

    private Collection<CfMenuexchangerules> cfMenuexchangerulesesByIdoforg_0;

    public Collection<CfMenuexchangerules> getCfMenuexchangerulesesByIdoforg_0() {
        return cfMenuexchangerulesesByIdoforg_0;
    }

    public void setCfMenuexchangerulesesByIdoforg_0(Collection<CfMenuexchangerules> cfMenuexchangerulesesByIdoforg_0) {
        this.cfMenuexchangerulesesByIdoforg_0 = cfMenuexchangerulesesByIdoforg_0;
    }

    private Collection<CfOrderdetails> cfOrderdetailsesByIdoforg;

    public Collection<CfOrderdetails> getCfOrderdetailsesByIdoforg() {
        return cfOrderdetailsesByIdoforg;
    }

    public void setCfOrderdetailsesByIdoforg(Collection<CfOrderdetails> cfOrderdetailsesByIdoforg) {
        this.cfOrderdetailsesByIdoforg = cfOrderdetailsesByIdoforg;
    }

    private Collection<CfOrders> cfOrdersesByIdoforg;

    public Collection<CfOrders> getCfOrdersesByIdoforg() {
        return cfOrdersesByIdoforg;
    }

    public void setCfOrdersesByIdoforg(Collection<CfOrders> cfOrdersesByIdoforg) {
        this.cfOrdersesByIdoforg = cfOrdersesByIdoforg;
    }

    private CfContracts cfContractsByIdofcontract;

    public CfContracts getCfContractsByIdofcontract() {
        return cfContractsByIdofcontract;
    }

    public void setCfContractsByIdofcontract(CfContracts cfContractsByIdofcontract) {
        this.cfContractsByIdofcontract = cfContractsByIdofcontract;
    }

    private CfContragents cfContragentsByDefaultsupplier;

    public CfContragents getCfContragentsByDefaultsupplier() {
        return cfContragentsByDefaultsupplier;
    }

    public void setCfContragentsByDefaultsupplier(CfContragents cfContragentsByDefaultsupplier) {
        this.cfContragentsByDefaultsupplier = cfContragentsByDefaultsupplier;
    }

    private CfPersons cfPersonsByIdofofficialperson;

    public CfPersons getCfPersonsByIdofofficialperson() {
        return cfPersonsByIdofofficialperson;
    }

    public void setCfPersonsByIdofofficialperson(CfPersons cfPersonsByIdofofficialperson) {
        this.cfPersonsByIdofofficialperson = cfPersonsByIdofofficialperson;
    }

    private Collection<CfSynchistory> cfSynchistoriesByIdoforg;

    public Collection<CfSynchistory> getCfSynchistoriesByIdoforg() {
        return cfSynchistoriesByIdoforg;
    }

    public void setCfSynchistoriesByIdoforg(Collection<CfSynchistory> cfSynchistoriesByIdoforg) {
        this.cfSynchistoriesByIdoforg = cfSynchistoriesByIdoforg;
    }
}
