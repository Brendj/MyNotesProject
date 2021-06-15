package ru.iteco.restservice.servise;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iteco.restservice.controller.menu.request.PreorderComplexRequest;
import ru.iteco.restservice.controller.menu.request.PreorderDishRequest;
import ru.iteco.restservice.controller.menu.request.RegularPreorderRequest;
import ru.iteco.restservice.db.repo.readonly.*;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.*;
import ru.iteco.restservice.model.enums.EntityStateType;
import ru.iteco.restservice.model.enums.PreorderMobileGroupOnCreateType;
import ru.iteco.restservice.model.enums.PreorderState;
import ru.iteco.restservice.model.enums.SettingsIds;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;
import ru.iteco.restservice.model.preorder.RegularPreorder;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtComplexesItem;
import ru.iteco.restservice.model.wt.WtDish;
import ru.iteco.restservice.servise.data.PreOrderFeedingSettingValue;
import ru.iteco.restservice.servise.data.PreorderComplexChangeData;
import ru.iteco.restservice.servise.data.PreorderDateComparator;

import java.util.*;

@Service
public class PreorderService {
    @Autowired
    ClientReadOnlyRepo clientRepo;
    @Autowired
    PreorderComplexReadOnlyRepo pcRepo;
    @Autowired
    PreorderMenuDetailReadOnlyRepo pmdRepo;
    @Autowired
    ClientGuardianReadOnlyRepo cgRepo;
    @Autowired
    SpecialDateReadOnlyRepo sdRepo;
    @Autowired
    ECafeSettingsReadOnlyRepo settingsRepo;
    @Autowired
    ProductionCalendarReadOnlyRepo calendarRepo;
    @Autowired
    ClientGroupReadOnlyRepo cgroupRepo;
    @Autowired
    GroupNamesToOrgsReadOnlyRepo gntoRepo;
    @Autowired
    RegularPreorderReadOnlyRepo regularRepo;
    @Autowired
    WtComplexReadOnlyRepo complexRepo;
    @Autowired
    WtDishReadOnlyRepo dishRepo;

    @Autowired
    PreorderDAO preorderDAO;
    @Autowired
    ComplexService complexService;

    private final Logger logger = LoggerFactory.getLogger(PreorderService.class);

    public static final Integer SOAP_RC_CLIENT_NOT_FOUND = 110;
    public static final Integer SOAP_RC_SEVERAL_CLIENTS_WERE_FOUND = 120;
    public static final Integer SOAP_RC_PREORDERS_NOT_UNIQUE_CLIENT = 644;
    public static final Integer SOAP_RC_WRONG_GROUP = 710;
    public static final Integer SOAP_RC_MOBILE_DIFFERENT_GROUPS = 711;
    public static final Integer DEFAULT_FORBIDDEN_DAYS = 2;

    public void checkCreateParameters(PreorderComplexRequest preorderComplexRequest) throws IllegalArgumentException {
        if (preorderComplexRequest.getAmount() == null
                || preorderComplexRequest.getComplexId() == null
                || preorderComplexRequest.getContractId() == null
                || preorderComplexRequest.getDate() == null) throw new IllegalArgumentException("Не заполнены обязательные параметры");
        if (preorderComplexRequest.getAmount() <= 0) throw new IllegalArgumentException("Количество должно быть > 0");
    }

    public void checkEditParameters(PreorderComplexRequest preorderComplexRequest) throws IllegalArgumentException {
        if (preorderComplexRequest.getPreorderId() == null
                || preorderComplexRequest.getContractId() == null
                || preorderComplexRequest.getAmount() == null) throw new IllegalArgumentException("Не заполнены обязательные параметры");
        if (preorderComplexRequest.getAmount() <= 0) throw new IllegalArgumentException("Количество должно быть > 0");
    }

    public void checkDeleteParameters(PreorderComplexRequest preorderComplexRequest) throws IllegalArgumentException {
        if (preorderComplexRequest.getPreorderId() == null
                || preorderComplexRequest.getContractId() == null) throw new IllegalArgumentException("Не заполнены обязательные параметры");
    }

    public void checkDishCreateParameters(PreorderDishRequest preorderDishRequest) throws IllegalArgumentException {
        if (preorderDishRequest.getAmount() == null
                || preorderDishRequest.getComplexId() == null
                || preorderDishRequest.getDishId() == null
                || preorderDishRequest.getContractId() == null
                || preorderDishRequest.getDate() == null) throw new IllegalArgumentException("Не заполнены обязательные параметры");
        if (preorderDishRequest.getAmount() <= 0) throw new IllegalArgumentException("Количество должно быть > 0");
    }

