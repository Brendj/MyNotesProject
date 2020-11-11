/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.IPreorderDAOOperations;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodAgeGroupType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodType;
import ru.axetta.ecafe.processor.core.persistence.utils.*;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.service.GoodRequestsChangeAsyncNotificationService;
import ru.axetta.ecafe.processor.core.service.PreorderRequestsReportService;
import ru.axetta.ecafe.processor.core.service.PreorderRequestsReportServiceParam;
import ru.axetta.ecafe.processor.core.service.SubscriptionFeedingService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBase;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientSummaryBaseListResult;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientWithAddInfo;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientsWithResultCode;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.*;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.PreorderAllComplexesOnDateResult;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.PreorderAllComplexesResult;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.RegularPreorderItem;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.RegularPreordersList;

import org.apache.cxf.common.util.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

import static ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWS.processSummaryBase;


/**
 * Created by i.semenov on 05.03.2018.
 */
@Component
@Scope("singleton")
@DependsOn({"daoService", "runtimeContext"})
public class PreorderDAOService {
    private static final Logger logger = LoggerFactory.getLogger(PreorderDAOService.class);
    private final String NEW_LINE_DELIMITER = ";";
    public static final long BASE_ID_MENU_VALUE_FOR_MODIFY = 7700000;

    public static final Set<String> ELEMENTARY_SCHOOL = new HashSet<>(Arrays.asList("1", "2", "3", "4"));
    public static final Set<String> MIDDLE_SCHOOL =  new HashSet<>(Arrays.asList("5", "6", "7", "8", "9", "10", "11", "12"));
    public static final Long FREE_COMPLEX_GROUP_ITEM_ID = 1L;
    public static final Long PAID_COMPLEX_GROUP_ITEM_ID = 2L;
    public static final Long ALL_COMPLEX_GROUP_ITEM_ID = 3L;
    public static final Long ELEM_AGE_GROUP_ITEM_ID = 3L;
    public static final Long ELEM_DISCOUNT_ID = -90L;
    public static final Long MIDDLE_DISCOUNT_ID = -91L;
    public static final Long HIGH_DISCOUNT_ID = -92L;
    public static final Long RESERVE_DISCOUNT_ID = 50L;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager emReport;

