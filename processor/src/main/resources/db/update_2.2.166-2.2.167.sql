--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.167

--таблица отправленных статусов заявлений
alter table cf_etp_outgoing_message add column errormessage character varying(100);

--индекс для создания заявок
CREATE INDEX cf_goods_requests_orgowner_createddate_idx ON cf_goods_requests USING btree (orgowner, createddate);

alter table cf_applications_for_food add column sendtoaiscontingent integer NOT NULL DEFAULT 0;

--исправляем некорректное значение пола у клиентов, созданных через мос.ру
update cf_clients set gender = 0 where gender = 2;

create index cf_visitreqresolutionhist_IdOfRequest_IdOfOrgRegistry_idx on cf_visitreqresolutionhist USING  BTREE (IdOfRequest, IdOfOrgRegistry);

alter table cf_orgregistrychange_item add column state integer;

ALTER TABLE cf_registrychange ALTER COLUMN checkbenefits DROP NOT NULL;
ALTER TABLE cf_registrychange_employee ALTER COLUMN checkbenefits DROP NOT NULL;

--! ФИНАЛИЗИРОВАН 12.12.2018, НЕ МЕНЯТЬ