package ru.iteco.restservice.servise;

import ru.iteco.restservice.controller.menu.responsedto.CategoryItem;
import ru.iteco.restservice.controller.menu.responsedto.MenuItem;
import ru.iteco.restservice.db.repo.readonly.*;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ProhibitionMenu;
import ru.iteco.restservice.model.wt.WtCategory;
import ru.iteco.restservice.model.wt.WtCategoryItem;
import ru.iteco.restservice.model.wt.WtDish;
import ru.iteco.restservice.model.wt.WtGroupItem;
import ru.iteco.restservice.servise.data.ProhibitionData;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Service
public class MenuService {
    private final ClientReadOnlyRepo clientRepo;
    private final WtMenuReadOnlyRepo menuRepo;
    private final WtDishReadOnlyRepo dishRepo;
    private final ProhibitionMenuReadOnlyRepo prohibitionMenuReadOnlyRepo;
    private final WtCategoryItemRadOnlyRepo wtCategoryItemRadOnlyRepo;
    private final WtCategoryReadOnlyRepo wtCategoryRadOnlyRepo;

    @PersistenceContext(name = "writableEntityManager", unitName = "writablePU")
    private EntityManager writableEntityManager;

    public MenuService(ClientReadOnlyRepo clientRepo,
                       WtMenuReadOnlyRepo menuRepo,
                       WtDishReadOnlyRepo dishRepo,
                       ProhibitionMenuReadOnlyRepo prohibitionMenuReadOnlyRepo,
                       WtCategoryItemRadOnlyRepo wtCategoryItemRadOnlyRepo,
                       WtCategoryReadOnlyRepo  wtCategoryRadOnlyRepo){
        this.clientRepo = clientRepo;
        this.menuRepo = menuRepo;
        this.dishRepo = dishRepo;
        this.prohibitionMenuReadOnlyRepo = prohibitionMenuReadOnlyRepo;
        this.wtCategoryItemRadOnlyRepo = wtCategoryItemRadOnlyRepo;
        this.wtCategoryRadOnlyRepo = wtCategoryRadOnlyRepo;
    }

    public List<CategoryItem> getMenuList(Date date, Long contractId) {
        Client client = clientRepo.getClientByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        List<Long> menus = menuRepo.getWtMenuByDateAndOrg(date, client.getOrg());
        List<ProhibitionMenu> prohibitions = prohibitionMenuReadOnlyRepo.findByClientAndDeletedState(client);
        List<CategoryItem> result = generateWtMenuDetailWithProhibitions(client, menus, date, prohibitions);
        return result;
    }

    //todo Добавить привязку к запретам меню к ответу метода
    private List<CategoryItem> generateWtMenuDetailWithProhibitions(Client client, List<Long> menus, Date date, List<ProhibitionMenu> prohibitions) {
        List<CategoryItem> result = new ArrayList<>();
        List<WtDish> wtDishes = getWtDishesByMenuAndDate(menus, date);

        Map<WtCategory, List<WtDish>> map = new TreeMap<>();
        for (WtDish wtDish : wtDishes) {
            List<WtDish> list = map.get(wtDish.getCategory());
            if (list == null) list = new ArrayList<>();
            list.add(wtDish);
            map.put(wtDish.getCategory(), list);
        }
        for (WtCategory wtCategory : map.keySet()) {
            CategoryItem categoryItem = new CategoryItem(wtCategory);
            categoryItem.setProhibitionId(getProhibitionForCategory(wtCategory, prohibitions));
            List<WtDish> list = map.get(wtCategory);
            for (WtDish wtDish : list) {
                MenuItem menuItem = new MenuItem(wtDish, prohibitions);
                menuItem.setProhibitionId(getProhibitionForDish(wtDish, prohibitions));
                categoryItem.getMenuItems().add(menuItem);
            }
            result.add(categoryItem);
        }

        return result;
    }

    private Long getProhibitionForCategory(WtCategory wtCategory, List<ProhibitionMenu> prohibitions) {
        for (ProhibitionMenu pm : prohibitions) {
            if (pm.getCategory()!= null && pm.getCategory().equals(wtCategory)) {
                return pm.getIdOfProhibitions();
            }
        }
        return null;
    }

    private Long getProhibitionForDish(WtDish wtDish, List<ProhibitionMenu> prohibitions) {
        for (ProhibitionMenu pm : prohibitions) {
            if (pm.getDish() != null && pm.getDish().equals(wtDish)) {
                return pm.getIdOfProhibitions();
            }
        }
        return null;
    }

