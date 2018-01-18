package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created with IntelliJ IDEA.
 * User: Akhmetov
 * Date: 12.05.16
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class OrgIstkSummaryItem {
    private String schoolName;
    private Long isppId;
    private String address;
    private Long version;
    private String guid;
    private String strDistrict;
    private String shortNameInfoService;
    private Long mainBuildingId;


    public OrgIstkSummaryItem() {
    }

    public OrgIstkSummaryItem(String schoolName, Long isppId, String address, Long version, String guid, String strDistrict, String shortNameInfoService, Long mainBuildingId) {
        this.schoolName = schoolName;
        this.isppId = isppId;
        this.address = address;
        this.version = version;
        this.guid = guid;
        this.strDistrict = strDistrict;
        this.shortNameInfoService = shortNameInfoService;
        this.mainBuildingId = mainBuildingId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Long getIsppId() {
        return isppId;
    }

    public void setIsppId(Long isppId) {
        this.isppId = isppId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getStrDistrict() {
        return strDistrict;
    }

    public void setStrDistrict(String strDistrict) {
        this.strDistrict = strDistrict;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public Long getMainBuildingId() {
        return mainBuildingId;
    }

    public void setMainBuildingId(Long mainBuildingId) {
        this.mainBuildingId = mainBuildingId;
    }

}
