--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.70

-- Изменения для новых выгрузок для СМА
alter table cf_orgs add column OrganizationStatus integer NOT NULL DEFAULT 0;