    public void checkDishEditParameters(PreorderDishRequest preorderDishRequest) throws IllegalArgumentException {
        if (preorderDishRequest.getPreorderDishId() == null
                || preorderDishRequest.getContractId() == null
                || preorderDishRequest.getAmount() == null) throw new IllegalArgumentException("Не заполнены обязательные параметры");
        if (preorderDishRequest.getAmount() <= 0) throw new IllegalArgumentException("Количество должно быть > 0");
    }

    public void checkDishDeleteParameters(PreorderDishRequest preorderDishRequest) throws IllegalArgumentException {
        if (preorderDishRequest.getPreorderDishId() == null
                || preorderDishRequest.getContractId() == null) throw new IllegalArgumentException("Не заполнены обязательные параметры");
    }

    public void checkRegularPreorderCreateParameters(RegularPreorderRequest regularPreorderRequest) throws IllegalArgumentException {
        if (!regularPreorderRequest.enoughDataForCreate()) throw new IllegalArgumentException("Не заполнены обязательные параметры");
    }

    public void checkRegularPreorderEditParameters(RegularPreorderRequest regularPreorderRequest) throws IllegalArgumentException {
        if (!regularPreorderRequest.enoughDataForEdit()) throw new IllegalArgumentException("Не заполнены обязательные параметры");
    }

    public void checkRegularPreorderDeleteParameters(RegularPreorderRequest regularPreorderRequest) throws IllegalArgumentException {
        if (!regularPreorderRequest.enoughDataForDelete()) throw new IllegalArgumentException("Не заполнены обязательные параметры");
    }

    public PreorderComplex editPreorder(Long preorderId, Long contractId, String guardianMobile, Integer amount) throws Exception {
        PreorderComplex preorderComplex = pcRepo.findById(preorderId)
                .orElseThrow(() -> new NotFoundException("Предзаказ с указанным идентификатором не найден"));
        Client client = clientRepo.getClientByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        if (!preorderComplex.getClient().equals(client)) {
            throw new IllegalArgumentException("Предзаказ не принадлежит данному клиенту");
        }

        if (!isEditedDay(preorderComplex.getPreorderDate(), client)) throw new IllegalArgumentException("День недоступен для редактирования предзаказа");

        long nextVersion = pcRepo.getMaxVersion() + 1;
        preorderDAO.editPreorder(preorderComplex, guardianMobile, amount, nextVersion);
        return preorderComplex;
    }

    public PreorderComplex deletePreorder(Long preorderId, Long contractId, String guardianMobile) throws Exception {
        PreorderComplex preorderComplex = pcRepo.getPreorderComplexesWithDetails(preorderId)
                .orElseThrow(() -> new NotFoundException("Предзаказ с указанным идентификатором не найден"));
        Client client = clientRepo.getClientByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        if (!preorderComplex.getClient().equals(client)) {
            throw new IllegalArgumentException("Предзаказ не принадлежит данному клиенту");
        }

        if (!isEditedDay(preorderComplex.getPreorderDate(), client)) throw new IllegalArgumentException("День недоступен для редактирования предзаказа");

        long nextVersion = pcRepo.getMaxVersion() + 1;
        preorderDAO.deletePreorder(preorderComplex, guardianMobile, nextVersion);
        return preorderComplex;
    }

    public PreorderMenuDetail createPreorderMenuDetail(Long contractId, Date date, String guardianMobile, Long complexId,
                                              Long dishId, Integer amount) throws Exception {
        PreorderComplexChangeData data = getPreorderComplexChangeData(contractId, date, guardianMobile);

        long nextVersion = pcRepo.getMaxVersion() + 1;
        return preorderDAO.createPreorderMenuDetail(data, complexId, dishId, amount, guardianMobile, nextVersion);
    }

