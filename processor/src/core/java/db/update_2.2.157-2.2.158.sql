--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.158

--Флаг информирования об условиях предоставления питания по предзаказам
alter table cf_client_guardian add column informedspecialmenu integer;