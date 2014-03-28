package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 28.03.14
 * Time: 15:56
 * Настройки отчетов пользователей
 */
public class UserReportSetting {

    public final static int GOOD_REQUEST_REPORT = 0;
    public final static Map<Integer, String> map = new HashMap<Integer, String>();
    static {
        map.put(GOOD_REQUEST_REPORT, "Сводный отчет по заявкам");
    }

    private Long idOfUserReportSetting;
    private Integer numberOfReport;
    private User user;
    private String settings;

    public Long getIdOfUserReportSetting() {
        return idOfUserReportSetting;
    }

    public void setIdOfUserReportSetting(Long idOfUserReportSetting) {
        this.idOfUserReportSetting = idOfUserReportSetting;
    }

    public Integer getNumberOfReport() {
        return numberOfReport;
    }

    public void setNumberOfReport(Integer numberOfReport) {
        this.numberOfReport = numberOfReport;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }
}
