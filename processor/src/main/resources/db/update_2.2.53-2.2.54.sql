--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.54

-- Поправки в отчете по заявкам
-- Добавлено увидомление о заявках
--! предыдущие значения Суточной пробы и общего количества
alter table cf_goods_requests_positions add column LastDailySampleCount bigint default null;
alter table cf_goods_requests_positions add column LastTotalCount bigint default null;
--! нет необходимости хранить данную колнку
alter table cf_goods_requests_positions drop column UpdateHistory;

--! ФИНАЛИЗИРОВАН (Кадыров, 140110) НЕ МЕНЯТЬ