    private List<WtDish> getWtDishesByMenuAndDate(List<Long> menus, Date date) {
        List<Long> groupTypes = Arrays.asList(WtGroupItem.GROUP_BUFFET, WtGroupItem.GROUP_COMMERCIAL, WtGroupItem.GROUP_ALL);
        List<WtDish> res = new ArrayList<>();

        List<BigInteger> tempRes = dishRepo.getWtDishesForMenuList(menus, date, groupTypes);
        List<Long> list = new ArrayList<>();
        for (BigInteger id : tempRes) {
            list.add(id.longValue());
        }
        if (list.size() > 0) {
            res.addAll(dishRepo.getWtDishList(list));
        }
        return res;
    }

    @Transactional
    public Long createProhibitionData(Long contractId, Long idOfDish, Long idOfCategory, Long idOfCategoryItem){
        if(idOfDish == null && idOfCategory == null && idOfCategoryItem == null){
            throw new IllegalArgumentException("Для установки ограничения требуется указать идентификатор сущности."
                    + " Все ID пришли == null");
        }

        if(moreThenOneIsNotNull(idOfDish, idOfCategory, idOfCategoryItem)){
            throw new IllegalArgumentException("Для установки ограничения требуется указать только один идентификатор сущности.");
        }

        Client client = clientRepo
                .getClientByContractId(contractId)
                .orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));

        WtDish dish = null;
        WtCategory category = null;
        WtCategoryItem categoryItem = null;

        Long nextVersion = prohibitionMenuReadOnlyRepo.getMaxVersion() + 1;

        if(idOfDish != null){
            dish = dishRepo.findById(idOfDish)
                    .orElseThrow(() -> new NotFoundException("Не удалось найти блюдо по ID " + idOfDish));
        } else if(idOfCategory != null){
            category = wtCategoryRadOnlyRepo.findById(idOfCategory)
                    .orElseThrow(() -> new NotFoundException("Не удалось найти категорию по ID " + idOfCategory));
        } else if(idOfCategoryItem != null){
            categoryItem = wtCategoryItemRadOnlyRepo
                    .findById(idOfCategoryItem)
                    .orElseThrow(() -> new NotFoundException("Не удалось найти подкатегорию по ID " + idOfCategoryItem));
        } else {
            throw new RuntimeException();
        }

        ProhibitionMenu prohibitionMenu = new ProhibitionMenu(client, dish, category, categoryItem, nextVersion);

        prohibitionMenu = writableEntityManager.merge(prohibitionMenu);
        return prohibitionMenu.getIdOfProhibitions();
    }

    @Transactional
    public List<Long> deleteProhibitionById(Long id) {
        ProhibitionMenu prohibition = prohibitionMenuReadOnlyRepo
                .findByIdOfProhibitionsAndDeletedStateIsFalse(id)
                .orElseThrow(() -> new NotFoundException("Не удалось найти ограничение по ID: " + id));

        Set<ProhibitionMenu> deletedRows = new HashSet<>();
        List<ProhibitionMenu> categoryItemProhibitions;
        List<ProhibitionMenu> dishProhibitions;
        List<Long> deletedIds = new LinkedList<>();

        if(prohibition.getCategory() != null){
            categoryItemProhibitions = prohibitionMenuReadOnlyRepo
                    .findProhibitionWithCategoryItemsByClientAndCategory(prohibition.getClient(), prohibition.getCategory());
            deletedRows.addAll(categoryItemProhibitions);

            deletedRows.addAll(prohibitionMenuReadOnlyRepo
                       .findProhibitionWithDishByClientAndCategoryItems(
                               prohibition.getClient(),
                               prohibition.getCategory().getCategoryItems()));
        } else if(prohibition.getCategoryItem() != null){
            dishProhibitions = prohibitionMenuReadOnlyRepo
                    .findProhibitionWithDishByClientAndCategoryItem(prohibition.getClient(), prohibition.getCategoryItem());
            deletedRows.addAll(dishProhibitions);
        }
        deletedRows.add(prohibition);

        Long nextVersion = prohibitionMenuReadOnlyRepo.getMaxVersion() + 1;
        for(ProhibitionMenu p : deletedRows){
            p.setUpdateDate(new Date());
            p.setDeletedState(true);
            p.setVersion(nextVersion);

            writableEntityManager.merge(p);

            deletedIds.add(p.getIdOfProhibitions());
        }

        return deletedIds;
    }

    private boolean moreThenOneIsNotNull(Object ... args) {
        int count = 0;

        for(Object o : args){
            if(o != null) count++;
            if(count > 1) return true;
        }
        return false;
    }
}
