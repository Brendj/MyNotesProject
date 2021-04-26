package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.Org;
import ru.iteco.restservice.model.wt.WtMenu;

import java.util.Date;
import java.util.List;

public interface WtMenuReadOnlyRepo extends JpaRepository<WtMenu, Long> {
    @Query(value = "SELECT DISTINCT menu.idOfMenu FROM WtMenu menu "
                    + "LEFT JOIN menu.wtOrgGroup orgGroup "
                    + "left join menu.menuGroupMenus "
                    + "left join menu.orgs "
                    + "WHERE menu.beginDate <= :date AND menu.endDate >= :date "
                    + "AND menu.deleteState = 0 "
                    + "AND (:org IN elements(menu.orgs) OR :org IN elements(orgGroup.orgs))")
    List<Long> getWtMenuByDateAndOrg(@Param("date") Date date, @Param("org") Org org);



}
