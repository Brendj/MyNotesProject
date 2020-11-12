--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет issue 763
alter table cf_client_dtiszn_discount_info add column archivedate bigint;
COMMENT ON COLUMN cf_client_dtiszn_discount_info.archivedate IS 'Дата архивации льготы';

alter table cf_applications_for_food add column archivedate bigint;
COMMENT ON COLUMN cf_applications_for_food.archivedate IS 'Дата архивации заявления';