--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.68

-- тип сверки с реестром (полная - 1; изменения - 2)
alter table CF_RegistryChange add column type integer not null default 1;
CREATE index "cf_cf_registrychange_type_idx" ON CF_RegistryChange (type);
alter table CF_RegistryChange add column notificationId varchar(15) default null;
CREATE index "cf_cf_registrychange_notificationId_idx" ON CF_RegistryChange (notificationId);

-- Привязка клиента ИСПП к записи ЕМП
alter table CF_Clients add column SSOID varchar(50);
--! ФИНАЛИЗИРОВАН (Сунгатов, 140723) НЕ МЕНЯТЬ