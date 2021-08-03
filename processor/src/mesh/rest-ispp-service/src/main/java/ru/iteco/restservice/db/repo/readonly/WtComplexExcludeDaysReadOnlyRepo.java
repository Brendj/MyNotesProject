package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtComplexExcludeDays;

import java.util.List;

/**
 * Created by nuc on 06.05.2021.
 */
public interface WtComplexExcludeDaysReadOnlyRepo extends JpaRepository<WtComplexExcludeDays, Long> {
    @Query(value = "SELECT excludeDays from WtComplexExcludeDays excludeDays "
            + "WHERE excludeDays.complex = :complex ")
    List<WtComplexExcludeDays> getExcludeDaysByWtComplex(@Param("complex") WtComplex wtComplex);
}
