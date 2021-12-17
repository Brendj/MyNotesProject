--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.158

--Флаг информирования об условиях предоставления питания по предзаказам
alter table cf_client_guardian add column informedspecialmenu integer;

alter table cf_users add column idofperson bigint;
alter table cf_users add column department character varying(128);
alter table cf_history_card add column idoftransaction bigint;

--! ФИНАЛИЗИРОВАН 05.06.2018, НЕ МЕНЯТЬ