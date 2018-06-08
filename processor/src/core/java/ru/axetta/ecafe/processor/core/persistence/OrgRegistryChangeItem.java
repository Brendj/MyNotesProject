/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * User: Shamil
 * Date: 27.07.15
 */
public class OrgRegistryChangeItem {

    protected Long idOfOrgRegistryChangeItem;
    protected Long idOfOrg;
    protected Long createDate;
    protected Integer operationType;

    protected OrganizationType organizationType;
    protected OrganizationType organizationTypeFrom;
    protected String shortName;
    protected String shortNameFrom;
    protected String shortNameSupplierFrom;
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

    private OrgRegistryChange orgRegistryChange;
    private Boolean mainBuilding;

    protected String interdistrictCouncil;
    protected String interdistrictCouncilFrom;
    protected String interdistrictCouncilChief;
    protected String interdistrictCouncilChiefFrom;

    protected Boolean applied = false;
    private Long mainRegistry;

    public Long getMainRegistry() {
        return mainRegistry;
    }

    public void setMainRegistry(Long mainRegistry) {
        this.mainRegistry = mainRegistry;
    }


    public OrgRegistryChangeItem() {
    }

    public OrgRegistryChangeItem(Long idOfOrg, OrganizationType organizationType, OrganizationType organizationTypeFrom,
            String shortName, String shortNameFrom, String officialName, String officialNameFrom, Long createDate,
            Integer operationType,

            Boolean applied,

            String address, String addressFrom, String city, String cityFrom, String region, String regionFrom,

            Long unom, Long unomFrom, Long unad, Long unadFrom, Long uniqueAddressId, Long uniqueAddressIdFrom,
            String inn, String innFrom,

            String guid, String guidFrom, Long additionalId, String interdistrictCouncil,
            String interdistrictCouncilFrom, String interdistrictCouncilChief, String interdistrictCouncilChiefFrom,
            OrgRegistryChange orgRegistryChange, boolean mainBuilding, String shortNameSupplierFrom) {
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

        this.interdistrictCouncil = interdistrictCouncil;
        this.interdistrictCouncilFrom = interdistrictCouncilFrom;
        this.interdistrictCouncilChief = interdistrictCouncilChief;
        this.interdistrictCouncilChiefFrom = interdistrictCouncilChiefFrom;
        this.orgRegistryChange = orgRegistryChange;
        this.mainBuilding = mainBuilding;
        this.shortNameSupplierFrom = shortNameSupplierFrom;
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

    public String getShortNameSupplierFrom() {
        return shortNameSupplierFrom;
    }

    public void setShortNameSupplierFrom(String shortNameSupplierFrom) {
        this.shortNameSupplierFrom = shortNameSupplierFrom;
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

    public Long getIdOfOrgRegistryChangeItem() {
        return idOfOrgRegistryChangeItem;
    }

    public void setIdOfOrgRegistryChangeItem(Long idOfOrgRegistryChangeItem) {
        this.idOfOrgRegistryChangeItem = idOfOrgRegistryChangeItem;
    }

    public String getInterdistrictCouncil() {
        return interdistrictCouncil;
    }

    public void setInterdistrictCouncil(String interdistrictCouncil) {
        this.interdistrictCouncil = interdistrictCouncil;
    }

    public String getInterdistrictCouncilFrom() {
        return interdistrictCouncilFrom;
    }

    public void setInterdistrictCouncilFrom(String interdistrictCouncilFrom) {
        this.interdistrictCouncilFrom = interdistrictCouncilFrom;
    }

    public String getInterdistrictCouncilChief() {
        return interdistrictCouncilChief;
    }

    public void setInterdistrictCouncilChief(String interdistrictCouncilChief) {
        this.interdistrictCouncilChief = interdistrictCouncilChief;
    }

    public String getInterdistrictCouncilChiefFrom() {
        return interdistrictCouncilChiefFrom;
    }

    public void setInterdistrictCouncilChiefFrom(String interdistrictCouncilChiefFrom) {
        this.interdistrictCouncilChiefFrom = interdistrictCouncilChiefFrom;
    }

    public OrgRegistryChange getOrgRegistryChange() {
        return orgRegistryChange;
    }

    public void setOrgRegistryChange(OrgRegistryChange orgRegistryChange) {
        this.orgRegistryChange = orgRegistryChange;
    }

    public Boolean getMainBuilding() {
        if (mainBuilding == null) return false;
        return mainBuilding;
    }

    public void setMainBuilding(Boolean mainBuilding) {
        this.mainBuilding = mainBuilding;
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
}