package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.CategoryDiscount;
import ru.iteco.restservice.model.Org;
import ru.iteco.restservice.model.wt.WtDiscountRule;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by nuc on 05.05.2021.
 */
public interface WtDiscountRuleReadOnlyRepo extends JpaRepository<WtDiscountRule, Long> {
    @Query(value = "select d.idofrule from cf_wt_discountrules d "
            + "left join cf_wt_discountrules_categoryorg dco on dco.idofrule = d.idofrule "
            + "left join cf_categoryorg_orgs cor on cor.idofcategoryorg = dco.idofcategoryorg "
            + "left join cf_wt_discountrules_categorydiscount dc on dc.idofrule = d.idofrule "
            + "where (cor.idoforg = :idoforg or cor.idoforg is null) and dc.idofcategorydiscount in (:discounts)", nativeQuery = true)
    List<BigInteger> getWtDiscountRulesByCategoryOrg(@Param("discounts") List<CategoryDiscount> categoriesDiscount, @Param("idoforg") Long idOfOrg);

    @Query(value = "SELECT discountRule FROM WtDiscountRule discountRule "
            + "WHERE :discount IN ELEMENTS(discountRule.categoryDiscounts) AND discountRule in (:rules)")
    List<WtDiscountRule> getWtDiscountRuleBySecondDiscount(@Param("rules") Set<WtDiscountRule> wtDiscountRuleSet,
                                                          @Param("discount") CategoryDiscount discount);

    @Query(value = "SELECT discountRule FROM WtDiscountRule discountRule "
            + "WHERE :discount IN ELEMENTS(discountRule.categoryDiscounts)")
    List<WtDiscountRule> getWtDiscountRuleBySecondDiscount(@Param("discount") CategoryDiscount discount);

    @Query(value = "SELECT discountRule FROM WtDiscountRule discountRule "
            + "WHERE discountRule in (:rules) AND (:firstDiscount IN ELEMENTS(discountRule.categoryDiscounts) "
            + "OR :secondDiscount IN ELEMENTS(discountRule.categoryDiscounts))")
    Set<WtDiscountRule> getWtDiscountRuleByTwoDiscounts(@Param("rules") Set<WtDiscountRule> wtDiscountRuleSet,
                                                        @Param("firstDiscount") CategoryDiscount firstDiscount,
                                                        @Param("secondDiscount") CategoryDiscount secondDiscount);

    @Query(value = "select d.idofrule from cf_wt_discountrules d "
            + "left join cf_wt_discountrules_categoryorg dco on dco.idofrule = d.idofrule "
            + "left join cf_categoryorg_orgs cor on cor.idofcategoryorg = dco.idofcategoryorg "
            + "left join cf_wt_discountrules_categorydiscount dc on dc.idofrule = d.idofrule "
            + "where (cor.idoforg = :idOfOrg or cor.idoforg is null) and dc.idofcategorydiscount = :discount "
            + "and d.idofrule in (select dc2.idofrule from cf_wt_discountrules_categorydiscount dc2 "
            + "group by dc2.idofrule having count (dc2.idofrule) = 1)", nativeQuery = true)
    Set<BigInteger> getWtElemDiscountRulesIds(@Param("idOfOrg") Long idOfOrg, @Param("discount") Long discount);

}
