package ru.iteco.restservice.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iteco.restservice.controller.menu.responsedto.CategoryItem;
import ru.iteco.restservice.controller.menu.responsedto.MenuItem;
import ru.iteco.restservice.db.repo.readonly.ClientReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.ProhibitionMenuReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.WtDishReadOnlyRepo;
import ru.iteco.restservice.db.repo.readonly.WtMenuReadOnlyRepo;
import ru.iteco.restservice.errors.NotFoundException;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ProhibitionMenu;
import ru.iteco.restservice.model.wt.WtCategory;
import ru.iteco.restservice.model.wt.WtDish;
import ru.iteco.restservice.model.wt.WtGroupItem;
import ru.iteco.restservice.servise.data.ProhibitionData;

import java.math.BigInteger;
import java.util.*;

@Service
public class MenuService {


    @Autowired
    ClientReadOnlyRepo clientRepo;
    @Autowired
    WtMenuReadOnlyRepo menuRepo;
    @Autowired
    WtDishReadOnlyRepo dishRepo;
    @Autowired
    ProhibitionMenuReadOnlyRepo prohibitionMenuReadOnlyRepo;

    public List<CategoryItem> getMenuList(Date date, Long contractId) {
        Client client = clientRepo.getClientByContractId(contractId).orElseThrow(() -> new NotFoundException("Клиент не найден по номеру л/с"));
        List<Long> menus = menuRepo.getWtMenuByDateAndOrg(date, client.getOrg());
        ProhibitionData prohibitionData = getProhibitionData(client);
        List<CategoryItem> result = generateWtMenuDetailWithProhibitions(client, menus, date, prohibitionData);
        return result;
    }

    //todo Добавить привязку к запретам меню к ответу метода
    private List<CategoryItem> generateWtMenuDetailWithProhibitions(Client client, List<Long> menus, Date date, ProhibitionData prohibitionData) {
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
            List<WtDish> list = map.get(wtCategory);
            for (WtDish wtDish : list) {
                MenuItem menuItem = new MenuItem(wtDish);
                categoryItem.getMenuItems().add(menuItem);
            }
            result.add(categoryItem);
        }

        return result;
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

    public ProhibitionData getProhibitionData(Client client) {
        ProhibitionData prohibitionData = new ProhibitionData();

        List<ProhibitionMenu> prohibitions = prohibitionMenuReadOnlyRepo.findByClientAndDeletedState(client);

        for (ProhibitionMenu prohibition : prohibitions) {
            switch (prohibition.getProhibitionFilterType()) {
                case PROHIBITION_BY_FILTER:
                    prohibitionData.getProhibitByFilter().put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GOODS_NAME:
                    prohibitionData.getProhibitByName().put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GROUP_NAME:
                    prohibitionData.getProhibitByGroup().put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
            }
        }
        return prohibitionData;
    }

}
