/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 815

ALTER TABLE CF_feeding_settings ADD COLUMN discountAmount bigint,
  ADD COLUMN useDiscount integer NOT NULL DEFAULT 0,
  ALTER COLUMN limitAmount DROP NOT NULL;