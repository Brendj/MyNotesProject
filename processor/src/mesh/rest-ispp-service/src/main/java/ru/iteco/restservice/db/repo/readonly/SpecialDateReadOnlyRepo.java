package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.SpecialDate;

import java.util.Date;
import java.util.List;

/**
 * Created by nuc on 04.05.2021.
 */
public interface SpecialDateReadOnlyRepo extends JpaRepository<SpecialDate, Long> {
    @Query(value = "SELECT sd.isWeekend from SpecialDate sd "
            + "WHERE sd.date between :dateBegin and :dateEnd AND sd.deleted = 0 AND sd.idOfOrg = :org "
            + "AND (sd.idOfClientGroup = :idOfClientGroup OR sd.idOfClientGroup IS NULL)")
    List<Integer> getSpecialDate(@Param("dateBegin") Date dateBegin, @Param("dateEnd") Date dateEnd,
                                 @Param("org") Long org, @Param("idOfClientGroup") Long idOfClientGroup);
}
