/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.08.12
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */

/* Класс фильтрации сущностей */
public abstract class AbstractFilterEntity<E> {

    public abstract List<E> retrieve();

}
