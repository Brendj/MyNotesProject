--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.136

--Переносим поле createdFrom из клиентов на связку клиент-представитель
alter table cf_clients drop column createdFrom;

alter table cf_client_guardian add column createdFrom integer not null default 0;

--тип организации при внедрении
alter table cf_orgs add column organizationtype_initial integer NOT NULL DEFAULT 0;

--новый генератор первичного ключа для таблицы synchistory_exceptions
alter table cf_generators add column idOfSyncHistoryException bigint NOT NULL DEFAULT 0;
update cf_generators set idOfSyncHistoryException = (select  case when max(idOfSyncHistoryException) is null THEN 0 else (max(idOfSyncHistoryException)+1) end  from cf_synchistory_exceptions );

--изменeние типа поля с bigserial на bigint
ALTER TABLE cf_synchistory_exceptions ALTER COLUMN IdOfSyncHistoryException DROP DEFAULT;

--! ФИНАЛИЗИРОВАН (Семенов, 190617) НЕ МЕНЯТЬ