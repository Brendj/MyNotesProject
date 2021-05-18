package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.Org;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.wt.WtComplex;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by nuc on 13.05.2021.
 */
public interface PreorderComplexReadOnlyRepo extends CrudRepository<PreorderComplex, Long> {
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

    @Query(value= "select pc from PreorderComplex pc join fetch pc.preorderMenuDetails pmd " +
            "where pc.client = :client and pc.deletedState = 0 " +
            "and pc.preorderDate between :startDate and :endDate")
    List<PreorderComplex> getPreorderComplexesByClientAndDate(@Param("client") Client client,
                                                              @Param("startDate") Date startDate,
                                                              @Param("endDate") Date endDate);
}
