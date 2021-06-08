package ru.iteco.restservice.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iteco.restservice.controller.menu.responsedto.ComplexesResponse;
import ru.iteco.restservice.db.repo.readonly.*;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.*;
import ru.iteco.restservice.model.enums.EntityStateType;
import ru.iteco.restservice.model.enums.Predefined;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;
import ru.iteco.restservice.model.wt.*;
import ru.iteco.restservice.servise.data.PreorderAmountData;
import ru.iteco.restservice.servise.data.PreorderComplexAmountData;

import java.math.BigInteger;
import java.util.*;

@Service
public class ComplexService {

    @Autowired
    ClientReadOnlyRepo clientRepo;
    @Autowired
    WtDishReadOnlyRepo dishRepo;
    @Autowired
    CategoryDiscountReadOnlyRepo categoryDiscountReadOnlyRepo;
    @Autowired
    ProductionCalendarReadOnlyRepo productionCalendarReadOnlyRepo;
    @Autowired
    SpecialDateReadOnlyRepo specialDateReadOnlyRepo;
    @Autowired
    WtComplexReadOnlyRepo complexRepo;
    @Autowired
    WtDiscountRuleReadOnlyRepo discountRuleRepo;
    @Autowired
    GroupNamesToOrgsReadOnlyRepo groupNamesToOrgsReadOnlyRepo;
    @Autowired
    WtComplexExcludeDaysReadOnlyRepo wtComplexExcludeDaysReadOnlyRepo;
    @Autowired
    WtComplexesItemReadOnlyRepo wtComplexesItemReadOnlyRepo;

    @Autowired
    PreorderComplexReadOnlyRepo preorderComplexReadOnlyRepo;

    public static final Set<String> ELEMENTARY_SCHOOL = new HashSet<>(Arrays.asList("1", "2", "3", "4"));
    public static final Set<String> MIDDLE_SCHOOL =  new HashSet<>(Arrays.asList("5", "6", "7", "8", "9", "10", "11", "12"));

