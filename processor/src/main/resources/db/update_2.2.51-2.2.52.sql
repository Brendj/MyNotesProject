--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.52

-- суточная проба
alter table cf_goods_requests_positions add column DailySampleCount bigint default null;
alter table cf_goods_requests_positions add column UpdateHistory text default null;
alter table cf_contragents add column RequestNotifyMailList character varying(1024) default null;

alter table cf_users add column region varchar(10) default null;

--! ФИНАЛИЗИРОВАН (Кадыров, 131213) НЕ МЕНЯТЬ