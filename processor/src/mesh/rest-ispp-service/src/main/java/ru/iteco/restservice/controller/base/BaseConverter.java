/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.base;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class BaseConverter<DTO, Entity> {

    public DTO toDTO(Entity item) {
        throw new UnsupportedOperationException("toDTO not implemented!");
    }

    public List<DTO> toDTOs(Iterable<Entity> items) {
        List<DTO> res = new LinkedList<>();
        for (Entity item : items) {
            DTO view = toDTO(item);
            res.add(view);
        }
        return res;
    }

    public Set<DTO> toDTOs(Set<Entity> items) {
        Set<DTO> res = new HashSet<>();
        for (Entity item : items) {
            DTO view = toDTO(item);
            res.add(view);
        }
        return res;
    }


}