    public ComplexesResponse getComplexes(Date date, Long contractId) throws Exception {
        Client client = clientRepo.getClientAndDiscountsByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));

        int clientGroup = checkClientGroup(client);
        Set<Long> ageGroupIds = new HashSet<>();
        Map<String, Boolean> complexSign = getComplexSign(client, clientGroup, ageGroupIds);

        return getPreorderComplexesWithWtMenuList(client, date, client.getDiscounts(), ageGroupIds, complexSign);
    }

    private Map<String, Boolean> getComplexSign( Client client, int clientGroup, Set<Long> ageGroupIds) throws Exception {
        Map<String, Boolean> complexSign = new HashMap<>();
        complexSign.put("Paid", false);
        complexSign.put("Free", false);
        complexSign.put("Elem", false);
        complexSign.put("Middle", false);

        // Льготные категории
        List<CategoryDiscount> categoriesDiscount = client.getDiscounts();

        // 2 Группа клиента
        if (clientGroup == 1) {
            ageGroupIds.add(6L); // Сотрудники
            ageGroupIds.add(7L); // Все
            complexSign.put("Paid", true);
        } else if (clientGroup == 2) {
            throw new Exception("Клиент не активен в ИС ПП");
        } else if (clientGroup == 3) {

            // 2 Возрастная группа
            if (client.getAgeGroup() != null && !client.getAgeGroup().isEmpty()) {
                String ageGroupDesc = client.getAgeGroup().toLowerCase();
                if (ageGroupDesc.contains("дошкол")) {
                    throw new Exception("Клиент является обучающимся дошкольной группы.");
                } else {
                    checkParallel(client, categoriesDiscount, ageGroupIds, complexSign);
                }
            } else {
                complexSign.put("Paid", true);
            }
        } else {
            throw new Exception("Клиент без группы");
        }
        return complexSign;
    }

    private int checkClientGroup(Client client) {
        if (client.getClientGroup() != null) {
            Long clientGroupId = client.getClientGroup().getClientGroupId().getIdOfClientGroup();
            if (clientGroupId.equals(Predefined.CLIENT_EMPLOYEES.getValue()) || clientGroupId
                    .equals(Predefined.CLIENT_EMPLOYEE.getValue()) || clientGroupId
                    .equals(Predefined.CLIENT_ADMINISTRATION.getValue()) || clientGroupId
                    .equals(Predefined.CLIENT_TECH_EMPLOYEES.getValue()) || clientGroupId
                    .equals(Predefined.CLIENT_VISITORS.getValue()) || clientGroupId
                    .equals(Predefined.CLIENT_OTHERS.getValue())) {
                return 1;
            }
            if (clientGroupId.equals(Predefined.CLIENT_LEAVING.getValue()) || clientGroupId
                    .equals(Predefined.CLIENT_DELETED.getValue()) || clientGroupId
                    .equals(Predefined.CLIENT_DISPLACED.getValue())) {
                return 2;
            }
            if (clientGroupId < Predefined.CLIENT_EMPLOYEES.getValue()) {
                return 3;
            }
        }
        return -1;
    }

    private void checkParallel(Client client, List<CategoryDiscount> categoriesDiscount, Set<Long> ageGroupIds,
                               Map<String, Boolean> complexSign) throws Exception {
        String parallelDesc = client.getParallel().trim();

        // 5 Параллель для клиента-льготника + 3 Социальная льгота
        if (client.getParallel() != null) {
            if (ELEMENTARY_SCHOOL.contains(parallelDesc)) {
                ageGroupIds.add(3L); // 1-4
                complexSign.put("Elem", true);
            } else if (MIDDLE_SCHOOL.contains(parallelDesc)) {
                ageGroupIds.add(4L); // 5-11
                complexSign.put("Middle", true);
            } else {
                ageGroupIds.add(5L); // Колледж
            }
            complexSign.put("Paid", true);
            if (checkSocialDiscount(categoriesDiscount)) {
                complexSign.put("Free", true);
            }
        }
    }

    private boolean checkSocialDiscount(List<CategoryDiscount> categoriesDiscount) throws Exception {
        if (categoriesDiscount.size() > 0) {
            CategoryDiscount reserveDiscount = getReserveDiscount();
            if (categoriesDiscount.size() == 1 && categoriesDiscount.contains(reserveDiscount)) {
                return false;
            }
            for (CategoryDiscount categoryDiscount : categoriesDiscount) {
                if (categoryDiscount.getCategoryType() == 0 ||   // льготное
                        categoryDiscount.getCategoryType() == 3) {   // льготное вариативное
                    return true;
                }
            }
        }
        return false;
    }

    public CategoryDiscount getReserveDiscount() throws Exception {
        return categoryDiscountReadOnlyRepo.findById(CategoryDiscount.RESERVE_DISCOUNT_ID).orElseThrow(() -> new Exception("Не найдена льгота Резерв"));
    }

    public ComplexesResponse getPreorderComplexesWithWtMenuList(Client client, Date date,
                                                                List<CategoryDiscount> categoriesDiscount, Set<Long> ageGroupIds,
                                                                Map<String, Boolean> complexSign) throws Exception {
        ComplexesResponse response = new ComplexesResponse();
        Org org = client.getOrg();

        // Проверка даты по календарям
        if (isAvailableDate(client, org, date)) {

            Date startDate = CalendarUtils.startOfDay(date);
            Date endDate = CalendarUtils.endOfDay(date);
            Set<WtComplex> wtPaidComplexes = new HashSet<>();
            Set<WtComplex> wtDiscComplexes = new HashSet<>();
            Set<WtComplex> wtAllComplexes = new HashSet<>();

            // 7-9, 11-12 Платные комплексы по возрастным группам и группам
            if (complexSign.get("Paid")) {
                ageGroupIds.add(7L); // Все
                List<WtComplex> wtComComplexes = complexRepo.getPaidWtComplexesByAgeGroupsAndPortal(startDate, endDate, ageGroupIds,
                        org, WtComplex.PAID_COMPLEX_GROUP_ITEM_ID, WtComplex.ALL_COMPLEX_GROUP_ITEM_ID);
                for (WtComplex wtComplex : wtComComplexes) {
                    if (wtComplex.getWtComplexGroupItem().getIdOfComplexGroupItem().equals(WtComplex.PAID_COMPLEX_GROUP_ITEM_ID)) {
                        wtPaidComplexes.add(wtComplex);
                    } else {
                        wtAllComplexes.add(wtComplex);
                    }
                }
            }

            Set<WtComplex> resComplexes;

            // Правила по льготам
            if (categoriesDiscount.size() > 0) {

                Set<WtDiscountRule> wtDiscountRuleSet = getWtDiscountRulesByCategoryOrg(categoriesDiscount, org.getIdOfOrg());

                // 15 Льготные комплексы по правилам соц. скидок
                if (complexSign.get("Free") && !complexSign.get("Elem") && !complexSign.get("Middle")) {
                    Set<WtDiscountRule> discRules = getWtDiscountRulesWithMaxPriority(wtDiscountRuleSet);
                    resComplexes = complexRepo.getFreeWtComplexesByDiscountRules(startDate, startDate, discRules, org);
                    if (resComplexes.size() > 0) {
                        wtDiscComplexes.addAll(resComplexes);
                    }
                }

                // 13 Льготы для начальной школы
                if (complexSign.get("Free") && complexSign.get("Elem")) {
                    CategoryDiscount discount = categoryDiscountReadOnlyRepo.findById(CategoryDiscount.ELEM_DISCOUNT_ID)
                            .orElseThrow(()->new Exception("Не найдена льгота ид -90"));
                    Set<WtDiscountRule> discRules;
                    if (wtDiscountRuleSet.isEmpty())
                        discRules = new HashSet<>(discountRuleRepo.getWtDiscountRuleBySecondDiscount(discount));
                    else
                        discRules = new HashSet<>(discountRuleRepo.getWtDiscountRuleBySecondDiscount(wtDiscountRuleSet, discount));
                    discRules = getWtDiscountRulesWithMaxPriority(discRules);
                    resComplexes = complexRepo.getFreeWtComplexesByRulesAndAgeGroups(startDate, startDate, discRules, ageGroupIds,
                            org, org.getDefaultSupplier(), WtComplex.FREE_COMPLEX_GROUP_ITEM_ID, WtComplex.ALL_COMPLEX_GROUP_ITEM_ID);
                    if (resComplexes.size() > 0) {
                        wtDiscComplexes.addAll(resComplexes);
                    }
                }

                // 14 Льготы для средней и высшей школы
                if (complexSign.get("Free") && complexSign.get("Middle")) {
                    ageGroupIds.add(7L); // Все
                    CategoryDiscount middleDiscount = categoryDiscountReadOnlyRepo.findById(CategoryDiscount.MIDDLE_DISCOUNT_ID)
                            .orElseThrow(() -> new Exception("Не найдена льгота ид -91"));
                    CategoryDiscount highDiscount = categoryDiscountReadOnlyRepo.findById(CategoryDiscount.HIGH_DISCOUNT_ID)
                            .orElseThrow(() -> new Exception("Не найдена льгота ид -92"));
                    Set<WtDiscountRule> discRules = discountRuleRepo.getWtDiscountRuleByTwoDiscounts(wtDiscountRuleSet, middleDiscount,
                            highDiscount);
                    discRules = getWtDiscountRulesWithMaxPriority(discRules);
                    resComplexes = complexRepo.getFreeWtComplexesByRulesAndAgeGroups(startDate, startDate, discRules, ageGroupIds,
                            org, org.getDefaultSupplier(), WtComplex.FREE_COMPLEX_GROUP_ITEM_ID, WtComplex.ALL_COMPLEX_GROUP_ITEM_ID);
                    if (resComplexes.size() > 0) {
                        wtDiscComplexes.addAll(resComplexes);
                    }
                }
            }

            // 10 Льготные комплексы для начальной школы
            if (!complexSign.get("Free") && complexSign.get("Elem")) {
                Set<WtDiscountRule> discRules = getWtElemDiscountRules(org);
                discRules = getWtDiscountRulesWithMaxPriority(discRules);
                resComplexes = complexRepo.getFreeWtComplexesByRulesAndAgeGroups(startDate, startDate, discRules, ageGroupIds,
                        org, org.getDefaultSupplier(), WtComplex.FREE_COMPLEX_GROUP_ITEM_ID, WtComplex.ALL_COMPLEX_GROUP_ITEM_ID);
                if (resComplexes.size() > 0) {
                    wtDiscComplexes.addAll(resComplexes);
                }
            }

            PreorderAmountData preorderComplexAmounts = getPreorderComplexAmounts(client, startDate, endDate);

            Map<WtComplex, List<WtDish>> map = getDishesMap(wtPaidComplexes, startDate, endDate);
            response.getPaidComplexes().fillComplexInfo(wtPaidComplexes, map, preorderComplexAmounts);

            map = getDishesMap(wtAllComplexes, startDate, endDate);
            response.getPaidAndFreeComplexes().fillComplexInfo(wtAllComplexes, map, preorderComplexAmounts);

            map = getDishesMap(wtDiscComplexes, startDate, endDate);
            response.getFreeComplexes().fillComplexInfo(wtDiscComplexes, map, preorderComplexAmounts);

            return response;

        }
        return response;
    }

    private PreorderAmountData getPreorderComplexAmounts(Client client, Date startDate, Date endDate) {
        PreorderAmountData result = new PreorderAmountData();

        List<PreorderComplex> list = preorderComplexReadOnlyRepo.getPreorderComplexesByClientAndDate(client, startDate, endDate);
        for (PreorderComplex pc : list) {
            PreorderComplexAmountData data = new PreorderComplexAmountData(pc.getArmComplexId().longValue(), pc.getAmount(),
                    pc.getIdOfPreorderComplex());
            for (PreorderMenuDetail pmd : pc.getPreorderMenuDetails()) {
                data.getDishAmounts().put(pmd.getIdOfDish(), pmd.getAmount());
            }
            result.getPreorderComplexAmountData().add(data);
        }
        return result;
    }

    private Map<WtComplex, List<WtDish>> getDishesMap(Set<WtComplex> wtComplexes, Date startDate, Date endDate) {
        Map<WtComplex, List<WtDish>> map = new HashMap<>();
        for (WtComplex wtComplex : wtComplexes) {
            List<WtDish> dishes = getWtMenuItemsExt(wtComplex, startDate,
                    endDate);
            map.put(wtComplex, dishes);
        }
        return map;
    }

    private List<WtDish> getWtMenuItemsExt(WtComplex wtComplex, Date startDate,
                                           Date endDate) {
        List<WtDish> result = new ArrayList<>();

        // Определяем подходящий состав комплекса
        WtComplexesItem complexItem = getWtComplexItemByCycle(wtComplex, startDate);
        if (complexItem != null) {
            result = dishRepo.getWtDishesByComplexItemAndDates(complexItem, startDate, endDate, EntityStateType.ACTIVE);
        }
        return result;
    }

    public WtComplexesItem getWtComplexItemByCycle(WtComplex wtComplex, Date date) {
        Map<Date, Integer> cycleDates = new HashMap<>();
        Date startComplexDate = wtComplex.getBeginDate();
        Date endComplexDate = wtComplex.getEndDate();
        Integer startComplexDay = wtComplex.getStartCycleDay();
        Integer daysInCycle = wtComplex.getDayInCycle();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = startComplexDate;
        int count = startComplexDay;
        WtComplexesItem res = null;
        if (endComplexDate.getTime() < date.getTime()) {
            return null;    // Дата не входит в цикл
        }

        if (wtComplex.getCycleMotion() != null) {   // комплекс настроен
            // Составляем карту дней цикла
            List<WtComplexExcludeDays> excludeDays = wtComplexExcludeDaysReadOnlyRepo.getExcludeDaysByWtComplex(wtComplex);
            while (currentDate.getTime() <= date.getTime()) {
                calendar.setTime(currentDate);
                // смотрим рабочую неделю
                if ((wtComplex.getCycleMotion() == 0 &&     // 5-дневная рабочая неделя
                        calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY &&
                        calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) || (wtComplex.getCycleMotion() == 1 &&
                        // 6-дневная рабочая неделя
                        calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)) {
                    // проверка, выпадает ли день на выходные
                    Boolean isHoliday = checkExcludeDays(currentDate, excludeDays);
                    if (!isHoliday) {
                        cycleDates.put(currentDate, count++);
                    }
                }
                calendar.add(Calendar.DATE, 1);     // переходим к следующему дню
                currentDate = calendar.getTime();
                if (count > daysInCycle) {  // вышли за пределы цикла
                    if (daysInCycle == 1) {
                        count = 1;
                    } else {
                        count %= daysInCycle;
                    }
                }
            }
            Integer cycleDay = cycleDates.get(date);
            if (cycleDay != null) {
                res = wtComplexesItemReadOnlyRepo.findByWtComplexAndCycleDay(wtComplex, cycleDay);
            }
        }
        return res;
    }

    public Boolean checkExcludeDays(Date date, List<WtComplexExcludeDays> excludeDays) {
        try {
            for (WtComplexExcludeDays wtExcludeDays : excludeDays) {
                if (wtExcludeDays.getDate().getTime() == date.getTime())
                    return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Set<WtDiscountRule> getWtDiscountRulesByCategoryOrg(List<CategoryDiscount> categoriesDiscount, Long idOfOrg) {
        List<BigInteger> list = discountRuleRepo.getWtDiscountRulesByCategoryOrg(categoriesDiscount, idOfOrg);
        Set<Long> set = new HashSet<>();
        for (BigInteger value : list) {
            set.add(value.longValue());
        }
        return new HashSet<>(discountRuleRepo.findAllById(set));
    }

    private Set<WtDiscountRule> getWtElemDiscountRules(Org org) {
        Set<BigInteger> discRulesIds = discountRuleRepo.getWtElemDiscountRulesIds(org.getIdOfOrg(), CategoryDiscount.ELEM_DISCOUNT_ID);
        Set<Long> list = new HashSet<>();
        for (BigInteger value : discRulesIds) {
            list.add(value.longValue());
        }
        return new HashSet<>(discountRuleRepo.findAllById(list));
    }

    public Set<WtDiscountRule> getWtDiscountRulesWithMaxPriority(Set<WtDiscountRule> rules) {
        Set<WtDiscountRule> res = new HashSet<>();
        int max = 0;
        for (WtDiscountRule rule : rules) {
            if (rule.getPriority() > max) {
                max = rule.getPriority();
            }
        }
        for (WtDiscountRule rule : rules) {
            if (rule.getPriority() == max) {
                res.add(rule);
            }
        }
        return res;
    }

    public boolean isAvailableDate(Client client, Org org, Date date) {
        ClientGroup clientGroup = client.getClientGroup();
        Calendar calendar = Calendar.getInstance();
        // проверка по производственному календарю
        Boolean isWorkingDay = checkWorkingDay(date);
        if (isWorkingDay != null) {
            if (isWorkingDay) {
                // проверка по календарю учебных дней
                Boolean isLearningDay = checkLearningDayByOrgAndClientGroup(date, org, clientGroup);
                if (isLearningDay != null && isLearningDay) {
                    return false;
                }
                if (isLearningDay == null) {
                    // проверка на субботу + 6-дневную рабочую неделю
                    calendar.setTime(date);
                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        GroupNamesToOrgs gnto = groupNamesToOrgsReadOnlyRepo.findByIdOfOrgAndGroupName(org.getIdOfOrg(), clientGroup.getGroupName())
                                .orElse(null);
                        if (gnto != null && !gnto.getSixDaysWorkWeek()) {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        } else {
            Boolean isLearningDay = checkLearningDayByOrgAndClientGroup(date, org, clientGroup);
            if (isLearningDay != null && isLearningDay) {
                return false;
            }
        }
        return true;
    }

    public Boolean checkLearningDayByOrgAndClientGroup(Date date, Org org, ClientGroup clientGroup) {
        try {
            List<Integer> res = specialDateReadOnlyRepo.getSpecialDate(CalendarUtils.startOfDay(date), CalendarUtils.endOfDay(date),
                    org.getIdOfOrg(), clientGroup.getClientGroupId().getIdOfClientGroup());
            if (res != null && res.size() > 0) {
                return res.get(0) == 1;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean checkWorkingDay(Date date) {
        try {
            List<Integer> res = productionCalendarReadOnlyRepo.getWorkingDays(CalendarUtils.startOfDay(date), CalendarUtils.endOfDay(date));
            if (res != null && res.size() > 0) {
                return res.get(0) == 1;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
