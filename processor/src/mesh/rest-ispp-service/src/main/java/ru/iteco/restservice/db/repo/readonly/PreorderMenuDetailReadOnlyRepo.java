package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;

import java.util.Date;
import java.util.List;

public interface PreorderMenuDetailReadOnlyRepo extends CrudRepository<PreorderMenuDetail, Long> {
    @Query(value = "select sum(p.amount), p.preorderDate, coalesce(p.preorderComplex.idOfOrgOnCreate, p.client.org.idOfOrg) from PreorderMenuDetail p "
            + "where p.client.idOfClient = :idOfClient and p.preorderDate between :startDate and :endDate and p.preorderComplex.deletedState = false "
            + "group by p.preorderDate, coalesce(p.preorderComplex.idOfOrgOnCreate, p.client.org.idOfOrg)")
    List getPreorderAmount(@Param("idOfClient") Long idOfClient, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
