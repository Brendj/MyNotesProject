package ru.iteco.meshsync.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteco.meshsync.EntityType;
import ru.iteco.meshsync.models.EntityChanges;

import java.util.List;

public interface EntityChangesRepo extends JpaRepository<EntityChanges, Long> {
    EntityChanges findFirstByPersonGUIDAndEntity(String personGUID, EntityType entity);

    List<EntityChanges> findAllByOrderByAuditAsc(Pageable pageable);

    List<EntityChanges> findAlLBy(Pageable pageable);

    void deleteById(Long id);

    void deleteByPersonGUID(String guid);
}
