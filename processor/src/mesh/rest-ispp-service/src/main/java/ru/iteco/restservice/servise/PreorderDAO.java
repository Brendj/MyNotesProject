package ru.iteco.restservice.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.restservice.db.repo.readonly.WtComplexReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.enums.PreorderMobileGroupOnCreateType;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;
import ru.iteco.restservice.model.wt.WtCategoryItem;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtComplexesItem;
import ru.iteco.restservice.model.wt.WtDish;

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

    @Transactional
    public Long createPreorder(Client client, Date date, Long complexId, Integer amount, Long version,
                               String guardianMobile, PreorderMobileGroupOnCreateType mobileGroupOnCreate) {
        WtComplex complex = complexRepo.findById(complexId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден комплекс с идентификатором %s", complexId)));
        WtComplexesItem complexesItem = complexService.getWtComplexItemByCycle(complex, date);

        PreorderComplex preorderComplex = new PreorderComplex(client, date, complex, amount, version,
                guardianMobile, mobileGroupOnCreate);

        Set<PreorderMenuDetail> preorderMenuDetails = new HashSet<>();
        for (WtDish wtDish : complexesItem.getDishes()) {
            if (wtDish.getDeleteState().equals(1)) continue;
            String groupName = getMenuGroupByWtDishAndCategories(wtDish);
            PreorderMenuDetail preorderMenuDetail = new PreorderMenuDetail(preorderComplex, wtDish, client, date, 0,
                    groupName, guardianMobile, mobileGroupOnCreate);
            preorderMenuDetails.add(preorderMenuDetail);
        }
        preorderComplex.setPreorderMenuDetails(preorderMenuDetails);
        entityManager.merge(preorderComplex);
        return preorderComplex.getIdOfPreorderComplex();
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
