--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.171

--новый параметр в конфигурации ecafe.processor.etp.nodes=список нод с доступом к ЕТП (через запятую без пробелов)

--поле с номером телефона кто внес последние изменения
alter table cf_preorder_complex
  add column mobile varchar(32);

alter table cf_regular_preorders
  add column mobile varchar(32);

--индексы на таблицу с дтисзн льготами
CREATE INDEX cf_client_dtiszn_discount_info_idofclient_dtiszncode_idx ON cf_client_dtiszn_discount_info USING btree (idofclient, dtiszncode);
CREATE INDEX cf_client_dtiszn_discount_info_version_idx ON cf_client_dtiszn_discount_info USING btree (version);

--! ФИНАЛИЗИРОВАН 25.12.2018, НЕ МЕНЯТЬ