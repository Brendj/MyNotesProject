--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.167

--таблица отправленных статусов заявлений
alter table cf_etp_outgoing_message add column errormessage character varying(100);

alter table cf_applications_for_food add column sendtoaiscontingent integer NOT NULL DEFAULT 0;
