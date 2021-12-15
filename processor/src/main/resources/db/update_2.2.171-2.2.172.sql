--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.172

--расширяем длину поля для ServiceNumber
alter table cf_etp_incoming_message
  alter column etpmessageid type character varying(40);

--индексы по версии заявления ЛП и статусов
CREATE INDEX cf_applications_for_food_version_idx ON cf_applications_for_food USING btree (version);

CREATE INDEX cf_applications_for_food_history_version_idx ON cf_applications_for_food_history USING btree (version);

--дата создания льготы на процессинге
alter table cf_client_dtiszn_discount_info
  add column createddateinternal bigint;


ALTER TABLE cf_menuexchange ALTER COLUMN menudata TYPE character varying (1048576);

--! ФИНАЛИЗИРОВАН 28.12.2018, НЕ МЕНЯТЬ