package ru.iteco.emias.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.emias.models.EMIAS;

import java.util.List;

public interface EMIASRepository extends JpaRepository<EMIAS, Long> {
    @Query(value =
            "select * from cf_emias where guid=:guid and kafka is true",
            nativeQuery = true)
    List<EMIAS> findEmiasbyGuid(@Param("guid") String guid);


    @Query(value =
            "select max(\"version\") from cf_emias where kafka is true",
            nativeQuery = true)
    Long getMaxVersion();
}
