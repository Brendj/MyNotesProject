package ru.iteco.restservice.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.restservice.db.repo.readonly.PreorderComplexReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.PreorderMenuDetailReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.WtComplexReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.WtDishReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.enums.PreorderMobileGroupOnCreateType;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;
import ru.iteco.restservice.model.wt.WtCategoryItem;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtComplexesItem;
import ru.iteco.restservice.model.wt.WtDish;
import ru.iteco.restservice.servise.data.PreorderComplexChangeData;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PreorderDAO {
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
    public void editPreorder(PreorderComplex preorderComplex, String guardianMobile, Integer amount, long version) {
        preorderComplex.editAmount(guardianMobile, amount, version);
        entityManager.merge(preorderComplex);
    }

    @Transactional
    public void deletePreorder(PreorderComplex preorderComplex, String guardianMobile, long version) {
        for (PreorderMenuDetail pmd : preorderComplex.getPreorderMenuDetails()) {
            pmd.delete(guardianMobile);
            entityManager.merge(pmd);
        }
        preorderComplex.delete(guardianMobile, version);
        entityManager.merge(preorderComplex);
    }

    @Transactional
    public PreorderMenuDetail createPreorderMenuDetail(PreorderComplexChangeData data, Long complexId, Long dishId, Integer amount,
                                                       String guardianMobile, long version) {
        WtDish wtDish = dishRepo.findById(dishId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено блюдо с идентификатором %s", dishId)));
        PreorderComplex preorderComplex = pcRepo.getPreorderComplexesByClientDateAndComplexId(data.getClient(), complexId.intValue(),
                data.getStartDate(), data.getEndDate());
        if (preorderComplex == null) {
            preorderComplex = createPreorder(data.getClient(), data.getStartDate(), data.getEndDate(), complexId, 0,
                    version, guardianMobile, data.getMobileGroupOnCreate(), true);
        }
        PreorderMenuDetail preorderMenuDetail = pmdRepo.getPreorderMenuDetailByPreorderComplexAndDishId(preorderComplex, dishId);

        if (preorderMenuDetail != null) {
            throw new IllegalArgumentException("У данного клиента уже существует предзаказ на выбранную дату и блюдо");
        }

        String groupName = getMenuGroupByWtDishAndCategories(wtDish);
        preorderMenuDetail = new PreorderMenuDetail(preorderComplex, wtDish, preorderComplex.getClient(),
                preorderComplex.getPreorderDate(), amount, groupName, guardianMobile, preorderComplex.getMobileGroupOnCreate());
        preorderComplex.updateWithVersion(version);
        entityManager.merge(preorderComplex);
        return entityManager.merge(preorderMenuDetail);
    }

    @Transactional
    public PreorderMenuDetail editPreorderMenuDetail(PreorderMenuDetail preorderMenuDetail, String guardianMobile, Integer amount, long version) {
        PreorderComplex preorderComplex = pcRepo.findById(preorderMenuDetail.getPreorderComplex().getIdOfPreorderComplex())
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено предзаказ с идентификатором %s",
                        preorderMenuDetail.getPreorderComplex().getIdOfPreorderComplex())));;
        preorderComplex.updateWithVersion(version);
        entityManager.merge(preorderComplex);
        preorderMenuDetail.setAmount(amount);
        return entityManager.merge(preorderMenuDetail);
    }

    @Transactional
    public void deletePreorderMenuDetail(PreorderMenuDetail preorderMenuDetail, String guardianMobile, long version) {
        preorderMenuDetail.delete(guardianMobile);
        entityManager.merge(preorderMenuDetail);

        PreorderComplex preorderComplex = preorderMenuDetail.getPreorderComplex();
        List<PreorderMenuDetail> list = pmdRepo.getPreorderMenuDetailsForDeleteTest(preorderComplex, preorderMenuDetail.getIdOfPreorderMenuDetail());
        if (list.size() == 0) {
            preorderComplex.delete(guardianMobile, version);
        } else {
            preorderComplex.updateWithVersion(version);
        }
        entityManager.merge(preorderComplex);
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
