--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 344

CREATE SEQUENCE application_for_food_id_seq;
select setval('application_for_food_id_seq', (SELECT coalesce(max(version), 0) + 1 from cf_applications_for_food));

CREATE SEQUENCE application_for_food_history_id_seq;
select setval('application_for_food_history_id_seq', (SELECT coalesce(max(version), 0) + 1 from cf_applications_for_food_history));
