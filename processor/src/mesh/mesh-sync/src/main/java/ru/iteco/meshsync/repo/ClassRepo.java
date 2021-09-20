/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.meshsync.repo;

import ru.iteco.meshsync.models.ClassEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassRepo extends JpaRepository<ClassEntity, Long> {
    Optional<ClassEntity> findByUid(String uid);
}
