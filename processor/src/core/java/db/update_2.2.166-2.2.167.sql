--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.167

--таблица отправленных статусов заявлений
alter table cf_etp_outgoing_message add column errormessage character varying(100);

--индекс для создания заявок
CREATE INDEX cf_goods_requests_orgowner_createddate_idx ON cf_goods_requests USING btree (orgowner, createddate);alter table cf_applications_for_food add column sendtoaiscontingent integer NOT NULL DEFAULT 0;

alter table cf_applications_for_food add column sendtoaiscontingent integer NOT NULL DEFAULT 0;