    public PreorderMenuDetail editPreorderMenuDetail(Long preorderDishId, Long contractId,
                                                     String guardianMobile, Integer amount) throws Exception {
        PreorderMenuDetail preorderMenuDetail = pmdRepo.findById(preorderDishId)
                .orElseThrow(() -> new NotFoundException("Предзаказ на блюдо с указанным идентификатором не найден"));
        Client client = clientRepo.getClientByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        if (!preorderMenuDetail.getClient().equals(client)) {
            throw new IllegalArgumentException("Предзаказ на блюдо не принадлежит данному клиенту");
        }

        if (!isEditedDay(preorderMenuDetail.getPreorderDate(), client)) throw new IllegalArgumentException("День недоступен для редактирования предзаказа");

        long nextVersion = pcRepo.getMaxVersion() + 1;
        return preorderDAO.editPreorderMenuDetail(preorderMenuDetail, guardianMobile, amount, nextVersion);
    }

    public PreorderMenuDetail deletePreorderMenuDetail(Long preorderDishId, Long contractId, String guardianMobile) throws Exception {
        PreorderMenuDetail preorderMenuDetail = pmdRepo.getPreorderMenuDetailWithPreorderComplex(preorderDishId)
                .orElseThrow(() -> new NotFoundException("Предзаказ на блюдо с указанным идентификатором не найден"));
        Client client = clientRepo.getClientByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        if (!preorderMenuDetail.getClient().equals(client)) {
            throw new IllegalArgumentException("Предзаказ не принадлежит данному клиенту");
        }

        if (!isEditedDay(preorderMenuDetail.getPreorderDate(), client)) throw new IllegalArgumentException("День недоступен для редактирования предзаказа");

        long nextVersion = pcRepo.getMaxVersion() + 1;
        preorderDAO.deletePreorderMenuDetail(preorderMenuDetail, guardianMobile, nextVersion);
        return preorderMenuDetail;
    }

    public RegularPreorder createRegularPreorder(RegularPreorderRequest regularPreorderRequest) throws Exception {
        PreorderComplexChangeData data = getPreorderComplexChangeData(regularPreorderRequest.getContractId(),
                RegularPreorder.convertDate(regularPreorderRequest.getStartDate()), regularPreorderRequest.getGuardianMobile());
        RegularPreorder regularPreorder;
        if (regularPreorderRequest.getDishId() == null) {
            regularPreorder = regularRepo.getRegularPreorderByClientAndComplex(data.getClient(), regularPreorderRequest.getComplexId());
        } else {
            regularPreorder = regularRepo.getRegularPreorderByClientComplexAndDish(data.getClient(), regularPreorderRequest.getComplexId(),
                    regularPreorderRequest.getDishId());
        }
        if (regularPreorder != null) {
            throw new IllegalArgumentException("У данного клиента уже существует регулярный предзаказ на выбранный комплекс или блюдо");
        }

        return preorderDAO.createRegularPreorder(data, regularPreorderRequest);
    }

    public RegularPreorder editRegularPreorder(RegularPreorderRequest regularPreorderRequest) throws Exception {
        RegularPreorder regularPreorder = regularRepo.findById(regularPreorderRequest.getRegularPreorderId())
                .orElseThrow(() -> new NotFoundException("Регулярный заказ с указанным идентификатором не найден"));

        Client client = clientRepo.getClientByContractId(regularPreorderRequest.getContractId())
                .orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        if (!regularPreorder.getClient().equals(client)) {
            throw new IllegalArgumentException("Регулярный заказ не принадлежит данному клиенту");
        }

        return preorderDAO.editRegularPreorder(regularPreorder, regularPreorderRequest);
    }

    public void deleteRegularPreorder(Long regularPreorderId, Long contractId) throws Exception {
        RegularPreorder regularPreorder = regularRepo.findById(regularPreorderId)
                .orElseThrow(() -> new NotFoundException("Регулярный заказ с указанным идентификатором не найден"));

        Client client = clientRepo.getClientByContractId(contractId)
                .orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        if (!regularPreorder.getClient().equals(client)) {
            throw new IllegalArgumentException("Регулярный заказ не принадлежит данному клиенту");
        }

        preorderDAO.deleteRegularPreorder(regularPreorder);
    }

    public PreorderComplex createPreorder(Long contractId, Date date, String guardianMobile, Long complexId,
                                          Integer amount) throws Exception {
        PreorderComplexChangeData data = getPreorderComplexChangeData(contractId, date, guardianMobile);

        PreorderComplex preorderComplex = pcRepo.getPreorderComplexesByClientDateAndComplexId(data.getClient(), complexId.intValue(),
                data.getStartDate(), data.getEndDate());
        if (preorderComplex != null) {
            throw new IllegalArgumentException("У данного клиента уже существует предзаказ на выбранную дату и комплекс");
        }
        long nextVersion = pcRepo.getMaxVersion() + 1;

        return preorderDAO.createPreorder(data.getClient(), data.getStartDate(), data.getEndDate(), complexId, amount,
                nextVersion, guardianMobile, data.getMobileGroupOnCreate(), false);
    }

