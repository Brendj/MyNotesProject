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