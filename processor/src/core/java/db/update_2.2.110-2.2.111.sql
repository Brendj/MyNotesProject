--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.110

--Увеличение размерности поля для условия выборки в отчетах по расписанию
ALTER TABLE cf_ruleconditions ALTER  conditionconstant TYPE character varying (4200);

--Добавление обязательного поля shortnameinfoservice для таблицы с загруженными из файла организациями
ALTER TABLE cf_not_planned_orgs ADD COLUMN shortnameinfoservice character varying(128);

--Дополнительный статус организации (например, устарел гуид относительно АИС Реестр)
ALTER TABLE cf_orgs_sync ADD COLUMN errorstate integer;

--Сохранение флага Учет последнего события входа в планах питания
ALTER TABLE cf_clients ADD COLUMN uselasteemodeforplan integer;

-- Добавление колонки в Org с указанием последней даты изменения
alter table cf_orgs ADD COLUMN lastupdate bigint;
--! Изначальная дата последнего изменения установлена на 10.02.2016 00:00:00.000
update cf_orgs set lastupdate=1455051600000;