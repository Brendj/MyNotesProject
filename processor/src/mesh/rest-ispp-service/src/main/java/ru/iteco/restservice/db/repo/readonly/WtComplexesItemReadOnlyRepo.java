package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteco.restservice.model.wt.WtComplex;
import ru.iteco.restservice.model.wt.WtComplexesItem;

import java.util.List;

/**
 * Created by nuc on 06.05.2021.
 */
public interface WtComplexesItemReadOnlyRepo extends JpaRepository<WtComplexesItem, Long> {
    public WtComplexesItem findByWtComplexAndCycleDay(WtComplex wtComplex, Integer cycleDay);
}
