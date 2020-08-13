--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 609

alter table cf_mh_persons add column lastupdate_rest timestamp without time zone;

--новые параметры в конфиг:
--ecafe.processing.mesh.rest.address
--ecafe.processing.mesh.rest.api.key
--ecafe.processing.mesh.rest.persons.top