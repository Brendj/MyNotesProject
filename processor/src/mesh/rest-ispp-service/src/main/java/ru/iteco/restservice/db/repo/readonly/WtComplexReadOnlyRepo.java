package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.Contragent;
import ru.iteco.restservice.model.Org;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtDiscountRule;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by nuc on 05.05.2021.
 */
public interface WtComplexReadOnlyRepo extends JpaRepository<WtComplex, Long> {

    @Query(value = "SELECT complex FROM WtComplex complex "
            + "LEFT JOIN complex.wtOrgGroup orgGroup "
            + "WHERE complex.isPortal = true AND complex.deleteState = 0 "
            + "AND complex.wtAgeGroupItem.idOfAgeGroupItem in (:ageGroupIds) "
            + "AND complex.beginDate < :startDate AND complex.endDate > :endDate "
            + "AND (:org IN ELEMENTS(complex.orgs) or :org IN ELEMENTS(orgGroup.orgs)) "
            + "AND (complex.wtComplexGroupItem.idOfComplexGroupItem = :paidComplex OR "
            + "complex.wtComplexGroupItem.idOfComplexGroupItem = :allComplexes)")
    List<WtComplex> getPaidWtComplexesByAgeGroupsAndPortal(@Param("startDate") Date startDate,
                                                           @Param("endDate") Date endDate,
                                                           @Param("ageGroupIds") Set<Long> ageGroupIds,
                                                           @Param("org") Org org,
                                                           @Param("paidComplex") Long paidComplex,
                                                           @Param("allComplexes") Long allComplexes);

    @Query(value = "select complex from WtComplex complex LEFT JOIN complex.wtOrgGroup orgGroup "
            + "where complex.deleteState = 0 and complex.beginDate <= :startDate AND complex.endDate >= :endDate "
            + "and (:rules) in elements(complex.discountRules) and (:org IN ELEMENTS(complex.orgs) or :org IN ELEMENTS(orgGroup.orgs))")
    Set<WtComplex> getFreeWtComplexesByDiscountRules(@Param("startDate") Date startDate,
                                                     @Param("endDate") Date endDate,
                                                     @Param("rules") Set<WtDiscountRule> wtDiscountRuleSet,
                                                     @Param("org") Org org);

    @Query(value = "select complex from WtComplex complex "
            + "LEFT JOIN complex.wtOrgGroup orgGroup "
            + "where complex.deleteState = 0 and complex.beginDate <= :startDate AND complex.endDate >= :endDate "
            + "and complex.wtAgeGroupItem.idOfAgeGroupItem in (:ageGroupIds) "
            + "and complex.contragent = :contragent "
            + "and (:rules) in elements(complex.discountRules) "
            + "AND (:org IN ELEMENTS(complex.orgs) or :org IN ELEMENTS(orgGroup.orgs))"
            + "and (complex.wtComplexGroupItem.idOfComplexGroupItem = :freeComplex "
            + "or complex.wtComplexGroupItem.idOfComplexGroupItem = :allComplexes)")
    Set<WtComplex> getFreeWtComplexesByRulesAndAgeGroups(@Param("startDate") Date startDate,
                                                         @Param("endDate") Date endDate,
                                                         @Param("rules") Set<WtDiscountRule> wtDiscountRuleSet,
                                                         @Param("ageGroupIds") Set<Long> ageGroupIds,
                                                         @Param("org") Org org,
                                                         @Param("contragent") Contragent contragent,
                                                         @Param("freeComplex") Long freeComplex,
                                                         @Param("allComplexes") Long allComplexes);
    @Query(value = "SELECT complex FROM WtComplex complex "
            + "LEFT JOIN complex.wtOrgGroup orgGroup "
            + "WHERE complex.beginDate <= :startDate AND complex.endDate >= :endDate "
            + "AND complex.deleteState = 0 "
            + "AND complex.idOfComplex = :idOfComplex "
            + "AND (:org IN ELEMENTS(complex.orgs) or :org IN ELEMENTS(orgGroup.orgs)) ")
    WtComplex getWtComplex(@Param("org") Org org,
                           @Param("idOfComplex") Long idOfComplex,
                           @Param("startDate") Date startDate,
                           @Param("endDate") Date endDate);
}
