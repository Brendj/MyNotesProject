/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 854

-- 854
ALTER TABLE CF_feeding_settings ADD COLUMN useDiscountBuffet integer NOT NULL DEFAULT 0;
