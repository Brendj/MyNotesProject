/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by nuc on 21.04.2020.
 */
public enum CardActivityType {
        INITIAL("Инициализация", 0),
        ENTER_EVENT("Проход в ОО", 1),
        ORDER("Покупка", 2),
        ENTER_MUSEUM("Проход в музей, учреждение культуры", 3),
        ENTER_LIBRARY("Проход в библиотеку", 4);

        private final String value;
        public final int order;

        private CardActivityType(String value, int order) {
            this.value = value;
            this.order = order;
        }

        public String getValue() {
            return value;
        }

        public int getOrder() {
            return order;
        }
}
