/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.meshsync.mesh.service.DAO;

import ru.iteco.client.model.ModelClass;
import ru.iteco.meshsync.models.ClassEntity;
import ru.iteco.meshsync.repo.ClassRepo;

import org.springframework.stereotype.Service;

@Service
public class ClassService {
    private final ClassRepo classRepo;

    public ClassService(ClassRepo classRepo){
        this.classRepo = classRepo;
    }

    public ClassEntity getByUid(String uid) {
        if(uid == null){
            return null;
        }
        return classRepo.findByUid(uid).orElse(null);
    }

    public void remove(ClassEntity classEntity) {
        classRepo.delete(classEntity);
    }

    public void save(ClassEntity classEntity) {
        classRepo.save(classEntity);
    }

    public ClassEntity getAndChange(ModelClass propertyClass) {
        ClassEntity entity = classRepo.findByUid(propertyClass.getUid().toString()).orElse(null);
        if (entity == null) {
            entity = new ClassEntity();
            entity.setId(propertyClass.getId());
            entity.setUid(propertyClass.getUid().toString());
        }
        entity.setName(propertyClass.getName());
        entity.setOrganizationId(propertyClass.getOrganizationId());
        entity.setEducationStageId(propertyClass.getEducationStageId());
        entity.setParallelId(propertyClass.getParallelId());

        entity = classRepo.save(entity);
        return entity;
    }
}
