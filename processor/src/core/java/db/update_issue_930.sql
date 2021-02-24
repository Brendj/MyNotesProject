--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 930

alter table cf_orgregistrychange add column globalId bigint,
  add column globalIdFrom bigint;

alter table cf_orgregistrychange_item add column globalId bigint,
  add column globalIdFrom bigint;

COMMENT ON COLUMN cf_orgregistrychange.globalId IS 'Идентификатор НСИ-3';
COMMENT ON COLUMN cf_orgregistrychange.globalIdFrom IS 'Идентификатор НСИ-3 в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange_item.globalId IS 'Идентификатор НСИ-3';
COMMENT ON COLUMN cf_orgregistrychange_item.globalIdFrom IS 'Идентификатор НСИ-3 в ИСПП';