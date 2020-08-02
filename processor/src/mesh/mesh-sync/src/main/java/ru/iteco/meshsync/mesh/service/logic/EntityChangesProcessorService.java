package ru.iteco.meshsync.mesh.service.logic;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.iteco.meshsync.mesh.service.DAO.EntityChangesService;
import ru.iteco.meshsync.models.EntityChanges;
import ru.iteco.meshsync.taskscheduler.Scheduled;

import java.util.List;

@Service
public class EntityChangesProcessorService implements Scheduled {
    private static final Logger log = LoggerFactory.getLogger(EntityChangesProcessorService.class);
    private static final Integer SAMPLE_SIZE = 750;

    private final EntityChangesService entityChangesService;
    private final MeshService meshService;

    public EntityChangesProcessorService(EntityChangesService entityChangesService,
                                         MeshService meshService){
        this.entityChangesService = entityChangesService;
        this.meshService = meshService;
    }

    @Override
    public void process() {
        List<EntityChanges> entityChangesList = null;
        do {
            Pageable samp = PageRequest.of(0,  SAMPLE_SIZE);
            entityChangesList = entityChangesService.getAllEntityChanges(samp);
            if (CollectionUtils.isEmpty(entityChangesList)) {
                log.info("No non-process EntityChanges in DB");
                break;
            }
            for (EntityChanges changes : entityChangesList) {
                boolean process = meshService.processEntityChanges(changes);
                if (process) {
                    entityChangesService.deleteChangesForPersonGUID(changes.getPersonGUID());
                } else {
                    entityChangesService.save(changes);
                }
            }
        } while (CollectionUtils.isNotEmpty(entityChangesList));
    }
}
