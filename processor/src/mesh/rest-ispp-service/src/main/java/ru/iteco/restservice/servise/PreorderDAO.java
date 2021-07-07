package ru.iteco.restservice.servise;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.restservice.controller.menu.request.PreorderDishInfo;
import ru.iteco.restservice.controller.menu.request.RegularPreorderRequest;
import ru.iteco.restservice.db.repo.readonly.*;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ProductionCalendar;
import ru.iteco.restservice.model.SpecialDate;
import ru.iteco.restservice.model.enums.EntityStateType;
import ru.iteco.restservice.model.enums.PreorderMobileGroupOnCreateType;
import ru.iteco.restservice.model.enums.PreorderState;
import ru.iteco.restservice.model.enums.RegularPreorderState;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;
import ru.iteco.restservice.model.preorder.RegularPreorder;
import ru.iteco.restservice.model.wt.WtCategoryItem;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtComplexesItem;
import ru.iteco.restservice.model.wt.WtDish;
import ru.iteco.restservice.servise.data.PreorderComplexChangeData;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class PreorderDAO {
    private final Logger logger = LoggerFactory.getLogger(PreorderDAO.class);

    @PersistenceContext(name = "writableEntityManager", unitName = "writablePU")
    private EntityManager entityManager;

    @Autowired
    ComplexService complexService;
    @Autowired
    WtComplexReadOnlyRepo complexRepo;
    @Autowired
    WtDishReadOnlyRepo dishRepo;
    @Autowired
    PreorderMenuDetailReadOnlyRepo pmdRepo;
    @Autowired
    PreorderComplexReadOnlyRepo pcRepo;
    @Autowired
    SpecialDateReadOnlyRepo specialDateRepo;
    @Autowired
    ProductionCalendarReadOnlyRepo productionCalendarRepo;
    @Autowired
    SpecialDateReadOnlyRepo sdRepo;
    @Autowired
    ClientGroupReadOnlyRepo cgroupRepo;
    @Autowired
    ClientReadOnlyRepo clientRepo;

    @Autowired
    PreorderService preorderService;

    @Transactional
    public PreorderComplex createPreorder(Client client, Date startDate, Date endDate, Long complexId, Integer amount, Long version,
                               String guardianMobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate, boolean isDishMode) {
        WtComplex complex = complexRepo.findById(complexId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден комплекс с идентификатором %s", complexId)));
        WtComplexesItem complexesItem = complexService.getWtComplexItemByCycle(complex, startDate);

        PreorderComplex preorderComplex = new PreorderComplex(client, endDate, complex, amount, version,
                guardianMobile, mobileGroupOnCreate);

        if (!isDishMode) {
            Set<PreorderMenuDetail> preorderMenuDetails = new HashSet<>();
            for (WtDish wtDish : complexesItem.getDishes()) {
                if (wtDish.getDeleteState().equals(1)) continue;
                String groupName = getMenuGroupByWtDishAndCategories(wtDish);
                PreorderMenuDetail preorderMenuDetail = new PreorderMenuDetail(preorderComplex, wtDish, client, endDate, 0,
                        groupName, guardianMobile, mobileGroupOnCreate);
                preorderMenuDetails.add(preorderMenuDetail);
            }
            preorderComplex.setPreorderMenuDetails(preorderMenuDetails);
        }
        return entityManager.merge(preorderComplex);
    }

    @Transactional
    public void editPreorder(PreorderComplex preorderComplex, Integer amount, long version) {
        preorderComplex.editAmount(amount, version);
        entityManager.merge(preorderComplex);
    }

    @Transactional
    public void deletePreorder(PreorderComplex preorderComplex, String guardianMobile, long version) {
        for (PreorderMenuDetail pmd : preorderComplex.getPreorderMenuDetails()) {
            pmd.delete(guardianMobile);
            entityManager.merge(pmd);
        }
        preorderComplex.deleteByReason(version, true, PreorderState.OK);
        entityManager.merge(preorderComplex);
    }

    private PreorderMenuDetail createPreorderMenuDetail(PreorderComplex preorderComplex, Long dishId, Integer amount, String guardianMobile) {
        WtDish wtDish = dishRepo.findById(dishId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено блюдо с идентификатором %s", dishId)));
        String groupName = getMenuGroupByWtDishAndCategories(wtDish);
        return new PreorderMenuDetail(preorderComplex, wtDish, preorderComplex.getClient(),
                preorderComplex.getPreorderDate(), amount, groupName, guardianMobile, preorderComplex.getMobileGroupOnCreate());
    }

    @Transactional
    public List<PreorderMenuDetail> createPreorderMenuDetail(PreorderComplexChangeData data, Long complexId, List<PreorderDishInfo> dishes,
                                                       String guardianMobile, long version) {
        List<PreorderMenuDetail> result = new ArrayList<>();
        PreorderComplex preorderComplex = pcRepo.getPreorderComplexesByClientDateAndComplexId(data.getClient(), complexId.intValue(),
                data.getStartDate(), data.getEndDate());
        if (dishes.size() > 0) {
            if (preorderComplex == null) {
                preorderComplex = createPreorder(data.getClient(), data.getStartDate(), data.getEndDate(), complexId, 0,
                        version, guardianMobile, data.getMobileGroupOnCreate(), true);
            }
            entityManager.merge(preorderComplex);
        }
        for (PreorderDishInfo info : dishes) {
            WtDish wtDish = dishRepo.findById(info.getDishId())
                    .orElseThrow(() -> new NotFoundException(String.format("Не найдено блюдо с идентификатором %s", info.getDishId())));

            PreorderMenuDetail preorderMenuDetail = pmdRepo.getPreorderMenuDetailByPreorderComplexAndDishId(preorderComplex, info.getDishId());
            if (preorderMenuDetail != null) {
                throw new IllegalArgumentException("У данного клиента уже существует предзаказ на выбранную дату и блюдо");
            }

            String groupName = getMenuGroupByWtDishAndCategories(wtDish);
            preorderMenuDetail = new PreorderMenuDetail(preorderComplex, wtDish, preorderComplex.getClient(),
                    preorderComplex.getPreorderDate(), info.getAmount(), groupName, guardianMobile, preorderComplex.getMobileGroupOnCreate());
            preorderComplex.updateWithVersion(version);

            PreorderMenuDetail pmd = entityManager.merge(preorderMenuDetail);
            result.add(pmd);
        }
        return result;

    }

    @Transactional
    public List<PreorderMenuDetail> editPreorderMenuDetail(Long contractId, String guardianMobile, Long preorderId, List<PreorderDishInfo> dishes, long version) throws Exception {
        List<PreorderMenuDetail> result = new ArrayList<>();
        Client client = clientRepo.getClientByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        PreorderComplex preorderComplex = pcRepo.getPreorderComplexesWithDetails(preorderId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден предзаказ с идентификатором %s", preorderId)));
        if (!preorderComplex.getClient().equals(client)) {
            throw new IllegalArgumentException("Предзаказ на блюдо не принадлежит данному клиенту");
        }
        if (!preorderService.isEditedDay(preorderComplex.getPreorderDate(), client)) throw new IllegalArgumentException("День недоступен для редактирования предзаказа");

        for (PreorderDishInfo info : dishes) {
            PreorderMenuDetail preorderMenuDetail = pmdRepo.getPreorderMenuDetailByPreorderComplexAndDishId(preorderComplex, info.getDishId());
            if (preorderMenuDetail != null) {
                if (info.getAmount() > 0)
                    preorderMenuDetail.setAmount(info.getAmount());
                else
                    preorderMenuDetail.delete(guardianMobile);
            } else if (info.getAmount() > 0) {
                preorderMenuDetail = createPreorderMenuDetail(preorderComplex, info.getDishId(), info.getAmount(), guardianMobile);
            }

            if (preorderMenuDetail != null) {
                result.add(entityManager.merge(preorderMenuDetail));
            }
        }
        boolean delete = true;
        for (PreorderMenuDetail pmd : preorderComplex.getPreorderMenuDetails()) {
            if (pmd.getDeletedState().equals(0)) {
                delete = false;
                break;
            }
        }
        if (delete) {
            preorderComplex.deleteByReason(version, true, PreorderState.OK);
        } else {
            preorderComplex.updateWithVersion(version);
        }
        entityManager.merge(preorderComplex);
        return result;
    }

    @Transactional
    public RegularPreorder createRegularPreorder(PreorderComplexChangeData data, RegularPreorderRequest regularPreorderRequest) throws Exception {
        WtComplex complex = complexRepo.findById(regularPreorderRequest.getComplexId().longValue())
                .orElseThrow(() -> new NotFoundException(String.format("Не найден комплекс с идентификатором %s", regularPreorderRequest.getComplexId())));
        WtDish dish = null;
        if (regularPreorderRequest.getDishId() != null) {
            dish = dishRepo.findById(regularPreorderRequest.getDishId())
                    .orElseThrow(() -> new NotFoundException(String.format("Не найдено блюдо с идентификатором %s", regularPreorderRequest.getDishId())));
        }

        String itemCode = (dish == null ? null : dish.getCode());
        RegularPreorder regularPreorder = new RegularPreorder(data.getClient(), regularPreorderRequest.getStartDate(),
                regularPreorderRequest.getEndDate(), itemCode, regularPreorderRequest.getComplexId(),
                regularPreorderRequest.getAmount(), complex.getName(), regularPreorderRequest.getMonday(),
                regularPreorderRequest.getTuesday(), regularPreorderRequest.getWednesday(),
                regularPreorderRequest.getThursday(), regularPreorderRequest.getFriday(), regularPreorderRequest.getSaturday(),
                complex.getPrice() == null ? 0 : (complex.getPrice().multiply(BigDecimal.valueOf(100))).longValue(),
                regularPreorderRequest.getGuardianMobile(),
                RegularPreorderState.CHANGE_BY_USER,
                data.getMobileGroupOnCreate(), dish == null ? null : dish.getIdOfDish());

        RegularPreorder rp = entityManager.merge(regularPreorder);
        createPreordersFromRegular(rp, true);
        return rp;
    }

    @Transactional
    public RegularPreorder editRegularPreorder(RegularPreorder regularPreorder, RegularPreorderRequest regularPreorderRequest) throws Exception {
        regularPreorder.setStartDate(RegularPreorder.convertDate(regularPreorderRequest.getStartDate()));
        regularPreorder.setEndDate(RegularPreorder.convertDate(regularPreorderRequest.getEndDate()));
        regularPreorder.setAmount(regularPreorderRequest.getAmount());
        regularPreorder.setMonday(regularPreorderRequest.getMonday() ? 1 : 0);
        regularPreorder.setTuesday(regularPreorderRequest.getTuesday() ? 1 : 0);
        regularPreorder.setWednesday(regularPreorderRequest.getWednesday() ? 1 : 0);
        regularPreorder.setThursday(regularPreorderRequest.getThursday() ? 1 : 0);
        regularPreorder.setFriday(regularPreorderRequest.getFriday() ? 1 : 0);
        regularPreorder.setSaturday(regularPreorderRequest.getSaturday() ? 1 : 0);
        regularPreorder.setLastUpdate(new Date());
        RegularPreorder rp = entityManager.merge(regularPreorder);
        createPreordersFromRegular(rp, false);
        return rp;
    }

    @Transactional
    public void deleteRegularPreorder(RegularPreorder regularPreorder) throws Exception {
        regularPreorder.setDeletedState(1);
        regularPreorder.setLastUpdate(new Date());
        entityManager.merge(regularPreorder);
        deleteGeneratedPreordersByRegular(regularPreorder, PreorderState.OK);
    }

    private void deleteGeneratedPreordersByRegular(RegularPreorder regularPreorder, PreorderState state) throws Exception {
        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        if (dateTo.after(regularPreorder.getEndDate())) dateTo = regularPreorder.getEndDate();
        Date currentDate = CalendarUtils.startOfDay(new Date());
        List<SpecialDate> specialDates = sdRepo.getSpecialDatesByOrg(currentDate, dateTo, regularPreorder.getClient().getOrg().getIdOfOrg());
        List<ProductionCalendar> productionCalendar = productionCalendarRepo.findByDayBetween(new Date(), dateTo);
        Date dateFrom = getStartDateForGeneratePreordersInternal(regularPreorder.getClient(), productionCalendar, specialDates);
        long nextVersion = pcRepo.getMaxVersion() + 1;
        Query delQuery = entityManager.createQuery("update PreorderComplex set deletedState = 1, lastUpdate = :lastUpdate, amount = 0, state = :state, version = :version "
                + "where regularPreorder = :regularPreorder and preorderDate > :dateFrom and idOfGoodsRequestPosition is null");
        delQuery.setParameter("lastUpdate", new Date());
        delQuery.setParameter("regularPreorder", regularPreorder);
        delQuery.setParameter("dateFrom", dateFrom);
        delQuery.setParameter("state", state);
        delQuery.setParameter("version", nextVersion);
        delQuery.executeUpdate();

        delQuery = entityManager.createQuery("update PreorderMenuDetail set deletedState = 1, amount = 0, state = :state "
                //+ "where preorderComplex.idOfPreorderComplex in (select idOfPreorderComplex from PreorderComplex "
                + "where regularPreorder = :regularPreorder and preorderDate > :dateFrom and idOfGoodsRequestPosition is null");
        delQuery.setParameter("regularPreorder", regularPreorder);
        delQuery.setParameter("dateFrom", dateFrom);
        delQuery.setParameter("state", state);
        delQuery.executeUpdate();
    }

    @Transactional
    public void createPreordersFromRegular(RegularPreorder regularPreorder, boolean doDeleteExisting) throws Exception {
        if (regularPreorder == null || regularPreorder.getDeletedState().equals(1)) return;

        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        if (dateTo.after(regularPreorder.getEndDate())) dateTo = regularPreorder.getEndDate();
        Date currentDate = CalendarUtils.startOfDay(new Date());
        long nextVersion = pcRepo.getMaxVersion() + 1;

        List<SpecialDate> specialDates = sdRepo.getSpecialDatesByOrg(currentDate, dateTo, regularPreorder.getClient().getOrg().getIdOfOrg());
        //List<OrgGoodRequest> preorderRequests = getOrgGoodRequests(regularPreorder.getClient().getOrg().getIdOfOrg(), currentDate, dateTo);
        List<ProductionCalendar> productionCalendar = productionCalendarRepo.findByDayBetween(new Date(), dateTo);
        currentDate = getStartDateForGeneratePreordersInternal(regularPreorder.getClient(), productionCalendar, specialDates);

        if (currentDate.before(regularPreorder.getStartDate())) currentDate = regularPreorder.getStartDate();
        /*if (doDeleteExisting) {
            deleteGeneratedPreordersByRegular((Session) em.getDelegate(), regularPreorder, PreorderState.OK);
        }*/

        //получаем список всех предзаказов по регуляру (в т.ч. удаленные)
        List<PreorderComplex> preorderComplexes = pcRepo.getPreorderComplexesByRegular(regularPreorder.getClient(), regularPreorder);

        //все предзаказы клиента за период,
        List<PreorderComplex> preordersForClient = pcRepo.getPreorderComplexesByClient(regularPreorder.getClient(), regularPreorder.getIdOfComplex(),
                currentDate, dateTo);

        currentDate = CalendarUtils.startOfDayInUTC(CalendarUtils.addHours(currentDate, 12));

        while (currentDate.before(dateTo) || currentDate.equals(dateTo)) {
            logger.info(String.format("Processing regular preorder %s on date %s...", regularPreorder, CalendarUtils.dateToString(currentDate)));
            /*if (orgGoodRequestExists(preorderRequests, currentDate)) {
                //если на тек день есть заявка, то этот день пропускаем
                logger.info("OrgGoodRequest exists");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }*/
            Boolean isWeekend = preorderService.isWeekendByProductionCalendar(currentDate, productionCalendar);
            isWeekend = preorderService.isWeekendBySpecialDateAndSixWorkWeek(
                    isWeekend, currentDate, regularPreorder.getClient().getClientGroup().getClientGroupId().getIdOfClientGroup(),
                    regularPreorder.getClient().getOrg().getIdOfOrg(), specialDates);
            if (!isWeekend) isWeekend = preorderService.isHolidayByProductionCalendar(currentDate, productionCalendar);
            PreorderComplex pc = findPreorderComplexOnDate(preorderComplexes, currentDate, 0);
            if (isWeekend && pc != null) {
                logger.info("Delete by change of calendar");
                deletePreorderComplex(pc, nextVersion, PreorderState.CHANGED_CALENDAR);
            }

            //генерить ли предзаказ по дню недели в регулярном заказе
            boolean doGenerate = doGenerate(currentDate, regularPreorder);
            if (!doGenerate && pc != null) {
                logger.info("Delete by user - no generate day in regular");
                deletePreorderComplex(pc, nextVersion, PreorderState.OK);
            }
            if (isWeekend || !doGenerate) {
                logger.info("Weekend or not generated day");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            //проверяем, нет ли на дату удаленного пользователем предзаказа
            /*PreorderComplex pc2 = findPreorderComplexOnDate(preordersForClient, currentDate, null);
            if (pc2 != null) {
                if (pc2.getDeletedState().equals(1) && pc2.getState() == PreorderState.OK && pc2.getLastUpdate().after(regularPreorder.getCreatedDate())) {
                    logger.info("Client deleted preorder on this date earlier");
                    currentDate = CalendarUtils.addDays(currentDate, 1);
                    continue;
                }
            }*/

            //предзаказ на комплекс
            PreorderComplex preorderComplex = findPreorderComplexOnDate(preorderComplexes, currentDate, null);
            WtDish wtDish = null;
            WtComplex wtComplex = complexRepo.getWtComplex(regularPreorder.getClient().getOrg(), regularPreorder.getIdOfComplex().longValue(),
                    CalendarUtils.startOfDay(currentDate), CalendarUtils.endOfDay(currentDate));

            if (wtComplex == null) {
                if (preorderComplex != null && preorderComplex.getDeletedState().equals(0)) {
                    //предзаказ есть, но комплекса нет - удаляем ранее сгенерированный предзаказ
                    logger.info("Deleting preorderComplex due to the complex does not exist");
                    deletePreorderComplex(preorderComplex, nextVersion, PreorderState.DELETED);
                } else {
                    logger.info("Not generate by complex not exists");
                }
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            WtComplexesItem complexItem = complexService.getWtComplexItemByCycle(wtComplex, CalendarUtils.startOfDay(currentDate));
            if (complexItem == null) {
                logger.info("No menu details / wtDishes found");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            List<WtDish> wtDishes = dishRepo
                    .getWtDishesByComplexItemAndDates(complexItem, CalendarUtils.startOfDay(currentDate),
                            CalendarUtils.endOfDay(currentDate), EntityStateType.ACTIVE);
            if (wtDishes.size() == 0) {
                logger.info("No wtDishes found");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            if ((preorderComplex == null || (preorderComplex != null && allowCreateNewPreorderComplex(preorderComplex)))
                    && !forcePreorderComplexExists(regularPreorder, currentDate)) {
                //на искомую дату нет предзаказа, надо создавать

                boolean comparePrice = !isMenuDetailPreorder(regularPreorder);
                //здесь сравниваем по цене, если заказ на комплекс, а не на блюдо
                if (!comparePrice) {
                    wtDish = dishRepo.getWtDishByItemCodeAndId(complexItem, CalendarUtils.startOfDay(currentDate),
                            CalendarUtils.endOfDay(currentDate), regularPreorder.getIdOfDish());
                    if (wtDish == null){
                        logger.info("Not found menu detail / wtDish");
                        currentDate = CalendarUtils.addDays(currentDate, 1);
                        continue;
                    }
                }
                logger.info("===Create preorder complex from regular===");
                preorderComplex = createWtPreorderComplex(regularPreorder.getIdOfComplex(), regularPreorder.getClient(),
                        currentDate, !isMenuDetailPreorder(regularPreorder) ? regularPreorder.getAmount() : 0, wtComplex,
                        nextVersion, regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
                preorderComplex.setRegularPreorder(regularPreorder);
                entityManager.persist(preorderComplex);
            } else if (preorderComplex != null
                    && doEditRegular(preorderComplex, regularPreorder.getAmount())
                    && !isMenuDetailPreorder(regularPreorder)) {
                editPreorder(preorderComplex, regularPreorder.getAmount(), nextVersion);
            } else if (!isMenuDetailPreorder(regularPreorder)) {
                logger.info("Preorder complex exists or deleted by user");
                currentDate = CalendarUtils.addDays(currentDate, 1);
                continue;
            }

            if (isMenuDetailPreorder(regularPreorder)) {
                if (preorderComplex == null) {
                    preorderComplex = pcRepo.getPreorderComplexesByClientDateAndComplexId(regularPreorder.getClient(),
                            regularPreorder.getIdOfComplex(), CalendarUtils.startOfDay(currentDate), CalendarUtils.endOfDay(currentDate));
                }
                if (preorderComplex != null && preorderComplex.getDeletedState().equals(0)) {
                    if (wtDish == null) {
                        wtDish = dishRepo.getWtDishByItemCodeAndId(complexItem, CalendarUtils.startOfDay(currentDate),
                                CalendarUtils.endOfDay(currentDate), regularPreorder.getIdOfDish());
                    }
                    if (wtDish == null) {
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

                PreorderMenuDetail preorderMenuDetail = pmdRepo.findPreorderWtDish(regularPreorder.getClient(),
                        CalendarUtils.startOfDay(currentDate), CalendarUtils.endOfDay(currentDate),
                        regularPreorder.getIdOfComplex(), wtDish.getIdOfDish());
                if (preorderMenuDetail == null || (preorderMenuDetail != null && allowCreateNewPreorderMenuDetail(preorderMenuDetail))) {
                    if (!forcePreorderWtMenuDetailExists(regularPreorder, currentDate)) {
                        //на искомую дату нет предзаказа, надо создавать
                        logger.info("===Create preorder menudetail from regular===");
                        preorderMenuDetail = createPreorderWtMenuDetail(regularPreorder.getClient(), preorderComplex,
                                wtDish, currentDate, regularPreorder.getAmount(), regularPreorder.getMobile(),
                                regularPreorder.getMobileGroupOnCreate());
                        preorderMenuDetail.setRegularPreorder(regularPreorder);
                        entityManager.persist(preorderMenuDetail);

                        Set<PreorderMenuDetail> set = createPreorderWtMenuDetails(wtDishes, regularPreorder,
                                currentDate, preorderComplex, wtDish);
                        if (set.size() > 0) {
                            preorderComplex.setPreorderMenuDetails(set);
                            preorderComplex.setVersion(nextVersion);
                        }
                        entityManager.merge(preorderComplex);
                    }
                } else if (preorderMenuDetail != null) {
                    if (preorderMenuDetail.getAmount() != regularPreorder.getAmount()) {
                        preorderMenuDetail.setAmount(regularPreorder.getAmount());
                        preorderMenuDetail.setRegularPreorder(regularPreorder);
                        entityManager.merge(preorderMenuDetail);
                        preorderComplex.updateWithVersion(nextVersion);
                        entityManager.merge(preorderComplex);
                    } else {
                        currentDate = CalendarUtils.addDays(currentDate, 1);
                        continue;
                    }
                }
            }

            currentDate = CalendarUtils.addDays(currentDate, 1);

        }
    }

    private void deletePreorderComplex(PreorderComplex preorderComplex, long nextVersion, PreorderState preorderState) {
        for (PreorderMenuDetail pmd : preorderComplex.getPreorderMenuDetails()) {
            pmd.deleteByReason(nextVersion, true, preorderState);
            entityManager.merge(pmd);
        }
        preorderComplex.deleteByReason(nextVersion, true, preorderState);
        entityManager.merge(preorderComplex);
    }

    private Set<PreorderMenuDetail> createPreorderWtMenuDetails(List<WtDish> list, RegularPreorder regularPreorder, Date date,
                                                                PreorderComplex preorderComplex, WtDish wtDish) throws Exception {
        Set<PreorderMenuDetail> result = new HashSet<>();
        for (WtDish dish : list) {
            if (dish.equals(wtDish)) continue;
            if (!preorderWtMenuDetailExists(preorderComplex, regularPreorder.getClient(), date, dish.getIdOfDish())) {
                PreorderMenuDetail pmd = createPreorderWtMenuDetail(regularPreorder.getClient(), preorderComplex, dish, date, 0,
                        regularPreorder.getMobile(), regularPreorder.getMobileGroupOnCreate());
                pmd.setRegularPreorder(regularPreorder);
                result.add(pmd);
            }
        }
        return result;
    }

    private boolean preorderWtMenuDetailExists(PreorderComplex preorderComplex, Client client, Date date, Long idOfDish) {
        return (pmdRepo.getPreorderMenuDetailsList(preorderComplex, client, date, idOfDish).size() > 0);
    }

    private PreorderMenuDetail createPreorderWtMenuDetail(Client client, PreorderComplex preorderComplex,
                                                          WtDish wtDish, Date date, Integer amount, String mobile,
                                                          PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws NotFoundException {
        return new PreorderMenuDetail(preorderComplex, wtDish, preorderComplex.getClient(),
                preorderComplex.getPreorderDate(), amount, getMenuGroupByWtDishAndCategories(wtDish), mobile, mobileGroupOnCreate);
    }


    private boolean forcePreorderWtMenuDetailExists(RegularPreorder regularPreorder, Date date) {
        return pmdRepo.getIdOfPreorderMenuDertailIds(regularPreorder.getClient(), date,
                regularPreorder.getIdOfComplex(), regularPreorder.getIdOfDish()).size() > 0;
    }

    private boolean allowCreateNewPreorderMenuDetail(PreorderMenuDetail preorderMenuDetail) {
        return preorderMenuDetail.getDeletedState().equals(1) &&
                !(preorderMenuDetail.getState().equals(PreorderState.CHANGE_ORG)
                                || preorderMenuDetail.getState().equals(PreorderState.PREORDER_OFF));
    }
    private boolean isActualPreorderMenuDetailExists(PreorderComplex preorderComplex) {
        for (PreorderMenuDetail pmd : preorderComplex.getPreorderMenuDetails()) {
            if (pmd.getDeletedState().equals(0)) return true;
        }
        return false;
    }

    private PreorderComplex createWtPreorderComplex(Integer idOfComplex, Client client, Date date,
                                                    Integer complexAmount, WtComplex wtComplex, Long nextVersion, String guardianMobile,
                                                    PreorderMobileGroupOnCreateType mobileGroupOnCreate) throws NotFoundException {
        return new PreorderComplex(client, date, wtComplex, complexAmount, nextVersion,
                guardianMobile, mobileGroupOnCreate);
    }


    private boolean isMenuDetailPreorder(RegularPreorder regularPreorder) {
        return !StringUtils.isEmpty(regularPreorder.getItemCode());
    }

    private boolean forcePreorderComplexExists(RegularPreorder regularPreorder, Date date) {
        return pcRepo.getIdOfPreorderComplexIds(regularPreorder.getClient(), date, regularPreorder.getIdOfComplex()).size() > 0;
    }


    private boolean allowCreateNewPreorderComplex(PreorderComplex preorderComplex) {
        return preorderComplex.getDeletedState().equals(1) /*&&
                !(preorderComplex.getState().equals(PreorderState.OK)
                        || preorderComplex.getState().equals(PreorderState.CHANGE_ORG)
                        || preorderComplex.getState().equals(PreorderState.PREORDER_OFF))*/;
    }

    private boolean doEditRegular(PreorderComplex preorderComplex, Integer regularAmount) {
        return preorderComplex.getDeletedState().equals(0) && preorderComplex.getAmount() != regularAmount;
    }

    private boolean doGenerate(Date date, RegularPreorder regularPreorder) {
        boolean doGenerate = false;
        int dayOfWeek = CalendarUtils.getDayOfWeek(date);
        if (dayOfWeek == 2 && regularPreorder.getMonday().equals(1)) doGenerate = true;
        if (dayOfWeek == 3 && regularPreorder.getTuesday().equals(1)) doGenerate = true;
        if (dayOfWeek == 4 && regularPreorder.getWednesday().equals(1)) doGenerate = true;
        if (dayOfWeek == 5 && regularPreorder.getThursday().equals(1)) doGenerate = true;
        if (dayOfWeek == 6 && regularPreorder.getFriday().equals(1)) doGenerate = true;
        if (dayOfWeek == 7 && regularPreorder.getSaturday().equals(1)) doGenerate = true;
        return doGenerate;
    }

    private PreorderComplex findPreorderComplexOnDate(List<PreorderComplex> preorderComplexes, Date currentDate, Integer deletedState) {
        for (PreorderComplex pc : preorderComplexes) {
            if (pc.getPreorderDate().equals(currentDate) && (deletedState == null ? true : pc.getDeletedState() == deletedState)) return pc;
        }
        return null;
    }

    private Date getStartDateForGeneratePreordersInternal(Client client, List<ProductionCalendar> productionCalendar,
                                                          List<SpecialDate> specialDates) throws Exception {
        Date currentDate = CalendarUtils.startOfDay(new Date());
        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        if (productionCalendar == null) productionCalendar = productionCalendarRepo.findByDayBetween(new Date(), dateTo);
        Integer forbiddenDays = preorderService.getPreorderFeedingForbiddenDays(client.getOrg().getIdOfOrg()); //настройка - количество дней запрета редактирования
        if (forbiddenDays == null) {
            forbiddenDays = PreorderService.DEFAULT_FORBIDDEN_DAYS;
        }
        String groupName = cgroupRepo.getClientGroupName(client.getOrg().getIdOfOrg(), client.getClientGroup().getClientGroupId().getIdOfClientGroup()).orElse("");
        boolean isSixWorkWeek = preorderService.isSixWorkWeek(client.getOrg().getIdOfOrg(), groupName);
        int i = 0;
        Date result = CalendarUtils.addDays(currentDate, 1);
        int workDaysPast = 0;
        while (i < forbiddenDays) {
            boolean isWorkDateBySpecialDates = getIsWorkDate(isSixWorkWeek, result, specialDates, client);
            Boolean isWeekend = preorderService.isWeekendByProductionCalendar(result, productionCalendar);
            if (isWorkDateBySpecialDates) i++;
            if (!isWeekend && !isWorkDateBySpecialDates) workDaysPast++;
            result = CalendarUtils.addDays(result, 1);
        }
        return CalendarUtils.addDays(result, -workDaysPast);
    }

    private boolean getIsWorkDate(boolean isSixWorkWeek, Date currentDate, List<SpecialDate> specialDates, Client client) {
        boolean isWorkDate = CalendarUtils.isWorkDateWithoutParser(isSixWorkWeek, currentDate); //без учета проиводственного календаря
        for (SpecialDate specialDate : specialDates) {
            if (CalendarUtils.betweenDate(specialDate.getDate(), CalendarUtils.startOfDay(currentDate), CalendarUtils.endOfDay(currentDate))) {
                if (specialDate.getIdOfClientGroup() != null
                        && specialDate.getIdOfClientGroup().equals(client.getClientGroup().getClientGroupId().getIdOfClientGroup())) {
                    isWorkDate = (specialDate.getWeekend() == 0);
                    break; //нашли в таблице календаря запись по группе клиента - выходим
                }
                if (specialDate.getIdOfClientGroup() == null) {
                    isWorkDate = (specialDate.getWeekend() == 0); //нашли в таблице календаря запись по ОО клиента (у записи не указана какая-либо группа)
                }
            }
        }
        return isWorkDate;
    }

    public String getMenuGroupByWtDishAndCategories(WtDish wtDish) {
        StringBuilder sb = new StringBuilder();
        List<WtCategoryItem> items = getCategoryItemsByWtDish(wtDish);
        for (WtCategoryItem ci : items) {
            sb.append(ci.getDescription()).append(",");
        }
        if (items.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private List<WtCategoryItem> getCategoryItemsByWtDish(WtDish wtDish) {
        return entityManager.createQuery("select dish.categoryItems from WtDish dish where dish = :dish")
                .setParameter("dish", wtDish)
                .getResultList();
    }
}
