/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.wt.WtCategory;

import org.springframework.data.repository.CrudRepository;

public interface WtCategoryReadOnlyRepo extends CrudRepository<WtCategory, Long> {

}