    private PreorderComplexChangeData getPreorderComplexChangeData(Long contractId, Date date,
                                                                   String guardianMobile) throws Exception {
        Client client = clientRepo.getClientByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        Date startDate = CalendarUtils.startOfDay(date);
        Date endDate = CalendarUtils.endOfDay(date);

        if (guardianMobile != null && guardianMobile.equals("")) guardianMobile = null;
        List<Client> clientsByMobile = clientRepo.getClientsByMobile(client.getIdOfClient(), guardianMobile);
        Integer value = getClientGroupResult(clientsByMobile);
        PreorderMobileGroupOnCreateType mobileGroupOnCreate;
        if (value >= SOAP_RC_CLIENT_NOT_FOUND) {
            mobileGroupOnCreate = PreorderMobileGroupOnCreateType.UNKNOWN;
        } else {
            mobileGroupOnCreate = PreorderMobileGroupOnCreateType.fromInteger(value);
            if (mobileGroupOnCreate.equals(PreorderMobileGroupOnCreateType.PARENT_EMPLOYEE)) {
                if (client.getMobile() != null && guardianMobile != null && client.getMobile().equals(guardianMobile)) {
                    mobileGroupOnCreate = PreorderMobileGroupOnCreateType.EMPLOYEE;
                } else {
                    mobileGroupOnCreate = PreorderMobileGroupOnCreateType.PARENT;
                }
            }
        }

        if (!isEditedDay(date, client)) throw new IllegalArgumentException("День недоступен для редактирования предзаказа");
        PreorderComplexChangeData data = new PreorderComplexChangeData(client, startDate, endDate, mobileGroupOnCreate);

        return data;
    }

    private boolean isEditedDay(Date date, Client client) throws Exception {
        boolean result = false;
        Date today = CalendarUtils.startOfDay(new Date());
        Integer syncCountDays = PreorderComplex.getDaysOfRegularPreorders();
        Map<String, Integer[]> sd = getSpecialDates(CalendarUtils.addHours(today, 12), syncCountDays,
                client.getOrg().getIdOfOrg(), client);
        for (Map.Entry<String, Integer[]> entry : sd.entrySet()) {
            if (date.equals(CalendarUtils.parseDate(entry.getKey()))) {
                result = (entry.getValue())[0].equals(0);
                break;
            }
        }
        return result;
    }

    public Map<String, Integer[]> getSpecialDates(Date today, Integer syncCountDays, Long orgId, Client client) throws Exception {
        Comparator comparator = new PreorderDateComparator();
        Map map = new TreeMap(comparator);

        Calendar c = getCalendar();
        c.setTime(today);

        Date endDate = CalendarUtils.addDays(today, syncCountDays);                   //14 календарных дней вперед

        Map<Date, Long[]> usedAmounts = existPreordersByDate(client.getIdOfClient(), today, endDate);     //для показа есть ли предзаказы по датам
        List<SpecialDate> specialDates = sdRepo.getSpecialDatesByOrg(today, endDate, orgId);              //выходные дни по ОО в целом или ее группам
        Integer forbiddenDays = getPreorderFeedingForbiddenDays(client.getOrg().getIdOfOrg());            //дни запрета редактирования
        List<ProductionCalendar> productionCalendar = calendarRepo.findByDayBetween(CalendarUtils.startOfDay(today),
                CalendarUtils.endOfDay(endDate));

        int two_days = 0;
        while (c.getTimeInMillis() < endDate.getTime() ){
            Date currentDate = CalendarUtils.startOfDayInUTC(c.getTime());
            Boolean isWeekend = isWeekendByProductionCalendar(currentDate, productionCalendar);
            if (isWeekend && two_days == 0) two_days++;

            if (two_days <= forbiddenDays) {
                c.add(Calendar.DATE, 1);
                map.put(CalendarUtils.dateToString(currentDate), new Integer[] {1, usedAmounts.get(currentDate) == null ? 0 : usedAmounts.get(currentDate)[0].intValue(),
                        usedAmounts.get(currentDate) == null ? client.getOrg().getIdOfOrg().intValue() : usedAmounts.get(currentDate)[1].intValue()});
                if (!isWeekend) {
                    two_days++;
                }
                continue; //находимся в днях запрета редактирования
            }

            isWeekend = isWeekendBySpecialDateAndSixWorkWeek(isWeekend, currentDate, client.getClientGroup().getClientGroupId().getIdOfClientGroup(),
                    client.getOrg().getIdOfOrg(), specialDates);
            if (!isWeekend) isWeekend = isHolidayByProductionCalendar(currentDate, productionCalendar); //если праздничный день по производственному календарю - то запрет редактирования

            c.add(Calendar.DATE, 1);
            map.put(CalendarUtils.dateToString(currentDate), new Integer[] {isWeekend ? 1 : 0, usedAmounts.get(currentDate) == null ? 0 : usedAmounts.get(currentDate)[0].intValue(),
                    usedAmounts.get(currentDate) == null ? client.getOrg().getIdOfOrg().intValue() : usedAmounts.get(currentDate)[1].intValue()});
        }
        return map;
    }

