--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.127

--Создание индексов по задаче EP-752
CREATE INDEX cf_clientmigrationhistory_idofclient_idx ON cf_clientmigrationhistory USING btree (idofclient);

CREATE INDEX cf_clientmigrationhistory_idoforg_idx ON cf_clientmigrationhistory USING btree (idoforg);

CREATE INDEX cf_clientmigrationhistory_idofoldorg_idx ON cf_clientmigrationhistory USING btree (idofoldorg);

CREATE INDEX cf_daily_org_registries_idofregistries_idx ON cf_daily_org_registries USING btree (idofregistries);

CREATE INDEX cf_clients_categorydiscounts_idofclient_idx ON cf_clients_categorydiscounts USING btree (idofclient);

CREATE INDEX cf_registry_talon_orgowner_idx ON cf_registry_talon USING btree (orgowner);

CREATE INDEX cf_registry_talon_talondate_idx ON cf_registry_talon USING btree (talondate);

CREATE INDEX cf_clientscomplexdiscounts_idofclient_idx ON cf_clientscomplexdiscounts USING btree (idofclient);

CREATE INDEX cf_clientscomplexdiscounts_idofrule_idx ON cf_clientscomplexdiscounts USING btree (idofrule);

CREATE INDEX cf_discountchangehistory_idofclient_idx ON cf_discountchangehistory USING btree (idofclient);

CREATE INDEX cf_discountchangehistory_idoforg_idx ON cf_discountchangehistory USING btree (idoforg);

CREATE INDEX cf_registrychange_guardians_createddate_idx ON cf_registrychange_guardians USING btree (createddate);

CREATE INDEX cf_registrychange_guardians_idofregistrychange_idx ON cf_registrychange_guardians USING btree (idofregistrychange);

--для запроса в быстрой синхре по построению секции AccRegistryUpdate
CREATE INDEX cf_orders_transaction_idx ON cf_orders USING btree (idoftransaction);

-- Таблица разногласий добавили 1 поле
ALTER TABLE CF_RegistryChange ADD COLUMN ageTypeGroup character varying (128);

ALTER TABLE CF_Clients ADD COLUMN ageTypeGroup character varying (128);

ALTER TABLE CF_RegistryChange ADD COLUMN ageTypeGroupFrom character varying (128);

--Идентификатор организации, в которой создали позицию
ALTER TABLE cf_taloon_approval ADD COLUMN idoforgcreated bigint;

--! ФИНАЛИЗИРОВАН (Семенов, 191216) НЕ МЕНЯТЬ