package ru.axetta.ecafe.processor.core.report.statistics.telephone.number;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.01.14
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
public class TelephoneNumberCountItem {

    private String district;
    private String shortName;
    private String group;
    private Long countTelephone;
    private Long countActiveTelephone;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getCountTelephone() {
        return countTelephone;
    }

    public void setCountTelephone(Long countTelephone) {
        this.countTelephone = countTelephone;
    }

    public Long getCountActiveTelephone() {
        return countActiveTelephone;
    }

    public void setCountActiveTelephone(Long countActiveTelephone) {
        this.countActiveTelephone = countActiveTelephone;
    }
}
