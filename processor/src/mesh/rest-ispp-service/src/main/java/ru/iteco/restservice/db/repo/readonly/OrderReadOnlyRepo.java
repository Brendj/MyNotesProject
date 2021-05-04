/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.Order;
import ru.iteco.restservice.model.compositid.OrderCompositeId;

import org.springframework.data.repository.CrudRepository;

public interface OrderReadOnlyRepo extends CrudRepository<Order, OrderCompositeId> {

}
