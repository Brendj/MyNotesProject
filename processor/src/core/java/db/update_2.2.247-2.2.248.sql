/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 248

ALTER TABLE CF_feeding_settings ADD COLUMN useDiscountBuffet integer NOT NULL DEFAULT 0;

comment on column CF_feeding_settings.useDiscountBuffet is 'Флаг "Скидка для буфетной продукции"';

CREATE INDEX cf_clientpayments_idoftransaction_idx ON cf_clientpayments USING btree (idoftransaction);

--! ФИНАЛИЗИРОВАН 22.01.2021, НЕ МЕНЯТЬ