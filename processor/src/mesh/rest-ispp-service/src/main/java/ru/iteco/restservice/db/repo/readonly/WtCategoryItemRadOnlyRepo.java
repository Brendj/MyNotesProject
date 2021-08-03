/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.wt.WtCategoryItem;

import org.springframework.data.repository.CrudRepository;

public interface WtCategoryItemRadOnlyRepo extends CrudRepository<WtCategoryItem, Long> {

}
