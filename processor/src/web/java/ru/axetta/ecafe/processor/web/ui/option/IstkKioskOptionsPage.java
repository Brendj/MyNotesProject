package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.web.istkservice.client.*;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.xml.ws.BindingProvider;
import java.util.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Akhmetov
 * Date: 24.03.16
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class IstkKioskOptionsPage extends BasicWorkspacePage {

    private static final String EMPTY_STRING = "";
    private static final Long SUCCESS_CODE = 0L;
    private static final String ERROR_DURING_USING_WEB_SERVICE = "Error using client of web service istk-school:";
    private static final Logger logger = LoggerFactory.getLogger(IstkKioskOptionsPage.class);

    private String wsAddress;
    private boolean tableVisible = false;
    private List<SchoolItem> schoolItemList;
    private SchoolItem currentItem;
    private Map<Long, Boolean> changedPermissions = new HashMap<Long, Boolean>();

    public Object connect() {
        try {
            IstkController port = getIstkControllerEndpoint();
            final SchoolListResult listResult = port.getSchools();
            changedPermissions.clear();

            if (listResult.getResultCode().equals(SUCCESS_CODE)) {
                final List<PlainSchool> schools = listResult.getSchoolList();
                final SchoolListResult.Districts districts = listResult.getDistricts();
                final SchoolListResult.Areas areas = listResult.getAreas();
                schoolItemList = fillSchoolItemList(schools, districts, areas);
                tableVisible = true;
            } else {
                printWarn(listResult.getResultDescription());
                tableVisible = false;
            }
        } catch (Exception ex) {
            logger.error(String.format("%s %s", ERROR_DURING_USING_WEB_SERVICE, "method - getSchools()"), ex);
            tableVisible = false;
            printError(
                    "Ошибка. Проверьте адрес веб-сервиса. Формат адреса: http://{hostName}:8080/istk-school/soap/client");

        }
        return null;
    }

    public Object processGameFlag() {
        if(currentItem.getPermitGame() != currentItem.getOldPermitValue()) {
            changedPermissions.put(currentItem.getSchoolId(), currentItem.getPermitGame());
            currentItem.style = SchoolItem.CHANGED_PERMISSION_STYLE;
        }else {
            changedPermissions.remove(currentItem.getSchoolId());
            currentItem.style = SchoolItem.DEFAULT_CSS_STYLE;
        }
        return null;
    }

    public Object saveChangedParameters() {
        try {
            IstkController port = getIstkControllerEndpoint();
            final BaseResult result = port.saveSchoolsGamePermissions(getParamsAsPair(changedPermissions));
            if (result.getResultCode().equals(SUCCESS_CODE)) {
                refreshData();
                printMessage("Данные успешно сохранены");
            }else {
                printWarn(result.getResultDescription());
            }
        }catch(Exception ex) {
            logger.error(String.format("%s %s", ERROR_DURING_USING_WEB_SERVICE, "method - saveSchoolsGamePermissions()"));
            printError("Возникла ошибка во время сохранения данных.");
        }
        return null;
    }

    private List<SchoolItem> fillSchoolItemList(List<PlainSchool> schools, SchoolListResult.Districts districts,
            SchoolListResult.Areas areas) {
        List<SchoolItem> schoolItems = new ArrayList<SchoolItem>();

        for (PlainSchool school : schools) {
            Long schoolId = school.getSchoolId();
            String schoolName = school.getSchoolName();
            String district = getDistrict(school.getDistrictId(), districts);
            String area = getArea(school.getAreaId(), areas);
            Long isppId = school.getIsppId();
            Boolean showGame = school.isPermitGame();
            schoolItems.add(new SchoolItem(schoolId, schoolName, district, area, isppId, showGame));
        }
        return schoolItems;
    }

    private String getDistrict(Long districtId, SchoolListResult.Districts districts) {
        for (SchoolListResult.Districts.Entry district : districts.getEntry()) {
            if (district.getKey().equals(districtId)) {
                return district.getValue();
            }
        }
        return EMPTY_STRING;
    }

    private String getArea(Long areaId, SchoolListResult.Areas areas) {
        for (SchoolListResult.Areas.Entry area : areas.getEntry()) {
            if (area.getKey().equals(areaId)) {
                return area.getValue();
            }
        }
        return EMPTY_STRING;
    }

    private IstkController getIstkControllerEndpoint() {
        IstkController_Service service = new IstkController_Service();
        IstkController port = service.getIstkControllerPort();
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsAddress);
        return port;
    }

    private List<Pair> getParamsAsPair(Map<Long, Boolean> params) {
        List<Pair> pairs = new ArrayList<Pair>();
        for(Map.Entry<Long, Boolean> entry : params.entrySet()) {
            Pair pair = new Pair();
            pair.setId(entry.getKey());
            pair.setFlag(entry.getValue());
            pairs.add(pair);
        }
        return pairs;
    }

    private void refreshData() {
        changedPermissions.clear();
        for(SchoolItem item : schoolItemList) {
            item.setOldPermitValue(item.getPermitGame());
            item.setStyle(SchoolItem.DEFAULT_CSS_STYLE);
        }
    }

    @Override
    public String getPageFilename() {
        return "option/istk_kiosk_option";
    }

    public String getWsAddress() {
        return wsAddress;
    }

    public void setWsAddress(String wsAddress) {
        this.wsAddress = wsAddress;
    }

    public boolean isTableVisible() {
        return tableVisible;
    }

    public List<SchoolItem> getSchoolItemList() {
        return schoolItemList;
    }

    public SchoolItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(SchoolItem currentItem) {
        this.currentItem = currentItem;
    }

    public Map<Long, Boolean> getChangedPermissions() {
        return changedPermissions;
    }

    public static class SchoolItem {
        private static final String DEFAULT_CSS_STYLE = "output-text";
        private static final String CHANGED_PERMISSION_STYLE = "changed-permission-option";

        private Long schoolId;
        private String schoolName;
        private String district;
        private String area;
        private Long isppId;
        private Boolean permitGame;
        private Boolean oldPermitValue;
        private String style = DEFAULT_CSS_STYLE;

        public SchoolItem(Long schoolId, String schoolName, String district, String area, Long isppId,
                Boolean permitGame) {
            this.schoolId = schoolId;
            this.schoolName = schoolName;
            this.district = district;
            this.area = area;
            this.isppId = isppId;
            this.permitGame = permitGame;
            this.oldPermitValue = permitGame;
        }

        public void setPermitGame(Boolean permitGame) {
            this.permitGame = permitGame;
        }

        public Long getSchoolId() {
            return schoolId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public String getDistrict() {
            return district;
        }

        public String getArea() {
            return area;
        }

        public Long getIsppId() {
            return isppId;
        }

        public Boolean getPermitGame() {
            return permitGame;
        }

        public Boolean getOldPermitValue() {
            return oldPermitValue;
        }

        public void setOldPermitValue(Boolean oldPermitValue) {
            this.oldPermitValue = oldPermitValue;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        @Override
        public String toString() {
            return "SchoolItem{" +
                    "schoolId=" + schoolId +
                    ", schoolName='" + schoolName + '\'' +
                    ", district='" + district + '\'' +
                    ", area='" + area + '\'' +
                    ", isppId=" + isppId +
                    ", permitGame=" + permitGame +
                    ", oldPermitValue=" + oldPermitValue +
                    ", style='" + style + '\'' +
                    '}';
        }
    }
}
