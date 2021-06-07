package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.ProductionCalendar;

import java.util.Date;
import java.util.List;

/**
 * Created by nuc on 04.05.2021.
 */
public interface ProductionCalendarReadOnlyRepo extends JpaRepository<ProductionCalendar, Long> {
    @Query(value = "SELECT calend.flag from ProductionCalendar calend WHERE calend.day "
            + "between :dateBegin and :dateEnd")
    List<Integer> getWorkingDays(@Param("dateBegin") Date dateBegin, @Param("dateEnd") Date dateEnd);

    List<ProductionCalendar> findByDayBetween(Date startDate, Date endDate);
}
