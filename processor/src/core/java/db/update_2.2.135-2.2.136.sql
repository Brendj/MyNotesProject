--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.136

--Переносим поле createdFrom из клиентов на связку клиент-представитель
alter table cf_clients drop column createdFrom;

alter table cf_client_guardian add column createdFrom integer not null default 0;