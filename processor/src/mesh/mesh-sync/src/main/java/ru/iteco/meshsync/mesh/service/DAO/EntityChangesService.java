package ru.iteco.meshsync.mesh.service.DAO;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.iteco.meshsync.models.EntityChanges;
import ru.iteco.meshsync.repo.EntityChangesRepo;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class EntityChangesService {
    private final EntityChangesRepo entityChangesRepo;

    public EntityChangesService(EntityChangesRepo entityChangesRepo){
        this.entityChangesRepo = entityChangesRepo;
    }

    public List<EntityChanges> getAllEntityChanges(Pageable pageable) {
        return entityChangesRepo.findAllByOrderByAuditAsc(pageable);
    }

    @Transactional
    public void save(EntityChanges changes) {
        if(changes == null){
            throw new IllegalArgumentException("Get param as NULL");
        }
        entityChangesRepo.save(changes);
    }

    @Transactional
    public void deleteChanges(EntityChanges changes) {
        if(changes == null){
            throw new IllegalArgumentException("Get param as NULL");
        }
        entityChangesRepo.deleteById(changes.getId());
    }

    @Transactional
    public void deleteChangesForPersonGUID(String guid){
        entityChangesRepo.deleteByPersonGUID(guid);
    }

    @Transactional
    public void saveForReprocess(EntityChanges entityChanges){
        EntityChanges fromDB = entityChangesRepo.findFirstByPersonGUIDAndEntity(entityChanges.getPersonGUID(), entityChanges.getEntity());
        if(fromDB != null){
            fromDB.setEntityId(entityChanges.getEntityId());
            entityChangesRepo.save(fromDB);
        } else {
            entityChangesRepo.save(entityChanges);
        }
    }
}
