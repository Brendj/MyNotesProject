package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.enums.EntityStateType;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtComplexesItem;
import ru.iteco.restservice.model.wt.WtDish;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface WtDishReadOnlyRepo extends JpaRepository<WtDish, Long> {
    @Query(value = "select dish from WtDish dish " +
            "left join fetch dish.menuGroupMenus menus " +
            "left join fetch menus.menuGroup " +
            "where dish.idOfDish = :idOfDish")
    Optional<WtDish> getWtDishWithGroup(@Param("idOfDish") Long idOfDish);

    @Query(value = "select dish from WtDish dish " +
            "join fetch dish.category cat " +
            "left join fetch dish.categoryItems items " +
            //"left join fetch dish.menuGroupMenus menus " +
            //"left join fetch menus.menuGroup " +
            "where dish.idOfDish in (:idOfDishList) " +
            "order by cat.description, dish.dishName")
    List<WtDish> getWtDishList(@Param("idOfDishList") List<Long> idOfDishList);

    /*@Query(value = "select dish from WtDish dish " +
            "join fetch dish.category cat " +
            "left join fetch dish.categoryItems catItems " +
            "left join dish.menuGroupMenus menus " +
            "where menus.menu.idOfMenu in (:menus)")
    List<WtDish> getWtDishesByMenuAndDate(List<Long> menus, Date date);*/

    @Query(value = "SELECT DISTINCT dish.idofdish FROM cf_wt_dishes dish "
            + "LEFT JOIN cf_wt_dish_groupitem_relationships groups ON dish.idofdish = groups.idofdish "
            + "LEFT JOIN cf_wt_menu_group_dish_relationships mgdr ON dish.idofdish = mgdr.idofdish "
            + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgdr.idofmenumenugrouprelation = mgr.id "
            + "WHERE groups.idofgroupitem IN (:groupTypes) AND mgr.idofmenu in (:idOfMenus) "
            + "AND mgr.deletestate = 0 AND dish.deletestate = 0 "
            + "AND ((dish.dateofbeginmenuincluding <= :date AND dish.dateofendmenuincluding >= :date) "
            + "OR (dish.dateofbeginmenuincluding IS NULL AND dish.dateofendmenuincluding >= :date) "
            + "OR (dish.dateofbeginmenuincluding <= :date AND dish.dateofendmenuincluding IS NULL) "
            + "OR (dish.dateofbeginmenuincluding IS NULL AND dish.dateofendmenuincluding IS NULL))", nativeQuery = true)
    List<BigInteger> getWtDishesForMenuList(@Param("idOfMenus") List<Long> menus, @Param("date") Date date,
                                            @Param("groupTypes") List<Long> groupTypes);

    @Query(value = "SELECT DISTINCT dish FROM WtDish dish join dish.complexItems complex "
            + "left join fetch dish.repeatableComplex repeat "
            + "WHERE complex = :complexItem "
            + "AND dish.deleteState = 0 "
            + "AND ((dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding >= :endDate) "
            + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding >= :endDate) "
            + "OR (dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding IS NULL) "
            + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding IS NULL)) "
            + "AND (repeat is null or (repeat.state = :state and repeat.complexId = complex.wtComplex.idOfComplex))")
    List<WtDish> getWtDishesByComplexItemAndDates(@Param("complexItem") WtComplexesItem complexItem,
                                                  @Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate,
                                                  @Param("state") EntityStateType state);

    @Query(value = "SELECT dish FROM WtDish dish "
            + "LEFT JOIN dish.complexItems complexItems "
            + "WHERE dish.idOfDish = :idOfDish "
            + "AND :complexItem IN ELEMENTS(dish.complexItems) "
            + "AND dish.deleteState = 0 "
            + "AND ((dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding >= :endDate) "
            + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding >= :endDate) "
            + "OR (dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding IS NULL) "
            + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding IS NULL))")
    WtDish getWtDishByItemCodeAndId(@Param("complexItem") WtComplexesItem complexItem,
                                    @Param("startDate") Date startDate,
                                    @Param("endDate") Date endDate,
                                    @Param("idOfDish") Long idOfDish);

    @Query(value = "SELECT dish FROM WtDish dish LEFT JOIN dish.complexItems items "
            + "where items.wtComplex = :complex and dish.deleteState = 0")
    List<WtDish> getWtDishesByComplex(@Param("complex") WtComplex complex);

}
