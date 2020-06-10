--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 540

CREATE SEQUENCE clientphoto_version_seq;
select setval('clientphoto_version_seq', (select coalesce(max(version), 0) + 1 from cf_clientphoto));