    public boolean isWeekendByProductionCalendar(Date date, List<ProductionCalendar> productionCalendar) {
        for (ProductionCalendar pc : productionCalendar) {
            if (pc.getDay().equals(date)) return true;
        }
        return false;
    }

    public boolean isHolidayByProductionCalendar(Date date, List<ProductionCalendar> productionCalendar) {
        for (ProductionCalendar pc : productionCalendar) {
            if (pc.getDay().equals(date) && pc.getFlag().equals(ProductionCalendar.HOLIDAY)) return true;
        }
        return false;
    }

    public Boolean isWeekendBySpecialDateAndSixWorkWeek(boolean isWeekendByProductionCalendar, Date currentDate, Long idOfClientGroup, Long idOfOrg, List<SpecialDate> specialDates) {
        boolean isWeekend = isWeekendByProductionCalendar;
        Boolean isWeekendSD = isWeekendBySpecialDate(currentDate, idOfClientGroup, specialDates); //выходной по данным таблицы SpecialDates
        int day = CalendarUtils.getDayOfWeek(currentDate);
        if (isWeekendSD == null) { //нет данных по дню в КУД
            if (day == Calendar.SATURDAY) {
                String groupName = cgroupRepo.getClientGroupName(idOfOrg, idOfClientGroup).orElse("");
                isWeekend = !isSixWorkWeek(idOfOrg, groupName);
            }
        } else {
            isWeekend = isWeekendSD;
        }
        return isWeekend;
    }

    public boolean isSixWorkWeek(Long idOfOrg, String groupName) {
        GroupNamesToOrgs gnto = gntoRepo.findByIdOfOrgAndGroupName(idOfOrg, groupName)
                .orElse(null);
        if (gnto == null) return false;
        return gnto.getSixDaysWorkWeek() == null ? false : gnto.getSixDaysWorkWeek();
    }

    public Boolean isWeekendBySpecialDate(Date date, Long idOfClientGroup, List<SpecialDate> specialDates) {
        Boolean isWeekend = null;
        if(specialDates != null){
            for (SpecialDate specialDate : specialDates) {
                if (CalendarUtils.betweenOrEqualDate(specialDate.getDate(), date, CalendarUtils.addDays(date, 1)) && specialDate.isDeleted()) {
                    if (specialDate.getIdOfClientGroup() == null || specialDate.getIdOfClientGroup().equals(idOfClientGroup))
                        isWeekend = specialDate.isWeekend();
                    if (specialDate.getIdOfClientGroup() != null && specialDate.getIdOfClientGroup().equals(idOfClientGroup))
                        break;
                }
            }
        }
        return isWeekend;
    }

    public Integer getPreorderFeedingForbiddenDays(Long idOfOrg) {
        Integer forbiddenDays = null;
        try {
            List list = settingsRepo.getECafeSettingsByOrg(idOfOrg, SettingsIds.PreOrderFeeding);

            if (list == null || list.isEmpty()) {
                return DEFAULT_FORBIDDEN_DAYS;
            }
            if (list.size() > 1) {
                logger.error("Организация имеет более одной настройки id OO=" + idOfOrg);
                return DEFAULT_FORBIDDEN_DAYS;
            }
            ECafeSettings settings = (ECafeSettings) list.get(0);
            PreOrderFeedingSettingValue parser = new PreOrderFeedingSettingValue(settings.getSettingValue());
            forbiddenDays = parser.getForbiddenDaysCount();

        } catch (Exception e) {
            logger.error(String.format("Can't get preorders feeding forbidden days value. IdOfOrg = %d", idOfOrg), e);
        }
        if (forbiddenDays == null) {
            forbiddenDays = DEFAULT_FORBIDDEN_DAYS;
        }
        return forbiddenDays;
    }

