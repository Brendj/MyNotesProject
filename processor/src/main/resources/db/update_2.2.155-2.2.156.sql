--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.156

--Краткое название блюда из атрибута Name пакета синхронизации
alter table cf_menudetails add column shortname character varying(256);

CREATE INDEX cf_zerotransactions_version_idx ON cf_zerotransactions USING btree (version);

CREATE INDEX cf_goods_requests_positions_idofgoodsrequest_idx ON cf_goods_requests_positions USING btree (idofgoodsrequest);

CREATE INDEX cf_goods_requests_donedate_idx ON cf_goods_requests USING btree (donedate);

--! ФИНАЛИЗИРОВАН 24.04.2018, НЕ МЕНЯТЬ