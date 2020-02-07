/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 212

-- 395: Создание таблицы cf_menu_suppliers
CREATE TABLE cf_menu_suppliers
(
    idOfMenuSupplier bigint NOT NULL,
    idOfCategoryItem bigint,
    idOfTypeProduction bigint,
    idOfAgeGroupItem bigint,
    idOfDietType bigint,
    idOfComplexGroupItem bigint,
    idOfGroupItem bigint,
    idOfDish bigint,
    idOfMenuGroup bigint,
    idOfMenu bigint,
    idOfComplex bigint,
    CONSTRAINT cf_menu_suppliers_pk PRIMARY KEY (idOfMenuSupplier)
)
    WITH (
        OIDS=FALSE
);