    public Map<Date, Long[]> existPreordersByDate(Long idOfClient, Date startDate, Date endDate) {
        Map map = new HashMap<Date, Long[]>();
        List result = pcRepo.getPreorderAmount(idOfClient, CalendarUtils.startOfDay(startDate), CalendarUtils.startOfDay(endDate));
        if (result != null) {
            for (Object obj : result) {
                Object[] row = (Object[]) obj;
                Long[] arr = new Long[2];
                arr[0] = (Long)row[0];//количество заказов
                arr[1] = (Long)row[2];//ид ОО
                map.put((Date)row[1], arr);
            }
        }

        List result2 = pmdRepo.getPreorderAmount(idOfClient, CalendarUtils.startOfDay(startDate), CalendarUtils.startOfDay(endDate));
        if (result2 != null) {
            for (Object obj : result2) {
                Object[] row = (Object[]) obj;
                Date date2 = (Date)row[1];
                Long amount = (Long)row[0];
                Long idOfOrg = (Long)row[2];;
                if (map.containsKey(date2)) {
                    amount = amount + ((Long[])map.get(date2))[0];
                }
                Long[] arr = new Long[2];
                arr[0] = amount;
                arr[1] = idOfOrg;
                map.put(date2, arr);
            }
        }
        return map;
    }

    private Calendar getCalendar() {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        return c;
    }

    public Integer getClientGroupResult(List<Client> clients) {
        Map<Integer, Integer> map = new HashMap<>();
        boolean isStudent = false;
        boolean isParent = false;
        boolean isTrueParent = false;
        boolean isEmployee = false;
        boolean isEmployeeParent = false;
        for (Client client : clients) {
            PreorderMobileGroupOnCreateType type = null;
            if (client.isStudent()) {
                type = PreorderMobileGroupOnCreateType.STUDENT;
                isStudent = true;
            }
            if (client.isParentMsk()) {
                type = PreorderMobileGroupOnCreateType.PARENT;
                isParent = true;
                isTrueParent = clientHasChildren(client.getIdOfClient());
            }
            if (client.isSotrudnikMsk()) {
                type = PreorderMobileGroupOnCreateType.EMPLOYEE;
                isEmployee = true;
                isEmployeeParent = clientHasChildren(client.getIdOfClient());
            }
            if (type == null) continue;
            Integer count = map.get(type);
            if (count == null) count = 0;
            map.put(type.ordinal(), count + 1);
        }
        if (map.size() == 0) return SOAP_RC_CLIENT_NOT_FOUND;

        for (Integer value : map.keySet()) {
            if (map.get(value) > 1) return SOAP_RC_SEVERAL_CLIENTS_WERE_FOUND;
        }

        if ((isStudent && isParent && isEmployee) || (isEmployee && isStudent) || (isParent && isStudent)) {
            return SOAP_RC_PREORDERS_NOT_UNIQUE_CLIENT;
        }
        PreorderMobileGroupOnCreateType value;
        if (isEmployeeParent && !isStudent && !isParent)
            value = PreorderMobileGroupOnCreateType.PARENT_EMPLOYEE;
        else if (isTrueParent && !isStudent && !isEmployee)
            value = PreorderMobileGroupOnCreateType.PARENT;
        else if (isEmployee && !isStudent && !isParent)
            value = PreorderMobileGroupOnCreateType.EMPLOYEE;
        else if (isStudent && !isEmployee && !isParent)
            value = PreorderMobileGroupOnCreateType.STUDENT;
        else value = null;
        if (value == null) {
            if (map.size() == 1) {
                return SOAP_RC_WRONG_GROUP;
            } else {
                return SOAP_RC_MOBILE_DIFFERENT_GROUPS;
            }
        }

        return value.ordinal();
    }

    private boolean clientHasChildren(Long idOfClient) {
        return cgRepo.getIdOfClientGuardianList(idOfClient).size() > 0;
    }
}
