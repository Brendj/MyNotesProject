/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.01.15
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public class OrgRegistryChange {
    public static final int CREATE_OPERATION = 1;
    public static final int MODIFY_OPERATION = 2;
    public static final int DELETE_OPERATION = 3;
    public static final int SIMILAR = 4;

    protected Long idOfOrgRegistryChange;
    protected Long idOfOrg;
    protected Long createDate;
    protected Integer operationType;

    protected OrganizationType organizationType;
    protected OrganizationType organizationTypeFrom;
    protected String shortName;
    protected String shortNameFrom;
    protected String officialName;
    protected String officialNameFrom;

    protected String address;
    protected String addressFrom;
    protected String city;
    protected String cityFrom;
    protected String region;
    protected String regionFrom;

    protected Long unom;
    protected Long unomFrom;
    protected Long unad;
    protected Long unadFrom;
    protected Long uniqueAddressId;
    protected Long uniqueAddressIdFrom;
    protected String inn;
    protected String innFrom;

    protected String guid;
    protected String guidFrom;
    protected Long additionalId;

    protected Long ekisId;
    protected Long ekisIdFrom;
    protected String egissoId;
    protected String egissoIdFrom;
    protected String municipalDistrict;
    protected String municipalDistrictFrom;
    protected String shortAddress;
    protected String shortAddressFrom;
    protected String founder;
    protected String founderFrom;
    protected String subordination;
    protected String subordinationFrom;
    protected Long globalId;
    protected Long globalIdFrom;

    private Set<OrgRegistryChangeItem> orgs;

    protected Boolean applied = false;



    public OrgRegistryChange() {
    }

    public OrgRegistryChange(Long idOfOrg,
                             OrganizationType organizationType, OrganizationType organizationTypeFrom,
                             String shortName, String shortNameFrom,
                             String officialName, String officialNameFrom,
                             Long createDate, Integer operationType,

                             Boolean applied,

                             String address, String addressFrom,
                             String city, String cityFrom,
                             String region, String regionFrom,

                             Long unom, Long unomFrom,
                             Long unad, Long unadFrom,
                             Long uniqueAddressId, Long uniqueAddressIdFrom,
                             String inn, String innFrom,

                             String guid, String guidFrom,
                             Long additionalId,
                             Long ekisId, Long ekisIdFrom,
                             String egissoId, String egissoIdFrom,
                             String shortAddress, String shortAddressFrom,
                             String municipalDistrict, String municipalDistrictFrom,
                             String founder, String founderFrom,
                             String subordination, String subordinationFrom,
                             Long globalId, Long globalIdFrom
            ) {
        this.idOfOrg = idOfOrg;

        this.organizationType = organizationType;
        this.organizationTypeFrom = organizationTypeFrom;
        this.shortName = shortName;
        this.officialName = officialName;
        this.shortNameFrom = shortNameFrom;
        this.officialNameFrom = officialNameFrom;
        this.createDate = createDate;
        this.operationType = operationType;

        this.applied = applied;

        this.address = address;
        this.addressFrom = addressFrom;
        this.city = city;
        this.cityFrom = cityFrom;
        this.region = region;
        this.regionFrom = regionFrom;

        this.unom = unom;
        this.unomFrom = unomFrom;
        this.unad = unad;
        this.unadFrom = unadFrom;
        this.uniqueAddressId = uniqueAddressId;
        this.uniqueAddressIdFrom = uniqueAddressIdFrom;
        this.inn = inn;
        this.innFrom = innFrom;

        this.guid = guid;
        this.guidFrom = guidFrom;
        this.additionalId = additionalId;

        this.ekisId = ekisId;
        this.ekisIdFrom = ekisIdFrom;
        this.egissoId = egissoId;
        this.egissoIdFrom = egissoIdFrom;
        this.shortAddress = shortAddress;
        this.shortAddressFrom = shortAddressFrom;
        this.municipalDistrict = municipalDistrict;
        this.municipalDistrictFrom = municipalDistrictFrom;
        this.founder = founder;
        this.founderFrom = founderFrom;
        this.subordination = subordination;
        this.subordinationFrom = subordinationFrom;
        this.globalId = globalId;
        this.globalIdFrom = globalIdFrom;
    }

    public Long getAdditionalId() {
        return additionalId;
    }

    public void setAdditionalId(Long additionalId) {
        this.additionalId = additionalId;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public Boolean getApplied() {
        return applied;
    }

    public void setApplied(Boolean applied) {
        this.applied = applied;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationType getOrganizationTypeFrom() {
        return organizationTypeFrom;
    }

    public void setOrganizationTypeFrom(OrganizationType organizationTypeFrom) {
        this.organizationTypeFrom = organizationTypeFrom;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortNameFrom() {
        return shortNameFrom;
    }

    public void setShortNameFrom(String shortNameFrom) {
        this.shortNameFrom = shortNameFrom;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getOfficialNameFrom() {
        return officialNameFrom;
    }

    public void setOfficialNameFrom(String officialNameFrom) {
        this.officialNameFrom = officialNameFrom;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityFrom() {
        return cityFrom;
    }

    public void setCityFrom(String cityFrom) {
        this.cityFrom = cityFrom;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionFrom() {
        return regionFrom;
    }

    public void setRegionFrom(String regionFrom) {
        this.regionFrom = regionFrom;
    }

    public Long getUnom() {
        return unom;
    }

    public void setUnom(Long unom) {
        this.unom = unom;
    }

    public Long getUnomFrom() {
        return unomFrom;
    }

    public void setUnomFrom(Long unomFrom) {
        this.unomFrom = unomFrom;
    }

    public Long getUnad() {
        return unad;
    }

    public void setUnad(Long unad) {
        this.unad = unad;
    }

    public Long getUnadFrom() {
        return unadFrom;
    }

    public void setUnadFrom(Long unadFrom) {
        this.unadFrom = unadFrom;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGuidFrom() {
        return guidFrom;
    }

    public void setGuidFrom(String guidFrom) {
        this.guidFrom = guidFrom;
    }

    public Long getIdOfOrgRegistryChange() {
        return idOfOrgRegistryChange;
    }

    public void setIdOfOrgRegistryChange(Long idOfOrgRegistryChange) {
        this.idOfOrgRegistryChange = idOfOrgRegistryChange;
    }

    public Set<OrgRegistryChangeItem> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<OrgRegistryChangeItem> orgs) {
        this.orgs = orgs;
    }

    public Long getUniqueAddressId() {
        return uniqueAddressId;
    }

    public void setUniqueAddressId(Long uniqueAddressId) {
        this.uniqueAddressId = uniqueAddressId;
    }

    public Long getUniqueAddressIdFrom() {
        return uniqueAddressIdFrom;
    }

    public void setUniqueAddressIdFrom(Long uniqueAddressIdFrom) {
        this.uniqueAddressIdFrom = uniqueAddressIdFrom;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getInnFrom() {
        return innFrom;
    }

    public void setInnFrom(String innFrom) {
        this.innFrom = innFrom;
    }

    @Override
    public boolean equals(Object obj){
        if(this.getClass() != obj.getClass()){
            return false;
        }
        OrgRegistryChange other = (OrgRegistryChange) obj;
        return this.idOfOrgRegistryChange.equals(other.getIdOfOrgRegistryChange());
    }

    public Long getEkisId() {
        return ekisId;
    }

    public void setEkisId(Long ekisId) {
        this.ekisId = ekisId;
    }

    public Long getEkisIdFrom() {
        return ekisIdFrom;
    }

    public void setEkisIdFrom(Long ekisIdFrom) {
        this.ekisIdFrom = ekisIdFrom;
    }

    public String getEgissoId() {
        return egissoId;
    }

    public void setEgissoId(String egissoId) {
        this.egissoId = egissoId;
    }

    public String getEgissoIdFrom() {
        return egissoIdFrom;
    }

    public void setEgissoIdFrom(String egissoIdFrom) {
        this.egissoIdFrom = egissoIdFrom;
    }

    public String getMunicipalDistrict() {
        return municipalDistrict;
    }

    public void setMunicipalDistrict(String municipalDistrict) {
        this.municipalDistrict = municipalDistrict;
    }

    public String getMunicipalDistrictFrom() {
        return municipalDistrictFrom;
    }

    public void setMunicipalDistrictFrom(String municipalDistrictFrom) {
        this.municipalDistrictFrom = municipalDistrictFrom;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getShortAddressFrom() {
        return shortAddressFrom;
    }

    public void setShortAddressFrom(String shortAddressFrom) {
        this.shortAddressFrom = shortAddressFrom;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getFounderFrom() {
        return founderFrom;
    }

    public void setFounderFrom(String founderFrom) {
        this.founderFrom = founderFrom;
    }

    public String getSubordination() {
        return subordination;
    }

    public void setSubordination(String subordination) {
        this.subordination = subordination;
    }

    public String getSubordinationFrom() {
        return subordinationFrom;
    }

    public void setSubordinationFrom(String subordinationFrom) {
        this.subordinationFrom = subordinationFrom;
    }

    public Long getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    public Long getGlobalIdFrom() {
        return globalIdFrom;
    }

    public void setGlobalIdFrom(Long globalIdFrom) {
        this.globalIdFrom = globalIdFrom;
    }
}