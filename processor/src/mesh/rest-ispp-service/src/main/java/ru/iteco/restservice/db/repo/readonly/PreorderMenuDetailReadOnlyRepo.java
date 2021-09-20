package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PreorderMenuDetailReadOnlyRepo extends CrudRepository<PreorderMenuDetail, Long> {
    @Query(value = "select sum(p.amount), p.preorderDate, coalesce(p.preorderComplex.idOfOrgOnCreate, p.client.org.idOfOrg) from PreorderMenuDetail p "
            + "where p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate and p.preorderComplex.deletedState = 0 "
            + "group by p.preorderDate, coalesce(p.preorderComplex.idOfOrgOnCreate, p.client.org.idOfOrg)")
    List getPreorderAmount(@Param("idOfClient") Long idOfClient, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value= "select pmd from PreorderMenuDetail pmd "
            + "where pmd.preorderComplex = :preorderComplex and pmd.deletedState = 0 and pmd.idOfDish = :idOfDish")
    PreorderMenuDetail getPreorderMenuDetailByPreorderComplexAndDishId(@Param("preorderComplex") PreorderComplex preorderComplex,
                                                                 @Param("idOfDish") Long idOfDish);

    @Query(value= "select pmd from PreorderMenuDetail pmd join fetch pmd.preorderComplex pc "
            + "where pmd.idOfPreorderMenuDetail = :idOfPreorderMenuDetail")
    Optional<PreorderMenuDetail> getPreorderMenuDetailWithPreorderComplex(@Param("idOfPreorderMenuDetail") Long idOfPreorderMenuDetail);

    @Query(value= "select pmd from PreorderMenuDetail pmd "
            + "where pmd.preorderComplex = :preorderComplex and pmd.deletedState = 0 and pmd.idOfPreorderMenuDetail <> :idOfPreorderMenuDetail")
    List<PreorderMenuDetail> getPreorderMenuDetailsForDeleteTest(@Param("preorderComplex") PreorderComplex preorderComplex,
            @Param("idOfPreorderMenuDetail") Long idOfPreorderMenuDetail);

    @Query(value = "select pmd from PreorderMenuDetail pmd left join pmd.preorderComplex pc "
            + "where pmd.client = :client and pmd.preorderDate between :startDate and :endDate and "
            + "pc.armComplexId = :idOfComplex and pmd.idOfDish = :idOfDish")
    PreorderMenuDetail findPreorderWtDish(@Param("client") Client client,
                                          @Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate,
                                          @Param("idOfComplex") Integer idOfComplex,
                                          @Param("idOfDish") Long idOfDish);

    @Query(value = "select pmd.idOfPreorderMenuDetail from PreorderMenuDetail pmd "
            + "left join pmd.preorderComplex pc "
            + "where pmd.client = :client and pmd.preorderDate = :date and "
            + "pc.armComplexId = :idOfComplex and pmd.idOfDish = :idOfDish and pmd.deletedState = 0")
    List<Long> getIdOfPreorderMenuDertailIds(@Param("client") Client client,
                                             @Param("date") Date date,
                                             @Param("idOfComplex") Integer idOfComplex,
                                             @Param("idOfDish") Long idOfDish);

    @Query(value = "select pmd.idOfPreorderMenuDetail from PreorderMenuDetail pmd where pmd.preorderComplex = :preorderComplex "
            + "and pmd.client = :client and pmd.preorderDate = :preorderDate and pmd.idOfDish = :idOfDish and pmd.deletedState = 0")
    List<Long> getPreorderMenuDetailsList(@Param("preorderComplex") PreorderComplex preorderComplex,
                                                        @Param("client") Client client,
                                                        @Param("preorderDate") Date preorderDate,
                                                        @Param("idOfDish") Long idOfDish);
}
