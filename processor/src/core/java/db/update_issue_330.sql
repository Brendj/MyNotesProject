--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 330

alter table cf_client_guardian add column allowedpreorder integer;

--! ФИНАЛИЗИРОВАН 09.12.2019, НЕ МЕНЯТЬ