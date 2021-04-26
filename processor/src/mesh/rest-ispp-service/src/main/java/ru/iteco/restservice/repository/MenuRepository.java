package ru.iteco.restservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.iteco.restservice.controller.menu.responsedto.MenuItem;
import ru.iteco.restservice.db.repo.readonly.WtDishReadOnlyRepo;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.wt.WtDish;
import ru.iteco.restservice.servise.data.ProhibitionData;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Repository
public class MenuRepository {
    @PersistenceContext(name = "readonlyEntityManager", unitName = "readonlyPU")
    EntityManager emReport;

    @Autowired
    WtDishReadOnlyRepo dishRepo;


    public List<MenuItem> generateWtMenuDetailWithProhibitions(Client client, List<Long> menus, Date date, ProhibitionData prohibitionData) {
        List<MenuItem> result = new ArrayList<>();
        List<WtDish> wtDishes = getWtDishesByMenuAndDate(menus, date);

        for (WtDish wtDish : wtDishes) {
            List<MenuItem> menuItemList = getMenuItemExt(client.getOrg().getIdOfOrg(), wtDish, date);
            // Добавляем блокировки
            for (MenuItem menuItem : menuItemList) {
                if (prohibitionData.getProhibitByGroup().containsKey(menuItem.getGroup())) {
                    menuItem.setIdOfProhibition(prohibitionData.getProhibitByGroup().get(menuItem.getGroup()));
                } else {
                    if (prohibitionData.getProhibitByName().containsKey(wtDish.getDishName())) {
                        menuItem.setIdOfProhibition(prohibitionData.getProhibitByName().get(wtDish.getDishName()));
                    } else {
                        for (String filter : prohibitionData.getProhibitByFilter().keySet()) {
                            if (wtDish.getDishName().contains(filter)) {
                                menuItem.setIdOfProhibition(prohibitionData.getProhibitByFilter().get(filter));
                            }
                        }
                    }
                }
            }
            result.addAll(menuItemList);
        }

        return result;
    }

    private List<MenuItem> getMenuItemExt(Long idOfOrg, WtDish wtDish, Date date) {
        List<MenuItem> result = new ArrayList<>();
        Set<String> menuGroupSet = null;
        menuGroupSet = getWtMenuGroupByWtDish(idOfOrg, wtDish);
        for (String menuGroup : menuGroupSet) {
            MenuItem menuItem = new MenuItem();
            menuItem.setMenuDate(date);
            menuItem.setGroup(menuGroup);
            menuItem.setName(wtDish.getDishName());
            menuItem.setPrice(wtDish.getPrice().multiply(new BigDecimal(100)).longValue());
            menuItem.setCalories(wtDish.getCalories());
            menuItem.setOutput(wtDish.getQty() == null ? "" : wtDish.getQty());
            menuItem.setCarbohydrates(wtDish.getCarbohydrates());
            menuItem.setFat(wtDish.getFat());
            menuItem.setProtein(wtDish.getProtein());
            result.add(menuItem);
        }
        return result;
    }

    public Set<String> getWtMenuGroupByWtDish(Long idOfOrg, WtDish wtDish) {
        Query query = emReport.createNativeQuery("SELECT mg.name FROM cf_wt_menu_groups mg "
                + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgr.idofmenugroup = mg.id "
                + "LEFT JOIN cf_wt_menu_group_dish_relationships mgd ON mgd.idofmenumenugrouprelation = mgr.id "
                + "LEFT JOIN cf_wt_dishes d ON mgd.idofdish = d.idofdish "
                + "LEFT JOIN cf_wt_menu m ON m.idofmenu = mgr.idofmenu "
                + "WHERE (m.idoforggroup in (select og.idoforggroup from cf_wt_org_groups og "
                + "join cf_wt_org_group_relations ogr on og.idoforggroup = ogr.idoforggroup where ogr.idoforg = :idOfOrg) "
                + "OR m.idofmenu in (select idofmenu from cf_wt_menu_org mo where mo.idoforg = :idOfOrg))"
                + "and d.idofdish = :idOfDish and mgr.deletestate = 0 ");
        query.setParameter("idOfDish", wtDish.getIdOfDish());
        query.setParameter("idOfOrg", idOfOrg);
        List list = query.getResultList();
        Set<String> result = new HashSet<>();
        if (list.size() == 0) result.add("");
        for (Object obj : list) {
            result.add((String)obj);
        }
        return result;
    }


    public List<WtDish> getWtDishesByMenuAndDate(List<Long> menus, Date date) {
        List<Long> groupTypes = Arrays.asList(3L, 4L, 5L); // Буфет, Коммерческое питание, Все
        List<WtDish> res = new ArrayList<>();

        Query query = emReport.createNativeQuery("SELECT DISTINCT dish.idofdish FROM cf_wt_dishes dish "
                + "LEFT JOIN cf_wt_dish_groupitem_relationships groups ON dish.idofdish = groups.idofdish "
                + "LEFT JOIN cf_wt_menu_group_dish_relationships mgdr ON dish.idofdish = mgdr.idofdish "
                + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgdr.idofmenumenugrouprelation = mgr.id "
                + "WHERE groups.idofgroupitem IN (:groupTypes) AND mgr.idofmenu in (:idOfMenus) "
                + "AND mgr.deletestate = 0 AND dish.deletestate = 0 "
                + "AND ((dish.dateofbeginmenuincluding <= :startDate AND dish.dateofendmenuincluding >= :endDate) "
                + "OR (dish.dateofbeginmenuincluding IS NULL AND dish.dateofendmenuincluding >= :endDate) "
                + "OR (dish.dateofbeginmenuincluding <= :startDate AND dish.dateofendmenuincluding IS NULL) "
                + "OR (dish.dateofbeginmenuincluding IS NULL AND dish.dateofendmenuincluding IS NULL))");

        query.setParameter("idOfMenus", menus);
        query.setParameter("startDate", date, TemporalType.DATE);
        query.setParameter("endDate", date, TemporalType.DATE);
        query.setParameter("groupTypes", groupTypes);

        List<BigInteger> tempRes = query.getResultList();
        if (tempRes != null && !tempRes.isEmpty()) {
            for (BigInteger id : tempRes) {
                Optional<WtDish> dish = dishRepo.findById(id.longValue());
                dish.ifPresent(entity -> res.add(entity));
            }
        }
        return res;
    }

}