    @PostConstruct
    public void createPreorderDAOOperationsImpl() {
        IPreorderDAOOperations impl = new IPreorderDAOOperations() {
            @Override
            public void generatePreordersBySchedule(PreorderRequestsReportServiceParam params) {
                generatePreordersByScheduleInternal(params);
            }

            @Override
            public void relevancePreorders(PreorderRequestsReportServiceParam params) {
                RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).relevancePreorders(params);
            }

            @Override
            public void dailyCheckPreorders() {
                RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).dailyCheckPreorders();
            }
        };
        RuntimeContext.getAppContext().getBean(DAOService.class).setPreorderDAOOperationsImpl(impl);
    }

    @Transactional
    public void saveToken(SudirToken token, ClientSummaryBaseListResult clientSummary) {
        em.merge(token);
        Set<SudirTokenClient> set = new HashSet<SudirTokenClient>();
        for (ClientSummaryBase clientSummaryBase : clientSummary.getClientSummary()) {
            Long contractId = clientSummaryBase.getContractId();
            SudirTokenClient stc = new SudirTokenClient(contractId, token.getAccess_token());
            em.merge(stc);
            set.add(stc);
        }
    }

    @Transactional(readOnly = true)
    public boolean matchToken(String token, Long contractId) {
        Query query = em.createQuery("select t from SudirTokenClient t where t.access_token = :token and t.contractId = :contractId");
        query.setParameter("token", token);
        query.setParameter("contractId", contractId);
        query.setMaxResults(1);
        try {
            SudirTokenClient tokenClient = (SudirTokenClient) query.getSingleResult();
            return true;
        } catch (Exception e) {
            logger.info(String.format("Attempt to access client data contractId=%s with token=%s", contractId, token));
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void setSpecialMenuFlag(Long contractId, Integer specialMenu) throws Exception {
        Query query = em.createQuery("update Client c set c.specialMenu = :specialMenu where c.contractId = :contractId");
        query.setParameter("specialMenu", specialMenu == 0 ? false : true);
        query.setParameter("contractId", contractId);
        int res = query.executeUpdate();
        if (res != 1) throw new Exception("Client not found");
    }

    public boolean isSpecialConfigDate(Client client, Date date) {
        Long idOfOrg = client.getOrg().getIdOfOrg();
        String prop = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.preorder.specialDates", "");
        if (StringUtils.isEmpty(prop)) return false;
        ObjectMapper mapper = new ObjectMapper();
        SpecialConfigDates dates = null;
        try {
            dates = mapper.readValue(prop, SpecialConfigDates.class);

            for (SpecialConfigDate dateConfig : dates.getSpecialConfigDateList()) {
                if (idOfOrg.equals(dateConfig.getIdOfOrg())) {
                    for (String strDate : dateConfig.getDates()) {
                        if (CalendarUtils.parseDate(strDate).equals(date)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Can't get special config dates: ", e);
            return false;
        }

        return false;
    }

    @Transactional
    public PreorderAllComplexesResult getPreordersWithMenuListSinceDate(Long contractId, Date date) throws Exception {
        PreorderAllComplexesResult result = new PreorderAllComplexesResult();
        Client client = getClientByContractId(contractId);

        Query query = emReport.createNativeQuery("select cast(-1 as bigint) as idofcomplexinfo, pc.amount, pc.deletedState, pc.state, pc.idofregularpreorder, "
                + "pc.modeofadd, pc.modefree, "
                + "pc.armcomplexid, pc.complexname, pc.complexprice, pc.idofpreordercomplex,  pc.mobileGroupOnCreate, pc.preorderdate "
                + "from cf_preorder_complex pc where pc.idofclient = :idOfClient and pc.preorderdate >= :startDate and pc.deletedstate = 0 "
                + "order by modeOfAdd");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
        Map<Date, List<PreorderComplexItemExt>> map = new TreeMap<>();
        List<PreorderComplexItemExt> list = getPreorderComplexItemExtList(query, client, date, true, false);
        for (PreorderComplexItemExt item : list) {
            Date dt = item.getPreorderDate();
            List<PreorderComplexItemExt> items = map.get(dt);
            if (items == null) {
                items = new ArrayList<>();
            }
            item.setPreorderDate(null);
            items.add(item);
            map.put(dt, items);
        }
        List<PreorderAllComplexesOnDateResult> list2 = new ArrayList<>();
        for (Date dateResult : map.keySet()) {
            PreorderAllComplexesOnDateResult qqq = new PreorderAllComplexesOnDateResult();
            qqq.setDate(CalendarUtils.getXMLGregorianCalendarByDate(dateResult));
            qqq.setItems(map.get(dateResult));
            list2.add(qqq);
        }
        result.setList(list2);
        return result;
    }

    private List<PreorderComplexItemExt> getPreorderComplexItemExtList(Query query, Client client, Date date, boolean withPreorderDate, boolean includeZeroAmount) {
        List res = query.getResultList();
        List<PreorderComplexItemExt> list = new ArrayList<PreorderComplexItemExt>();
        for (Object o : res) {
            Object[] row = (Object[]) o;
            Long id = ((BigInteger)row[0]).longValue();
            Integer amount = (Integer) row[1];
            Integer state = (Integer) row[3];
            Long idOfRegularPreorder = row[4] == null ? null : ((BigInteger)row[4]).longValue();
            Integer modeOfAdd = (Integer) row[5];
            Integer modeFree = (Integer) row[6];
            Integer idOfComplex = (Integer) row[7];
            String complexName = (String) row[8];
            Long complexPrice = ((BigInteger)row[9]).longValue();
            Long idOfPreorderComplex = (row[10] == null ? null : ((BigInteger)row[10]).longValue());
            Integer mobileGroupOnCreate = (row[11] == null ? null : (Integer)row[11]);
            Long preorderDate = null;
            if (withPreorderDate) {
                preorderDate = (row[12] == null ? null : ((BigInteger)row[12]).longValue());
            }
            PreorderComplexItemExt complexItemExt = new PreorderComplexItemExt(idOfComplex, complexName, complexPrice, modeOfAdd, modeFree);
            complexItemExt.setAmount(amount == null ? 0 : amount);
            complexItemExt.setState(state == null ? 0 : state);
            complexItemExt.setIsRegular(idOfRegularPreorder == null ? false : true);
            complexItemExt.setCreatorRole(mobileGroupOnCreate);
            complexItemExt.setPreorderDate(preorderDate == null ? null : new Date(preorderDate));

            List<PreorderMenuItemExt> menuItemExtList = getMenuItemsExt(id, client.getIdOfClient(), date, idOfPreorderComplex,
                    modeOfAdd == ComplexInfo.SET_DISHES_COMPLEX ? includeZeroAmount : true);
            if (menuItemExtList.size() > 0) {
                complexItemExt.setMenuItemExtList(menuItemExtList);
                list.add(complexItemExt);
            }
        }
        return list;
    }

    @Transactional(readOnly = true)
    public PreorderListWithComplexesGroupResult getPreorderComplexesWithMenuList(Long contractId, Date date) {
        PreorderListWithComplexesGroupResult groupResult = new PreorderListWithComplexesGroupResult();
        Client client = getClientByContractId(contractId);
        if (isSpecialConfigDate(client, date)) {
            List<PreorderComplexGroup> groupList = new ArrayList<PreorderComplexGroup>();
            groupResult.setComplexesWithGroups(groupList);
            return groupResult;
        }
        Set<CategoryDiscount> clientDiscounts = client.getCategories();
        Boolean hasDiscount = false;
        for (CategoryDiscount categoryDiscount : clientDiscounts) {
            if(!categoryDiscount.getCategoryName().toLowerCase().contains("резерв")){
                hasDiscount |= (categoryDiscount.getCategoryType() == CategoryDiscountEnumType.CATEGORY_WITH_DISCOUNT);
            }
        }

        Query query = emReport.createNativeQuery("select ci.idofcomplexinfo, pc.amount, pc.deletedState, pc.state, pc.idofregularpreorder, "
                + "coalesce(pc.modeofadd, ci.modeofadd) as modeofadd, coalesce(pc.modefree, ci.modefree) as modefree, ci.idofcomplex, "
                + "coalesce(pc.complexname, ci.complexname) as complexname, "
                + "coalesce(pc.complexprice, ci.currentprice) as currentprice, pc.idofpreordercomplex, pc.mobileGroupOnCreate "
                + " from cf_complexinfo ci join cf_orgs o on o.idoforg = ci.idoforg "
                + " left outer join (select * from cf_preorder_complex pc where pc.idofclient = :idOfClient "
                + " and pc.preorderdate between :startDate and :endDate and pc.deletedstate = 0) as pc on (ci.idoforg = :idOfOrg and ci.menudate = pc.preorderdate and ci.idofcomplex = pc.armcomplexid) "
                + " where ci.MenuDate between :startDate and :endDate "
                + " and (ci.UsedSpecialMenu=1 or ci.ModeFree=1) and ci.idoforg = :idOfOrg "
                + " and (o.OrganizationType = :school or o.OrganizationType = :professional) and ci.modevisible = 1 and (pc.deletedstate is null or pc.deletedstate = 0) "
                + "union "
                + "select cast(-1 as bigint) as idofcomplexinfo, pc.amount, pc.deletedState, pc.state, pc.idofregularpreorder, pc.modeofadd, pc.modefree, "
                + "pc.armcomplexid, pc.complexname, pc.complexprice, pc.idofpreordercomplex, pc.mobileGroupOnCreate "
                + "from cf_preorder_complex pc where pc.idofclient = :idOfClient and pc.preorderdate between :startDate and :endDate and pc.deletedstate = 0 "
                + "and not exists (select idofcomplexinfo from cf_complexinfo ci2 where ci2.idoforg = :idOfOrg and ci2.menudate = pc.preorderdate and ci2.idofcomplex = pc.armcomplexid "
                + " and ci2.modevisible = 1 and (ci2.usedspecialmenu = 1 or ci2.modefree = 1)) "
                + "order by modeOfAdd");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
        query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("school", OrganizationType.SCHOOL.getCode());
        query.setParameter("professional", OrganizationType.PROFESSIONAL.getCode());
        Map<String, PreorderComplexGroup> groupMap = new HashMap<String, PreorderComplexGroup>();
        List<PreorderComplexItemExt> list = getPreorderComplexItemExtList(query, client, date, false, true);
        for (PreorderComplexItemExt item : list) {
            PreorderGoodParamsContainer complexParams = getComplexParams(item, client, date);
            if (isAcceptableComplex(item, client.getClientGroup(), hasDiscount, complexParams, null, null)) {
                String groupName = getPreorderComplexGroup(item, complexParams);
                if (groupName.isEmpty()) continue;
                item.setType(getPreorderComplexSubgroup(item));
                PreorderComplexGroup group = groupMap.get(groupName);
                if (group == null) {
                    group = new PreorderComplexGroup(groupName);
                    groupMap.put(groupName, group);
                }
                group.addItem(item);
            }
        }
        List<PreorderComplexGroup> groupList = new ArrayList<PreorderComplexGroup>(groupMap.values());
        for (PreorderComplexGroup group : groupList) {
            Collections.sort(group.getItems());
        }
        Collections.sort(groupList);
        groupResult.setComplexesWithGroups(groupList);

        return groupResult;
    }

    @Transactional(readOnly = true)
    public PreorderListWithComplexesGroupResult getPreorderComplexesWithWtMenuList(Client client, Date date,
            Set<CategoryDiscount> categoriesDiscount, Set<Long> ageGroupIds, Map<String, Boolean> complexSign) {
        PreorderListWithComplexesGroupResult groupResult = new PreorderListWithComplexesGroupResult();
        Org org = client.getOrg();

        // Проверка даты по календарям
        if (isAvailableDate(client, org, date)) {

            Date startDate = CalendarUtils.startOfDay(date);
            Date endDate = CalendarUtils.endOfDay(date);
            Set<WtComplex> wtComplexes = new HashSet<>();
            Set<WtComplex> wtDiscComplexes = new HashSet<>();

            // 7-9, 11-12 Платные комплексы по возрастным группам и группам
            if (complexSign.get("Paid")) {
                ageGroupIds.add(7L); // Все
                Set<WtComplex> wtComComplexes = getPaidWtComplexesByAgeGroupsAndPortal(startDate, endDate, ageGroupIds,
                        org);
                if (wtComComplexes.size() > 0) {
                    wtComplexes.addAll(wtComComplexes);
                }
            }

            Set<WtComplex> resComplexes = new HashSet<>();

            // Правила по льготам
            if (categoriesDiscount.size() > 0) {

                Set<WtDiscountRule> wtDiscountRuleSet = getWtDiscountRulesByCategoryOrg(categoriesDiscount, org);

                // 15 Льготные комплексы по правилам соц. скидок
                if (complexSign.get("Free") && !complexSign.get("Elem") && !complexSign.get("Middle")) {
                    Set<WtDiscountRule> discRules = getWtDiscountRulesWithMaxPriority(wtDiscountRuleSet);
                    resComplexes = getFreeWtComplexesByDiscountRules(startDate, startDate, discRules);
                    if (resComplexes.size() > 0) {
                        wtDiscComplexes.addAll(resComplexes);
                    }
                }

                // 13 Льготы для начальной школы
                if (complexSign.get("Free") && complexSign.get("Elem")) {
                    CategoryDiscount discount = getElemDiscount();
                    Set<WtDiscountRule> discRules = getWtDiscountRuleBySecondDiscount(wtDiscountRuleSet, discount);
                    discRules = getWtDiscountRulesWithMaxPriority(discRules);
                    resComplexes = getFreeWtComplexesByRulesAndAgeGroups(startDate, startDate, discRules, ageGroupIds);
                    if (resComplexes.size() > 0) {
                        wtDiscComplexes.addAll(resComplexes);
                    }
                }

                // 14 Льготы для средней и высшей школы
                if (complexSign.get("Free") && complexSign.get("Middle")) {
                    ageGroupIds.add(7L); // Все
                    CategoryDiscount middleDiscount = getMiddleDiscount();
                    CategoryDiscount highDiscount = getHighDiscount();
                    Set<WtDiscountRule> discRules = getWtDiscountRuleByTwoDiscounts(wtDiscountRuleSet, middleDiscount,
                            highDiscount);
                    discRules = getWtDiscountRulesWithMaxPriority(discRules);
                    resComplexes = getFreeWtComplexesByRulesAndAgeGroups(startDate, startDate, discRules, ageGroupIds);
                    if (resComplexes.size() > 0) {
                        wtDiscComplexes.addAll(resComplexes);
                    }
                }
            }

            // 10 Льготные комплексы для начальной школы
            if (!complexSign.get("Free") && complexSign.get("Elem")) {
                Set<WtDiscountRule> discRules = getWtElemDiscountRules(org);
                discRules = getWtDiscountRulesWithMaxPriority(discRules);
                resComplexes = getFreeWtComplexesByRulesAndAgeGroups(startDate, startDate, discRules, ageGroupIds);
                if (resComplexes.size() > 0) {
                    wtDiscComplexes.addAll(resComplexes);
                }
            }

            if (wtDiscComplexes.size() > 0) {
                wtComplexes.addAll(wtDiscComplexes);
            }

            if (wtComplexes.size() > 0) {
                Map<String, PreorderComplexGroup> groupMap = new HashMap<>();
                List<PreorderComplexItemExt> list = new ArrayList<>();
                Map<Integer, Integer> amountByComplexes = getAmountForPreorderComplexes(client, startDate, endDate);

                for (WtComplex wtComplex : wtComplexes) {

                    Integer idOfComplex = wtComplex.getIdOfComplex().intValue();
                    String complexName = wtComplex.getName();
                    // Режим добавления блюд: если комплекс составной - режим составного комплекса,
                    // если нет - режим фиксированной цены
                    Integer complexType = wtComplex.getComposite() ? 4 : 2;
                    Long complexPrice = (wtComplex.getPrice() == null) ? 0L :
                            wtComplex.getPrice().multiply(new BigDecimal(100)).longValue();
                    Integer amount = getAmountForPreorderComplex(amountByComplexes, idOfComplex);
                    boolean isRegular = getRegularSignForPreorderComplex(client, idOfComplex, startDate, endDate);

                    PreorderComplexItemExt complexItemExt;
                    PreorderComplexItemExt complexItemExt2 = null;
                    // Проверка типа питания
                    int isDiscountComplex = wtComplex.getWtComplexGroupItem().getIdOfComplexGroupItem().intValue();
                    if (isDiscountComplex == 1) {
                        complexItemExt = new PreorderComplexItemExt(idOfComplex, complexName, complexPrice, complexType,
                                true, amount, wtComplex.getDeleteState(), isRegular);
                    } else if (isDiscountComplex == 3 && wtDiscComplexes.contains(wtComplex)) {
                        complexItemExt = new PreorderComplexItemExt(idOfComplex, complexName, complexPrice, complexType,
                                true, amount, wtComplex.getDeleteState(), isRegular);
                        complexItemExt2 = new PreorderComplexItemExt(idOfComplex, complexName, complexPrice, complexType,
                                false, amount, wtComplex.getDeleteState(), isRegular);
                    } else {
                        complexItemExt = new PreorderComplexItemExt(idOfComplex, complexName, complexPrice, complexType,
                                false, amount, wtComplex.getDeleteState(), isRegular);
                    }

                    List<PreorderMenuItemExt> menuItemExtList = getWtMenuItemsExt(wtComplex, client, org, startDate,
                            endDate);
                    if (menuItemExtList.size() > 0) {
                        // Распределение по группам
                        String groupName = wtComplex.getWtDietType().getDescription();
                        PreorderComplexGroup group = groupMap.get(groupName);
                        if (group == null) {
                            group = new PreorderComplexGroup(groupName);
                            groupMap.put(groupName, group);
                        }

                        complexItemExt.setMenuItemExtList(menuItemExtList);
                        group.addItem(complexItemExt);
                        list.add(complexItemExt);
                        if (complexItemExt2 != null) {
                            complexItemExt2.setMenuItemExtList(menuItemExtList);
                            group.addItem(complexItemExt2);
                            list.add(complexItemExt2);
                        }
                    }
                }

                for (PreorderComplexItemExt item : list) {
                    item.setType(getPreorderComplexSubgroup(item));
                }
                List<PreorderComplexGroup> groupList = new ArrayList<>(groupMap.values());
                for (PreorderComplexGroup group : groupList) {
                    Collections.sort(group.getItems());
                }
                Collections.sort(groupList);
                groupResult.setComplexesWithGroups(groupList);
            }
        }
        return groupResult;
    }

    private Integer getAmountForPreorderComplex(Map<Integer, Integer> amountByComplexes, Integer idOfComplex) {
        Integer result = amountByComplexes.get(idOfComplex);
        return result == null ? 0 : result;
    }

    private Map<Integer, Integer> getAmountForPreorderComplexes(Client client, Date startDate, Date endDate) {
        Map<Integer, Integer> result = new HashMap<>();
        Query query = emReport.createQuery("SELECT pc.armComplexId, sum(pc.amount) FROM PreorderComplex pc "
                + "WHERE pc.client = :client AND pc.preorderDate between :startDate and :endDate "
                + "AND pc.deletedState = false group by pc.armComplexId");
        query.setParameter("client", client);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        List list = query.getResultList();
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            Integer complexId = HibernateUtils.getDbInt(row[0]);
            Long amountSum = (row[1] == null ? 0L : (Long)row[1]);
            if (amountSum == null) amountSum = 0L;
            if (complexId != null) result.put(complexId, amountSum.intValue());
        }
        return result;
    }

    private boolean getRegularSignForPreorderComplex(Client client, Integer idOfComplex, Date startDate, Date endDate) {
        Query query = emReport.createQuery("SELECT pc.regularPreorder FROM PreorderComplex pc "
                + "WHERE pc.client = :client AND pc.preorderDate between :startDate and :endDate "
                + "AND pc.deletedState = false AND pc.armComplexId = :idOfComplex");
        query.setParameter("client", client);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("idOfComplex", idOfComplex);
        List<RegularPreorder> res = query.getResultList();
        return res.size() != 0 && res.get(0) != null;
    }

    private Integer getAmountForPreorderMenuDetail(WtDish wtDish, Map<Long, Integer> amounts) {
        Integer amount = amounts.get(wtDish.getIdOfDish());
        return amount == null ? 0 : amount;
    }

    private boolean getRegularSignForPreorderMenuDetail(WtDish wtDish, Map<Long, Long> regularSigns) {
        Long idOfRegularPreorder = regularSigns.get(wtDish.getIdOfDish());
        if (idOfRegularPreorder == null) return false;
        return idOfRegularPreorder > 0;
    }

    public PreorderGoodParamsContainer getComplexParams(PreorderComplexItemExt item, Client client, Date date) {
        Integer goodTypeCode = GoodType.UNSPECIFIED.getCode();
        Integer ageGroupCode = GoodAgeGroupType.UNSPECIFIED.getCode();
        try {
            Query query = emReport.createNativeQuery("SELECT g.goodtype, g.agegroup "
                    + " FROM  cf_clients c "
                    + " INNER JOIN cf_complexinfo ci ON c.idoforg = ci.idoforg "
                    + " INNER JOIN cf_goods g ON ci.idofgood = g.idofgood "
                    + " WHERE ci.idofcomplex = :idOfComplex "
                    + " AND c.contractid = :contractID "
                    + " AND ci.complexname like :nameOfComplex "
                    + " AND ci.MenuDate BETWEEN :startDate AND :endDate ");
            query.setParameter("idOfComplex", item.getIdOfComplexInfo());
            query.setParameter("contractID", client.getContractId());
            query.setParameter("nameOfComplex", item.getComplexName());
            query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
            query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
            query.setMaxResults(1);
            Object[] result = (Object[]) query.getSingleResult();
            if (result.length != 0) {
                goodTypeCode = (Integer) result[0];
                ageGroupCode = (Integer) result[1];
            }
        } catch (Exception e){
            logger.debug("Can't get result from DB: " + e.getMessage());
        }
        return new PreorderGoodParamsContainer(goodTypeCode, ageGroupCode);
    }

    public RegularPreordersList getRegularPreordersList(Long contractId) {
        RegularPreordersList rp = new RegularPreordersList();
        Query regularsQuery = emReport.createQuery("select r from RegularPreorder r where r.client.contractId = :contractId and r.deletedState = false");
        regularsQuery.setParameter("contractId", contractId);
        List<RegularPreorder> reglist = regularsQuery.getResultList();
        if (reglist.size() > 0) {
            List<RegularPreorderItem> items = new ArrayList<RegularPreorderItem>();
            for (RegularPreorder regularPreorder : reglist) {
                RegularPreorderItem item = new RegularPreorderItem(regularPreorder);
                items.add(item);
            }
            rp.setRegularPreorders(items);
        }
        return rp;
    }

    private String getPreorderComplexGroup(PreorderComplexItemExt item, PreorderGoodParamsContainer container) {
        String groupName = "";
        if(!container.getGoodType().equals(GoodType.UNSPECIFIED.getCode())) {
            groupName = GoodType.fromInteger(container.getGoodType()).toString();
        } else {
            if (match(item, "завтрак")) {
                groupName = "Завтрак";
            }
            else if (match(item, "обед")) {
                groupName = "Обед";
            }
            else if (match(item, "полдник")) {
                groupName = "Полдник";
            }
            else if (match(item, "ужин")) {
                groupName = "Ужин";
            }
        }
        return groupName;
    }

    private String getPreorderComplexSubgroup(PreorderComplexItemExt item) {
        if (item.getDiscount()) {
            return "За счет средств бюджета города Москвы";
        } else {
            return "За счет родителей (представителей) обучающегося. " + NEW_LINE_DELIMITER
                 + "В случае, если вашему ребенку предоставляется льготное питание, то вы в дополнение можете заказать платное питание.";
        }
    }

    private boolean match(PreorderComplexItemExt item, String str) {
        return item.getComplexName().toLowerCase().indexOf(str) > -1;
    }

    private List<PreorderMenuItemExt> getMenuItemsExt (Long idOfComplexInfo, Long idOfClient, Date date, Long idOfPreorderComplex,
            boolean includeZeroAmount) {
        List<PreorderMenuItemExt> menuItemExtList = new ArrayList<PreorderMenuItemExt>();
        Query query = null;
        if (idOfPreorderComplex == null) {
            query = emReport.createNativeQuery("SELECT md.idofmenudetail, pmd.amount, pmd.idofregularpreorder, pmd.state, g.dailysale, pmd.idofpreordermenudetail, "
                    + "pmd.mobileGroupOnCreate "
                    + "FROM CF_MenuDetails md INNER JOIN CF_ComplexInfoDetail cid ON cid.IdOfMenuDetail = md.IdOfMenuDetail "
                    + "JOIN CF_Goods g ON md.IdOfGood = g.IdOfGood "
                    + "left join (SELECT pmd.amount, pmd.idofregularpreorder, pmd.state, pmd.armidofmenu, pmd.idofpreordermenudetail, pmd.mobileGroupOnCreate "
                    + "FROM cf_preorder_menudetail pmd WHERE pmd.idofclient = :idOfClient "
                    + "AND pmd.preorderdate BETWEEN :startDate AND :endDate AND pmd.deletedstate = 0) as pmd on pmd.armidofmenu = md.localidofmenu "
                    + "WHERE cid.IdOfComplexInfo = :idOfComplexInfo and md.itemcode is not null and md.itemcode <> ''");
            query.setParameter("idOfComplexInfo", idOfComplexInfo);
            query.setParameter("idOfClient", idOfClient);
            query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
            query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
        } else {
            query = emReport.createNativeQuery("select cast(-1 as bigint) as idofmenudetail, pmd.amount, pmd.idofregularpreorder, pmd.state, "
                    + "cast(0 as integer) as dailysale, pmd.idofpreordermenudetail, pmd.mobileGroupOnCreate "
                    + "from cf_preorder_menudetail pmd where pmd.idofpreordercomplex = :idOfPreorderComplex and pmd.deletedState = 0 " //and pmd.amount > 0 "
                    + "union "
                    + "select md.idofmenudetail, null, null, null, g.dailysale, null, null from cf_menudetails md INNER JOIN CF_ComplexInfoDetail cid ON cid.IdOfMenuDetail = md.IdOfMenuDetail "
                    + "JOIN CF_Goods g ON md.IdOfGood = g.IdOfGood "
                    + "WHERE cid.IdOfComplexInfo = :idOfComplexInfo and md.itemcode is not null and md.itemcode <> ''"
                    + "and not exists (select idofpreordermenudetail from cf_preorder_menudetail pmd2 "
                    + "where pmd2.armidofmenu = md.localidofmenu and pmd2.preorderdate between :startDate AND :endDate and pmd2.idofclient = :idOfClient and pmd2.deletedstate = 0 and pmd2.amount > 0) ");
            query.setParameter("idOfPreorderComplex", idOfPreorderComplex);
            query.setParameter("idOfComplexInfo", idOfComplexInfo);
            query.setParameter("idOfClient", idOfClient);
            query.setParameter("startDate", CalendarUtils.startOfDay(date).getTime());
            query.setParameter("endDate", CalendarUtils.endOfDay(date).getTime());
        }

        List res = query.getResultList();
        Set<Long> set = new HashSet<Long>();
        for (Object o : res) {
            Object[] row = (Object[]) o;
            Long id = ((BigInteger)row[0]).longValue();
            Integer amount = (row[1] == null ? 0 : (Integer) row[1]);
            if (amount == 0 && !includeZeroAmount) continue;
            Long idOfRegularPreorder = row[2] == null ? null : ((BigInteger)row[2]).longValue();
            Integer state = (Integer) row[3];
            Boolean isAvailableForRegular = (Integer) row[4] == 1;
            Long idOfPreorderMenuDetail = (row[5] == null ? null : ((BigInteger)row[5]).longValue());
            Integer mobileGroupOnCreate = (row[6] == null ? null : (Integer)row[6]);
            PreorderMenuItemExt menuItemExt = null;
            if (idOfPreorderMenuDetail == null) {
                MenuDetail menuDetail = emReport.find(MenuDetail.class, id);
                menuItemExt = new PreorderMenuItemExt(menuDetail);
            } else {
                PreorderMenuDetail pmd = em.find(PreorderMenuDetail.class, idOfPreorderMenuDetail);
                menuItemExt = new PreorderMenuItemExt(pmd);
            }
            menuItemExt.setAmount(amount);
            menuItemExt.setState(state == null ? 0 : state);
            menuItemExt.setIsRegular(idOfRegularPreorder == null ? false : true);
            menuItemExt.setAvailableForRegular(isAvailableForRegular);
            menuItemExt.setCreatorRole(mobileGroupOnCreate);
            if (!set.contains(menuItemExt.getIdOfMenuDetail())) {
                menuItemExtList.add(menuItemExt);
                set.add(menuItemExt.getIdOfMenuDetail());
            }
        }
        return menuItemExtList;
    }

    private List<WtCategoryItem> getCategoryItemsByWtDish(WtDish wtDish) {
        return RuntimeContext.getAppContext().getBean(DAOReadonlyService.class).getCategoryItemsByWtDish(wtDish);
    }

    public String getWtMenuGroupByWtDish(WtDish wtDish) {
        return RuntimeContext.getAppContext().getBean(DAOReadonlyService.class).getWtMenuGroupByWtDish(wtDish);
    }

    public String getMenuGroupByWtDishAndCategories(WtDish wtDish) {
        StringBuilder sb = new StringBuilder();
        List<WtCategoryItem> items = getCategoryItemsByWtDish(wtDish);
        if (items != null && items.size() > 0) {
            for (WtCategoryItem ci : items) {
                sb.append(ci.getDescription()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private Map<Long, Integer> getAmountDishForPreorderMenuDetailByComplex(Client client, Date startDate, Date endDate,
            WtComplex wtComplex) {
        Query query = emReport.createQuery("SELECT pmd.idOfDish, sum(pmd.amount) FROM PreorderMenuDetail pmd "
                + "LEFT JOIN pmd.preorderComplex complex "
                + "WHERE pmd.client = :client AND pmd.preorderDate between :startDate and :endDate "
                + "AND pmd.deletedState = false AND complex.armComplexId = :idOfComplex group by pmd.idOfDish");
        query.setParameter("client", client);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("idOfComplex", wtComplex.getIdOfComplex().intValue());
        List list = query.getResultList();
        Map<Long, Integer> result = new HashMap<>();
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            Long idOfDish = (Long)row[0];
            Long sum = (Long)row[1];
            if (idOfDish != null) result.put(idOfDish, sum == null ? 0 : sum.intValue());
        }
        return result;
    }

    private Map<Long, Long> getIdOfRegularForPreorderMenuDetailByComplex(Client client, Date startDate, Date endDate,
            WtComplex wtComplex) {
        Query query = emReport.createQuery("SELECT pmd.idOfDish, pmd.regularPreorder.idOfRegularPreorder FROM PreorderMenuDetail pmd "
                + "JOIN pmd.preorderComplex complex "
                + "WHERE pmd.client = :client AND pmd.preorderDate between :startDate and :endDate "
                + "AND pmd.deletedState = false AND complex.armComplexId = :idOfComplex");
        query.setParameter("client", client);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("idOfComplex", wtComplex.getIdOfComplex().intValue());
        List list = query.getResultList();
        Map<Long, Long> result = new HashMap<>();
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            Long idOfDish = (Long)row[0];
            Long idOfRegularPreorder = (Long)row[1];
            if (idOfDish != null) {
                result.put(idOfDish, idOfRegularPreorder == null ? 0L : idOfRegularPreorder);
            }
        }
        return result;
    }

    private List<PreorderMenuItemExt> getWtMenuItemsExt(WtComplex wtComplex, Client client, Org org, Date startDate,
            Date endDate) {
        List<PreorderMenuItemExt> menuItemExtList = new ArrayList<>();

        // Определяем подходящий состав комплекса
        WtComplexesItem complexItem = getWtComplexItemByCycle(wtComplex, startDate);
        List<WtDish> wtDishes;
        Map<Long, Integer> amounts = null;
        Map<Long, Long> regularSigns = null;
        boolean isComposite = wtComplex.getComposite();
        if (isComposite) {
            amounts = getAmountDishForPreorderMenuDetailByComplex(client, startDate, endDate, wtComplex);
            regularSigns = getIdOfRegularForPreorderMenuDetailByComplex(client, startDate, endDate, wtComplex);
        }

        if (complexItem != null) {
            wtDishes = DAOReadExternalsService.getInstance()
                    .getWtDishesByComplexItemAndDates(complexItem, startDate, endDate);
            for (WtDish wtDish : wtDishes) {
                PreorderMenuItemExt menuItemExt = new PreorderMenuItemExt(wtDish);
                menuItemExt.setGroup(getMenuGroupByWtDishAndCategories(wtDish));
                menuItemExt.setName(wtDish.getDishName());
                menuItemExt.setFullName(wtDish.getComponentsOfDish());
                menuItemExt.setPrice(wtDish.getPrice().multiply(new BigDecimal(100)).longValue());
                menuItemExt.setCalories(wtDish.getCalories() == null ? (double) 0 : wtDish.getCalories().doubleValue());
                menuItemExt.setOutput(wtDish.getQty() == null ? "" : wtDish.getQty());
                menuItemExt.setAvailableNow(0);
                menuItemExt.setCarbohydrates(wtDish.getCarbohydrates() == null ? (double) 0 : wtDish.getCarbohydrates().doubleValue());
                menuItemExt.setFat(wtDish.getFat() == null ? (double) 0 : wtDish.getFat().doubleValue());
                menuItemExt.setProtein(wtDish.getProtein() == null ? (double) 0 : wtDish.getProtein().doubleValue());
                menuItemExt.setIdOfMenuDetail(wtDish.getIdOfDish());
                menuItemExt.setAmount(isComposite ? getAmountForPreorderMenuDetail(wtDish, amounts) : 0);
                menuItemExt.setIsRegular(isComposite ? getRegularSignForPreorderMenuDetail(wtDish, regularSigns) : false);
                menuItemExt.setAvailableForRegular(false);
                menuItemExtList.add(menuItemExt);
            }
        }
        return menuItemExtList;
    }

    public Client getClientByContractId(Long contractId) {
        try {
            Query query = emReport.createQuery("select c from Client c where c.contractId = :contractId");
            query.setParameter("contractId", contractId);
            return (Client) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Org getOrgByContractId(Long contractId) {
        try {
            Query query = emReport.createQuery("select c.org from Client c where c.contractId = :contractId");
            query.setParameter("contractId", contractId);
            return (Org) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
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

    @Transactional(rollbackFor = Exception.class)
    public void savePreorderComplexes(PreorderSaveListParam list, String guardianMobile) throws Exception {
        //Собираем коллекцию в нужном виде
        Map<Integer, ComplexListParam> map = new HashMap<Integer, ComplexListParam>();
        for (ComplexListParam complex : list.getComplexes()) {
            Integer idOfComplex = complex.getIdOfComplex();
            ComplexListParam param = map.get(idOfComplex);
            if (param == null) {
                param = new ComplexListParam();
                param.setAmount(complex.getAmount());
                param.setIdOfComplex(complex.getIdOfComplex());
                param.setRegularComplex(complex.getRegularComplex());
                param.setMenuItems(complex.getMenuItems());
                map.put(complex.getIdOfComplex(), param);
            } else {
                if (param.getMenuItems() != null) {
                    param.getMenuItems().addAll(complex.getMenuItems());
                } else {
                    param.setMenuItems(complex.getMenuItems());
                }
            }
        }
        List<ComplexListParam> complexes = new ArrayList<ComplexListParam>();
        for (Map.Entry<Integer, ComplexListParam> entry : map.entrySet()) {
            ComplexListParam param = new ComplexListParam();
            param.setIdOfComplex(entry.getKey());
            param.setAmount(entry.getValue().getAmount());
            param.setRegularComplex(entry.getValue().getRegularComplex());
            param.setMenuItems(entry.getValue().getMenuItems());
            complexes.add(param);
        }
        list.setComplexes(complexes);
        //собрали

        Long contractId = list.getContractId();
        Client client = getClientByContractId(contractId);
        Date date = CalendarUtils.parseDate(list.getDate());
        Date startDate = CalendarUtils.startOfDay(date);
        Date endDate = CalendarUtils.endOfDay(date);
        long nextVersion = nextVersionByPreorderComplex();

        Org org = getOrgByContractId(contractId);
        boolean isWtMenu = org.getUseWebArm();  // меню веб-технолога

        Session session = (Session)emReport.getDelegate();
        List<Client> clientsByMobile = PreorderUtils.getClientsByMobile(session, client.getIdOfClient(), guardianMobile);
        Integer value = PreorderUtils.getClientGroupResult(session, clientsByMobile);
        PreorderMobileGroupOnCreateType mobileGroupOnCreate;
        if (value >= PreorderUtils.SOAP_RC_CLIENT_NOT_FOUND) {
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

        if (!isEditedDay(date, client)) throw new NotEditedDayException("День недоступен для редактирования предзаказа");

        Query queryComplexSelect = em.createQuery("select p from PreorderComplex p "
                + "where p.client.idOfClient = :idOfClient and p.armComplexId = :idOfComplexInfo "
                + "and p.preorderDate between :startDate and :endDate and p.deletedState = false");
        queryComplexSelect.setParameter("idOfClient", client.getIdOfClient());
        queryComplexSelect.setParameter("startDate", startDate);
        queryComplexSelect.setParameter("endDate", endDate);

        Query queryMenuSelect;
        if (!isWtMenu) {
            queryMenuSelect = em.createQuery("select m from PreorderMenuDetail m "
                    + "where m.client.idOfClient = :idOfClient and m.preorderComplex.idOfPreorderComplex = :idOfPreorderComplex "
                    + "and m.armIdOfMenu = :armIdOfMenu and m.deletedState = false");
        } else {
            queryMenuSelect = em.createQuery("select m from PreorderMenuDetail m "
                    + "where m.client.idOfClient = :idOfClient and m.preorderComplex.idOfPreorderComplex = :idOfPreorderComplex "
                    + "and m.idOfDish = :idOfDish and m.deletedState = false");
        }
        queryMenuSelect.setParameter("idOfClient", client.getIdOfClient());

        for (ComplexListParam complex : list.getComplexes()) {
            Integer complexAmount = complex.getAmount();
            Integer idOfComplex = complex.getIdOfComplex();
            boolean complexSelected = (complexAmount > 0);
            boolean regularMenuItems = false;
            if (complex.getMenuItems() != null) {
                for (MenuItemParam menuItem : complex.getMenuItems()) {
                    if (menuItem.getAmount() > 0) {
                        complexSelected = true;
                    }
                    if (menuItem.getRegularMenuDetail() != null) {
                        regularMenuItems = true;
                    }
                }
            }
            RegularPreorderParam regularComplex = complex.getRegularComplex();
            if (regularComplex != null) {
                if (regularComplex.getEnabled() && complex.getAmount() > 0) {
                    createRegularPreorder(client, regularComplex, complex.getAmount(), complex.getIdOfComplex(),
                                date, true, null, guardianMobile, mobileGroupOnCreate);

                } else {
                    deleteRegularPreorder(client, complex.getIdOfComplex(), true, null, date,
                                guardianMobile, regularComplex.getStartDate(), regularComplex.getEndDate(), isWtMenu);
                }
                continue;
            }

            PreorderComplex preorderComplex = null;
            if (!regularMenuItems) {
                queryComplexSelect.setParameter("idOfComplexInfo", idOfComplex);
                try {
                    preorderComplex = (PreorderComplex) queryComplexSelect.getSingleResult();
                } catch (NoResultException e) {
                    if (complexSelected) {
                        if (!isWtMenu) {
                            preorderComplex = createPreorderComplex(idOfComplex, client, date, complexAmount, null,
                                    nextVersion, guardianMobile, mobileGroupOnCreate);
                        } else {
                            preorderComplex = createWtPreorderComplex(idOfComplex, client, date, complexAmount, null,
                                    nextVersion, guardianMobile, mobileGroupOnCreate);
                        }
                        if (complex.getMenuItems() == null) {
                            //создаем детализацию предзаказа по блюдам меню, т.к. ее нет в запросе
                            Set<PreorderMenuDetail> set;
                            if (!isWtMenu) {
                                set = createPreorderMenuDetails(idOfComplex, client, date, preorderComplex, guardianMobile, mobileGroupOnCreate);
                            } else {
                                set = createPreorderWtMenuDetails(idOfComplex, client, date, preorderComplex, guardianMobile, mobileGroupOnCreate);
                            }
                            preorderComplex.setPreorderMenuDetails(set);
                            em.merge(preorderComplex);
                            continue;
                        }
                    }
                }

                if (preorderComplex == null)
                    continue;
            }

            Set<PreorderMenuDetail> set = new HashSet<PreorderMenuDetail>();
            boolean menuDetailChanged = false;
            if (complex.getMenuItems() != null) {
                for (MenuItemParam menuItem : complex.getMenuItems()) {
                    RegularPreorderParam regularMenuItem = menuItem.getRegularMenuDetail();
                    if (regularMenuItem != null) {
                        if (regularMenuItem.getEnabled() && menuItem.getAmount() > 0) {
                            createRegularPreorder(client, regularMenuItem, menuItem.getAmount(), idOfComplex, date,
                                        false, menuItem.getIdOfMenuDetail(), guardianMobile, mobileGroupOnCreate);
                        } else {
                            deleteRegularPreorder(client, null, false, menuItem.getIdOfMenuDetail(),
                                        date, guardianMobile, regularMenuItem.getStartDate(), regularMenuItem.getEndDate(), isWtMenu);
                        }
                        continue;
                    }
                    queryMenuSelect.setParameter("idOfPreorderComplex", preorderComplex.getIdOfPreorderComplex());
                    if (!isWtMenu) {
                        queryMenuSelect.setParameter("armIdOfMenu", menuItem.getIdOfMenuDetail());
                    } else {
                        queryMenuSelect.setParameter("idOfDish", menuItem.getIdOfMenuDetail());
                    }
                    boolean menuSelected = (menuItem.getAmount() > 0);
                    PreorderMenuDetail preorderMenuDetail;
                    try {
                        preorderMenuDetail = (PreorderMenuDetail) queryMenuSelect.getSingleResult();
                        if (!menuDetailChanged && (!preorderMenuDetail.getAmount().equals(menuItem.getAmount())
                                || !preorderMenuDetail.getDeletedState().equals(!menuSelected))) {
                            menuDetailChanged = true;
                        }
                        if (!preorderMenuDetail.getAmount().equals(menuItem.getAmount())) {
                            preorderMenuDetail.setMobile(guardianMobile);
                            preorderMenuDetail.setMobileGroupOnCreate(mobileGroupOnCreate);
                        }
                        preorderMenuDetail.setAmount(menuItem.getAmount());
                        preorderMenuDetail.setDeletedState(!menuSelected);
                        em.merge(preorderMenuDetail);
                    } catch (NoResultException e) {
                        if (!isWtMenu) {
                            preorderMenuDetail = createPreorderMenuDetail(client, preorderComplex, null, date, menuItem.getIdOfMenuDetail(),
                                    menuItem.getAmount(), guardianMobile, mobileGroupOnCreate);
                        } else {
                            preorderMenuDetail = createPreorderWtMenuDetail(client, preorderComplex, null, date, menuItem.getIdOfMenuDetail(),
                                    menuItem.getAmount(), guardianMobile, mobileGroupOnCreate);
                        }
                        if (!menuDetailChanged) menuDetailChanged = true;
                    }
                    set.add(preorderMenuDetail);
                }
            }
            if (preorderComplex != null) {

                if (preorderComplex.getPreorderMenuDetails() == null) {
                    preorderComplex.setPreorderMenuDetails(set);
                }
                if (set.size() > 0) {
                    preorderComplex.getPreorderMenuDetails().addAll(set);
                }

                boolean deleted = false;
                if (preorderComplex.getAmount() > 0 && complex.getAmount() == 0) {
                    deleted = true;
                }
                if (preorderComplex.getAmount() == 0) {
                    deleted = true;
                    for (PreorderMenuDetail pmd : preorderComplex.getPreorderMenuDetails()) {
                        if (pmd.getAmount() > 0) {
                            deleted = false;
                            break;
                        }
                    }
                }
                if (!preorderComplex.getAmount().equals(complex.getAmount())) {
                    preorderComplex.setMobile(guardianMobile);
                    preorderComplex.setMobileGroupOnCreate(mobileGroupOnCreate);
                    updateMobileGroupOnCreateOnMenuDetails(preorderComplex, guardianMobile, mobileGroupOnCreate);
                    preorderComplex.setAmount(complex.getAmount());
                    preorderComplex.setLastUpdate(new Date());
                    preorderComplex.setDeletedState(deleted);
                    preorderComplex.setVersion(nextVersion);
                } else if (!preorderComplex.getDeletedState().equals(deleted)) {
                    preorderComplex.setLastUpdate(new Date());
                    preorderComplex.setDeletedState(deleted);
                    preorderComplex.setVersion(nextVersion);
                } else if (menuDetailChanged) {
                    preorderComplex.setLastUpdate(new Date());
                    preorderComplex.setVersion(nextVersion);
                }

                /*if (set.size() > 0) {
                    preorderComplex.setPreorderMenuDetails(set);
                }
                if (preorderComplex.getDeletedState()) {
                    Query delQuery = em.createQuery("update PreorderMenuDetail set deletedState = true, amount = 0 where preorderComplex.idOfPreorderComplex = :idOfPreorderComplex");
                    delQuery.setParameter("idOfPreorderComplex", preorderComplex.getIdOfPreorderComplex());
                    delQuery.executeUpdate();
                }*/
                em.merge(preorderComplex);
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void updateMobileGroupOnCreateOnMenuDetails(PreorderComplex preorderComplex, String mobile,
            PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        Query query = em.createQuery("update PreorderMenuDetail pmd set pmd.mobile = :mobile, pmd.mobileGroupOnCreate = :mobileGroupOnCreate "
                + "where pmd.preorderComplex = :preorderComplex");
        query.setParameter("mobile", mobile);
        query.setParameter("mobileGroupOnCreate", mobileGroupOnCreate);
        query.setParameter("preorderComplex", preorderComplex);
        query.executeUpdate();
    }

    private boolean regularEquals(RegularPreorderParam regularComplex, RegularPreorder regularPreorder) {
        return regularComplex.getMonday().equals(regularPreorder.getMonday())
                && regularComplex.getTuesday().equals(regularPreorder.getTuesday())
                && regularComplex.getWednesday().equals(regularPreorder.getWednesday())
                && regularComplex.getThursday().equals(regularPreorder.getThursday())
                && regularComplex.getFriday().equals(regularPreorder.getFriday())
                && regularComplex.getSaturday().equals(regularPreorder.getSaturday())
                && regularComplex.getStartDate().equals(regularPreorder.getStartDate())
                && regularComplex.getEndDate().equals(regularPreorder.getEndDate());
    }

    private boolean regularDatesIntersect(RegularPreorderParam regularComplex, RegularPreorder regularPreorder) {
        return CalendarUtils.betweenOrEqualDate(regularComplex.getStartDate(), regularPreorder.getStartDate(), regularPreorder.getEndDate())
                || CalendarUtils.betweenOrEqualDate(regularComplex.getEndDate(), regularPreorder.getStartDate(), regularPreorder.getEndDate())
                || CalendarUtils.betweenOrEqualDate(regularPreorder.getStartDate(), regularComplex.getStartDate(), regularComplex.getEndDate());
    }

    private RegularPreorder createNewRegular(Client client, RegularPreorderParam regularComplex,
            Integer amount, Integer idOfComplex, Date date, boolean isComplex, Long idOfMenu, String guardianMobile,
            PreorderMobileGroupOnCreateType mobileGroupOnCreate, String menuDetailName, Long menuDetailPrice,
            String itemCode) throws Exception {
        RegularPreorder regularPreorder = null;
        boolean isWtMenu = client.getOrg().getUseWebArm();
        if (isComplex) {
            String complexName = null;
            Long complexPrice = null;

            if (!isWtMenu) {
                ComplexInfo ci = getComplexInfo(client, idOfComplex, date);
                if (getMenuDetailList(ci.getIdOfComplexInfo()).size() == 0) {
                    throw new MenuDetailNotExistsException("Не найдены блюда для комплекса с ид.=" + idOfComplex.toString());
                }
                if (ci != null) {
                    complexName = ci.getComplexName();
                    complexPrice = ci.getCurrentPrice();
                }
            } else {
                WtComplex wtComplex = getWtComplex(client, idOfComplex, date);
                if (wtComplex != null) {
                    // Определяем подходящий состав комплекса
                    WtComplexesItem complexItem = getWtComplexItemByCycle(wtComplex, CalendarUtils.startOfDay(date));
                    List<WtDish> wtDishes = null;
                    if (complexItem != null) {
                        wtDishes = DAOReadExternalsService.getInstance()
                                .getWtDishesByComplexItemAndDates(complexItem, CalendarUtils.startOfDay(date), CalendarUtils.endOfDay(date));
                    }
                    if (wtDishes == null || wtDishes.size() == 0) {
                        throw new MenuDetailNotExistsException("Не найдены блюда для комплекса с ид.=" + idOfComplex.toString());
                    }
                    complexName = wtComplex.getName();
                    complexPrice = wtComplex.getPrice() == null ? 0L : wtComplex.getPrice().multiply(new BigDecimal(100)).longValue();
                }
            }

            regularPreorder = new RegularPreorder(client, regularComplex.getStartDate(), regularComplex.getEndDate(), null, idOfComplex,
                    amount, complexName, regularComplex.getMonday(), regularComplex.getTuesday(), regularComplex.getWednesday(),
                    regularComplex.getThursday(), regularComplex.getFriday(), regularComplex.getSaturday(), complexPrice, guardianMobile,
                    RegularPreorderState.CHANGE_BY_USER, mobileGroupOnCreate);
            em.persist(regularPreorder);
        } else {
            regularPreorder = new RegularPreorder(client, regularComplex.getStartDate(), regularComplex.getEndDate(), itemCode, idOfComplex,
                    amount, menuDetailName, regularComplex.getMonday(), regularComplex.getTuesday(), regularComplex.getWednesday(),
                    regularComplex.getThursday(), regularComplex.getFriday(), regularComplex.getSaturday(), menuDetailPrice, guardianMobile,
                    RegularPreorderState.CHANGE_BY_USER, mobileGroupOnCreate);
            if (isWtMenu) regularPreorder.setIdOfDish(idOfMenu);
            em.persist(regularPreorder);
        }
        return regularPreorder;
    }

    private void createRegularPreorder(Client client, RegularPreorderParam regularComplex,
            Integer amount, Integer idOfComplex, Date date, boolean isComplex, Long idOfMenu, String guardianMobile,
            PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws Exception {
        String menuDetailName = null;
        Long menuDetailPrice = null;
        String itemCode = null;
        boolean isWtMenu = client.getOrg().getUseWebArm();

        String condition;
        if (!isWtMenu) {
            condition = isComplex ? " and m.idOfComplex = :idOfComplex " : " and m.itemCode = :itemCode ";
        } else {
            condition = isComplex ? " and m.idOfComplex = :idOfComplex " : " and m.idOfDish = :idOfDish ";
        }
        Query query = em.createQuery("select m from RegularPreorder m "
                + "where m.client = :client " + condition + " and m.deletedState = false");
        query.setParameter("client", client);

        if (isComplex)
            query.setParameter("idOfComplex", idOfComplex);
        else {
            if (!isWtMenu) {
                MenuDetail md = getMenuDetail(client, idOfMenu, date);
                if (md == null) return;
                menuDetailName = md.getMenuDetailName();
                menuDetailPrice = md.getPrice();
                itemCode = md.getItemCode();
                query.setParameter("itemCode", itemCode);
            } else {
                WtDish wtDish = getWtDish(idOfMenu, date);
                if (wtDish == null) return;
                menuDetailName = wtDish.getDishName();
                menuDetailPrice = wtDish.getPrice() == null ? 0L : wtDish.getPrice().multiply(new BigDecimal(100)).longValue();
                itemCode = wtDish.getCode();
                query.setParameter("idOfDish", idOfMenu);
            }
        }
        List<RegularPreorder> regularPreorderList = query.getResultList();
        if (regularPreorderList.size() > 0) {
            for (RegularPreorder regularPreorderItem : regularPreorderList) {
                if (regularEquals(regularComplex, regularPreorderItem) && regularPreorderItem.getAmount().equals(amount)) {
                    return;
                }
                if (regularDatesIntersect(regularComplex, regularPreorderItem)) {
                    deleteRegularPreorderInternal((Session) em.getDelegate(), regularPreorderItem, PreorderState.OK,
                            guardianMobile, RegularPreorderState.CHANGE_BY_USER);
                }
            }
        }
        RegularPreorder regularPreorder = createNewRegular(client, regularComplex, amount, idOfComplex, date, isComplex, idOfMenu, guardianMobile,
                    mobileGroupOnCreate, menuDetailName, menuDetailPrice, itemCode);
        createPreordersFromRegular(regularPreorder, true);
    }

    private void deleteRegularPreorder(Client client, Integer idOfComplex, boolean isComplex, Long idOfMenu, Date date,
            String guardianMobile, Date startDate, Date endDate, boolean isWtMenu) throws Exception {
        String condition = isComplex ? " and m.idOfComplex = :idOfComplex " : " and m.itemCode = :itemCode ";
        Query regularPreorderSelect = em.createQuery("select m from RegularPreorder m "
                + "where m.client = :client " + condition + " and m.deletedState = false and m.startDate = :startDate "
                + "and m.endDate = :endDate");
        regularPreorderSelect.setParameter("client", client);
        regularPreorderSelect.setParameter("startDate", startDate);
        regularPreorderSelect.setParameter("endDate", endDate);
        if (isComplex)
            regularPreorderSelect.setParameter("idOfComplex", idOfComplex);
        else {
            if (!isWtMenu) {
                MenuDetail menuDetail = getMenuDetail(client, idOfMenu, date);
                regularPreorderSelect.setParameter("itemCode", menuDetail.getItemCode());
            } else {
                WtDish wtDish = getWtDish(idOfMenu, date);
                if (wtDish == null) {
                    throw new MenuDetailNotExistsException("Не найдено блюдо с ид.=" + idOfMenu.toString());
                }
                regularPreorderSelect.setParameter("idOfDish", idOfMenu);
            }
        }
        RegularPreorder regularPreorder;
        try {
            regularPreorder = (RegularPreorder) regularPreorderSelect.getSingleResult();
        } catch (NoResultException e) {
            throw new RegularAlreadyDeleted("Для выбранного комплекса или блюда не настроен повтор заказа");
        }
        deleteRegularPreorderInternal((Session) em.getDelegate(), regularPreorder, PreorderState.OK,
                guardianMobile, RegularPreorderState.CHANGE_BY_USER);
    }

    private void deleteRegularPreorderInternal(Session session, RegularPreorder regularPreorder, PreorderState state,
            String guardianMobile, RegularPreorderState regularPreorderState) throws Exception {
        deleteGeneratedPreordersByRegular(session, regularPreorder, state);

        regularPreorder.setState(regularPreorderState);
        regularPreorder.setDeletedState(true);
        regularPreorder.setLastUpdate(new Date());
        regularPreorder.setMobile(guardianMobile);
        session.update(regularPreorder);
    }

    @Transactional
    public void dailyCheck() {
        Date currentDate = CalendarUtils.startOfDayInUTC(new Date());
        int day = CalendarUtils.getDayOfMonth(currentDate);
        Date dateFrom = CalendarUtils.getFirstDayOfMonth(currentDate);
        if (day <= PreorderRequestsReportService.DAY_PREORDER_CHECK) {
            dateFrom = CalendarUtils.getFirstDayOfPrevMonth(currentDate);
        }
        dateFrom = CalendarUtils.startOfDayInUTC(dateFrom);

        List<Date> weekends = GoodRequestsChangeAsyncNotificationService.getInstance().getProductionCalendarDates(currentDate);
        Integer maxDays = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                .getMaxDateToCreateRequests(currentDate, weekends, PreorderRequestsReportService.MAX_FORBIDDEN_DAYS);
        Date dateTo = CalendarUtils.addDays(currentDate, maxDays);
        dateTo = CalendarUtils.startOfDay(dateTo);
        while (dateFrom.before(dateTo)) {
            dailyCheckOnDate(dateFrom);
            dateFrom = CalendarUtils.addDays(dateFrom, 1);
        }
    }

    public List<PreorderCheck> getPreorderCheckListForNotification() {
        Query query = emReport.createQuery("select pc from PreorderCheck pc "
                + "where pc.lastUpdate between :startDate and :endDate and pc.alarm = true");
        query.setParameter("startDate", CalendarUtils.startOfDay(new Date()));
        query.setParameter("endDate", CalendarUtils.endOfDay(new Date()));

        return query.getResultList();
    }

    private void dailyCheckOnDate(Date date) {
        Query query = emReport.createQuery("select coalesce(sum(pc.amount), 0) "
                + "from PreorderComplex pc, Org o "
                + "where o.idOfOrg = pc.idOfOrgOnCreate "
                + "and pc.deletedState = false and pc.modeOfAdd <> :mode "
                + "and pc.preorderDate = :date and upper(o.tag) not like '%ТЕСТ%' ");
        query.setParameter("date", date);
        query.setParameter("mode", PreorderComplex.COMPLEX_MODE_4);
        Long preorderAmount = (Long)query.getSingleResult();

        query = emReport.createQuery("select coalesce(sum(pmd.amount), 0) "
                + "from PreorderComplex pc, PreorderMenuDetail pmd, Org o "
                + "where pmd.preorderComplex = pc and o.idOfOrg = pc.idOfOrgOnCreate "
                + "and pc.deletedState = false and pmd.deletedState = false "
                + "and pc.modeOfAdd = :mode "
                + "and pc.preorderDate = :date and upper(o.tag) not like '%ТЕСТ%' ");
        query.setParameter("date", date);
        query.setParameter("mode", PreorderComplex.COMPLEX_MODE_4);
        preorderAmount += (Long)query.getSingleResult();

        query = emReport.createQuery("select coalesce(sum(case when pos.unitsScale = 3 then pos.totalCount else div(pos.totalCount, 1000) end), 0) as count1 "
                + "from PreorderComplex pc, Org o, GoodRequestPosition pos "
                + "where o.idOfOrg = pc.idOfOrgOnCreate "
                + "and pc.idOfGoodsRequestPosition = pos.globalId "
                + "and pc.deletedState = false and pos.deletedState = false and pc.idOfGoodsRequestPosition is not null "
                + "and pc.preorderDate = :date and upper(o.tag) not like '%ТЕСТ%' ");
        query.setParameter("date", date);
        Long pcAmount = (Long)query.getSingleResult();

        query = emReport.createQuery("select coalesce(sum(case when pos.unitsScale = 3 then pos.totalCount else div(pos.totalCount, 1000) end), 0) "
                + "from PreorderComplex pc, PreorderMenuDetail pmd, Org o, GoodRequestPosition pos "
                + "where pmd.preorderComplex = pc and o.idOfOrg = pc.idOfOrgOnCreate "
                + "and pmd.idOfGoodsRequestPosition = pos.globalId "
                + "and pc.deletedState = false and pmd.deletedState = false and pos.deletedState = false "
                + "and pmd.idOfGoodsRequestPosition is not null "
                + "and pc.preorderDate = :date and upper(o.tag) not like '%ТЕСТ%' ");
        query.setParameter("date", date);
        Long pmdAmount = (Long)query.getSingleResult();

        // для нового меню делить на 1000 не нужно!
        //Long goodRequestAmount = (pcAmount + pmdAmount) / 1000L;
        Long goodRequestAmount = pcAmount + pmdAmount;

        query = em.createQuery("select pc from PreorderCheck pc where pc.date = :date order by createdDate desc");
        query.setParameter("date", date);
        query.setMaxResults(1);
        PreorderCheck preorderCheck;
        try {
            preorderCheck = (PreorderCheck) query.getSingleResult();
        } catch (NoResultException e) {
            preorderCheck = new PreorderCheck(date, preorderAmount, goodRequestAmount, !preorderAmount.equals(goodRequestAmount));
        }
        if (!preorderAmount.equals(preorderCheck.getPreorderAmount()) || !goodRequestAmount.equals(preorderCheck.getGoodRequestAmount())) {
            PreorderCheck preorderCheckNew = new PreorderCheck(date, preorderAmount, goodRequestAmount, true);
            preorderCheck.setAlarm(true);
            em.merge(preorderCheckNew);
        } else {
            preorderCheck.setLastUpdate(new Date());
            em.merge(preorderCheck);
        }
    }

    @Transactional
    public void relevancePreordersToOrgs(PreorderRequestsReportServiceParam params) {
        logger.info("Start relevancePreordersToOrgs process");
        long nextVersion;
        Query query = em.createQuery("select pc, c.org.idOfOrg from PreorderComplex pc join pc.client c "
                + "where pc.preorderDate > :date and pc.deletedState = false "
                + params.getJPACondition()
                + "order by pc.preorderDate");
        query.setParameter("date", new Date());
        List list = query.getResultList();
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            PreorderComplex preorderComplex = (PreorderComplex) row[0];
            Long idOfOrg = (Long) row[1];
            if (preorderComplex.getIdOfOrgOnCreate() != null && !preorderComplex.getIdOfOrgOnCreate().equals(idOfOrg)) {
                if (isGoodRequestExists(preorderComplex)) continue;
                nextVersion = nextVersionByPreorderComplex();
                testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.CHANGE_ORG, true, false);
                continue;
            }
        }
        logger.info("End relevancePreordersToOrgs process");
    }

    @Transactional
    public List<PreorderComplex> getPreorderComplexListForRelevanceToMenu(PreorderRequestsReportServiceParam params) {
        Query query = em.createQuery("select pc from PreorderComplex pc left join fetch pc.preorderMenuDetails join fetch pc.client c join fetch c.org "
                + "where pc.preorderDate > :date and pc.deletedState = false "
                + params.getJPACondition()
                + "order by pc.preorderDate");
        query.setParameter("date", new Date());
        return query.getResultList();
    }

    @Transactional
    public List<ModifyMenu> relevancePreordersToMenu(PreorderComplex preorderComplex, long nextVersion) {
        List<ModifyMenu> modifyMenuList = new ArrayList<>();

        ComplexInfo complexInfo = getComplexInfo(preorderComplex, preorderComplex.getArmComplexId(), preorderComplex.getPreorderDate());
        if (complexInfo == null) {
            // проверяем существование заявок на комплекс и блюда
            if (!isGoodRequestExists(preorderComplex)) {
                testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.DELETED, false, true);
            }
            return null;
        }
        if (preorderComplex.getPreorderMenuDetails().size() == 0 || getMenuDetailList(complexInfo.getIdOfComplexInfo()).size() == 0) {
            if (!isGoodRequestExists(preorderComplex)) {
                testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.DELETED, false, false);
            }
            return null;
        }
        if (preorderComplex.getAmount() > 0) {
            if (!preorderComplex.getComplexPrice().equals(complexInfo.getCurrentPrice())) {
                if (!isGoodRequestExists(preorderComplex)) {
                    testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.CHANGED_PRICE, false, false);
                }
                return null;
            }
        } else {
            for (PreorderMenuDetail preorderMenuDetail : preorderComplex.getPreorderMenuDetails()) {
                //if (preorderMenuDetail.getIdOfGoodsRequestPosition() != null) continue; - перенесена ниже
                if (!preorderMenuDetail.getDeletedState() && preorderMenuDetail.getAmount() > 0) {
                    MenuDetail menuDetail = getMenuDetail(preorderComplex.getClient(), preorderMenuDetail.getItemCode(),
                            preorderMenuDetail.getPreorderDate(), null, complexInfo.getIdOfComplexInfo());
                    if (menuDetail == null) {
                        if (!isGoodRequestExists(preorderComplex)) {
                            testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.DELETED, false,
                                    false);
                        }
                        break;
                    } else {
                        if (!preorderMenuDetail.getMenuDetailPrice().equals(menuDetail.getPrice())) {
                            if (!isGoodRequestExists(preorderComplex)) {
                                testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.CHANGED_PRICE,
                                        false, false);
                            }
                            break;
                        } else if (!preorderMenuDetail.getArmIdOfMenu().equals(menuDetail.getLocalIdOfMenu())) {
                            logger.info(String.format("Change localIdOfMenu %s to %s at preorder %s adding to process list",
                                    preorderMenuDetail.getArmIdOfMenu(), menuDetail.getLocalIdOfMenu(), preorderComplex));
                            ModifyMenu modifyMenu = new ModifyMenu(menuDetail.getLocalIdOfMenu(),
                                    preorderMenuDetail.getIdOfPreorderMenuDetail(), preorderComplex.getIdOfPreorderComplex());
                            modifyMenuList.add(modifyMenu);
                        }
                    }
                }
            }
        }
        return modifyMenuList;
    }

    private boolean isGoodRequestExists(PreorderComplex preorderComplex) {
        if (preorderComplex.getIdOfGoodsRequestPosition() != null) {
            logger.info("Preorder can't be deleted " + preorderComplex.toString() + " due to OrgGoodRequest for PreorderComplex exists");
            return true;
        }
        for (PreorderMenuDetail preorderMenuDetail : preorderComplex.getPreorderMenuDetails()) {
            if (preorderMenuDetail.getIdOfGoodsRequestPosition() != null) {
                logger.info("Preorder can't be deleted " + preorderComplex.toString() + " due to OrgGoodRequest for PreorderMenuDetail exists");
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void relevancePreordersToWtMenu(PreorderComplex preorderComplex, long nextVersion) {
        List<ModifyMenu> modifyMenuList = new ArrayList<>();
        Date preorderDate = preorderComplex.getPreorderDate();

        WtComplex wtComplex = getWtComplex(preorderComplex.getClient(), preorderComplex.getArmComplexId(), preorderDate);

        if (wtComplex == null) {
            testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.DELETED, false, true);
            return;
        }

        // Определяем подходящий состав комплекса
        WtComplexesItem complexItem = getWtComplexItemByCycle(wtComplex, CalendarUtils.startOfDay(preorderDate));
        List<WtDish> wtDishes = new ArrayList<>();
        if (complexItem != null) {
            wtDishes = DAOReadExternalsService.getInstance()
                    .getWtDishesByComplexItemAndDates(complexItem, CalendarUtils.startOfDay(preorderDate), CalendarUtils.endOfDay(preorderDate));
        }
        if (complexItem == null || preorderComplex.getPreorderMenuDetails().size() == 0 || wtDishes.size() == 0) {
            testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.DELETED, false, false);
            return;
        }

        if (preorderComplex.getAmount() > 0) {
            if (!preorderComplex.getComplexPrice().equals(wtComplex.getPrice().multiply(new BigDecimal(100)).longValue())) {
                testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.CHANGED_PRICE, false, false);
                return;
            }
        } else {
            for (PreorderMenuDetail preorderMenuDetail : preorderComplex.getPreorderMenuDetails()) {
                if (preorderMenuDetail.getIdOfGoodsRequestPosition() != null) continue;
                if (!preorderMenuDetail.getDeletedState() && preorderMenuDetail.getAmount() > 0) {
                    WtDish wtDish = getWtDishByItemCodeAndId(complexItem, preorderDate, preorderMenuDetail.getIdOfDish());
                    if (wtDish == null) {
                        testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.DELETED, false, false);
                        break;
                    } else {
                        if (!preorderMenuDetail.getMenuDetailPrice().equals(wtDish.getPrice().multiply(new BigDecimal(100)).longValue())) {
                            testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.CHANGED_PRICE, false, false);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public void changeLocalIdOfMenu(List<ModifyMenu> modifyMenuList, Long nextVersion) {
        logger.info("Start change localIdOfMenu");
        doChangeLocalIdOfMenu(modifyMenuList, BASE_ID_MENU_VALUE_FOR_MODIFY, nextVersion, false);
        doChangeLocalIdOfMenu(modifyMenuList, 0, nextVersion, true);
        logger.info("End change localIdOfMenu");
    }

    private void doChangeLocalIdOfMenu(List<ModifyMenu> modifyMenuList, long offset, Long nextVersion, boolean updatePreorderComplex) {
        for (ModifyMenu modifyMenu : modifyMenuList) {
            Query query = em.createQuery("update PreorderMenuDetail set armIdOfMenu= :armIdOfMenu where idOfPreorderMenuDetail = :idOfPreorderMenuDetail");
            query.setParameter("armIdOfMenu", offset + modifyMenu.getNewIdOfMenu());
            query.setParameter("idOfPreorderMenuDetail", modifyMenu.getIdOfPreorderMenuDetail());
            query.executeUpdate();
            if (updatePreorderComplex) {
                Query query2 = em.createQuery("update PreorderComplex set version = :version, lastUpdate = :lastUpdate where idOfPreorderComplex = :idOfPreorderComplex");
                query2.setParameter("version", nextVersion);
                query2.setParameter("lastUpdate", new Date());
                query2.setParameter("idOfPreorderComplex", modifyMenu.getIdOfPreorderComplex());
                query2.executeUpdate();
            }
        }
    }

    @Transactional
    public void relevancePreordersToOrgFlag(PreorderRequestsReportServiceParam params) {
        logger.info("Start relevancePreordersToOrgFlag process");
        long nextVersion;
        Query query = em.createQuery("select pc from PreorderComplex pc join pc.client.org o "
                + "where pc.preorderDate > :date and pc.deletedState = false and o.preordersEnabled = false "
                + params.getJPACondition()
                + "order by pc.preorderDate");
        query.setParameter("date", new Date());
        List<PreorderComplex> list = query.getResultList();
        for (PreorderComplex preorderComplex : list) {
            if (isGoodRequestExists(preorderComplex)) continue;
            nextVersion = nextVersionByPreorderComplex();
            testAndDeletePreorderComplex(nextVersion, preorderComplex, PreorderState.PREORDER_OFF, true, false);
        }

        logger.info("End relevancePreordersToOrgFlag process");
    }

    private void testAndDeletePreorderComplex(long nextVersion, PreorderComplex preorderComplex, PreorderState preorderState,
            boolean deleteRegular, boolean forceDelete) {
        try {
            Boolean doDelete = false;
            Date today = CalendarUtils.startOfDay(new Date());
            Integer syncCountDays = PreorderComplex.getDaysOfRegularPreorders();
            Map<String, Integer[]> sd = getSpecialDates(CalendarUtils.addHours(today, 12), syncCountDays,
                    preorderComplex.getClient().getOrg().getIdOfOrg(), preorderComplex.getClient());
            for (Map.Entry<String, Integer[]> entry : sd.entrySet()) {
                if (preorderComplex.getPreorderDate().equals(CalendarUtils.parseDate(entry.getKey()))) {
                    doDelete = (entry.getValue())[0].equals(0);
                    break;
                }
            }
            if (doDelete || forceDelete) {
                deletePreorderComplex(preorderComplex, nextVersion, preorderState);
                if (deleteRegular && preorderComplex.getRegularPreorder() != null) {
                    deleteRegularPreorderInternal((Session)em.getDelegate(), preorderComplex.getRegularPreorder(),
                            preorderState, null, RegularPreorderState.CHANGE_BY_SERVICE);
                }
                logger.info("Deleted preorder id=" + preorderComplex.getIdOfPreorderComplex() + " " + preorderComplex.toString() + (forceDelete ? " (force delete = true)" : ""));
            } else {
                logger.info("Preorder id=" + preorderComplex.getIdOfPreorderComplex() + "can't be deleted " + preorderComplex.toString());
            }
        } catch (Exception e) {
            logger.error("Error in testAndDeletePreorderComplex: ", e);
        }
    }

    private void generatePreordersByScheduleInternal(PreorderRequestsReportServiceParam params) {
        try {
            logger.info("Start of generating regular preorders");
            if (params.isEmpty()) {
                RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).generatePreordersBySchedule(params);
            }
            logger.info("Successful end of generating regular preorders");
        } catch (Exception e) {
            logger.error("Error in generating regular preorders: ", e);
        }
        try {
            logger.info("Start additional tasks for preorders");
            RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).additionalTasksForPreorders(params);
            logger.info("Successful end additional tasks for preorders");
        } catch (Exception e) {
            logger.error("Error in additional tasks for preorders: ", e);
        }
        try {
            logger.info("Start additional tasks for regulars");
            if (params.isEmpty()) {
                RuntimeContext.getAppContext().getBean(PreorderOperationsService.class).additionalTasksForRegulars(params);
            }
            logger.info("Successful end additional tasks for regulars");
        } catch (Exception e) {
            logger.error("Error in additional tasks for regualrs: ", e);
        }

    }

    private void deleteGeneratedPreordersByRegular(Session session, RegularPreorder regularPreorder, PreorderState state) throws Exception {
        Date dateFrom = getStartDateForGeneratePreordersInternal(regularPreorder.getClient());
        long nextVersion = DAOUtils.nextVersionByPreorderComplex(session);
        org.hibernate.Query delQuery = session.createQuery("update PreorderComplex set deletedState = true, lastUpdate = :lastUpdate, amount = 0, state = :state, version = :version "
                + "where regularPreorder = :regularPreorder and preorderDate > :dateFrom and idOfGoodsRequestPosition is null");
        delQuery.setParameter("lastUpdate", new Date());
        delQuery.setParameter("regularPreorder", regularPreorder);
        delQuery.setParameter("dateFrom", dateFrom);
        delQuery.setParameter("state", state);
        delQuery.setParameter("version", nextVersion);
        delQuery.executeUpdate();

        delQuery = session.createQuery("update PreorderMenuDetail set deletedState = true, amount = 0, state = :state "
                //+ "where preorderComplex.idOfPreorderComplex in (select idOfPreorderComplex from PreorderComplex "
                + "where regularPreorder = :regularPreorder and preorderDate > :dateFrom and idOfGoodsRequestPosition is null");
        delQuery.setParameter("regularPreorder", regularPreorder);
        delQuery.setParameter("dateFrom", dateFrom);
        delQuery.setParameter("state", state);
        delQuery.executeUpdate();
    }

    private Date getMaxPreorderDate(RegularPreorder regularPreorder) {
        Query query = em.createQuery("select max(pc.preorderDate) from PreorderComplex pc where pc.regularPreorder = :regularPreorder");
        query.setParameter("regularPreorder", regularPreorder);
        return (Date)query.getSingleResult();
    }

    @Transactional
    private List<OrgGoodRequest> getOrgGoodRequests(long idOfOrg, Date startDate, Date endDate) {
        return emReport.createQuery("select ogr from OrgGoodRequest ogr where ogr.idOfOrg = :idOfOrg and ogr.day between :startDate and :endDate order by ogr.day")
                .setParameter("idOfOrg",idOfOrg)
                .setParameter("startDate", startDate)
                .setParameter("endDate",endDate)
                .getResultList();
    }

    private boolean orgGoodRequestExists(List<OrgGoodRequest> list, Date date) {
        for (OrgGoodRequest org : list) {
            if (org.getDay().equals(date)) return true;
        }
        return false;
    }

    @Transactional
    public void processAdditionalTaskForPreorder(PreorderComplex preorderComplex, List<ProductionCalendar> productionCalendar,
            List<SpecialDate> specialDates, Long idOfClientGroup) throws Exception {
        Boolean isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                .isWeekendByProductionCalendar(preorderComplex.getPreorderDate(), productionCalendar);
        isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isWeekendBySpecialDateAndSixWorkWeek(
                isWeekend, preorderComplex.getPreorderDate(), idOfClientGroup, preorderComplex.getIdOfOrgOnCreate(), specialDates);
        if (!isWeekend) isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                .isHolidayByProductionCalendar(preorderComplex.getPreorderDate(), productionCalendar);
        if (isWeekend) {
            if (isEditedDay(preorderComplex.getPreorderDate(), preorderComplex.getClient())) {
                logger.info(String.format("Delete preorderComplex %s by change of calendar", preorderComplex.toString()));
                long nextVersion = nextVersionByPreorderComplex();
                deletePreorderComplex(preorderComplex, nextVersion, PreorderState.CHANGED_CALENDAR);
            } else {
                logger.info(String.format("PreorderComplex %s must be deleted by change of calendar, but not editable day", preorderComplex));
            }
        }
    }

    @Transactional
    public void deleteExpiredRegularPreorder(RegularPreorder regularPreorder) {
        Date currentDate = new Date();
        if (currentDate.before(regularPreorder.getEndDate())) return;
        regularPreorder.setState(RegularPreorderState.CHANGE_BY_SERVICE);
        regularPreorder.setDeletedState(true);
        em.merge(regularPreorder);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createPreordersFromRegular(RegularPreorder regularPreorder, boolean doDeleteExisting) throws Exception {
        //генерация предзаказов по ид. регулярного заказа
        //для блюд - по коду товара и цене
        //для комплексов - по ид. комплеса + цена.
        if (regularPreorder == null || regularPreorder.getDeletedState()) return;
        if (!em.contains(regularPreorder)) regularPreorder = em.find(RegularPreorder.class, regularPreorder.getIdOfRegularPreorder());

        //проверка на даты: от текущего дня пропускаем дни запрета редактирвоания и генерируем на 2 недели вперед
        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        if (dateTo.after(regularPreorder.getEndDate())) dateTo = regularPreorder.getEndDate();
        Date currentDate = CalendarUtils.startOfDay(new Date());
        long nextVersion = nextVersionByPreorderComplex();

        //данные из производственного календаря за период
        List<SpecialDate> specialDates = DAOReadonlyService.getInstance().getSpecialDates(currentDate, dateTo, regularPreorder.getClient().getOrg().getIdOfOrg());
        List<OrgGoodRequest> preorderRequests = getOrgGoodRequests(regularPreorder.getClient().getOrg().getIdOfOrg(), currentDate, dateTo);
        currentDate = getStartDateForGeneratePreordersInternal(regularPreorder.getClient());

        if (currentDate.before(regularPreorder.getStartDate())) currentDate = regularPreorder.getStartDate();
        if (doDeleteExisting) {
            deleteGeneratedPreordersByRegular((Session) em.getDelegate(), regularPreorder, PreorderState.OK);
        }

        //получаем список всех предзаказов (в т.ч. удаленные)
        List<PreorderComplex> preorderComplexes = getPreorderComplexesByRegular(regularPreorder, currentDate, dateTo);

        List<ProductionCalendar> productionCalendar = DAOReadonlyService.getInstance().getProductionCalendar(new Date(), dateTo);
        currentDate = CalendarUtils.startOfDayInUTC(CalendarUtils.addHours(currentDate, 12));

        while (currentDate.before(dateTo) || currentDate.equals(dateTo)) {
            logger.info(String.format("Processing regular preorder %s on date %s...", regularPreorder, CalendarUtils.dateToString(currentDate)));
            if (orgGoodRequestExists(preorderRequests, currentDate)) {
                //если на тек день есть заявка, то этот день пропускаем
                logger.info("OrgGoodRequest exists");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }
            Boolean isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isWeekendByProductionCalendar(currentDate, productionCalendar);
            isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isWeekendBySpecialDateAndSixWorkWeek(
                    isWeekend, currentDate, regularPreorder.getClient().getIdOfClientGroup(), regularPreorder.getClient().getOrg().getIdOfOrg(), specialDates);
            if (!isWeekend) isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isHolidayByProductionCalendar(currentDate, productionCalendar);
            if (isWeekend) {
                PreorderComplex pc = findPreorderComplexOnDate(preorderComplexes, currentDate, false);
                if (pc != null) {
                    logger.info("Delete by change of calendar");
                    deletePreorderComplex(pc, nextVersion, PreorderState.CHANGED_CALENDAR);
                }
            }

            //генерить ли предзаказ по дню недели в регулярном заказе
            boolean doGenerate = doGenerate(currentDate, regularPreorder);
            if (isWeekend || !doGenerate) {
                logger.info("Weekend or not generated day");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            boolean isWtMenu = regularPreorder.getClient().getOrg().getUseWebArm();

            //предзаказ на комплекс
            PreorderComplex preorderComplex = findPreorderComplexOnDate(preorderComplexes, currentDate, null);
            MenuDetail menuDetail = null;
            WtDish wtDish = null;
            ComplexInfo complexInfo = null;
            WtComplex wtComplex = null;

            if (!isWtMenu) {
                //комплекс на дату и с ценой рег. заказа
                complexInfo = getComplexInfo(regularPreorder.getClient(), regularPreorder.getIdOfComplex(), currentDate);
            } else {
                wtComplex = getWtComplex(regularPreorder.getClient(), regularPreorder.getIdOfComplex(), currentDate);
            }
            if ((!isWtMenu && complexInfo == null) || (isWtMenu && wtComplex == null)){
                if (preorderComplex != null && !preorderComplex.getDeletedState()) {
                    //предзаказ есть, но комплекса нет - удаляем ранее сгенерированный предзаказ
                    logger.info("Deleting preorderComplex due to the complex does not exist");
                    deletePreorderComplex(preorderComplex, nextVersion, PreorderState.DELETED);
                } else {
                    logger.info("Not generate by complex not exists");
                }
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            List menuDetails = null;
            WtComplexesItem complexItem = null;
            if (!isWtMenu) {
                menuDetails = getMenuDetailList(complexInfo.getIdOfComplexInfo());
            } else {
                // Определяем подходящий состав комплекса и блюда
                complexItem = getWtComplexItemByCycle(wtComplex, CalendarUtils.startOfDay(currentDate));
            }
            if ((!isWtMenu && menuDetails.size() == 0) || (isWtMenu && complexItem == null)) {
                logger.info("No menu details / wtDishes found");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            List<WtDish> wtDishes = new ArrayList<>();
            if (isWtMenu) {
                wtDishes = DAOReadExternalsService.getInstance()
                        .getWtDishesByComplexItemAndDates(complexItem, CalendarUtils.startOfDay(currentDate), CalendarUtils.endOfDay(currentDate));
                if (wtDishes.size() == 0) {
                    logger.info("No wtDishes found");
                    currentDate = CalendarUtils.addDays(currentDate, 1);
                    continue;
                }
            }

            if ((preorderComplex == null || (preorderComplex != null && allowCreateNewPreorderComplex(preorderComplex)))
                    && !forcePreorderComplexExists(regularPreorder, currentDate)) {
                //на искомую дату нет предзаказа, надо создавать

                boolean comparePrice = !isMenuDetailPreorder(regularPreorder);
                //здесь сравниваем по цене, если заказ на комплекс, а не на блюдо
                if (!comparePrice) {
                    if (!isWtMenu) {
                        menuDetail = getMenuDetail(regularPreorder.getClient(), regularPreorder.getItemCode(),
                                currentDate, regularPreorder.getPrice(), complexInfo.getIdOfComplexInfo());
                    } else {
                        wtDish = getWtDishByItemCodeAndId(complexItem, currentDate, regularPreorder.getIdOfDish());
                    }
                    if ((!isWtMenu && menuDetail == null) || (isWtMenu && wtDish == null)){
                        logger.info("Not found menu detail / wtDish");
                        currentDate = CalendarUtils.addDays(currentDate, 1);
                        continue;
                    }
                }
                if (!isWtMenu) {
                    //не найден комплекс или цена не совпадает с рег. заказом
                    if (comparePrice ? !complexInfo.getCurrentPrice().equals(regularPreorder.getPrice()) : false) {
                        logger.info("Complex price not not match to regular");
                        currentDate = CalendarUtils.addDays(currentDate, 1);
                        continue;
                    }
                }

                logger.info("===Create preorder complex from regular===");
                if (!isWtMenu) {
                    preorderComplex = createPreorderComplex(regularPreorder.getIdOfComplex(), regularPreorder.getClient(),
                            complexInfo.getMenuDate(),
                            !isMenuDetailPreorder(regularPreorder) ? regularPreorder.getAmount() : 0, complexInfo,
                            nextVersion, regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
                } else {
                    preorderComplex = createWtPreorderComplex(regularPreorder.getIdOfComplex(), regularPreorder.getClient(),
                            currentDate, !isMenuDetailPreorder(regularPreorder) ? regularPreorder.getAmount() : 0, wtComplex,
                            nextVersion, regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
                }
                preorderComplex.setRegularPreorder(regularPreorder);
                em.persist(preorderComplex);

            } else if (!isMenuDetailPreorder(regularPreorder)) {
                logger.info("Preorder complex exists or deleted by user");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            if (isMenuDetailPreorder(regularPreorder)) {
                if (preorderComplex == null) {
                    preorderComplex = findPreorderComplex(currentDate, regularPreorder.getClient(), regularPreorder.getIdOfComplex());
                }

                if (preorderComplex != null && !preorderComplex.getDeletedState()) {
                    if (!isWtMenu && menuDetail == null) {
                        menuDetail = getMenuDetail(regularPreorder.getClient(), regularPreorder.getItemCode(),
                                currentDate, regularPreorder.getPrice(), complexInfo.getIdOfComplexInfo());
                    }
                    if (isWtMenu && wtDish == null) {
                        wtDish = getWtDishByItemCodeAndId(complexItem, currentDate, regularPreorder.getIdOfDish());
                    }
                    if ((!isWtMenu && menuDetail == null) || (isWtMenu && wtDish == null)) {
                        if (isActualPreorderMenuDetailExists(preorderComplex)) {
                            logger.info("Not found menu detail, another menu details exist");
                        } else {
                            logger.info("Not found menu detail, deleting preorderComplex");
                            deletePreorderComplex(preorderComplex, nextVersion, PreorderState.DELETED); //не нашли блюдо из предзаказа - удаляем предзаказ
                        }
                        currentDate = CalendarUtils.addDays(currentDate, 1);
                        continue;
                    }
                }

                PreorderMenuDetail preorderMenuDetail = null;
                if (!isWtMenu) {
                    preorderMenuDetail = findPreorderMenuDetail(currentDate, regularPreorder.getClient(), menuDetail.getLocalIdOfMenu());
                } else {
                    preorderMenuDetail = findPreorderWtDish(currentDate, regularPreorder.getClient(),
                            regularPreorder.getIdOfComplex(), wtDish.getIdOfDish());
                }
                if (preorderMenuDetail == null || (preorderMenuDetail != null && allowCreateNewPreorderMenuDetail(preorderMenuDetail))) {
                    if (!isWtMenu && !forcePreorderMenuDetailExists(regularPreorder, currentDate)) {
                        //на искомую дату нет предзаказа, надо создавать
                        logger.info("===Create preorder menudetail from regular===");
                        preorderMenuDetail = createPreorderMenuDetail(regularPreorder.getClient(), preorderComplex, menuDetail,
                                menuDetail.getMenu().getMenuDate(), menuDetail.getLocalIdOfMenu(), regularPreorder.getAmount(),
                                regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
                        preorderMenuDetail.setRegularPreorder(regularPreorder);
                        em.persist(preorderMenuDetail);
                    }
                    if (isWtMenu && !forcePreorderWtMenuDetailExists(regularPreorder, currentDate)) {
                        //на искомую дату нет предзаказа, надо создавать
                        logger.info("===Create preorder menudetail from regular===");
                        preorderMenuDetail = createPreorderWtMenuDetail(regularPreorder.getClient(), preorderComplex,
                                wtDish, currentDate, regularPreorder.getAmount(), regularPreorder.getMobile(),
                                regularPreorder.getMobileGroupOnCreate());
                        preorderMenuDetail.setRegularPreorder(regularPreorder);
                        em.persist(preorderMenuDetail);
                    }
                } else if (preorderMenuDetail != null) {
                    if (preorderMenuDetail.getAmount() == 0) {
                        preorderMenuDetail.setAmount(regularPreorder.getAmount());
                        preorderMenuDetail.setRegularPreorder(regularPreorder);
                        em.merge(preorderMenuDetail);
                    } else {
                        currentDate = CalendarUtils.addDays(currentDate, 1);
                        continue;
                    }
                }
            }

            Set<PreorderMenuDetail> set;
            if (!isWtMenu) {
                set = createPreorderMenuDetails(menuDetails, regularPreorder.getClient(),
                        complexInfo.getMenuDate(), preorderComplex, regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
            } else {
                set = createPreorderWtMenuDetails(wtDishes, regularPreorder.getClient(),
                        currentDate, preorderComplex, regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
            }
            if (set.size() > 0) {
                preorderComplex.setPreorderMenuDetails(set);
                preorderComplex.setVersion(nextVersion);
            }
            em.merge(preorderComplex);
            currentDate = CalendarUtils.addDays(currentDate, 1);
        }
        //Проверяем есть ли от сегодняшнего дня актуальные предзаказы. если нет ни одного - удаляем рег правило
        testAndDeleteRegularPreorder(regularPreorder);
    }

    private boolean isMenuDetailPreorder(RegularPreorder regularPreorder) {
        return !StringUtils.isEmpty(regularPreorder.getItemCode());
    }

    private boolean forcePreorderComplexExists(RegularPreorder regularPreorder, Date date) {
        //существует ли актуальный предзаказ на комплекс без привязки к регуляру
        return em.createQuery("select pc.idOfPreorderComplex from PreorderComplex pc "
                + "where pc.client = :client and pc.preorderDate = :date and pc.armComplexId = :complexId and pc.deletedState = false")
                .setParameter("client", regularPreorder.getClient())
                .setParameter("date", date)
                .setParameter("complexId", regularPreorder.getIdOfComplex())
                .getResultList().size() > 0;
    }

    private boolean forcePreorderMenuDetailExists(RegularPreorder regularPreorder, Date date) {
        //существует ли актуальный предзаказ на блюдо без привязки к регуляру
        return em.createQuery("select pmd.idOfPreorderMenuDetail from PreorderMenuDetail pmd "
                + "where pmd.client = :client and pmd.preorderDate = :date and pmd.itemCode = :itemCode and pmd.deletedState = false")
                .setParameter("client", regularPreorder.getClient())
                .setParameter("date", date)
                .setParameter("itemCode", regularPreorder.getItemCode())
                .getResultList().size() > 0;
    }

    private boolean forcePreorderWtMenuDetailExists(RegularPreorder regularPreorder, Date date) {
        //существует ли актуальный предзаказ на блюдо без привязки к регуляру
        return em.createQuery("select pmd.idOfPreorderMenuDetail from PreorderMenuDetail pmd "
                + "left join pmd.preorderComplex pc "
                + "where pmd.client = :client and pmd.preorderDate = :date and "
                //+ "pmd.regularPreorder is null and "
                + "pc.armComplexId = :idOfComplex and pmd.idOfDish = :idOfDish and pmd.deletedState = false")
                .setParameter("client", regularPreorder.getClient())
                .setParameter("date", date)
                .setParameter("idOfComplex", regularPreorder.getIdOfComplex())
                .setParameter("idOfDish", regularPreorder.getIdOfDish())
                .getResultList().size() > 0;
    }

    private boolean allowCreateNewPreorderComplex(PreorderComplex preorderComplex) {
        return preorderComplex.getDeletedState() &&
                !(preorderComplex.getState().equals(PreorderState.OK)
                        || preorderComplex.getState().equals(PreorderState.CHANGE_ORG)
                        || preorderComplex.getState().equals(PreorderState.PREORDER_OFF));
    }

    private boolean allowCreateNewPreorderMenuDetail(PreorderMenuDetail preorderMenuDetail) {
        return preorderMenuDetail.getDeletedState() &&
                !(//preorderMenuDetail.getState().equals(PreorderState.OK) ||
                        preorderMenuDetail.getState().equals(PreorderState.CHANGE_ORG)
                        || preorderMenuDetail.getState().equals(PreorderState.PREORDER_OFF));
    }

    private PreorderComplex findPreorderComplexOnDate(List<PreorderComplex> preorderComplexes, Date currentDate, Boolean deletedState) {
        for (PreorderComplex pc : preorderComplexes) {
            if (pc.getPreorderDate().equals(currentDate) && (deletedState == null ? true : pc.getDeletedState() == deletedState)) return pc;
        }
        return null;
    }

    private List<PreorderComplex> getPreorderComplexesByRegular(RegularPreorder regularPreorder, Date dateFrom, Date dateTo) {
        return em.createQuery("select pc from PreorderComplex pc "
                + "where pc.client = :client and pc.regularPreorder = :regularPreorder "
                + "order by pc.createdDate desc")
                .setParameter("client", regularPreorder.getClient())
                .setParameter("regularPreorder", regularPreorder)
                .getResultList();
    }

    private void testAndDeleteRegularPreorder(RegularPreorder regularPreorder) {
        if (!CalendarUtils.betweenDate(new Date(), regularPreorder.getStartDate(), regularPreorder.getEndDate())) return;
        Query query = em.createQuery("select pc.idOfPreorderComplex from PreorderComplex pc join pc.preorderMenuDetails pmd "
                + "where (pc.regularPreorder = :regularPreorder or pmd.regularPreorder = :regularPreorder) "
                + "and pc.preorderDate > :date and pc.deletedState = false and pc.idOfGoodsRequestPosition is null "
                + "and pmd.deletedState = false");
        query.setParameter("regularPreorder", regularPreorder);
        query.setParameter("date", new Date());
        if (query.getResultList().size() == 0) {
            regularPreorder.setState(RegularPreorderState.CHANGE_BY_SERVICE);
            regularPreorder.setDeletedState(true);
            regularPreorder.setLastUpdate(new Date());
            em.merge(regularPreorder);
        }
    }

    private void deletePreorderComplex(PreorderComplex preorderComplex, long nextVersion, PreorderState preorderState) {
        for (PreorderMenuDetail pmd : preorderComplex.getPreorderMenuDetails()) {
            pmd.deleteByReason(nextVersion, true, preorderState);
            em.merge(pmd);
        }
        preorderComplex.deleteByReason(nextVersion, true, preorderState);
        em.merge(preorderComplex);
    }

    private boolean isActualPreorderMenuDetailExists(PreorderComplex preorderComplex) {
        for (PreorderMenuDetail pmd : preorderComplex.getPreorderMenuDetails()) {
            if (!pmd.getDeletedState()) return true;
        }
       return false;
    }

    @Transactional
    public List<RegularPreorder> getRegularPreorders(PreorderRequestsReportServiceParam params) {
        Query query = em.createQuery("select r from RegularPreorder r join fetch r.client c "
                + "where r.deletedState = false and r.endDate > :date" + params.getRegularPreorderJPACondition());
        query.setParameter("date", new Date());
        return query.getResultList();
    }

    @Transactional
    public List getAllActualPreorders(PreorderRequestsReportServiceParam params) {
        Query query = em.createQuery("select pc, pc.client.clientGroup.compositeIdOfClientGroup.idOfClientGroup from PreorderComplex pc join fetch pc.client c "
                + "where pc.deletedState = false and pc.preorderDate > :date" + params.getJPACondition());
        query.setParameter("date", new Date());
        return query.getResultList();
    }

    @Transactional
    public List<RegularPreorder> getExpiredRegularPreorders(PreorderRequestsReportServiceParam params) {
        Query query = em.createQuery("select r from RegularPreorder r join fetch r.client c "
                + "where r.deletedState = false and r.endDate < :date" + params.getRegularPreorderJPACondition());
        query.setParameter("date", new Date());
        return query.getResultList();
    }

    private Date getStartDateForGeneratePreordersInternal(Client client) throws Exception {
        Date currentDate = CalendarUtils.startOfDay(new Date());
        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        List<SpecialDate> specialDates = DAOReadonlyService.getInstance().getSpecialDates(currentDate, dateTo, client.getOrg().getIdOfOrg());//данные из производственного календаря за период
        Integer forbiddenDays = DAOUtils.getPreorderFeedingForbiddenDays(client); //настройка - количество дней запрета редактирования
        if (forbiddenDays == null) {
            forbiddenDays = PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
        }
        String groupName = DAOReadonlyService.getInstance().getClientGroupName(client.getOrg().getIdOfOrg(), client.getIdOfClientGroup());
        boolean isSixWorkWeek = DAOReadonlyService.getInstance().isSixWorkWeek(client.getOrg().getIdOfOrg(), groupName);
        int i = 0;
        Date result = CalendarUtils.addDays(currentDate, 1);
        while (i < forbiddenDays) {
            boolean isWorkDate = getIsWorkDate(isSixWorkWeek, result, specialDates, client);
            if (isWorkDate) i++;
            result = CalendarUtils.addDays(result, 1);
        }
        return result;
    }

    private boolean getIsWorkDate(boolean isSixWorkWeek, Date currentDate, List<SpecialDate> specialDates, Client client) {
        boolean isWorkDate = CalendarUtils.isWorkDateWithoutParser(isSixWorkWeek, currentDate); //без учета проиводственного календаря
        for (SpecialDate specialDate : specialDates) {
            if (CalendarUtils.betweenDate(specialDate.getDate(), CalendarUtils.startOfDay(currentDate), CalendarUtils.endOfDay(currentDate))) {
                if (specialDate.getIdOfClientGroup() != null && specialDate.getIdOfClientGroup().equals(client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup())) {
                    isWorkDate = !specialDate.getIsWeekend();
                    break; //нашли в таблице календаря запись по группе клиента - выходим
                }
                if (specialDate.getIdOfClientGroup() == null) {
                    isWorkDate = !specialDate.getIsWeekend(); //нашли в таблице календаря запись по ОО клиента (у записи не указана какая-либо группа)
                }
            }
        }
        return isWorkDate;
    }

    private void deletePreorders(RegularPreorder regularPreorder, Date currentDate, Long nextVersion) {
        Query query = em.createQuery("update PreorderComplex pc set pc.deletedState = true, pc.amount = 0, pc.lastUpdate = :lastUpdate, pc.version = :version "
                + "where preorderDate between :startDate and :endDate and regularPreorder = :regularPreorder");
        query.setParameter("lastUpdate", new Date());
        query.setParameter("version", nextVersion);
        query.setParameter("startDate", CalendarUtils.startOfDay(currentDate));
        query.setParameter("endDate", CalendarUtils.endOfDay(currentDate));
        query.setParameter("regularPreorder", regularPreorder);
        query.executeUpdate();
    }

    private boolean doGenerate(Date date, RegularPreorder regularPreorder) {
        boolean doGenerate = false;
        int dayOfWeek = CalendarUtils.getDayOfWeek(date);
        if (dayOfWeek == 2 && regularPreorder.getMonday()) doGenerate = true;
        if (dayOfWeek == 3 && regularPreorder.getTuesday()) doGenerate = true;
        if (dayOfWeek == 4 && regularPreorder.getWednesday()) doGenerate = true;
        if (dayOfWeek == 5 && regularPreorder.getThursday()) doGenerate = true;
        if (dayOfWeek == 6 && regularPreorder.getFriday()) doGenerate = true;
        if (dayOfWeek == 7 && regularPreorder.getSaturday()) doGenerate = true;
        return doGenerate;
    }

    private PreorderComplex findPreorderComplex(Date date, Client client, Integer idOfComplex) {
        Query query = em.createQuery("select pc from PreorderComplex pc "
                + "where pc.client = :client and pc.preorderDate between :startDate and :endDate and pc.armComplexId = :idOfComplex and pc.deletedState = false");
        query.setParameter("client", client);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("idOfComplex", idOfComplex);
        List<PreorderComplex> list = query.getResultList();
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    private PreorderMenuDetail findPreorderMenuDetail(Date date, Client client, Long armIdOfMenu) {
        Query query = em.createQuery("select pmd from PreorderMenuDetail pmd "
                + "where pmd.client = :client and pmd.preorderDate between :startDate and :endDate and pmd.armIdOfMenu = :armIdOfMenu ");
        query.setParameter("client", client);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("armIdOfMenu", armIdOfMenu);
        List<PreorderMenuDetail> list = query.getResultList();
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    private PreorderMenuDetail findPreorderMenuDetail(Date date, Client client, String itemCode) {
        Query query = em.createQuery("select pmd from PreorderMenuDetail pmd "
                + "where pmd.client = :client and pmd.preorderDate between :startDate and :endDate and pmd.itemCode = :itemCode and pmd.deletedState = false");
        query.setParameter("client", client);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("itemCode", itemCode);
        List<PreorderMenuDetail> list = query.getResultList();
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    private PreorderMenuDetail findPreorderWtDish(Date date, Client client, Integer idOfComplex, Long idOfDish) {
        Query query = em.createQuery("select pmd from PreorderMenuDetail pmd left join pmd.preorderComplex pc "
                + "where pmd.client = :client and pmd.preorderDate between :startDate and :endDate and "
                + "pc.armComplexId = :idOfComplex and pmd.idOfDish = :idOfDish");
        query.setParameter("client", client);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        query.setParameter("idOfDish", idOfDish);
        query.setParameter("idOfComplex", idOfComplex);
        List<PreorderMenuDetail> list = query.getResultList();
        return (list == null || list.size() == 0) ? null : list.get(0);
    }

    private PreorderComplex createPreorderComplex(Integer idOfComplex, Client client, Date date, Integer complexAmount, ComplexInfo ci,
            Long nextVersion, String guardianMobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws MenuDetailNotExistsException {
        PreorderComplex preorderComplex = new PreorderComplex();
        preorderComplex.setClient(client);
        preorderComplex.setPreorderDate(date);
        preorderComplex.setAmount(complexAmount);
        preorderComplex.setVersion(nextVersion);
        preorderComplex.setDeletedState(false);
        preorderComplex.setGuid(UUID.randomUUID().toString());
        preorderComplex.setUsedSum(0L);
        preorderComplex.setUsedAmount(0L);
        preorderComplex.setArmComplexId(idOfComplex);
        preorderComplex.setCreatedDate(new Date());
        preorderComplex.setLastUpdate(new Date());
        preorderComplex.setState(PreorderState.OK);
        preorderComplex.setIdOfOrgOnCreate(client.getOrg().getIdOfOrg());
        preorderComplex.setMobile(guardianMobile);
        preorderComplex.setMobileGroupOnCreate(mobileGroupOnCreate);
        if (ci == null) {
            ci = getComplexInfo(client, idOfComplex, date);
            if (ci != null && getMenuDetailList(ci.getIdOfComplexInfo()).size() == 0) {
                throw new MenuDetailNotExistsException("Не найдены блюда для комплекса с ид.=" + idOfComplex.toString());
            }
        }
        if (ci != null) {
            preorderComplex.setComplexName(ci.getComplexName());
            preorderComplex.setComplexPrice(ci.getCurrentPrice());
            preorderComplex.setModeFree(ci.getModeFree());
            preorderComplex.setModeOfAdd(ci.getModeOfAdd());
        } else {
            throw new MenuDetailNotExistsException("Не найден комплекс с ид.=" + idOfComplex.toString());
        }
        return preorderComplex;
    }

    private PreorderComplex createWtPreorderComplex(Integer idOfComplex, Client client, Date date,
            Integer complexAmount, WtComplex wtComplex, Long nextVersion, String guardianMobile,
            PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws MenuDetailNotExistsException {
        PreorderComplex preorderComplex = new PreorderComplex();
        preorderComplex.setClient(client);
        preorderComplex.setArmComplexId(idOfComplex);
        preorderComplex.setPreorderDate(date);
        preorderComplex.setAmount(complexAmount);
        preorderComplex.setVersion(nextVersion);
        preorderComplex.setDeletedState(false);
        preorderComplex.setGuid(UUID.randomUUID().toString());
        preorderComplex.setUsedSum(0L);
        preorderComplex.setUsedAmount(0L);
        preorderComplex.setCreatedDate(new Date());
        preorderComplex.setLastUpdate(new Date());
        preorderComplex.setState(PreorderState.OK);
        preorderComplex.setIdOfOrgOnCreate(client.getOrg().getIdOfOrg());
        preorderComplex.setMobile(guardianMobile);
        preorderComplex.setMobileGroupOnCreate(mobileGroupOnCreate);
        if (wtComplex == null) {
            wtComplex = getWtComplex(client, idOfComplex, date);
            if (wtComplex != null && getWtDishesByComplex(wtComplex).size() == 0) {
                throw new MenuDetailNotExistsException("Не найдены блюда для комплекса с ид.=" + idOfComplex.toString());
            }
        }
        if (wtComplex != null) {
            preorderComplex.setComplexName(wtComplex.getName());
            preorderComplex.setComplexPrice(wtComplex.getPrice() == null ? 0L :
                    wtComplex.getPrice().multiply(new BigDecimal(100)).longValue());
            preorderComplex.setModeFree(0);
            preorderComplex.setModeOfAdd(wtComplex.getComposite() ? 4 : 2);
        } else {
            throw new MenuDetailNotExistsException("Не найден комплекс с ид.=" + idOfComplex.toString());
        }
        return preorderComplex;
    }

    private PreorderComplex createWtPreorderComplex(WtComplex wtComplex, Client client, Date date,
            Long nextVersion, String guardianMobile) {
        PreorderComplex preorderComplex = new PreorderComplex();
        preorderComplex.setIdOfPreorderComplex(wtComplex.getIdOfComplex());
        preorderComplex.setClient(client);
        preorderComplex.setPreorderDate(date);
        preorderComplex.setVersion(nextVersion);
        preorderComplex.setDeletedState(false);
        preorderComplex.setGuid(UUID.randomUUID().toString());
        preorderComplex.setUsedSum(0L);
        preorderComplex.setUsedAmount(0L);
        preorderComplex.setCreatedDate(new Date());
        preorderComplex.setLastUpdate(new Date());
        preorderComplex.setState(PreorderState.OK);
        preorderComplex.setIdOfOrgOnCreate(client.getOrg().getIdOfOrg());
        preorderComplex.setMobile(guardianMobile);
        preorderComplex.setDeletedState(wtComplex.getDeleteState() != 0);
        preorderComplex.setVersion(nextVersion);
        return preorderComplex;
    }

    private boolean preorderMenuDetailExists(PreorderComplex preorderComplex, Client client, Date date, Long idOfMenu) {
        Query query = em.createQuery("select pmd.idOfPreorderMenuDetail from PreorderMenuDetail pmd where pmd.preorderComplex.idOfPreorderComplex = :preorderComplex "
                + "and pmd.client = :client and pmd.preorderDate = :preorderDate and pmd.armIdOfMenu = :idOfMenu and pmd.deletedState = false");
        query.setParameter("preorderComplex", preorderComplex.getIdOfPreorderComplex());
        query.setParameter("client", client);
        query.setParameter("preorderDate", date);
        query.setParameter("idOfMenu", idOfMenu);
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (NonUniqueResultException e) {
            logger.error("Error in preorderMenuDetailExists: ", e);
            return true;
        }
    }

    private boolean preorderWtMenuDetailExists(PreorderComplex preorderComplex, Client client, Date date, Long idOfDish) {
        Query query = em.createQuery("select pmd.idOfPreorderMenuDetail from PreorderMenuDetail pmd where pmd.preorderComplex.idOfPreorderComplex = :preorderComplex "
                + "and pmd.client = :client and pmd.preorderDate = :preorderDate and pmd.idOfDish = :idOfDish and pmd.deletedState = false");
        query.setParameter("preorderComplex", preorderComplex.getIdOfPreorderComplex());
        query.setParameter("client", client);
        query.setParameter("preorderDate", date);
        query.setParameter("idOfDish", idOfDish);
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (NonUniqueResultException e) {
            logger.error("Error in preorderWtMenuDetailExists: ", e);
            return true;
        }
    }

    private List getMenuDetailList(Long idOfComplexInfo) {
        Query query = emReport.createNativeQuery("SELECT md.idofmenudetail, md.LocalIdOfMenu "
                + "FROM CF_MenuDetails md INNER JOIN CF_ComplexInfoDetail cid ON cid.IdOfMenuDetail = md.IdOfMenuDetail "
                + "WHERE cid.IdOfComplexInfo = :idOfComplexInfo");
        query.setParameter("idOfComplexInfo", idOfComplexInfo);
        return query.getResultList();
    }

    private Set<PreorderMenuDetail> createPreorderMenuDetails(List list, Client client, Date date,
            PreorderComplex preorderComplex, String mobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws Exception {
        Set<PreorderMenuDetail> result = new HashSet<PreorderMenuDetail>();
        for (Object o : list) {
            Object[] row = (Object[]) o;
            Long idOfMenu = ((BigInteger) row[1]).longValue();
            if (!preorderMenuDetailExists(preorderComplex, client, date, idOfMenu)) {
                PreorderMenuDetail pmd = createPreorderMenuDetail(client, preorderComplex, null, date, idOfMenu, 0,
                        mobile, mobileGroupOnCreate);
                result.add(pmd);
            }
        }
        return result;
    }

    private Set<PreorderMenuDetail> createPreorderWtMenuDetails(List<WtDish> list, Client client, Date date,
            PreorderComplex preorderComplex, String mobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws Exception {
        Set<PreorderMenuDetail> result = new HashSet<>();
        for (WtDish dish : list) {
            if (!preorderWtMenuDetailExists(preorderComplex, client, date, dish.getIdOfDish())) {
                PreorderMenuDetail pmd = createPreorderWtMenuDetail(client, preorderComplex, dish, date, 0,
                        mobile, mobileGroupOnCreate);
                result.add(pmd);
            }
        }
        return result;
    }

    private Set<PreorderMenuDetail> createPreorderMenuDetails(Integer idOfComplex, Client client, Date date,
            PreorderComplex preorderComplex, String mobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws Exception {
        ComplexInfo ci = getComplexInfo(client, idOfComplex, date);
        List list = getMenuDetailList(ci.getIdOfComplexInfo());
        return createPreorderMenuDetails(list, client, date, preorderComplex, mobile, mobileGroupOnCreate);
    }

    private Set<PreorderMenuDetail> createPreorderWtMenuDetails(Integer idOfComplex, Client client, Date date,
            PreorderComplex preorderComplex, String mobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate)
            throws Exception {
        Set<PreorderMenuDetail> result = new HashSet<>();
        WtComplex wtComplex = getWtComplex(client, idOfComplex, date);
        if (wtComplex == null) {
            return result;
        }
        // Определяем подходящий состав комплекса
        WtComplexesItem complexItem = getWtComplexItemByCycle(wtComplex, CalendarUtils.startOfDay(date));
        List<WtDish> wtDishes;
        if (complexItem != null) {
            wtDishes = DAOReadExternalsService.getInstance()
                    .getWtDishesByComplexItemAndDates(complexItem, CalendarUtils.startOfDay(date), CalendarUtils.endOfDay(date));
            for (WtDish wtDish : wtDishes) {
                if (!preorderWtMenuDetailExists(preorderComplex, client, date, wtDish.getIdOfDish())) {
                    PreorderMenuDetail pmd = createPreorderWtMenuDetail(client, preorderComplex, wtDish, date, 0,
                            mobile, mobileGroupOnCreate);
                    result.add(pmd);
                }
            }
        }
        return result;
    }

    private PreorderMenuDetail createPreorderMenuDetail(Client client, PreorderComplex preorderComplex, MenuDetail md,
            Date date, Long idOfMenu, Integer amount, String mobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws MenuDetailNotExistsException {
        PreorderMenuDetail preorderMenuDetail = new PreorderMenuDetail();
        preorderMenuDetail.setPreorderComplex(preorderComplex);
        preorderMenuDetail.setArmIdOfMenu(idOfMenu);
        preorderMenuDetail.setClient(client);
        preorderMenuDetail.setPreorderDate(date);
        preorderMenuDetail.setAmount(amount);
        preorderMenuDetail.setDeletedState(false);
        preorderMenuDetail.setState(PreorderState.OK);
        if (md == null) md = getMenuDetail(client, idOfMenu, date);
        if (md == null) {
            throw new MenuDetailNotExistsException("Не найдено блюдо с ид.=" + idOfMenu.toString());
        }
        preorderMenuDetail.setMenuDetailName(md.getMenuDetailName());
        preorderMenuDetail.setMenuDetailPrice(md.getPrice());
        preorderMenuDetail.setItemCode(md.getItemCode());
        preorderMenuDetail.setAvailableNow(md.getAvailableNow());
        preorderMenuDetail.setCalories(md.getCalories());
        preorderMenuDetail.setCarbohydrates(md.getCarbohydrates());
        preorderMenuDetail.setFat(md.getFat());
        preorderMenuDetail.setGroupName(md.getGroupName());
        preorderMenuDetail.setMenuDetailOutput(md.getMenuDetailOutput());
        preorderMenuDetail.setProtein(md.getProtein());
        preorderMenuDetail.setShortName(md.getShortName());
        preorderMenuDetail.setIdOfGood(md.getIdOfGood());
        preorderMenuDetail.setMobile(mobile);
        preorderMenuDetail.setMobileGroupOnCreate(mobileGroupOnCreate);
        return preorderMenuDetail;
    }

    private PreorderMenuDetail createPreorderWtMenuDetail(Client client, PreorderComplex preorderComplex, MenuDetail md,
            Date date, Long idOfDish, Integer amount, String mobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws MenuDetailNotExistsException {
        WtDish wtDish = null;
        if (md == null) wtDish = getWtDishById(idOfDish);
        if (wtDish == null) {
            throw new MenuDetailNotExistsException("Не найдено блюдо с ид.=" + idOfDish.toString());
        }
        return createPreorderWtMenuDetail(client, preorderComplex, wtDish, date, amount, mobile, mobileGroupOnCreate);
    }

    private PreorderMenuDetail createPreorderWtMenuDetail(Client client, PreorderComplex preorderComplex,
            WtDish wtDish, Date date, Integer amount, String mobile,
            PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws MenuDetailNotExistsException {
        PreorderMenuDetail preorderMenuDetail = new PreorderMenuDetail();
        preorderMenuDetail.setPreorderComplex(preorderComplex);
        preorderMenuDetail.setClient(client);
        preorderMenuDetail.setPreorderDate(date);
        preorderMenuDetail.setAmount(amount);
        preorderMenuDetail.setDeletedState(wtDish.getDeleteState() != 0);
        preorderMenuDetail.setState(PreorderState.OK);
        preorderMenuDetail.setMenuDetailName(wtDish.getComponentsOfDish());
        preorderMenuDetail.setMenuDetailPrice(wtDish.getPrice().multiply(new BigDecimal(100)).longValue());
        preorderMenuDetail.setGroupName(getMenuGroupByWtDishAndCategories(wtDish));
        preorderMenuDetail.setItemCode(wtDish.getCode());
        preorderMenuDetail.setAvailableNow(0);
        preorderMenuDetail.setCalories(wtDish.getCalories() == null ? (double) 0 : wtDish.getCalories().doubleValue());
        preorderMenuDetail.setCarbohydrates(wtDish.getCarbohydrates() == null ? (double) 0 :
                    wtDish.getCarbohydrates().doubleValue());
        preorderMenuDetail.setFat(wtDish.getFat() == null ? (double) 0 : wtDish.getFat().doubleValue());
        preorderMenuDetail.setMenuDetailOutput(wtDish.getQty() == null ? "" : wtDish.getQty());
        preorderMenuDetail.setProtein(wtDish.getProtein() == null ? (double) 0 : wtDish.getProtein().doubleValue());
        preorderMenuDetail.setShortName(wtDish.getDishName());
        preorderMenuDetail.setIdOfDish(wtDish.getIdOfDish());
        preorderMenuDetail.setMobile(mobile);
        preorderMenuDetail.setMobileGroupOnCreate(mobileGroupOnCreate);
        return preorderMenuDetail;
    }

    private ComplexInfo getComplexInfo(PreorderComplex preorderComplex, Integer idOfComplex, Date date) {
        Query query = emReport.createQuery("select ci from ComplexInfo ci where ci.org.idOfOrg = :idOfOrg "
                + "and ci.idOfComplex = :idOfComplex and ci.menuDate between :startDate and :endDate");
        query.setParameter("idOfOrg", preorderComplex.getIdOfOrgOnCreate());
        query.setParameter("idOfComplex", idOfComplex);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        try {
            return (ComplexInfo)query.getSingleResult();
        } catch (Exception e) {
            logger.error(String.format("Cant find complexInfo idOfComplex=%s, date=%s, idOfPreorderComplex=%s",
                    idOfComplex, date.getTime(), preorderComplex.getIdOfPreorderComplex()), e);
            return null;
        }
    }

    private ComplexInfo getComplexInfo(Client client, Integer idOfComplex, Date date) {
        Query query = emReport.createQuery("select ci from ComplexInfo ci where ci.org.idOfOrg = :idOfOrg "
                + "and ci.idOfComplex = :idOfComplex and ci.menuDate between :startDate and :endDate");
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfComplex", idOfComplex);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        try {
            return (ComplexInfo)query.getSingleResult();
        } catch (Exception e) {
            logger.error(String.format("Cant find complexInfo idOfComplex=%s, date=%s, idOfClient=%s", idOfComplex, date.getTime(), client.getIdOfClient()), e);
            return null;
        }
    }

    private WtComplex getWtComplex(Client client, Integer idOfComplex, Date date) {
        Query query = emReport.createQuery("SELECT complex FROM WtComplex complex "
                + "LEFT JOIN complex.wtOrgGroup orgGroup "
                + "WHERE complex.beginDate <= :startDate AND complex.endDate >= :endDate "
                + "AND complex.deleteState = 0 "
                + "AND complex.idOfComplex = :idOfComplex "
                + "AND (:org IN ELEMENTS(complex.orgs) or :org IN ELEMENTS(orgGroup.orgs)) ");
        query.setParameter("org", client.getOrg());
        query.setParameter("idOfComplex", idOfComplex.longValue());
        query.setParameter("startDate", CalendarUtils.startOfDay(date), TemporalType.DATE);
        query.setParameter("endDate", CalendarUtils.endOfDay(date), TemporalType.DATE);
        try {
            return (WtComplex)query.getSingleResult();
        } catch (Exception e) {
            logger.error(String.format("Can't find wtComplex idOfComplex=%s, date=%s, idOfClient=%s",
                    idOfComplex, date, client.getIdOfClient()), e);
            return null;
        }
    }

    private WtDish getWtDish(Long idOfDish, Date date) {
        Query query = emReport.createQuery(
                "SELECT dish FROM WtDish dish WHERE dish.idOfDish = :idOfDish "
                        + "AND dish.deleteState = 0 "
                        + "AND ((dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding >= :endDate) "
                        + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding >= :endDate) "
                        + "OR (dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding IS NULL) "
                        + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding IS NULL))");
        query.setParameter("idOfDish", idOfDish);
        query.setParameter("startDate", CalendarUtils.startOfDay(date), TemporalType.DATE);
        query.setParameter("endDate", CalendarUtils.endOfDay(date), TemporalType.DATE);
        try {
            return (WtDish)query.getSingleResult();
        } catch (Exception e) {
            logger.error(String.format("Can't find wtDish idOfDish=%s, date=%s", idOfDish, date, e));
            return null;
        }
    }

    private MenuDetail getMenuDetail(Client client, Long idOfMenu, Date date) {
        Query query = emReport.createQuery("select md from MenuDetail md where md.menu.org.idOfOrg = :idOfOrg "
                + "and md.localIdOfMenu = :idOfMenu and md.menu.menuDate between :startDate and :endDate");
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfMenu", idOfMenu);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        try {
            return (MenuDetail)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private MenuDetail getMenuDetail(Client client, String itemCode, Date date, Long price, Long idOfComplexInfo) {
        String priceCondition = (price == null ? "" : " and cid.menuDetail.price = :price");
        Query query = emReport.createQuery("select cid.menuDetail from ComplexInfoDetail cid where cid.complexInfo.idOfComplexInfo = :idOfComplexInfo and cid.complexInfo.org.idOfOrg = :idOfOrg "
                + "and cid.menuDetail.itemCode = :itemCode and cid.complexInfo.menuDate between :startDate and :endDate" + priceCondition);
        query.setParameter("idOfComplexInfo", idOfComplexInfo);
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("itemCode", itemCode);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        if (price != null) query.setParameter("price", price);
        try {
            return (MenuDetail)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private WtDish getWtDishByItemCodeAndPrice(WtComplexesItem complexItem, String itemCode, Date date, Long price, WtComplex wtComplex) {
        String priceCondition = (price == null ? "" : " and dish.price = :price");
        Query query = emReport.createQuery("SELECT dish FROM WtDish dish "
                + "LEFT JOIN dish.complexItems complexItems "
                + "WHERE dish.code = :itemCode AND complexItems.wtComplex = :complex "
                + "AND :complexItem IN ELEMENTS(dish.complexItems) "
                + "AND dish.deleteState = 0 "
                + "AND ((dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding >= :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding >= :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding IS NULL) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding IS NULL))"  + priceCondition);
        query.setParameter("complexItem", complexItem);
        query.setParameter("complex", wtComplex);
        query.setParameter("itemCode", itemCode);
        query.setParameter("startDate", CalendarUtils.startOfDay(date), TemporalType.DATE);
        query.setParameter("endDate", CalendarUtils.endOfDay(date), TemporalType.DATE);
        if (price != null) query.setParameter("price", new BigDecimal(price).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        try {
            return (WtDish)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private WtDish getWtDishByItemCodeAndId(WtComplexesItem complexItem, Date date, Long idOfDish) {
        Query query = emReport.createQuery("SELECT dish FROM WtDish dish "
                + "LEFT JOIN dish.complexItems complexItems "
                + "WHERE dish.idOfDish = :idOfDish "
                + "AND :complexItem IN ELEMENTS(dish.complexItems) "
                + "AND dish.deleteState = 0 "
                + "AND ((dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding >= :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding >= :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding IS NULL) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding IS NULL))");
        query.setParameter("complexItem", complexItem);
        query.setParameter("idOfDish", idOfDish);
        query.setParameter("startDate", CalendarUtils.startOfDay(date), TemporalType.DATE);
        query.setParameter("endDate", CalendarUtils.endOfDay(date), TemporalType.DATE);
        try {
            return (WtDish)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public String getAddress(Integer idOfOrg) {
        Query query = emReport.createQuery("select o.shortAddress from Org o where o.idOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg.longValue());
        return (String)query.getSingleResult();
    }

    @Transactional
    public Long getPreordersSum(Client client, Date startDate, Date endDate) {
        Query query = emReport.createQuery("select pc from PreorderComplex pc join fetch pc.preorderMenuDetails pmd "
                + "where pc.client.idOfClient = :idOfClient and pc.preorderDate between :startDate and :endDate "
                + "and pc.deletedState = false and pmd.deletedState = false");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        List<PreorderComplex> list = query.getResultList();
        return getPreorderSumFromList(list);
    }

    private long getPreorderSumFromList(List<PreorderComplex> list) {
        long sum = 0L;
        Set<Long> set = new HashSet<Long>();
        for (PreorderComplex complex : list) {
            if (!set.contains(complex.getIdOfPreorderComplex())) {
                sum += complex.getComplexPrice() * complex.getAmount() - complex.getUsedSum();
                set.add(complex.getIdOfPreorderComplex());
                for (PreorderMenuDetail pmd : complex.getPreorderMenuDetails()) {
                    sum += (pmd.getMenuDetailPrice() * pmd.getAmount()) - pmd.getUsedSum();
                }
            }
        }
        return sum;
    }

    @Transactional
    public long getNotPaidPreordersSum(Client client, Date dateFrom) {
        Query query = emReport.createQuery("select pc from PreorderComplex pc join fetch pc.preorderMenuDetails pmd "
                + "where pc.client.idOfClient = :idOfClient and pc.preorderDate >= :startDate "
                + "and pc.deletedState = false and pmd.deletedState = false and (pc.idOfGoodsRequestPosition is not null "
                + "or pmd.idOfGoodsRequestPosition is not null) ");
        query.setParameter("idOfClient", client.getIdOfClient());
        query.setParameter("startDate", dateFrom);
        List<PreorderComplex> list = query.getResultList();
        return getPreorderSumFromList(list);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public long nextVersionByPreorderComplex() {
        Session session = (Session)em.getDelegate();
        return DAOUtils.nextVersionByPreorderComplex(session);
    }

    @Transactional(readOnly = true)
    public Map<Date, Long[]> existPreordersByDate(Long idOfClient, Date startDate, Date endDate) {
        Map map = new HashMap<Date, Long[]>();
        Query query = emReport.createQuery("select sum(p.amount), p.preorderDate, coalesce(p.idOfOrgOnCreate, p.client.org.idOfOrg) as org from PreorderComplex p "
                + "where p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate and p.deletedState = false "
                + "group by p.preorderDate, coalesce(p.idOfOrgOnCreate, p.client.org.idOfOrg)");
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("startDate", CalendarUtils.startOfDay(startDate));
        query.setParameter("endDate", CalendarUtils.endOfDay(endDate));
        List result = query.getResultList();
        if (result != null) {
            for (Object obj : result) {
                Object[] row = (Object[]) obj;
                Long[] arr = new Long[2];
                arr[0] = (Long)row[0];//количество заказов
                arr[1] = (Long)row[2];//ид ОО
                map.put((Date)row[1], arr);
            }
        }

        query = emReport.createQuery("select sum(p.amount), p.preorderDate, coalesce(p.preorderComplex.idOfOrgOnCreate, p.client.org.idOfOrg) from PreorderMenuDetail p "
                + "where p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate and p.preorderComplex.deletedState = false "
                + "group by p.preorderDate, coalesce(p.preorderComplex.idOfOrgOnCreate, p.client.org.idOfOrg)");
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("startDate", CalendarUtils.startOfDay(startDate));
        query.setParameter("endDate", CalendarUtils.endOfDay(endDate));
        List result2 = query.getResultList();
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

    public boolean isAcceptableComplex(PreorderComplexItemExt complex, ClientGroup clientGroup,
            Boolean hasDiscount, PreorderGoodParamsContainer container, String ageTypeGroup,
            List<CategoryDiscount> clientDiscountsList) {
        if (clientGroup == null) return false;
        ////Если комплекс не льготный и нет централизованной видимости, то не включаем его в результат
        //if (!complex.getDiscount() && complex.getModeVisible() != 1)
        //    return false;
        String clientGroupName = clientGroup.getGroupName();
        Integer goodType = container.getGoodType();
        Integer ageGroup = container.getAgeGroup();

        //Для дошкольников
        if (ageTypeGroup != null && ageTypeGroup.toUpperCase().contains("ДОШКОЛ"))
        {
            //У дошкольника есть льгота и комплекс НЕ платный
            if (hasDiscount && complex.getDiscount()) {
                // тип возрастной группы 1,5-3 ИЛИ 3-7
                if (ageGroup.equals(GoodAgeGroupType.G_1_5_3.getCode()) || ageGroup.equals(GoodAgeGroupType.G_3_7.getCode())
                     //в названии комплекса присутствует %1,5-3% ИЛИ %3-7%
                    || complex.getComplexName().contains("1,5-3") || complex.getComplexName().contains("3-7")) {
                    //Смотрим льготы дошкольника
                    for (CategoryDiscount categoryDiscount: clientDiscountsList)
                    {
                        //Если льготы указаны правильно, то...
                        if ((categoryDiscount.getCategoryName().contains("1,5-3") && ageGroup.equals(GoodAgeGroupType.G_1_5_3.getCode()))
                                || (categoryDiscount.getCategoryName().contains("3-7") && ageGroup.equals(GoodAgeGroupType.G_3_7.getCode())))
                        {
                            return true;
                        }
                    }
                    return false;
                }
            }
            return false;
        }

        if(!container.getAgeGroup().equals(GoodAgeGroupType.UNSPECIFIED.getCode())
                && !container.getGoodType().equals(GoodType.UNSPECIFIED.getCode())){
            if (!hasDiscount) {
                // 1.2.1.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
                if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") || clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "Завтрак" + "(1-4)"
                    if ((complex.getDiscount() && goodType.equals(GoodType.BREAKFAST.getCode()) && ageGroup.equals(GoodAgeGroupType.G_1_4.getCode())) ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_1_4.getCode())) ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            (!complex.getDiscount()
                                    && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode()))) {
                        return true;
                    }
                }
                // 1.2.1.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
                else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") || clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") || clientGroupName.startsWith("9-")
                        || clientGroupName.startsWith("10-") || clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                    // а) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                    if ((!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_5_11.getCode())) ||
                    // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11) ("
                            (!complex.getDiscount()
                            && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode()))) {
                        return true;
                    }
                }
                // 1.2.1.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
                else {
                    // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                    if (!complex.getDiscount()
                            && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode())) {
                        return true;
                    }
                }
            } else {
                // 1.2.2.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
                if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") || clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "(1-4)"
                    if ((complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_1_4.getCode())) ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_1_4.getCode())) ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode()))) {
                        return true;
                    }
                }
                // 1.2.2.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
                else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") || clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") || clientGroupName.startsWith("9-")
                        || clientGroupName.startsWith("10-") || clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "(5-11)"
                    if ((complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_5_11.getCode())) ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.G_5_11.getCode())) ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode()))) {
                        return true;
                    }
                }
                // 1.2.2.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
                else {
                    // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                    if (!complex.getDiscount() && ageGroup.equals(GoodAgeGroupType.UNSPECIFIED.getCode())) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            if (!hasDiscount) {
                // 1.2.1.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
                if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") || clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "Завтрак" + "(1-4)"
                    if (complex.getDiscount() && complex.getComplexName().toLowerCase().contains("завтрак") && complex.getComplexName().contains("1-4") ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                            !complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            !complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
                // 1.2.1.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
                else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") || clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") || clientGroupName.startsWith("9-")
                        || clientGroupName.startsWith("10-") || clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                    // а) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                    if (!complex.getDiscount() && complex.getComplexName().contains("5-11") ||
                            // б) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            !complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
                // 1.2.1.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
                else {
                    // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                    if (!complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
            } else {
                // 1.2.2.1. если навазвание группы начинается на 1-, 2-, 3-, 4-, то выводим следующие комплексы:
                if (clientGroupName.startsWith("1-") || clientGroupName.startsWith("2-") || clientGroupName.startsWith("3-") || clientGroupName.startsWith("4-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "(1-4)"
                    if (complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(1-4)"
                            !complex.getDiscount() && complex.getComplexName().contains("1-4") ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            !complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
                // 1.2.2.2. если навазвание группы начинается на 5-, 6-, 7-, 8-, 9-, 10-, 11-, 12-, то выводим следующие комплексы:
                else if (clientGroupName.startsWith("5-") || clientGroupName.startsWith("6-") || clientGroupName.startsWith("7-") || clientGroupName.startsWith("8-") || clientGroupName.startsWith("9-")
                        || clientGroupName.startsWith("10-") || clientGroupName.startsWith("11-") || clientGroupName.startsWith("12-")) {
                    // а) комплексы с парамтером discount = true при условии наличия у них в названии "(5-11)"
                    if (complex.getDiscount() && complex.getComplexName().contains("5-11") ||
                            // б) комплексы с параметром discount = false при условии наличия у них в названии "(5-11)"
                            !complex.getDiscount() && complex.getComplexName().contains("5-11") ||
                            // в) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                            !complex.getDiscount() && (!complex.getComplexName().contains("1-4") && !complex.getComplexName().contains("5-11"))) {
                        return true;
                    }
                }
                // 1.2.2.3. если навазвание группы начинается на другие символы (отличные от двух предыдущих условий), то выводим следующие комплексы:
                else {
                    // а) комплексы с параметром discount = false при условии отсутствия у них в названии "(1-4)" и/или "(5-11)"
                    if (!complex.getDiscount() && (!complex.getComplexName().contains("(1-4)") && !complex.getComplexName().contains("(5-11)"))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Transactional
    public SubscriptionFeeding getClientSubscriptionFeeding(Client client) {
        Date date = new Date();
        Session session = (Session)emReport.getDelegate();
        return SubscriptionFeedingService
                .getInstance().getCurrentSubscriptionFeedingByClientToDay(session, client, date, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClientSummaryBaseListResult getClientSummaryByGuardianMobileWithPreorderEnableFilter(String mobile) {
        ClientSummaryBaseListResult clientSummaryBaseListResult = new ClientSummaryBaseListResult();
        try {
            List<ClientSummaryBase> clientSummaries = new ArrayList<ClientSummaryBase>();
            ClientsWithResultCode cd = getClientsByGuardMobile(mobile);

            if (cd != null && cd.getClients() != null) {
                for (Map.Entry<Client, ClientWithAddInfo> entry : cd.getClients().entrySet()) {
                    if (entry.getValue() == null) continue;
                    ClientSummaryBase base = processSummaryBase(entry.getKey());
                    base.setGuardianCreatedWhere(entry.getValue().getClientCreatedFrom().getValue());
                    if (base != null) {
                        clientSummaries.add(base);
                    }
                }
            }

            clientSummaryBaseListResult.setClientSummary(clientSummaries);
            clientSummaryBaseListResult.resultCode = cd.resultCode;
            clientSummaryBaseListResult.description = cd.description;

        } catch (Exception e) {
            logger.error("Error in getClientSummaryByGuardianMobileWithPreorderEnableFilter: ", e);
        }
        return clientSummaryBaseListResult;
    }

    public ClientsWithResultCode getClientsByGuardMobile(String mobile) {

        ClientsWithResultCode data = new ClientsWithResultCode();
        try {
            Map<Client, ClientWithAddInfo> clients = extractClientsFromGuardByGuardMobile(Client.checkAndConvertMobile(mobile));
            if (!clients.isEmpty()) {
                boolean onlyNotActiveCG = true;
                for (Map.Entry<Client, ClientWithAddInfo> entry : clients.entrySet()) {
                    if (entry.getValue() != null) {
                        onlyNotActiveCG = false;
                        break;
                    }
                }
                if (onlyNotActiveCG) {
                    data.description = "Связка не активна";
                } else {
                    data.setClients(clients);
                    data.description = "OK";
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            data.description = e.toString();
        }
        return data;
    }

    public Map<Client, ClientWithAddInfo> extractClientsFromGuardByGuardMobile(String guardMobile) throws Exception {
        Map<Client, ClientWithAddInfo> result = new HashMap<Client, ClientWithAddInfo>();
        String query = "select client.idOfClient from cf_clients client where (client.phone=:guardMobile or client.mobile=:guardMobile) "
                + "and client.IdOfClientGroup not in (:leaving, :deleted)"; //все клиенты с номером телефона
        Query q = emReport.createNativeQuery(query);
        q.setParameter("guardMobile", guardMobile);
        q.setParameter("leaving", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        q.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
        List<BigInteger> clients = q.getResultList();

        if (clients != null && !clients.isEmpty()){
            for(BigInteger id : clients){
                Long londId = id.longValue();
                Query q2 = emReport.createQuery("select c, cg from ClientGuardian cg, Client c "
                        + "join c.org as o "
                        + "where cg.idOfChildren = c.idOfClient and cg.idOfGuardian = :idOfGuardian "
                        + "and cg.deletedState = false and o.preordersEnabled = true");  //все дети текущего клиента
                q2.setParameter("idOfGuardian", londId);
                List list = q2.getResultList();
                if (list != null && list.size() > 0) {
                    for (Object o : list) {
                        Object[] row = (Object[])o;
                        ClientGuardian cg = (ClientGuardian) row[1];
                        ClientWithAddInfo addInfo = new ClientWithAddInfo();
                        if (!cg.isDisabled()) {
                            addInfo.setClientCreatedFrom(cg.getCreatedFrom());
                            addInfo.setInformedSpecialMenu(ClientManager.getInformedSpecialMenu((Session) emReport.getDelegate(), cg.getIdOfChildren(),
                                    cg.getIdOfGuardian()) ? 1 : 0);
                            //result.put((Client) row[0], cg.getCreatedFrom());
                        }
                        result.put((Client) row[0], addInfo);
                    }
                } else {
                    Query q3 = emReport.createQuery("select c from Client c "
                            + "join c.org as o "
                            + "join c.clientGroup g "
                            + "where c.idOfClient=:idOfClient and o.preordersEnabled = true "
                            + "and g.compositeIdOfClientGroup.idOfClientGroup < :employees");
                    q3.setParameter("idOfClient", londId);
                    q3.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
                    List list1 = q3.getResultList();
                    if (!list1.isEmpty()) {
                        Client c = (Client)list1.get(0);
                        ClientWithAddInfo addInfo = new ClientWithAddInfo();
                        addInfo.setClientCreatedFrom(ClientCreatedFromType.DEFAULT);
                        addInfo.setInformedSpecialMenu(0);
                        result.put(c, addInfo);
                    }
                }
            }
        }

        return result;
    }

    private Calendar getCalendar() {
        TimeZone timeZone = RuntimeContext.getInstance().getLocalTimeZone(null);
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        return c;
    }



    @Transactional(readOnly = true)
    public Map<String, Integer[]> getSpecialDates(Date today, Integer syncCountDays, Long orgId, Client client) throws Exception {
        Comparator comparator = new PreorderDateComparator();
        Map map = new TreeMap(comparator);

        Calendar c = getCalendar();
        c.setTime(today);

        Date endDate = CalendarUtils.addDays(today, syncCountDays);                   //14 календарных дней вперед

        Map<Date, Long[]> usedAmounts = existPreordersByDate(client.getIdOfClient(), today, endDate);                 //для показа есть ли предзаказы по датам
        List<SpecialDate> specialDates = DAOReadonlyService.getInstance().getSpecialDates(today, endDate, orgId);   //выходные дни по ОО в целом или ее группам
        Integer forbiddenDays = DAOUtils.getPreorderFeedingForbiddenDays(client);                                   //дни запрета редактирования
        List<ProductionCalendar> productionCalendar = DAOReadonlyService.getInstance().getProductionCalendar(today, endDate);

        int two_days = 0;
        while (c.getTimeInMillis() < endDate.getTime() ){
            Date currentDate = CalendarUtils.startOfDayInUTC(c.getTime());
            Boolean isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class).isWeekendByProductionCalendar(currentDate, productionCalendar);
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

            /*int day = CalendarUtils.getDayOfWeek(currentDate);
            Boolean isWeekendSD = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                    .isWeekendBySpecialDate(currentDate, client, specialDates); //выходной по данным таблицы SpecialDates
            if (isWeekendSD == null) { //нет данных по дню в КУД
                if (day == Calendar.SATURDAY) {
                    String groupName = DAOReadonlyService.getInstance().getClientGroupName(client);
                    isWeekend = !DAOReadonlyService.getInstance().isSixWorkWeek(orgId, groupName);
                }
            } else {
                isWeekend = isWeekendSD;
            }*/
            isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                    .isWeekendBySpecialDateAndSixWorkWeek(isWeekend, currentDate, client.getIdOfClientGroup(), client.getOrg().getIdOfOrg(), specialDates);
            if (!isWeekend) isWeekend = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                    .isHolidayByProductionCalendar(currentDate, productionCalendar); //если праздничный день по производственному календарю - то запрет редактирования

            c.add(Calendar.DATE, 1);
            map.put(CalendarUtils.dateToString(currentDate), new Integer[] {isWeekend ? 1 : 0, usedAmounts.get(currentDate) == null ? 0 : usedAmounts.get(currentDate)[0].intValue(),
                    usedAmounts.get(currentDate) == null ? client.getOrg().getIdOfOrg().intValue() : usedAmounts.get(currentDate)[1].intValue()});
        }
        return map;
    }

    public List<ClientGuardian> getClientGuardian(Client child, String guardianMobile) {
        Query query = emReport.createQuery("select cg from ClientGuardian cg, Client g "
                + "where g.idOfClient = cg.idOfGuardian and cg.idOfChildren = :idOfChildren and g.mobile = :guardianMobile");
        query.setParameter("idOfChildren", child.getIdOfClient());
        query.setParameter("guardianMobile", guardianMobile);
        return query.getResultList();
    }

    @Transactional
    public void deletePreordersByClient(Client client) throws Exception {
        Query query = em.createQuery("select pc from PreorderComplex pc where pc.client = :client and pc.preorderDate > :date and pc.deletedState = false");
        query.setParameter("client", client);
        query.setParameter("date", new Date());
        List<PreorderComplex> list = query.getResultList();
        if (list.size() == 0) return;
        /*Integer days = DAOUtils.getPreorderFeedingForbiddenDays(client.getOrg().getIdOfOrg());
        if (days != null) days++;
        else {
            days = PreorderComplex.DEFAULT_FORBIDDEN_DAYS + 1;
        }
        Date dateFrom = CalendarUtils.startOfDay(CalendarUtils.addDays(new Date(), days));*/
        Date dateFrom = getStartDateForGeneratePreordersInternal(client);
        long nextVersion = nextVersionByPreorderComplex();
        for (PreorderComplex preorderComplex : list) {
            preorderComplex.deleteByChangeOrg(nextVersion, preorderComplex.getPreorderDate().after(dateFrom));
            for (PreorderMenuDetail preorderMenuDetail : preorderComplex.getPreorderMenuDetails()) {
                preorderMenuDetail.deleteByChangeOrg(nextVersion, preorderComplex.getPreorderDate().after(dateFrom));
                em.merge(preorderMenuDetail);
            }
            em.merge(preorderComplex);
        }
        query = em.createQuery("update RegularPreorder set deletedState = true, lastUpdate = :date where client = :client and endDate > :date");
        query.setParameter("date", new Date());
        query.setParameter("client", client);
        query.executeUpdate();
    }

    @Transactional
    public Integer getMenuSyncCountDays(Long idOfOrg) {
        Org org = emReport.find(Org.class, idOfOrg);
        if (org.getConfigurationProvider() == null) {
            return PreorderComplex.DEFAULT_MENU_SYNC_COUNT_DAYS;
        } else {
            return org.getConfigurationProvider().getMenuSyncCountDays();
        }
    }

    public Org findOrg(long idOfOrg) {
        return emReport.find(Org.class, idOfOrg);
    }

    public List<WtMenu> getWtMenuByDates(Date beginDate, Date endDate, Org org) {
        Query query = emReport.createQuery("SELECT DISTINCT menu FROM WtMenu menu "
                + "LEFT JOIN FETCH menu.wtOrgGroup orgGroup "
                + "left join fetch menu.menuGroupMenus "
                + "left join fetch menu.orgs "
                + "WHERE menu.beginDate <= :beginDate AND menu.endDate >= :endDate "
                + "AND menu.deleteState = 0 "
                + "AND (:org IN elements(menu.orgs) OR :org IN elements(orgGroup.orgs))");
        query.setParameter("beginDate", beginDate, TemporalType.DATE);
        query.setParameter("endDate", endDate, TemporalType.DATE);
        query.setParameter("org", org);
        return query.getResultList();
    }

    public List<WtDish> getWtDishesByMenuAndDates(WtMenu menu, Date startDate, Date endDate) {
        List<Long> groupTypes = Arrays.asList(3L, 4L, 5L); // Буфет, Коммерческое питание, Все
        List<WtDish> res = new ArrayList<>();

        Query query = emReport.createNativeQuery("SELECT DISTINCT dish.idofdish FROM cf_wt_dishes dish "
                + "LEFT JOIN cf_wt_dish_groupitem_relationships groups ON dish.idofdish = groups.idofdish "
                + "LEFT JOIN cf_wt_menu_group_dish_relationships mgdr ON dish.idofdish = mgdr.idofdish "
                + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgdr.idofmenumenugrouprelation = mgr.id "
                + "WHERE groups.idofgroupitem IN (:groupTypes) AND mgr.idofmenu = :idOfMenu "
                + "AND mgr.deletestate = 0 AND dish.deletestate = 0 "
                + "AND ((dish.dateofbeginmenuincluding <= :startDate AND dish.dateofendmenuincluding >= :endDate) "
                + "OR (dish.dateofbeginmenuincluding IS NULL AND dish.dateofendmenuincluding >= :endDate) "
                + "OR (dish.dateofbeginmenuincluding <= :startDate AND dish.dateofendmenuincluding IS NULL) "
                + "OR (dish.dateofbeginmenuincluding IS NULL AND dish.dateofendmenuincluding IS NULL))");

        query.setParameter("idOfMenu", menu.getIdOfMenu());
        query.setParameter("startDate", startDate, TemporalType.DATE);
        query.setParameter("endDate", endDate, TemporalType.DATE);
        query.setParameter("groupTypes", groupTypes);

        List<BigInteger> tempRes = query.getResultList();
        if (tempRes != null && !tempRes.isEmpty()) {
            for (BigInteger id : tempRes) {
                WtDish dish = getWtDishById(id.longValue());
                if (dish != null) {
                    res.add(dish);
                }
            }
        }
        return res;
    }

    public Set<WtDiscountRule> getWtDiscountRules(Set<CategoryDiscount> categoriesDiscount) {
        Set<WtDiscountRule> wtDiscountRuleSet = new HashSet<>();
        if (categoriesDiscount != null && categoriesDiscount.size() > 0) {
            for (CategoryDiscount categoryDiscount : categoriesDiscount) {
                Query query = emReport.createQuery("SELECT discountRule FROM WtDiscountRule discountRule "
                        + "WHERE :categoryDiscount IN ELEMENTS(discountRule.categoryDiscounts)");
                query.setParameter("categoryDiscount", categoryDiscount);
                List<WtDiscountRule> res = query.getResultList();
                if (res != null && res.size() > 0) {
                    wtDiscountRuleSet.addAll(res);
                }
            }
        }
        return wtDiscountRuleSet;
    }

    public Set<WtDiscountRule> getWtDiscountRulesByCategoryOrg(Set<CategoryDiscount> categoriesDiscount, Org org) {
        Set<WtDiscountRule> wtDiscountRuleSet = new HashSet<>();
        Set<BigInteger> rulesIds = new HashSet<>();
        if (categoriesDiscount != null && categoriesDiscount.size() > 0) {
            for (CategoryDiscount categoryDiscount : categoriesDiscount) {
                Query query = emReport.createNativeQuery("select d.idofrule from cf_wt_discountrules d "
                        + "left join cf_wt_discountrules_categoryorg dco on dco.idofrule = d.idofrule "
                        + "left join cf_categoryorg_orgs cor on cor.idofcategoryorg = dco.idofcategoryorg "
                        + "left join cf_wt_discountrules_categorydiscount dc on dc.idofrule = d.idofrule "
                        + "where (cor.idoforg = :idoforg or cor.idoforg is null) and dc.idofcategorydiscount = :discount");
                query.setParameter("discount", categoryDiscount.getIdOfCategoryDiscount());
                query.setParameter("idoforg", org.getIdOfOrg());
                List<BigInteger> res = query.getResultList();
                if (res != null && res.size() > 0) {
                    rulesIds.addAll(res);
                }
            }
        }
        if (rulesIds.size() > 0) {
            WtDiscountRule rule;
            for (BigInteger id : rulesIds) {
                rule = getWtDiscountRuleById(id.longValue());
                if (rule != null) {
                    wtDiscountRuleSet.add(rule);
                }
            }
        }
        return wtDiscountRuleSet;
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

    public Set<WtDiscountRule> getWtElemDiscountRules(Org org) {
        Set<WtDiscountRule> wtDiscountRuleSet = new HashSet<>();
        Set<BigInteger> rulesIds = new HashSet<>();
        Query query = emReport.createNativeQuery("select d.idofrule from cf_wt_discountrules d "
                + "left join cf_wt_discountrules_categoryorg dco on dco.idofrule = d.idofrule "
                + "left join cf_categoryorg_orgs cor on cor.idofcategoryorg = dco.idofcategoryorg "
                + "left join cf_wt_discountrules_categorydiscount dc on dc.idofrule = d.idofrule "
                + "where cor.idoforg = :idoforg and dc.idofcategorydiscount = :discount "
                + "and d.idofrule in (select dc2.idofrule from cf_wt_discountrules_categorydiscount dc2 "
                + "group by dc2.idofrule having count (dc2.idofrule) = 1)");
        query.setParameter("discount", ELEM_DISCOUNT_ID);
        query.setParameter("idoforg", org.getIdOfOrg());
        List<BigInteger> res = query.getResultList();
        if (res != null && res.size() > 0) {
            rulesIds.addAll(res);
        }
        if (rulesIds.size() > 0) {
            WtDiscountRule rule;
            for (BigInteger id : rulesIds) {
                rule = getWtDiscountRuleById(id.longValue());
                if (rule != null) {
                    wtDiscountRuleSet.add(rule);
                }
            }
        }
        return wtDiscountRuleSet;
    }

    public Set<WtDiscountRule> getWtDiscountRuleBySecondDiscount(Set<WtDiscountRule> wtDiscountRuleSet,
            CategoryDiscount discount) {
        Set<WtDiscountRule> wtDiscountRules = new HashSet<>();
        if (discount == null) {
            return null;
        }
        if (wtDiscountRuleSet != null && wtDiscountRuleSet.size() > 0) {
            for (WtDiscountRule rule : wtDiscountRuleSet) {
                Query query = emReport.createQuery("SELECT discountRule FROM WtDiscountRule discountRule "
                        + "WHERE :discount IN ELEMENTS(discountRule.categoryDiscounts) AND discountRule = :rule");
                query.setParameter("discount", discount);
                query.setParameter("rule", rule);
                List<WtDiscountRule> res = query.getResultList();
                if (res != null && res.size() > 0) {
                    wtDiscountRules.addAll(res);
                }
            }
        }
        return wtDiscountRules;
    }

    public Set<WtDiscountRule> getWtDiscountRuleByTwoDiscounts(Set<WtDiscountRule> wtDiscountRuleSet,
            CategoryDiscount firstDiscount, CategoryDiscount secondDiscount) {
        Set<WtDiscountRule> wtDiscountRules = new HashSet<>();
        if (firstDiscount == null || secondDiscount == null) {
            return null;
        }
        if (wtDiscountRuleSet != null && wtDiscountRuleSet.size() > 0) {
            for (WtDiscountRule rule : wtDiscountRuleSet) {
                Query query = emReport.createQuery("SELECT discountRule FROM WtDiscountRule discountRule "
                        + "WHERE discountRule = :rule AND (:firstDiscount IN ELEMENTS(discountRule.categoryDiscounts) "
                        + "OR :secondDiscount IN ELEMENTS(discountRule.categoryDiscounts))");
                query.setParameter("firstDiscount", firstDiscount);
                query.setParameter("secondDiscount", secondDiscount);
                query.setParameter("rule", rule);
                List<WtDiscountRule> res = query.getResultList();
                if (res != null && res.size() > 0) {
                    wtDiscountRules.addAll(res);
                }
            }
        }
        return wtDiscountRules;
    }

    public CategoryDiscount getElemDiscount() {
        return getCategoryDiscountById(ELEM_DISCOUNT_ID);
    }

    public CategoryDiscount getMiddleDiscount() {
        return getCategoryDiscountById(MIDDLE_DISCOUNT_ID);
    }

    public CategoryDiscount getHighDiscount() {
        return getCategoryDiscountById(HIGH_DISCOUNT_ID);
    }

    public CategoryDiscount getReserveDiscount() {
        return getCategoryDiscountById(RESERVE_DISCOUNT_ID);
    }

    public CategoryDiscount getCategoryDiscountById(Long id) {
        Query query = emReport.createQuery("SELECT cd FROM CategoryDiscount cd "
                + "WHERE cd.idOfCategoryDiscount = :id");
        query.setParameter("id", id);
        List<CategoryDiscount> res = query.getResultList();
        if (res != null && res.size() > 0) {
            return res.get(0);
        }
        return null;
    }

    public WtComplexGroupItem getWtComplexGroupItem() {
        Long complexGroupId = 2L; // Платное питание
        Query query = emReport.createQuery("SELECT complexGroup FROM WtComplexGroupItem complexGroup "
                + "WHERE complexGroup.idOfComplexGroupItem = :complexGroupId");
        query.setParameter("complexGroupId", complexGroupId);
        return (WtComplexGroupItem) query.getSingleResult();
    }

    public List<WtAgeGroupItem> getWtAgeGroupItems(Client client, Set<CategoryDiscount> categoriesDiscount) {
        List<WtAgeGroupItem> ageGroupList = new ArrayList<>();
        Set<Long> ageGroupIds = new HashSet<>();
        if (client.getAgeTypeGroup() != null && client.getParallel() != null) {
            String ageGroupDesc = client.getAgeTypeGroup().toLowerCase();
            String parallelDesc = client.getParallel().toLowerCase();
            for (CategoryDiscount discount : categoriesDiscount) {
                if (ageGroupDesc.startsWith("дошкол")) {
                    if (discount.getDescription().contains("3-7")) {
                        ageGroupIds.add(2L); // 3-7
                    }
                    if (discount.getDescription().contains("5-3")) {
                        ageGroupIds.add(1L); // 1, 5-3
                    }
                }
            }
            if (ageGroupDesc.startsWith("средн") && ELEMENTARY_SCHOOL.contains(parallelDesc)) {
                ageGroupIds.add(3L); // 1-4
                ageGroupIds.add(7L); // Все
            }
            if (ageGroupDesc.startsWith("средн") && MIDDLE_SCHOOL.contains(parallelDesc)) {
                ageGroupIds.add(4L); // 5-11
            }
            if (ageGroupDesc.startsWith("npo") || ageGroupDesc.startsWith("spo")) {
                ageGroupIds.add(5L); // Колледж
                if (categoriesDiscount != null) {
                    ageGroupIds.add(7L); // Все
                }
            }
            if (ageGroupIds.size() > 0) {
                for (Long id : ageGroupIds) {
                    Query query = emReport.createQuery("SELECT ageGroup FROM WtAgeGroupItem ageGroup " +
                            "WHERE ageGroup.idOfAgeGroupItem = :id");
                    query.setParameter("id", id);
                    WtAgeGroupItem res = (WtAgeGroupItem) query.getSingleResult();
                    if (res != null) {
                        ageGroupList.add(res);
                    }
                }
            }
        }
        return ageGroupList;
    }

    public Set<WtComplex> getWtComplexesByComplexGroupAndAgeGroups (Date startDate, Date endDate,
            WtComplexGroupItem complexGroup, List<WtAgeGroupItem> ageGroupList) {
        Set<WtComplex> wtComplexes = new HashSet<>();
        if (ageGroupList != null && ageGroupList.size() > 0) {
            for (WtAgeGroupItem ageGroup : ageGroupList) {
                Set<WtComplex> res = getWtComplexesByComplexGroupAndAge(startDate, endDate, complexGroup, ageGroup);
                if (res != null && res.size() > 0) {
                    wtComplexes.addAll(res);
                }
            }
        } else {
            return getWtComplexesByComplexGroup(startDate, endDate, complexGroup);
        }
        return wtComplexes;
    }

    public Set<WtComplex> getPaidWtComplexesByAgeGroups (Date startDate, Date endDate, Set<Long> ageGroupIds, Org org) {
        Set<WtComplex> wtComplexes = new HashSet<>();
        if (ageGroupIds != null && ageGroupIds.size() > 0) {
            for (Long ageGroupId : ageGroupIds) {
                Query query = emReport.createQuery("SELECT complex FROM WtComplex complex "
                        + "LEFT JOIN complex.wtOrgGroup orgGroup "
                        + "WHERE complex.wtAgeGroupItem.idOfAgeGroupItem = :ageGroupId AND complex.deleteState = 0 "
                        + "AND complex.beginDate <= :startDate AND complex.endDate >= :endDate "
                        + "AND (:org IN ELEMENTS(complex.orgs) or :org IN ELEMENTS(orgGroup.orgs)) "
                        + "AND (complex.wtComplexGroupItem.idOfComplexGroupItem = :paidComplex OR "
                        + "complex.wtComplexGroupItem.idOfComplexGroupItem = :allComplexes)");
                query.setParameter("ageGroupId", ageGroupId);
                query.setParameter("org", org);
                query.setParameter("startDate", startDate, TemporalType.DATE);
                query.setParameter("endDate", endDate, TemporalType.DATE);
                query.setParameter("paidComplex", PAID_COMPLEX_GROUP_ITEM_ID);
                query.setParameter("allComplexes", ALL_COMPLEX_GROUP_ITEM_ID);
                List<WtComplex> res = query.getResultList();
                if (res != null && res.size() > 0) {
                    wtComplexes.addAll(res);
                }
            }
        }
        return wtComplexes;
    }

    public Set<WtComplex> getPaidWtComplexesByAgeGroupsAndPortal(Date startDate, Date endDate, Set<Long> ageGroupIds, Org org) {
        Set<WtComplex> wtComplexes = new HashSet<>();
        if (ageGroupIds != null && ageGroupIds.size() > 0) {
            for (Long ageGroupId : ageGroupIds) {
                Query query = emReport.createQuery("SELECT complex FROM WtComplex complex "
                        + "LEFT JOIN complex.wtOrgGroup orgGroup "
                        + "WHERE complex.isPortal = true AND complex.deleteState = 0 "
                        + "AND complex.wtAgeGroupItem.idOfAgeGroupItem = :ageGroupId "
                        + "AND complex.beginDate < :startDate AND complex.endDate > :endDate "
                        + "AND (:org IN ELEMENTS(complex.orgs) or :org IN ELEMENTS(orgGroup.orgs)) "
                        + "AND (complex.wtComplexGroupItem.idOfComplexGroupItem = :paidComplex OR "
                        + "complex.wtComplexGroupItem.idOfComplexGroupItem = :allComplexes)");
                query.setParameter("ageGroupId", ageGroupId);
                query.setParameter("org", org);
                query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
                query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
                query.setParameter("paidComplex", PAID_COMPLEX_GROUP_ITEM_ID);
                query.setParameter("allComplexes", ALL_COMPLEX_GROUP_ITEM_ID);
                List<WtComplex> res = query.getResultList();
                if (res != null && res.size() > 0) {
                    wtComplexes.addAll(res);
                }
            }
        }
        return wtComplexes;
    }

    public Set<WtComplex> getFreeWtComplexesByDiscountRules(Date startDate, Date endDate,
            Set<WtDiscountRule> wtDiscountRuleSet) {
        Set<WtComplex> wtComplexes = new HashSet<>();
        for (WtDiscountRule rule : wtDiscountRuleSet) {
            Query query = emReport.createQuery("select complex from WtComplex complex "
                    + "where complex.deleteState = 0 and complex.beginDate <= :startDate AND complex.endDate >= :endDate "
                    + "and :rule in elements(complex.discountRules)");
            query.setParameter("rule", rule);
            query.setParameter("startDate", startDate, TemporalType.DATE);
            query.setParameter("endDate", endDate, TemporalType.DATE);
            List<WtComplex> res = query.getResultList();
            if (res != null && res.size() > 0) {
                wtComplexes.addAll(res);
            }
        }
        return wtComplexes;
    }

    public Set<WtComplex> getFreeWtComplexesByRulesAndAgeGroups(Date startDate, Date endDate,
            Set<WtDiscountRule> wtDiscountRuleSet, Set<Long> ageGroupIds) {
        Set<WtComplex> wtComplexes = new HashSet<>();
        for (WtDiscountRule rule : wtDiscountRuleSet) {
            for (Long ageGroupId : ageGroupIds) {
                Query query = emReport.createQuery("select complex from WtComplex complex "
                        + "where complex.deleteState = 0 and complex.beginDate <= :startDate AND complex.endDate >= :endDate "
                        + "and complex.wtAgeGroupItem.idOfAgeGroupItem = :ageGroupId "
                        + "and :rule in elements(complex.discountRules) "
                        + "and (complex.wtComplexGroupItem.idOfComplexGroupItem = :freeComplex "
                        + "or complex.wtComplexGroupItem.idOfComplexGroupItem = :allComplexes)");
                query.setParameter("rule", rule);
                query.setParameter("startDate", startDate, TemporalType.DATE);
                query.setParameter("endDate", endDate, TemporalType.DATE);
                query.setParameter("ageGroupId", ageGroupId);
                query.setParameter("freeComplex", FREE_COMPLEX_GROUP_ITEM_ID);
                query.setParameter("allComplexes", ALL_COMPLEX_GROUP_ITEM_ID);
                List<WtComplex> res = query.getResultList();
                if (res != null && res.size() > 0) {
                    wtComplexes.addAll(res);
                }
            }
        }
        return wtComplexes;
    }

    public Set<WtComplex> getWtComplexesByComplexGroup(Date startDate, Date endDate, WtComplexGroupItem complexGroup) {
        Set<WtComplex> wtComplexes = new HashSet<>();
        Query query = emReport.createNativeQuery(
                "select c.idofcomplex from cf_wt_complexes c left join cf_wt_discountrules_complexes dc "
                        + "on dc.idofcomplex = c.idofcomplex where c.deleteState = 0 "
                        + "and c.beginDate < :startDate AND c.endDate > :endDate "
                        + "and c.idofcomplexgroupitem = :complexGroupId "
                        + "and dc.idofcomplex is null and c.is_portal = true");
        query.setParameter("complexGroupId", complexGroup.getIdOfComplexGroupItem());
        query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
        query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
        List<BigInteger> res = query.getResultList();
        if (res != null && !res.isEmpty()) {
            WtComplex complex;
            for (BigInteger id : res) {
                complex = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .getWtComplexById(id.longValue());
                if (complex != null) {
                    wtComplexes.add(complex);
                }
            }
        }
        return wtComplexes;
    }

    public Set<WtComplex> getWtComplexesByComplexGroupAndAge(Date startDate, Date endDate,
            WtComplexGroupItem complexGroup, WtAgeGroupItem ageGroup) {
        Set<WtComplex> wtComplexes = new HashSet<>();
        Query query = emReport.createNativeQuery(
                "select c.idofcomplex from cf_wt_complexes c left join cf_wt_discountrules_complexes dc "
                        + "on dc.idofcomplex = c.idofcomplex where c.deleteState = 0 "
                        + "and c.beginDate < :startDate AND c.endDate > :endDate "
                        + "and c.idofcomplexgroupitem = :complexGroupId " + "and c.idofagegroupitem = :ageGroupId "
                        + "and dc.idofcomplex is null and c.is_portal = true");
        query.setParameter("complexGroupId", complexGroup.getIdOfComplexGroupItem());
        query.setParameter("ageGroupId", ageGroup.getIdOfAgeGroupItem());
        query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
        query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
        List<BigInteger> res = query.getResultList();
        if (res != null && !res.isEmpty()) {
            WtComplex complex;
            for (BigInteger id : res) {
                complex = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .getWtComplexById(id.longValue());
                if (complex != null) {
                    wtComplexes.add(complex);
                }
            }
        }
        return wtComplexes;
    }

    public Set<WtComplex> getWtComplexesByDiscountRulesAndAgeGroups(Date startDate, Date endDate,
            Set<WtDiscountRule> wtDiscountRuleSet, List<WtAgeGroupItem> ageGroupList) {
        Set<WtComplex> wtComplexes = new HashSet<>();
        for (WtDiscountRule rule : wtDiscountRuleSet) {
            if (ageGroupList != null && ageGroupList.size() > 0) {
                for (WtAgeGroupItem ageGroup : ageGroupList) {
                    List<WtComplex> res = getWtComplexesByDiscountRuleAndAge(startDate, endDate, rule, ageGroup);
                    if (res != null && res.size() > 0) {
                        wtComplexes.addAll(res);
                    }
                }
            } else {
                List<WtComplex> res = getWtComplexesByDiscountRule(startDate, endDate, rule);
                if (res != null && res.size() > 0) {
                    wtComplexes.addAll(res);
                }
            }
        }
        return wtComplexes;
    }

    public List<WtComplex> getWtComplexesByDiscountRuleAndAge(Date startDate, Date endDate,
            WtDiscountRule wtDiscountRule, WtAgeGroupItem ageGroup) {
        Query query = emReport.createQuery("SELECT complex FROM WtComplex complex WHERE complex.deleteState = 0 "
                        + "AND complex.beginDate < :startDate AND complex.endDate > :endDate "
                        + "AND :rule IN ELEMENTS(complex.discountRules) "
                        + "AND complex.wtAgeGroupItem = :ageGroup");
        query.setParameter("ageGroup", ageGroup);
        query.setParameter("rule", wtDiscountRule);
        query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
        query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
        return query.getResultList();
    }

    public List<WtComplex> getWtComplexesByDiscountRule(Date startDate, Date endDate, WtDiscountRule wtDiscountRule) {
        Query query = emReport.createQuery("SELECT complex FROM WtComplex complex WHERE complex.deleteState = 0 "
                + "AND complex.beginDate < :startDate AND complex.endDate > :endDate "
                + "AND :rule IN ELEMENTS(complex.discountRules)");
        query.setParameter("rule", wtDiscountRule);
        query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
        query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
        return query.getResultList();
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
            List<WtComplexExcludeDays> excludeDays = DAOReadonlyService.getInstance().getExcludeDaysByWtComplex(wtComplex);
            while (currentDate.getTime() <= date.getTime()) {
                calendar.setTime(currentDate);
                // смотрим рабочую неделю
                if ((wtComplex.getCycleMotion() == 0 &&     // 5-дневная рабочая неделя
                        calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY &&
                        calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) || (wtComplex.getCycleMotion() == 1 &&
                        // 6-дневная рабочая неделя
                        calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)) {
                    // проверка, выпадает ли день на выходные
                    Boolean isHoliday = DAOReadonlyService.getInstance().checkExcludeDays(currentDate, excludeDays);
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
                res = getWtComplexItemByComplexAndCycleDay(wtComplex, cycleDay);
            }
        }
        return res;
    }

    public boolean isAvailableDate(Client client, Org org, Date date) {
        ClientGroup clientGroup = client.getClientGroup();
        Calendar calendar = Calendar.getInstance();
        // проверка по производственному календарю
        Boolean isWorkingDay = DAOReadonlyService.getInstance().checkWorkingDay(date);
        if (isWorkingDay != null) {
            if (isWorkingDay) {
                // проверка по календарю учебных дней
                Boolean isLearningDay = DAOReadonlyService.getInstance()
                        .checkLearningDayByOrgAndClientGroup(date, org, clientGroup);
                if (isLearningDay != null && !isLearningDay) {
                    return false;
                }
                if (isLearningDay == null) {
                    // проверка на субботу + 6-дневную рабочую неделю
                    calendar.setTime(date);
                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        Boolean isSixDaysWorkingWeek = DAOReadonlyService.getInstance()
                                .isSixDaysWorkingWeek(org, clientGroup.getGroupName());
                        if (isSixDaysWorkingWeek != null && !isSixDaysWorkingWeek) {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        } else {
            Boolean isLearningDay = DAOReadonlyService.getInstance()
                    .checkLearningDayByOrgAndClientGroup(date, org, clientGroup);
            if (isLearningDay != null && !isLearningDay) {
                return false;
            }
        }
        return true;
    }

    public List<WtComplex> getWtComplexesByDates(Date beginDate, Date endDate, Org org) {
        Query query = emReport.createQuery("SELECT complex FROM WtComplex complex "
                + "LEFT JOIN complex.wtOrgGroup orgGroup "
                + "WHERE complex.beginDate <= :beginDate AND complex.endDate >= :endDate "
                + "AND complex.deleteState = 0 "
                + "AND (:org IN ELEMENTS(complex.orgs) or :org IN ELEMENTS(orgGroup.orgs)) ");
        query.setParameter("beginDate", beginDate, TemporalType.DATE);
        query.setParameter("endDate", endDate, TemporalType.DATE);
        query.setParameter("org", org);
        return query.getResultList();
    }

    public List<WtDish> getWtDishesByComplex(WtComplex complex) {
        Query query = emReport.createQuery("SELECT dish FROM WtDish dish LEFT JOIN dish.complexItems items "
                + "where items.wtComplex = :complex and dish.deleteState = 0");
        query.setParameter("complex", complex);
        return query.getResultList();
    }

    public WtDish getWtDishById(Long idOfDish) {
        Query query = emReport.createQuery("SELECT dish FROM WtDish dish "
                + "where dish.idOfDish = :idOfDish");
        query.setParameter("idOfDish", idOfDish);
        List<WtDish> res = query.getResultList();
        if (res != null && res.size() > 0) {
            return res.get(0);
        }
        return null;
    }

    public WtComplex getWtComplexById(Long id) {
        try {
            Query query = emReport.createQuery("SELECT complex from WtComplex complex "
                    + "where complex.idOfComplex = :id");
            query.setParameter("id", id);
            return (WtComplex) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

        public WtDiscountRule getWtDiscountRuleById (Long id){
            try {
                Query query = emReport
                        .createQuery("SELECT rule from WtDiscountRule rule " + "where rule.idOfRule = :id");
                query.setParameter("id", id);
                return (WtDiscountRule) query.getSingleResult();
            } catch (Exception e) {
                return null;
            }
        }

    public List<WtDish> getWtDishesByComplexAndDates(WtComplex complex, Date startDate, Date endDate) {
        Query query = emReport.createQuery("SELECT DISTINCT dish FROM WtDish dish "
                + "LEFT JOIN dish.complexItems complexItems "
                + "WHERE complexItems.wtComplex = :complex "
                + "AND dish.deleteState = 0 "
                + "AND ((dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding >= :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding >= :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding IS NULL) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding IS NULL))");
        query.setParameter("complex", complex);
        query.setParameter("startDate", startDate, TemporalType.DATE);
        query.setParameter("endDate", endDate, TemporalType.DATE);
        return (List<WtDish>) query.getResultList();
    }

    public WtComplexesItem getWtComplexItemByComplexAndCycleDay(WtComplex complex, Integer cycleDay) {
        Query query = emReport.createQuery("SELECT ci FROM WtComplexesItem ci "
                + "WHERE ci.wtComplex = :complex AND ci.cycleDay = :cycleDay");
        query.setParameter("complex", complex);
        query.setParameter("cycleDay", cycleDay);
        List<WtComplexesItem> res = query.getResultList();
        if (res != null && res.size() > 0) {
            return res.get(0);
        }
        return null;
    }
}
