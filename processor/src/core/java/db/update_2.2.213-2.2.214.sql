/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 214

-- 395: добавление версий справочникам веб-технолога

ALTER TABLE cf_wt_typeofproduction_items add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_typeofproduction_items.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_agegroup_items add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_agegroup_items.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_diet_type add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_diet_type.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_complex_group_items add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_complex_group_items.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_group_items add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_group_items.version IS 'Версия (для АРМ)';

